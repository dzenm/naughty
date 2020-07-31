package com.dzenm.naughty.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dzenm.naughty.Naughty;

/**
 * 记得在AndroidManifest添加广播
 * <service android:name="com.sd.fireelevs.core.floating.service.FloatingBroadcast" />
 */
public class NaughtyBroadcast extends BroadcastReceiver {

    /**
     * 为了Notification更新信息
     */
    public static final String ACTION_NOTIFICATION = "action_notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(ACTION_NOTIFICATION)) {
                Naughty.getInstance().startActivity(context);
            }
        }
    }
}
