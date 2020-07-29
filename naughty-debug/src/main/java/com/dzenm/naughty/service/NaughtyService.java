package com.dzenm.naughty.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dzenm.naughty.NaughtyDelegate;

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

        NaughtyDelegate.isCreated = true;

        NaughtyDelegate.getInstance().onCreate(this);
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
        NaughtyDelegate.getInstance().dismiss();
        Log.d(TAG, "stopService");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        // 自杀服务
        stopSelf();
        super.onDestroy();
        NaughtyDelegate.isCreated = false;
        Log.d(TAG, "onDestroy");
        NaughtyDelegate.getInstance().onDestroy();
    }
}
