package com.btf.quick_tasks.dataBase.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponseModel {

    @SerializedName("status")
    private String status;

    @SerializedName("totalResults")
    private Integer totalResults;

    @SerializedName("articles")
    private List<ArticleResponseModel> articles;

    // Error fields
    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<ArticleResponseModel> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleResponseModel> articles) {
        this.articles = articles;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
