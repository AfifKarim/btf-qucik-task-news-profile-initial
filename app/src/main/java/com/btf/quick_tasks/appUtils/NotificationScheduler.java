package com.btf.quick_tasks.appUtils;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.btf.quick_tasks.dataBase.entites.TaskEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    public static void scheduleTaskNotification(Context context, TaskEntity task) {

        // no due date? skip.
        if (task.getDueDate() == null || task.getDueDate().isEmpty())
            return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date due = sdf.parse(task.getDueDate());
            long now = System.currentTimeMillis();

            long delay = due.getTime() - now;

            // Past-time task: do not schedule
            if (delay <= 0) return;

            Data data = new Data.Builder()
                    .putString("title", task.getTitle())
                    .putString("dueDate", task.getDueDate())
                    .build();

            OneTimeWorkRequest request =
                    new OneTimeWorkRequest.Builder(TaskNotificationWorker.class)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(data)
                            .build();

            WorkManager.getInstance(context).enqueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}