package com.example.home_safer.util;

import android.content.Context;
import android.widget.Toast;

public class StringToast {
    //toast工具类
    public void stringToast(Context context, String field) {
        Toast.makeText(context, field, Toast.LENGTH_SHORT).show();
    }
}
