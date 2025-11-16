package com.btf.quick_tasks.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.btf.quick_tasks.dataBase.dao.CommonDAO;
import com.btf.quick_tasks.dataBase.entites.TaskEntity;

@Database(
        entities = {
                TaskEntity.class,
        },
        version = 1, // keep this as 1
        exportSchema = false
)
public abstract class tasksDb extends RoomDatabase {

    private static tasksDb instance;

    public static synchronized tasksDb getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            tasksDb.class, "tasksDb")
                    .fallbackToDestructiveMigration() // ✅ auto-clear DB on schema mismatch
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract CommonDAO commonDao();
}
