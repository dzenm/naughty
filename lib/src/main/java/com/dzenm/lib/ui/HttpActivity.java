package com.dzenm.lib.ui;

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

import com.dzenm.lib.Naughty;
import com.dzenm.lib.R;
import com.dzenm.lib.service.NaughtyService;
import com.dzenm.lib.util.Utils;

public class HttpActivity extends AppCompatActivity {

    private static final String TAG = HttpActivity.class.getSimpleName();
    private static final int REQUEST_FLOATING = 0xF1;
    static final String FLOATING_BEAN = "floating_bean";
    ListFragment mFragment;
    int mFrameLayoutId;

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
        window.setStatusBarColor(Utils.resolveColor(this, R.attr.colorPrimary));

        checkServiceWithEnabled(this);

        Utils.clearStack(getSupportFragmentManager());
        mFragment = ListFragment.newInstance();
        Utils.switchFragment(mFrameLayoutId, getSupportFragmentManager(), null, mFragment);
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
        back(false);
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
        mFrameLayoutId = View.generateViewId();
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        frameLayout.setId(mFrameLayoutId);
        return frameLayout;
    }

    void back(boolean isFinished) {
        if (isFinished) {
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
    void checkServiceWithEnabled(final AppCompatActivity activity) {
        if (Naughty.isCreated || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        if (Utils.checkOverlaysPermission(activity)) {
            startService(new Intent(activity, NaughtyService.class));
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("授权失败")
                    .setMessage("未授予悬浮窗权限, 如需继续使用悬浮窗功能, 请前往授权")
                    .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivityForResult(intent, REQUEST_FLOATING);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
