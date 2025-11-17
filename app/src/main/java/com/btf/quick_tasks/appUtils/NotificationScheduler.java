package com.btf.quick_tasks.appUtils;

import android.content.Context;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    public static void startFifteenMinuteChecker(Context context) {

        Log.e("WorkerNotify", "⏳ Starting 15-minute WorkManager scheduler");

        OneTimeWorkRequest testRequest = new OneTimeWorkRequest.Builder(TaskNotificationWorker.class)
                .build();

        WorkManager.getInstance(context).enqueue(testRequest);

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        TaskNotificationWorker.class,
                        15, TimeUnit.MINUTES
                ).build();

        Log.e("WorkerNotify", "⏳ WorkRequest created (15 min interval)");

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        "task_checker",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                );

        Log.e("WorkerNotify", "✅ WorkManager scheduled successfully");
    }
}
