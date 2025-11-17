package com.btf.quick_tasks.ui.tasks;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btf.quick_tasks.R;
import com.btf.quick_tasks.appUtils.DatePickerFragment;
import com.btf.quick_tasks.appUtils.Global;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.databinding.FragmentTasksBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TasksFragment extends Fragment implements TasksAdapter.ItemLongClick {

    public static final String TAG = TasksFragment.class.getName();
    FragmentTasksBinding binding;
    TasksViewModel tasksViewModel;
    TasksAdapter tasksAdapter;
    List<TaskEntity> list;
    private ArrayAdapter<String> statusAdapter;

    public TasksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        binding = FragmentTasksBinding.inflate(getLayoutInflater(), container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        tasksAdapter = new TasksAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.recyclerviewTasks.setLayoutManager(llm);
        binding.recyclerviewTasks.setHasFixedSize(true);
        binding.recyclerviewTasks.setAdapter(tasksAdapter);
        tasksAdapter.setItemLongClick(TasksFragment.this);

        fetchData(Global.getCurrentDateYYMMDD() + " 00:00:00", Global.getCurrentDateYYMMDD() + " 23:59:59", null);
    }

    private void fetchData(String fromDate, String toDate, String status) {

        Log.d(TAG, "fetchData: " + "fromDate: " + fromDate + " " + "toDate: " + toDate + " " + "status: " + status);
        tasksViewModel.getTaskByStatusDate(fromDate, toDate, status)
                .observe(getViewLifecycleOwner(), ssJoinQueries -> {
                    tasksAdapter.setTaskList(ssJoinQueries);
                    list = ssJoinQueries;
                    if (list != null) {
                        if (list.size() == 0) {
                            binding.recyclerviewTasks.setVisibility(View.GONE);
                            binding.noTaksPH.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerviewTasks.setVisibility(View.VISIBLE);
                            binding.noTaksPH.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void itemLongClickListener(int position, TaskEntity taskEntity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Are you sure?")
                .setMessage("Won't be able to recover this!")
                .setPositiveButton("Yes, delete it!", (dialog, which) -> {

                    if (taskEntity.getId() != null) {
                        tasksViewModel.delete(taskEntity); // ⭐ ViewModel handles deletion
                    }

                    tasksAdapter.notifyItemRemoved(position);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

}