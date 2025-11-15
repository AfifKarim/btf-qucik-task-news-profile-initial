package com.btf.quick_tasks.ui.tasks;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.btf.quick_tasks.R;
import com.btf.quick_tasks.databinding.ItemTasksBinding;

import java.util.Arrays;
import java.util.List;

public class TasksAdapter extends ListAdapter<String, TasksAdapter.TasksViewHolder> {

    // Dummy images (replace with your own)
    private final List<Integer> drawables = Arrays.asList(
            R.drawable.ic_tasks,
            R.drawable.ic_tasks,
            R.drawable.ic_tasks,
            R.drawable.ic_tasks
    );

    public TasksAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTasksBinding binding = ItemTasksBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TasksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
        holder.taskTitle.setText(getItem(position));

        if (position < drawables.size()) {
            holder.taskImage.setImageDrawable(
                    ResourcesCompat.getDrawable(holder.taskImage.getResources(),
                            drawables.get(position), null)
            );
        }
    }

    static class TasksViewHolder extends RecyclerView.ViewHolder {

        ImageView taskImage;
        TextView taskTitle;

        TasksViewHolder(ItemTasksBinding binding) {
            super(binding.getRoot());
            taskImage = binding.taskImage;
            taskTitle = binding.taskTitle;
        }
    }
}
