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

public class TasksFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TasksAdapter.ItemLongClick {

    public static final String TAG = TasksFragment.class.getName();
    FragmentTasksBinding binding;
    TasksViewModel tasksViewModel;
    TasksAdapter tasksAdapter;
    List<TaskEntity> list;
    private String sfrmDate, stoDate, select_status = null;
    private boolean ffrmDate, ftoDate = false;
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

        sfrmDate = Global.getCurrentMonth() + "-01 00:00:00";
        stoDate   = Global.getCurrentDateWithTime();

        binding.fromDateTV.setText("From : " + sfrmDate);
        binding.toDateTV.setText("To : " + stoDate);

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterData(s.toString());
            }
        });

        binding.fromDateTV.setOnClickListener(v -> {
            ffrmDate=true;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(TasksFragment.this, 0);
            datePicker.show(getFragmentManager(), "targetDate");
        });

        binding.toDateTV.setOnClickListener(v -> {
            ftoDate=true;
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(TasksFragment.this, 0);
            datePicker.show(getFragmentManager(), "targetDate");
        });


        // Set adapter
        statusAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.select_status));
        binding.statusSP.setAdapter(statusAdapter);

        binding.statusSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    select_status = null;
                    fetchData(sfrmDate, stoDate, null);
                } else {
                    select_status = parent.getItemAtPosition(position).toString();
                    fetchData(sfrmDate, stoDate, select_status);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void fetchData(String fromDate, String toDate, String status) {

        Log.d(TAG, "fetchData: " + "fromDate: " + fromDate + " " + "toDate: " + toDate + " " + "status: " + status);
        tasksViewModel.getTaskByStatusDate(fromDate, toDate, status)
                .observe(getViewLifecycleOwner(), ssJoinQueries -> {
                    tasksAdapter.setTaskList(ssJoinQueries);
                    list = ssJoinQueries;
                    if (list != null) {
                        if (list.size() == 0) {
                            Toast.makeText(getContext(), "Empty Tasks List", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String selectedDate =simpleDateFormat.format(c.getTime());

        if (ffrmDate) {
            sfrmDate = selectedDate + " 00:00:00";
            binding.fromDateTV.setText("From : "+ sfrmDate);
            ffrmDate = false;
            fetchData(sfrmDate, stoDate, select_status);
            Log.d(TAG, "onDateSet -> fromDate selected: " + sfrmDate);
        }
        if (ftoDate) {
            stoDate = selectedDate + " 23:59:59";
            binding.toDateTV.setText("To :"+ stoDate);
            ftoDate = false;
            fetchData(sfrmDate, stoDate, select_status);
            Log.d(TAG, "onDateSet -> toDate selected: " + stoDate);
        }
    }

    private void filterData(String text) {
        List<TaskEntity> filteredList = new ArrayList<>();
        for (TaskEntity member : list) {
            if (member.getTitle().toLowerCase().contains(text.toLowerCase())
                    || member.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(member);
            }
        }
        tasksAdapter.setTaskList(filteredList);

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