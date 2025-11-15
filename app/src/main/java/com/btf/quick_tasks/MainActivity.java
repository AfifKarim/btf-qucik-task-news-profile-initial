package com.btf.quick_tasks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
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

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences prefs;

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

        // -------------------------
        // NORMAL ACTIVITY SETUP
        // -------------------------
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (binding.appBarMain.addTaskBtn != null) {
            binding.appBarMain.addTaskBtn.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setAnchorView(R.id.addTaskBtn).show());
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // -------------------------
        // NAVIGATION DRAWER SETUP
        // -------------------------
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_tasks, R.id.nav_news, R.id.nav_profile)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // -------------------------
            // SETUP SWITCH IN NAV DRAWER
            // -------------------------
            MenuItem themeItem = navigationView.getMenu().findItem(R.id.action_theme);
            if (themeItem != null) {
                LabeledSwitch labeledSwitch = themeItem.getActionView().findViewById(R.id.nav_switch);
                labeledSwitch.setOn(labeledSwitch.isOn()); // to force initial redraw

                // Set initial switch state
                labeledSwitch.setOn(isDark);

                labeledSwitch.setOnToggledListener((labeledSwitch1, isOn) -> {
                    toggleTheme(isOn);
                });
            }
        }

        // -------------------------
        // BOTTOM NAVIGATION (if needed)
        // -------------------------
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
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

        // Setup switch in toolbar menu
        MenuItem item = menu.findItem(R.id.action_theme);
        if (item != null) {
            LabeledSwitch labeledSwitch = item.getActionView().findViewById(R.id.nav_switch);
            boolean isDark = prefs.getBoolean("dark_mode", false);
            labeledSwitch.setOn(isDark);

            labeledSwitch.setOnToggledListener((labeledSwitch1, isOn) -> {
                toggleTheme(isOn);
            });
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
