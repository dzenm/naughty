package com.dzenm.naughty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dzenm.naughty.http.HttpInterceptor;
import com.dzenm.naughty.service.NaughtyService;
import com.dzenm.naughty.ui.HttpActivity;
import com.dzenm.naughty.ui.HttpBean;
import com.dzenm.naughty.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一, 请确保添加一下依赖库
 * implementation 'androidx.recyclerview:recyclerview:1.1.0'
 * implementation 'com.squareup.okhttp3:okhttp:3.14.0'
 * <p>
 * 第二, 在Okhttp中添加Interceptor
 * Naughty.get(this)
 */
public class Naughty {

    private static final String TAG = Naughty.class.getSimpleName();

    public static final int START = 1;
    public static final int RUNNING = 2;
    public static final int STOP = 3;

    @SuppressLint("StaticFieldLeak")
    private static volatile Naughty sInstance;
    private NaughtyService mService;

    /**
     * Service是否已经创建
     */
    public static boolean isCreated = false;

    /**
     * 是否显示悬浮窗
     */
    private boolean isShowing = false;

    /**
     * 是否是debug模式
     */
    private boolean isDebug = false;

    /**
     * 是否显示通知提示
     */
    private boolean isShowNotification = true;

    /**
     * 悬浮窗配置参数
     */
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    /**
     * 悬浮窗
     */
    private FrameLayout mDecorView;

    /**
     * 请求的数据
     */
    private List<HttpBean> mData = new ArrayList<>();

    private OnRequestListener mOnRequestListener;
    private IFloatingView mIFloatingView;

    public static Naughty getInstance() {
        if (sInstance == null) {
            synchronized (Naughty.class) {
                if (sInstance == null) {
                    sInstance = new Naughty();
                }
            }
        }
        return sInstance;
    }

    public Naughty setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public HttpInterceptor get(Context context) {
        return new HttpInterceptor(context);
    }

    /**
     * 获取HTTP请求的数据
     *
     * @return HTTP请求的数据
     */
    public List<HttpBean> get() {
        return mData;
    }

    /**
     * 添加HTTP请求的数据
     *
     * @param bean HTTP请求的数据
     */
    public void add(HttpBean bean) {
        mData.add(0, bean);
    }

    /**
     * 获取数据所在的位置
     *
     * @param bean 查找需要的数据
     * @return 数据所在的位置
     */
    public int indexOf(HttpBean bean) {
        return mData.indexOf(bean);
    }

    /**
     * 清空数据
     */
    public void clear() {
        mData.clear();
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        if (isCreated) {
            if (!isShowing && mWindowManager != null && mDecorView != null) {
                isShowing = true;
                mWindowManager.addView(mDecorView, mLayoutParams);
            }
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void dismiss() {
        if (isCreated) {
            if (isShowing && mWindowManager != null && mDecorView != null) {
                isShowing = false;
                mWindowManager.removeView(mDecorView);
            }
        }
    }

    public boolean isHttpFinished(int status) {
        return status == STOP;
    }

    /**
     * 如果不在悬浮窗点击进入的Activity里, 将会改变悬浮窗的状态
     *
     * @param isShowing 悬浮窗是否显示
     */
    public void onChanged(boolean isShowing) {
        if (isShowing) {
            show();
        } else {
            dismiss();
        }
    }

    /**
     * @see #isShowNotification
     */
    public boolean isShowNotification() {
        return isShowNotification;
    }

    /**
     * @see #isShowNotification()
     */
    public void setShowNotification(boolean showNotification) {
        isShowNotification = showNotification;
    }

    public OnRequestListener getOnRequestListener() {
        return mOnRequestListener;
    }

    public void setOnRequestListener(OnRequestListener listener) {
        this.mOnRequestListener = listener;
    }

    public void setIFloatingView(IFloatingView mIFloatingView) {
        this.mIFloatingView = mIFloatingView;
    }

    /**
     * 启动FloatingActivity
     *
     * @param context 上下文
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, HttpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(NaughtyService service) {
        this.mService = service;

        if (Utils.checkOverlaysPermission(service)) {
            mWindowManager = (WindowManager) service.getSystemService(Context.WINDOW_SERVICE);
            mLayoutParams = createFloatingViewParams();

            mDecorView = new FrameLayout(mService);
            mDecorView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            ));
            if (mIFloatingView == null) {
                mDecorView.addView(createFloatingView(service));
            } else {
                mDecorView.addView(mIFloatingView.create(mDecorView));
            }
            mDecorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(mService);
                }
            });
            mDecorView.setOnTouchListener(new FloatingTouchListener());
        }
    }

    public void onDestroy() {
        mService = null;
        isShowing = false;
        mWindowManager = null;
        mLayoutParams = null;
        mDecorView = null;
    }

    /**
     * Floating View 进行拖移时随着移动的位置进行改变
     */
    private class FloatingTouchListener implements View.OnTouchListener {

        // 开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
        private int mTouchStartX, mTouchStartY;
        // 开始时的坐标和结束时的坐标（相对于自身控件的坐标）
        private int mStartX, mStartY;
        // 判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
        private boolean isMove;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int touchCurrentX = (int) event.getRawX();
                    int touchCurrentY = (int) event.getRawY();
                    mLayoutParams.x -= touchCurrentX - mTouchStartX;
                    mLayoutParams.y += touchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mDecorView, mLayoutParams);

                    mTouchStartX = touchCurrentX;
                    mTouchStartY = touchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    int stopX = (int) event.getX();
                    int stopY = (int) event.getY();
                    if (Math.abs(mStartX - stopX) >= 1 || Math.abs(mStartY - stopY) >= 1) {
                        isMove = true;
                    }
                    break;
                default:
                    break;
            }
            // 如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove;
        }
    }

    /**
     * 创建Floating View配置信息
     *
     * @return Floating View配置信息
     */
    private WindowManager.LayoutParams createFloatingViewParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 200;
        layoutParams.height = 200;
        layoutParams.x = 0;
        layoutParams.y = 0;
        return layoutParams;
    }

    /**
     * 创建Floating View
     *
     * @param context 上下文
     * @return Floating View
     */
    private TextView createFloatingView(Context context) {
        TextView textView = new TextView(context);
        textView.setText("网络调试");
        textView.setBackgroundColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.ic_home_circle);
        return textView;
    }

    public interface OnRequestListener {

        void onInterceptor(HttpBean bean, int position);
    }

    public interface IFloatingView {

        View create(ViewGroup parent);
    }
}
