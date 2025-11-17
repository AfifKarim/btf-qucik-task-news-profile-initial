package com.btf.quick_tasks.appUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;
import com.btf.quick_tasks.dataBase.tasksDb;

import java.util.List;
import java.util.concurrent.Executors;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                NotificationHelper.createChannel(context);

                tasksDb db = tasksDb.getInstance(context.getApplicationContext());
                CommonDAO dao = db.commonDao();
                List<TaskEntity> tasks = dao.getAllUnshownTasks();

                if (tasks != null && !tasks.isEmpty()) {
                    for (TaskEntity t : tasks) {
                        NotificationScheduler.scheduleTask(context, t);
                    }
                    Log.d("taskNotify", "Rescheduled " + tasks.size() + " tasks on boot");
                }
            } catch (Exception e) {
                Log.e("taskNotify", "Error rescheduling tasks on boot", e);
            }
        });
    }
}
