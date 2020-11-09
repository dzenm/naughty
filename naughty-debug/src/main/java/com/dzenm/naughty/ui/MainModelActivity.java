package com.dzenm.naughty.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.NaughtyService;
import com.dzenm.naughty.R;
import com.dzenm.naughty.ui.http.ListFragment;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

public class MainModelActivity extends AppCompatActivity {

    private static final String TAG = MainModelActivity.class.getSimpleName();
    private static final int REQUEST_FLOATING = 0xF1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createView());
        Log.d(TAG, "onCreate task id: " + getTaskId());

        Window window = getWindow();
        // 添加状态栏背景可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 清除原有的状态栏半透明状态
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ViewUtils.resolveColor(this, R.attr.colorPrimary));

        checkServiceWithEnabled(this);

        Utils.clearStack(getSupportFragmentManager());
        ListFragment fragment = ListFragment.newInstance();
        Utils.switchFragment(getSupportFragmentManager(), null, fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Naughty.getInstance().onChanged(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Naughty.getInstance().onChanged(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy task id: " + getTaskId());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        onBackKeyboard(false);
    }

    /**
     * 权限请求回调结果, 如果已经授予权限, 开启后台服务, 如果未授权, 将会继续提示授权
     *
     * @param requestCode 请求的回调标识
     * @param resultCode  结果的状态
     * @param data        返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FLOATING && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Utils.checkOverlaysPermission(this)) {
                startService(new Intent(this, NaughtyService.class));
            } else {
                checkServiceWithEnabled(this);
            }
        }
    }

    private View createView() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        frameLayout.setId(R.id.frame_layout_id);
        return frameLayout;
    }

    /**
     * 返回到上一个 Activity 或 Fragment
     *
     * @param isFinishedCurrentActivity 是否结束当前 Activity
     */
    public void onBackKeyboard(boolean isFinishedCurrentActivity) {
        if (isFinishedCurrentActivity) {
            Naughty.getInstance().clear();
            super.finish();
        } else {
            final FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 1) {
                manager.popBackStackImmediate();
            } else {
                moveTaskToBack(true);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
    }

    /**
     * 启动悬浮窗, 启动之前会检测是否开启服务, 是否获取悬浮窗权限, 如果未开启, 则会提示授权
     *
     * @param activity 上下文
     */
    private void checkServiceWithEnabled(final AppCompatActivity activity) {
        if (Naughty.getInstance().isCreated || Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        if (Utils.checkOverlaysPermission(activity)) {
            startService(new Intent(activity, NaughtyService.class));
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.dialog_request_permission_failed_title))
                    .setMessage(activity.getString(R.string.dialog_request_permission_failed_message))
                    .setPositiveButton(activity.getString(R.string.dialog_request_permission_button_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivityForResult(intent, REQUEST_FLOATING);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(activity.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }
}
