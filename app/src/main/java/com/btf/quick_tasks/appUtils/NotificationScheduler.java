package com.btf.quick_tasks.appUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.btf.quick_tasks.dataBase.entites.TaskEntity;

import java.text.ParseException;

public class NotificationScheduler {

    public static void scheduleTask(Context context, TaskEntity task) {
        if (!ExactAlarmHelper.canScheduleExactAlarms(context)) {
            Log.e("taskNotify", "Exact alarm permission not granted for task: " + task.getTitle());
            return;
        }

        try {
            long triggerTime = Global.parseToMillis(task.getDueDate());
            long now = System.currentTimeMillis();
            if (triggerTime < now) triggerTime = now + 2000;

            Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("taskId", task.getId());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    task.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Log.d("taskNotify", "Scheduled alarm: " + task.getTitle() + " at " + Global.formatMillis(triggerTime));
            }
        } catch (ParseException e) {
            Log.e("taskNotify", "Failed to parse due date: " + task.getTitle(), e);
        }
    }
}
