package com.btf.quick_tasks.dataBase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.btf.quick_tasks.dataBase.entites.TaskEntity;

import java.util.List;

@Dao
public interface CommonDAO {

    @Insert
    void insert(TaskEntity task);

    @Query("select * from tasks " +
            "where status Like '%' || :selectStatus || '%' " +
            "and tasks.createdAt between :fromDate and :toDate " +
            "and tasks.updatedAt != 0 " +
            "order by tasks.id desc ")
    LiveData<List<TaskEntity>> getTaskByStatusDate(String fromDate, String toDate, String selectStatus);

    @Query("select * from tasks " +
            "where tasks.createdAt between :fromDate and :toDate " +
            "and tasks.updatedAt != 0 " +
            "order by tasks.id desc ")
    LiveData<List<TaskEntity>> getTaskByDate(String fromDate, String toDate);

}
