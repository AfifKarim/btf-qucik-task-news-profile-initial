package com.btf.quick_tasks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import com.btf.quick_tasks.appUtils.NotificationHelper;
import com.btf.quick_tasks.appUtils.NotificationScheduler;
import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.btf.quick_tasks.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences prefs;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------------------------
        // APPLY SAVED THEME FIRST
        // -------------------------
        prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        NotificationHelper.createNotificationChannel(this);
        // -------------------------
        // SCHEDULE NOTIFICATIONS FOR ALL TASKS
        // -------------------------
        scheduleAllTaskNotifications();

        // Inflate view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // -------------------------
        // NAVIGATION SETUP
        // -------------------------
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_tasks, R.id.nav_news, R.id.nav_profile)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // THEME SWITCH IN NAV DRAWER
            MenuItem themeItem = navigationView.getMenu().findItem(R.id.action_theme);
            if (themeItem != null) {
                LabeledSwitch labeledSwitch = themeItem.getActionView().findViewById(R.id.nav_switch);
                labeledSwitch.setOn(isDark);
                labeledSwitch.setOnToggledListener((sw, isOn) -> toggleTheme(isOn));
            }
        }

        // Bottom Nav
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // -------------------------
        // FAB CLICK ACTION
        // -------------------------
        binding.appBarMain.addTaskBtn.setOnClickListener(view -> {
            navController = Navigation.findNavController(
                    this, R.id.nav_host_fragment_content_main
            );
            navController.navigate(R.id.addTaskFragment);
        });

        // -------------------------
        // SHOW/HIDE FAB BASED ON DESTINATION
        // -------------------------
        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            if (destination.getId() == R.id.nav_tasks) {
                binding.appBarMain.toolbar.setTitle(getString(R.string.menu_tasks));
                binding.appBarMain.addTaskBtn.setVisibility(View.VISIBLE);
                binding.appBarMain.contentMain.bottomNavView.setVisibility(View.VISIBLE);
            } else if (destination.getId() == R.id.nav_news) {
                binding.appBarMain.toolbar.setTitle(getString(R.string.menu_news));
                binding.appBarMain.addTaskBtn.setVisibility(View.GONE);
                binding.appBarMain.contentMain.bottomNavView.setVisibility(View.VISIBLE);
            } else if (destination.getId() == R.id.nav_profile) {
                binding.appBarMain.toolbar.setTitle(getString(R.string.menu_user_profile));
                binding.appBarMain.addTaskBtn.setVisibility(View.GONE);
                binding.appBarMain.contentMain.bottomNavView.setVisibility(View.VISIBLE);
            } else if (destination.getId() == R.id.addTaskFragment) {
                if (args != null && args.getInt("Id", 0) != 0) {
                    binding.appBarMain.toolbar.setTitle(getString(R.string.menu_update_task));
                } else {
                    binding.appBarMain.toolbar.setTitle(getString(R.string.menu_add_task));
                }
                binding.appBarMain.addTaskBtn.setVisibility(View.GONE);
                binding.appBarMain.contentMain.bottomNavView.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.previewTaskFragment) {
                binding.appBarMain.toolbar.setTitle(getString(R.string.menu_preview_task));
                binding.appBarMain.addTaskBtn.setVisibility(View.GONE);
                binding.appBarMain.contentMain.bottomNavView.setVisibility(View.GONE);
            }
        });

    }

    private void toggleTheme(boolean darkMode) {
        prefs.edit().putBoolean("dark_mode", darkMode).apply();
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);

        MenuItem item = menu.findItem(R.id.action_theme);
        if (item != null) {
            LabeledSwitch labeledSwitch = item.getActionView().findViewById(R.id.nav_switch);
            boolean isDark = prefs.getBoolean("dark_mode", false);
            labeledSwitch.setOn(isDark);
            labeledSwitch.setOnToggledListener((sw, isOn) -> toggleTheme(isOn));
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Load all tasks from DB and schedule notifications for each one
     */
    private void scheduleAllTaskNotifications() {
        tasksDb db = tasksDb.getInstance(this);
        CommonDAO dao = db.commonDao();

        List<TaskEntity> tasks = dao.getAllTasksDirect();

        if (tasks == null || tasks.isEmpty()) {
            Log.e("Notify", "No tasks found, nothing to schedule.");
            return;
        }

        Log.e("Notify", "Scheduling notifications for " + tasks.size() + " tasks.");

        for (TaskEntity task : tasks) {
            NotificationScheduler.scheduleTaskNotification(this, task);
        }
    }
}
