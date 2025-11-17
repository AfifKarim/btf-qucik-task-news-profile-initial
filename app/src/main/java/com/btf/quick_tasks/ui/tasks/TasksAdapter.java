package com.btf.quick_tasks.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.btf.quick_tasks.R;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.databinding.ItemTasksBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    public static final String TAG = TasksAdapter.class.getName();
    List<TaskEntity> list = new ArrayList<>();
    Context context;
    ItemLongClick itemLongClick;

    public TasksAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTasksBinding binding = ItemTasksBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TasksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TasksViewHolder holder, int position) {

        TaskEntity m = list.get(position);

        holder.mbinding.taskTitle.setText(m.getTitle());
        holder.mbinding.taskDesc.setText(m.getDescription());
        holder.mbinding.taskCreatedAt.setText("Created Date: " + m.getCreatedAt());
        holder.mbinding.taskDueDate.setText("Due Date: " + m.getDueDate());
        holder.mbinding.taskPriority.setText(m.getPriority());

        holder.mbinding.getRoot().setOnLongClickListener(v -> {
            if (m != null) {
                itemLongClick.itemLongClickListener(position, m);
            }
            return true;
        });


        holder.mbinding.getRoot().setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("Id", m.getId());
            Navigation.findNavController(holder.itemView)
                    .navigate(R.id.previewTaskFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setTaskList(List<TaskEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void TaskFilter(List<TaskEntity> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public void setItemLongClick(ItemLongClick listener) {
        this.itemLongClick = listener;
    }

    public interface ItemLongClick {
        //        void itemLongClickListener(int position, AdmissionBasicEntity enrollID);
        void itemLongClickListener(int position, TaskEntity taskEntity);
    }


    public static class TasksViewHolder extends RecyclerView.ViewHolder {

        ItemTasksBinding mbinding;

        public TasksViewHolder(@NonNull ItemTasksBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }
    }
}
