package com.btf.quick_tasks.ui.tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import androidx.navigation.fragment.NavHostFragment;

import com.btf.quick_tasks.R;
import com.btf.quick_tasks.appUtils.DatePickerFragment;
import com.btf.quick_tasks.appUtils.Global;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.databinding.FragmentAddTasksBinding;
import com.btf.quick_tasks.ui.tasks.TasksViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskFragment extends Fragment {

    private FragmentAddTasksBinding binding;
    private TasksViewModel viewModel;
    private ArrayAdapter<String> prioritySpinner, statusSpinner;
    private String select_priority, select_status = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        binding = FragmentAddTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        setupSpinners();

        binding.btnSave.setOnClickListener(v -> saveTask());
    }

    private void setupSpinners() {

        prioritySpinner = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.select_priority));
        binding.prioritySpinner.setAdapter(prioritySpinner);

        binding.prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    select_priority = null;
                    return;
                }
                select_priority = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        statusSpinner = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.select_status));
        binding.statusSpinner.setAdapter(statusSpinner);

        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    select_status = null;
                    return;
                }
                select_status = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void saveTask() {
        String title = binding.titleET.getText().toString().trim();
        String desc = binding.descriptionET.getText().toString().trim();

        TaskEntity taskEntity = new TaskEntity(
                title, desc, select_priority, select_status
        );
        taskEntity.setCreatedAt(Global.getCurrentDateWithTime());
        taskEntity.setUpdatedAt(Global.getCurrentDateWithTime());

        List<String> list = taskEntity.checkValidation();

        if (!list.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (String error : list) {
                msg.append("<font color='red'>*</font> ") // 🔴 red star before each message
                        .append(error)
                        .append("<br>");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMessage(Html.fromHtml(msg.toString(), Html.FROM_HTML_MODE_LEGACY)) // ✅ Render HTML safely
                        .setIcon(R.drawable.ic_error)
                        .setTitle("Required Field")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            }

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            viewModel.insert(taskEntity);
            Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
            // Navigate back to TasksFragment
            NavHostFragment.findNavController(this).navigateUp();
        }
    }
}
