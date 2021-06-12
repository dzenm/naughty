package com.dzenm.naughty.ui;

import android.content.Context;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.R;
import com.dzenm.naughty.service.NaughtyService;
import com.dzenm.naughty.ui.http.HttpFragment;
import com.dzenm.naughty.util.SettingUtils;
import com.dzenm.naughty.util.ViewUtils;

/**
 * @author dzenm
 * 2020/8/4
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int mFrameLayoutId;

    /**
     * 悬浮窗权限请求回调状态码
     */
    private static final int REQUEST_FLOATING = 0xF1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrameLayoutId = View.generateViewId();
        setContentView(ViewUtils.createDecorView(this, mFrameLayoutId));
        Log.d(TAG, "onCreate task id: " + getTaskId());

        //getDelegate().setLocalNightMode(loadThemeMode(SettingUtils.getThemeMode(this)));

        setStatusBarStyle();

        checkServiceWithEnabled(this);

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(HttpFragment.class.getName());
        clearStack();

        switchFragment(null, savedInstanceState == null || fragment == null
                        ? HttpFragment.newInstance(Naughty.getInstance().get())
                        : fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Naughty.getInstance().isVisible(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Naughty.getInstance().isVisible(true);
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
     * 设置状态栏样式
     */
    private void setStatusBarStyle() {
        Window window = getWindow();
        // 添加状态栏背景可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 清除原有的状态栏半透明状态
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ViewUtils.resolveColor(this, R.attr.colorPrimary));
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
            if (checkOverlaysPermission(this)) {
                startService(new Intent(this, NaughtyService.class));
            } else {
                checkServiceWithEnabled(this);
            }
        }
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
            int size = manager.getBackStackEntryCount();
            if (size > 1) {
                manager.popBackStack();
            } else {
                moveTaskToBack(true);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
    }

    public int loadThemeMode(String value) {
        int mode = 0;
        String[] values = getResources().getStringArray(R.array.theme_mode);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) mode = i;
        }
        if (mode == 0) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        } else if (mode == 1) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        } else return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    /**
     * 启动悬浮窗, 启动之前会检测是否开启服务, 是否获取悬浮窗权限, 如果未开启, 则会提示授权
     *
     * @param activity 上下文
     */
    private void checkServiceWithEnabled(final AppCompatActivity activity) {
        if (Naughty.getInstance().isCreatedService
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || !SettingUtils.isEnabledFloating(activity))
            return;

        if (checkOverlaysPermission(activity)) {
            startService(new Intent(activity, NaughtyService.class));
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.dialog_request_permission_failed_title))
                    .setMessage(activity.getString(R.string.dialog_request_permission_failed_message))
                    .setPositiveButton(activity.getString(R.string.dialog_request_permission_button_confirm), new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
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

    /**
     * 切换Fragment
     *
     * @param current 当前所在的Fragment, 即需要隐藏的Fragment
     * @param target  目标Fragment, 即需要切换的Fragment
     */
    public void switchFragment(Fragment current, Fragment target) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction action = manager.beginTransaction();
        String className = target.getClass().getName();
        Fragment alreadyFragment = manager.findFragmentByTag(className);
        if (alreadyFragment != null) {
            action.remove(alreadyFragment);
        }
        action.add(mFrameLayoutId, target, className)
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out
                ).addToBackStack(className);
        if (current != null) {
            action.hide(current);
        }
        action.show(target).commit();
    }

    /**
     * 清空Fragment栈
     */
    public void clearStack() {
        FragmentManager manager = getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            manager.popBackStack();
        }
    }

    /**
     * 检查悬浮窗开启权限
     *
     * @param context 上下文
     * @return 是否开启悬浮窗权限
     */
    public static boolean checkOverlaysPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }
}
