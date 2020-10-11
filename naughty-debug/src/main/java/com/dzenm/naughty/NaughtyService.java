package com.dzenm.naughty;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * 记得在AndroidManifest添加服务
 * <service android:name="com.sd.fireelevs.core.floating.service.FloatingService" />
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
        Naughty.getInstance().dismiss();
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
