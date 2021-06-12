package com.dzenm.naughty.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dzenm.naughty.ui.MainActivity;

/**
 * author：dzenm on 3/9/2021 14:05
 * 记得在AndroidManifest添加广播
 */
public class NaughtyBroadcast extends BroadcastReceiver {

    private static final String TAG = NaughtyBroadcast.class.getSimpleName();

    /**
     * 为了Notification更新信息
     */
    public static final String NAUGHTY_ACTION_NOTIFICATION = "naughty_action_notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(NAUGHTY_ACTION_NOTIFICATION)) {
                Log.d(TAG, "receive naughty broadcast....");
                startActivity(context);
            }
        }
    }

    /**
     * 启动FloatingActivity
     *
     * @param context 上下文
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
