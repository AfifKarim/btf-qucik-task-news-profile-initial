package com.btf.quick_tasks.networkFile;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the original request
        okhttp3.Request request = chain.request();

        // Get the response from the request
        Response response = chain.proceed(request);

        // Get the raw response data
        ResponseBody responseBody = response.body();
        String rawResponse = responseBody.string();

        // Log the raw response data
        // You can use any logging mechanism you prefer, e.g., Logcat or a logging library
        Log.d("Raw Response", rawResponse);

        // Rebuild the response, as the response body can only be read once
        return response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(), rawResponse))
                .build();
    }
}

