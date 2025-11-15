package com.btf.quick_tasks.networkFile;

import com.btf.quick_tasks.dataBase.model.NewsResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("everything")
    Call<NewsResponseModel> getEverything(
            @Query("q") String q,
            @Query("from") String from,
            @Query("to") String to,
            @Query("sortBy") String sortBy,
            @Query("domains") String domains,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Call<NewsResponseModel> getTopHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("sources") String sources,
            @Query("apiKey") String apiKey
    );

}