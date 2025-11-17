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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.btf.quick_tasks.R;
import com.btf.quick_tasks.appUtils.DatePickerFragment;
import com.btf.quick_tasks.appUtils.Global;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.databinding.FragmentAddTasksBinding;
import com.btf.quick_tasks.databinding.FragmentPreviewTasksBinding;
import com.btf.quick_tasks.ui.tasks.TasksViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PreviewTaskFragment extends Fragment {

    private FragmentPreviewTasksBinding binding;
    private TasksViewModel viewModel;
    private int taskId = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPreviewTasksBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            taskId = getArguments().getInt("Id", -1);
        }

        if (taskId != 0) {
            loadTaskData(taskId);
        }

        binding.btnEdit.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putInt("Id", taskId);
            Navigation.findNavController(v).navigate(R.id.addTaskFragment, b);
        });
    }

    private void loadTaskData(int id) {
        viewModel.getTaskById(id).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                binding.previewTitle.setText(task.getTitle());
                binding.previewDescription.setText(task.getDescription());
                binding.previewPriority.setText(task.getPriority());
                binding.previewDueDate.setText(task.getDueDate());
                binding.previewStatus.setText(task.getStatus());
            }
        });
    }
}
