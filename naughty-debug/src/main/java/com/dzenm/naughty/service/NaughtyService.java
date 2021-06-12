package com.dzenm.naughty.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dzenm.naughty.Naughty;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 记得在AndroidManifest添加服务
 * <service android:name=".service.NaughtyService" />
 */
public class NaughtyService extends Service {

    private static final String TAG = NaughtyService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Naughty Service is Create");
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
        Naughty.getInstance().isVisible(false);
        Log.d(TAG, "stopService");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Naughty Service is Destroy");
        Naughty.getInstance().onDestroy();
    }
}
