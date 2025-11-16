package com.btf.quick_tasks.ui.tasks;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TasksRepository {

    private final Context context;
    CommonDAO commonDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();


    public TasksRepository(Application application) {
        this.context = application.getApplicationContext();
        commonDAO = tasksDb.getInstance(application).commonDao();
    }

    // Insert method on background thread
    public void insert(TaskEntity task) {
        executor.execute(() -> commonDAO.insert(task));
    }

    public LiveData<List<TaskEntity>> getTaskByStatusDate(String fromDate, String toDate, String selectStatus) {
        if (selectStatus != null && !selectStatus.isEmpty()) {
            return commonDAO.getTaskByStatusDate(fromDate, toDate, selectStatus);
        } else {
            return commonDAO.getTaskByDate(fromDate, toDate);
        }
    }
}
