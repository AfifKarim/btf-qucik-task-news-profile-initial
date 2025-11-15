package com.btf.quick_tasks.dataBase.model;

import com.google.gson.annotations.SerializedName;

public class SourceResponseModel {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
