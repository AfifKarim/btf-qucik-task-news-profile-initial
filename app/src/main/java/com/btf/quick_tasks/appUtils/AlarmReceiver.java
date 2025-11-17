package com.btf.quick_tasks.appUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;

import java.util.concurrent.Executors;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        final int taskId = intent.getIntExtra("taskId", -1);
        if (taskId == -1) {
            pendingResult.finish();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                tasksDb db = tasksDb.getInstance(context.getApplicationContext());
                CommonDAO dao = db.commonDao();
                TaskEntity task = dao.getTaskByIdDirect(taskId);

                if (task == null) return;
                if (Boolean.TRUE.equals(task.getShown())) return;

                NotificationHelper.createChannel(context);

                if (!NotificationHelper.hasNotificationPermission(context)) {
                    Log.d("taskNotify", "Notification permission not granted for task: " + task.getTitle());
                    return;
                }

                NotificationHelper.showNotification(context, task);

                task.setShown(true);
                dao.update(task);

                Log.d("taskNotify", "Notification sent: " + task.getTitle());
            } catch (Exception e) {
                Log.e("taskNotify", "Error in AlarmReceiver", e);
            } finally {
                pendingResult.finish();
            }
        });
    }
}
