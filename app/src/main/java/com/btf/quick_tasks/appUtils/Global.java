package com.btf.quick_tasks.appUtils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
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

    public  static String FormateDate(Date dt) {
        if (dt !=null ){
            return (new SimpleDateFormat(ShowDateFormate,AppDefaultLocal)).format(dt);
        }else {
            return "";
        }

    }

    public static void setSpinnerValue(ArrayAdapter<String> adapter, String value, Spinner spinner) {
        if (value != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }


}
