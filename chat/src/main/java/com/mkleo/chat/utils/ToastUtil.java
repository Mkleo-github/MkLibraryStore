package com.mkleo.chat.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    public static void show(Context context, String val) {
        show(context, val, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String val, int length) {
        Toast.makeText(context, val, length).show();
    }
}
