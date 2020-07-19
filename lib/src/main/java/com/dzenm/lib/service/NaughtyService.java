package com.dzenm.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dzenm.lib.Naughty;

/**
 * 记得在AndroidManifest添加服务
 * <service android:name="com.sd.fireelevs.core.floating.service.FloatingService" />
 */
public class NaughtyService extends Service {

    private static final String TAG = NaughtyService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        Naughty.isCreated = true;

        Naughty.getInstance().onCreate(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        Naughty.getInstance().dismiss();
        Log.d(TAG, "stopService");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        // 自杀服务
        stopSelf();
        super.onDestroy();
        Naughty.isCreated = false;
        Log.d(TAG, "onDestroy");
        Naughty.getInstance().onDestroy();
    }
}
