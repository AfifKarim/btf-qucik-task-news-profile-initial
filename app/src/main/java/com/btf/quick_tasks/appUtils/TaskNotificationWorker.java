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

    public TaskNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
        Log.e("WorkerNotify", "⚙️ Worker instance created");
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.e("WorkerNotify", "🚀 Worker started: Checking tasks...");

        try {

            // DB + DAO
            Log.e("WorkerNotify", "📥 Getting database instance");
            tasksDb db = tasksDb.getInstance(getApplicationContext());

            Log.e("WorkerNotify", "📥 Getting DAO");
            CommonDAO dao = db.commonDao();

            Log.e("WorkerNotify", "📦 Fetching tasks using getAllTasksDirect()");
            List<TaskEntity> taskList = dao.getAllTasksDirect();

            if (taskList == null || taskList.isEmpty()) {
                Log.e("WorkerNotify", "❗ No tasks found in DB");
                return Result.success();
            }

            Log.e("WorkerNotify", "📋 Total tasks found: " + taskList.size());

            long now = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            for (TaskEntity task : taskList) {

                Log.e("WorkerNotify", "➡️ Checking task ID: " + task.getId() + ", Title: " + task.getTitle());

                if (task.getDueDate() == null || task.getDueDate().isEmpty()) {
                    Log.e("WorkerNotify", "⛔ Skipped (dueDate missing)");
                    continue;
                }

                Log.e("WorkerNotify", "🕒 Task Due Date: " + task.getDueDate());

                Date due = sdf.parse(task.getDueDate());
                long diff = due.getTime() - now;

                Log.e("WorkerNotify", "⏱ Time difference (ms): " + diff);

                // Notify if due within NEXT 1 HOUR
                if (diff > 0 && diff <= (60 * 60 * 1000)) {

                    Log.e("WorkerNotify",
                            "🔔 Sending notification for task: " + task.getTitle());

                    NotificationHelper.showNotification(
                            getApplicationContext(),
                            "Task Due Soon",
                            "Task \"" + task.getTitle()
                                    + "\" is due at "
                                    + task.getDueDate()
                    );
                } else {
                    Log.e("WorkerNotify", "⛔ Not within 1-hour notification window");
                }
            }

        } catch (Exception e) {
            Log.e("WorkerNotify", "❌ Error in Worker: " + e.getMessage(), e);
            return Result.failure();
        }

        Log.e("WorkerNotify", "✅ Worker finished successfully");
        return Result.success();
    }
}
