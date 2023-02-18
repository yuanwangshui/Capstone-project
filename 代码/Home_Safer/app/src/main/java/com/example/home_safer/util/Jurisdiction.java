package com.example.home_safer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Jurisdiction {
    StringToast stringtoast;//对象
    //判断网络状态
    private boolean isConnectIsNomarl(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            //stringToast("network,当前网络:" + name);
            return true;
        } else {
            stringtoast.stringToast(context,"network,没有可用网络:");
            return false;
        }
    }
}
