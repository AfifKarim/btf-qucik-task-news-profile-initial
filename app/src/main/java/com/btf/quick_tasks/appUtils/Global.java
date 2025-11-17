package com.btf.quick_tasks.appUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Global {

    public static final String api_Key = "c88fe2f3285a469687290fc3dcc22622";
    public static String BASE_URL = "https://newsapi.org/v2/";
    public static String ShowDateFormate = "yyyy-MM-dd";
    public static Locale AppDefaultLocal = Locale.ENGLISH;

    public static void showDialog(Context context, int drawable, String title, String message){
        new android.app.AlertDialog.Builder(context)
                .setIcon(drawable)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    public static String getCurrentMonth() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateYYMMDD() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    public static String getCurrentDateWithTime() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(currentDate);
    }

    public  static String FormateDate(Date dt) {
        if (dt !=null ){
            return (new SimpleDateFormat(ShowDateFormate,AppDefaultLocal)).format(dt);
        }else {
            return "";
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static long parseToMillis(String dateTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
        sdf.setLenient(false);
        Date d = sdf.parse(dateTime);
        return d == null ? -1 : d.getTime();
    }

    public static String formatMillis(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN, Locale.ENGLISH);
        return sdf.format(new Date(millis));
    }

}
