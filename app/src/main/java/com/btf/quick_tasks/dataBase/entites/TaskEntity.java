package com.btf.quick_tasks.dataBase.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "tasks")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private Integer Id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "priority")
    private String priority;

    @ColumnInfo(name = "dueDate")
    private String dueDate;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "createdAt")
    private String createdAt;

    @ColumnInfo(name = "updatedAt")
    private String updatedAt;

    public TaskEntity(String title, String description, String priority, String dueDate, String status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
    }

    public TaskEntity(Integer id, String title, String description, String priority, String dueDate, String status, String createdAt, String updatedAt) {
        Id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> checkValidation() {
        List<String> list = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            list.add("Task Title Is Required");
        }

        if (description == null || description.trim().isEmpty()) {
            list.add("Description Is Required");
        }

        if (priority == null || priority.trim().isEmpty()) {
            list.add("Priority Is Required");
        }

        if (dueDate == null || dueDate.trim().isEmpty()) {
            list.add("Due Date Is Required");
        }

        if (status == null || status.trim().isEmpty()) {
            list.add("Status Is Required");
        }


        return list;
    }
}