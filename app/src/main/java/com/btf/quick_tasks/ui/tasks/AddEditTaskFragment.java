package com.btf.quick_tasks.ui.tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import androidx.navigation.fragment.NavHostFragment;

import com.btf.quick_tasks.MainActivity;
import com.btf.quick_tasks.R;
import com.btf.quick_tasks.appUtils.DatePickerFragment;
import com.btf.quick_tasks.appUtils.Global;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.databinding.FragmentAddTasksBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditTaskFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = AddEditTaskFragment.class.getName();
    private FragmentAddTasksBinding binding;
    private TasksViewModel viewModel;
    private ArrayAdapter<String> prioritySpinner, statusSpinner;
    private String select_priority, select_status, sdueDate = null;
    private boolean fdueDate = false;
    private int taskId = 0;        // NEW: For edit mode
    private TaskEntity currentTask; // NEW

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

        // NEW: Get taskId from Preview screen (for edit mode)
        if (getArguments() != null) {
            taskId = getArguments().getInt("Id", 0);
        }

        // NEW: If editing → Load existing data
        if (taskId != 0) {
            loadTaskForEdit(taskId);
        }

        setToolbarTitle();

        binding.dueDateET.setOnClickListener(v -> {
            fdueDate=true;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(AddEditTaskFragment.this, 0);
            datePicker.show(getFragmentManager(), "targetDate");
        });

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Save selected date only
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String selectedDate = sdf.format(c.getTime());

        if (fdueDate) {
            // Show TimePicker now
            TimePickerDialog timePicker = new TimePickerDialog(
                    getContext(),
                    (timeView, hourOfDay, minute) -> {

                        // Combine user-selected date + time
                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(Calendar.YEAR, year);
                        selectedCal.set(Calendar.MONTH, month);
                        selectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCal.set(Calendar.MINUTE, minute);
                        selectedCal.set(Calendar.SECOND, 0);
                        selectedCal.set(Calendar.MILLISECOND, 0);

                        // Current time
                        Calendar now = Calendar.getInstance();

                        // Validation: Check past time
                        if (selectedCal.before(now)) {
                            Global.showDialog(requireContext(), R.drawable.ic_error, "Invalid Due Date", "Due date cannot be in the past.");
                            sdueDate = null;
                            binding.dueDateET.setText("");
                            fdueDate = false;
                            return; // STOP here
                        }

                        // Format final datetime
                        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        sdueDate = finalFormat.format(selectedCal.getTime());

                        binding.dueDateET.setText(sdueDate);
                        Log.d(TAG, "Due DateTime Selected: " + sdueDate);

                        fdueDate = false;
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true
            );
            timePicker.show();
        }
    }

    // -------------------------------------------------
    //          LOAD DATA FOR EDIT MODE
    // -------------------------------------------------
    private void loadTaskForEdit(int id) {
        viewModel.getTaskById(id).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                currentTask = task;

                binding.titleET.setText(task.getTitle());
                binding.descriptionET.setText(task.getDescription());

                // Spinner selections
                binding.prioritySpinner.setSelection(prioritySpinner.getPosition(task.getPriority()));
                binding.statusSpinner.setSelection(statusSpinner.getPosition(task.getStatus()));

                sdueDate = task.getDueDate();
                binding.dueDateET.setText(task.getDueDate());

                binding.btnSave.setText("Update Task"); // NEW
            }
        });
    }

    // -------------------------------------------------
    //          SAVE / UPDATE TASK
    // -------------------------------------------------
    private void saveTask() {
        String title = binding.titleET.getText().toString().trim();
        String desc = binding.descriptionET.getText().toString().trim();

        // Prepare task object
        if (taskId == 0) {
            // NEW TASK
            currentTask = new TaskEntity(title, desc, select_priority, sdueDate, select_status);
            currentTask.setCreatedAt(Global.getCurrentDateWithTime());
            currentTask.setUpdatedAt(Global.getCurrentDateWithTime());
        } else {
            // UPDATE TASK
            if (currentTask != null) {
                currentTask.setTitle(title);
                currentTask.setDescription(desc);
                currentTask.setPriority(select_priority);
                currentTask.setStatus(select_status);
                currentTask.setDueDate(sdueDate);
                currentTask.setUpdatedAt(Global.getCurrentDateWithTime());
            } else {
                Toast.makeText(getContext(), "Error: Task not loaded", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate
        List<String> errors = currentTask.checkValidation();
        if (!errors.isEmpty()) {
            showValidationError(errors);
            return; // Stop here if invalid
        }

        // Save to database
        if (taskId == 0) {
            viewModel.insert(currentTask);
            Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.update(currentTask);
            Toast.makeText(getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
        }
        // Navigate back
        NavHostFragment.findNavController(this).navigateUp();
    }

    private void showValidationError(List<String> list) {
        StringBuilder msg = new StringBuilder();
        for (String error : list) {
            msg.append("<font color='red'>*</font> ").append(error).append("<br>");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Required Fields")
                .setIcon(R.drawable.ic_error);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(msg.toString(), Html.FROM_HTML_MODE_LEGACY));
        }
        builder.setPositiveButton("OK", (d, w) -> d.dismiss());
        builder.show();
    }

    private void setToolbarTitle() {
        if (getActivity() instanceof MainActivity) {
            if (taskId == 0) {
                ((MainActivity) getActivity()).getSupportActionBar()
                        .setTitle(getString(R.string.menu_add_task));
            } else {
                ((MainActivity) getActivity()).getSupportActionBar()
                        .setTitle(getString(R.string.menu_update_task));
            }
        }
    }
}
