package com.dzenm.crash;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * 记得在AndroidManifest添加服务
 * <service android:name="com.sd.fireelevs.core.crash.RestartService" />
 * <pre>
 * RestartService.restart(mActivity);
 * </pre>
 */
public class RestartService extends Service {

    private static final String INTENT_PACKAGE_NAME = "package_name";
    private static final String TAG = RestartService.class.getSimpleName();

    private Handler mHandler;

    public RestartService() {
        Log.d(TAG, "初始化重启服务");
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "重启APP...");
        restart(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void restart(Intent intent) {
        final String packageName = intent.getStringExtra(INTENT_PACKAGE_NAME);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                startActivity(launchIntent);
                Log.d(TAG, "结束重启服务");
                stopSelf();
            }
        }, 0);
    }

    /**
     * 通过服务重启APP
     *
     * @param context 上下文
     */
    public static void restart(Context context) {
        Log.d(TAG, "开始重启服务");
        // 开启一个新的服务，用来重启本APP
        Intent intent = new Intent(context, RestartService.class);
        intent.putExtra(INTENT_PACKAGE_NAME, context.getPackageName());
        context.startService(intent);
        // 杀死整个进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
