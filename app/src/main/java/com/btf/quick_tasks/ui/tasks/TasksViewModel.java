package com.btf.quick_tasks.ui.tasks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class TasksViewModel extends AndroidViewModel {

    private TasksRepository tasksRepository;

    public TasksViewModel(@NonNull Application application) {
        super(application);
        tasksRepository = new TasksRepository(application);
    }

    public void insert(TaskEntity task) {
        tasksRepository.insert(task);
    }

    public void update(TaskEntity task) {
        tasksRepository.update(task);
    }


    public void delete(TaskEntity task) {
        tasksRepository.delete(task);
    }

    public void deleteById(int taskId) {
        tasksRepository.deleteById(taskId);
    }

    public LiveData<TaskEntity> getTaskById(int id) {
        return tasksRepository.getTaskById(id);
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return tasksRepository.getAllTasks();
    }

    public LiveData<List<TaskEntity>> getTaskByStatusDate(String fromDate, String toDate, String selectStatus) {
        return tasksRepository.getTaskByStatusDate(fromDate, toDate, selectStatus);
    }

}
