package com.btf.quick_tasks.appUtils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskNotificationWorker extends Worker {

    public TaskNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("title");
        String dueDate = getInputData().getString("dueDate");

        if (title == null || dueDate == null) {
            return Result.failure();
        }

        NotificationHelper.showNotification(
                getApplicationContext(),
                "Task Due",
                "Task \"" + title + "\" is due now (" + dueDate + ")"
        );

        return Result.success();
    }
}
