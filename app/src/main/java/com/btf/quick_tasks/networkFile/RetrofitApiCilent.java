package com.btf.quick_tasks.networkFile;

import com.btf.quick_tasks.appUtils.Global;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiCilent {

    private static Retrofit retrofit = null;

    public static Retrofit getApiClient() {

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.MINUTES)
                    .readTimeout(20, TimeUnit.MINUTES)
                    .writeTimeout(20, TimeUnit.MINUTES)
                    .addInterceptor(new LoggingInterceptor()) // Add the logging interceptor
                    .build();

            retrofit = new Retrofit.Builder().baseUrl(Global.BASE_URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        return retrofit;
    }

    public static void reset() {
        retrofit = null;
    }

    public static String getNetworkError(Throwable t) {
        String ret;
        if (t instanceof IOException) {
            // Handle network-related errors
            ret = "Network error. Please check your internet connection.";

        } else if (t instanceof HttpException) {
            // Handle HTTP error based on the status code
            HttpException httpException = (HttpException) t;
            ret = "Http error: " + httpException.code() + " - " + httpException.message();

        } else if (t instanceof UnknownHostException) {
            // Handle hostname resolution errors
            ret = "Unable to resolve the server's hostname. Please check your internet connection or try again later.";

        } else if (t instanceof SSLHandshakeException) {
            // Handle SSL/TLS handshake errors
            ret = "SSL handshake error. Please check your internet connection or contact support.";

        } else if (t instanceof SocketTimeoutException) {
            // Handle socket timeout errors
            ret = "Request timeout. Please try after sometime.";

        } else if (t instanceof IOException) {
                // Handle network-related errors
                ret = "Network error. Please check your internet connection.";

        } else {
            // Handle other types of errors
            ret = "Unknown network error.";
        }

        return ret;
    }
}
