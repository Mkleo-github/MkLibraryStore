package com.mkleo.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class ActivityRouter {

    /**
     * 路由跳转
     *
     * @param context      上下文
     * @param activityPath 要跳转的类的全包名.类名
     * @param bundle       要传递的参数
     */
    public static void jump(Context context, String activityPath, Bundle bundle) {
        if (TextUtils.isEmpty(activityPath))
            return;
        try {
            Intent intent = new Intent(context, Class.forName(activityPath));
            if (bundle != null)
                intent.putExtras(bundle);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
