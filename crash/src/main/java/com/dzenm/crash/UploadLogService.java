package com.dzenm.crash;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

public class UploadLogService extends IntentService {

    private static final String TAG = UploadLogService.class.getSimpleName();
    private static final String MESSAGE_DEVICE = "message_device";
    private static final String MESSAGE_EXCEPTION = "message_exception";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * Used to name the worker thread, important only for debugging.
     */
    public UploadLogService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String device = intent.getStringExtra(MESSAGE_DEVICE);
        String exception = intent.getStringExtra(MESSAGE_EXCEPTION);
        Log.d(TAG, "device=======" + device);
        Log.d(TAG, "exception=======" + exception);
        try {
            // 上传信息
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 接口上传完成后，结束当前service
        stopSelf();
    }

    public static void upload(Context context, Throwable e) {
        Intent intent = new Intent(context, UploadLogService.class);
        intent.putExtra(MESSAGE_DEVICE, Build.DEVICE);
        intent.putExtra(MESSAGE_EXCEPTION, e.getMessage());
        context.startService(intent);
    }
}
