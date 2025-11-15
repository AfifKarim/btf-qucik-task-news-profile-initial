package com.btf.quick_tasks.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btf.quick_tasks.databinding.FragmentTasksBinding;
import com.btf.quick_tasks.ui.tasks.TasksAdapter;
import com.btf.quick_tasks.ui.tasks.TasksViewModel;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TasksViewModel viewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        TasksAdapter adapter = new TasksAdapter();

        binding.recyclerviewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewTasks.setAdapter(adapter);

        viewModel.getTexts().observe(getViewLifecycleOwner(), adapter::submitList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
