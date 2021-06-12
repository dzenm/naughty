package com.dzenm.naughty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.core.BaseNaughty;
import com.dzenm.log.LogHelper;
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.http.HttpInterceptor;
import com.dzenm.naughty.service.NaughtyBroadcast;
import com.dzenm.naughty.service.NaughtyService;
import com.dzenm.naughty.ui.log.LogAdapter;
import com.dzenm.naughty.util.SettingUtils;
import com.dzenm.naughty.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * @author dzenm
 * <p>
 * 第一, 请确保添加一下依赖库
 * implementation 'androidx.recyclerview:recyclerview:1.1.0'
 * implementation 'com.squareup.okhttp3:okhttp:3.14.0'
 * <p>
 * 第二, 在Okhttp中添加Interceptor
 * builder.addInterceptor(Naughty.newInstance().setDebug(true).get(this));
 */
public class Naughty extends BaseNaughty implements View.OnClickListener {

    private static final String TAG = Naughty.class.getSimpleName();

    /**
     * HTTP请求开始 {@link HttpInterceptor#intercept(Interceptor.Chain)}
     */
    public static final int START = 1;

    /**
     * HTTP正在请求中 {@link HttpInterceptor#intercept(Interceptor.Chain)}
     */
    public static final int RUNNING = 2;

    /**
     * HTTP请求结束 {@link HttpInterceptor#intercept(Interceptor.Chain)}
     */
    public static final int STOP = 3;

    @SuppressLint("StaticFieldLeak")
    private static volatile Naughty sInstance;

    /**
     * Naughty 后台服务
     */
    private NaughtyService mService;

    /**
     * Service是否已经创建
     */
    public boolean isCreatedService = false;

    /**
     * 悬浮窗配置参数
     */
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    /**
     * 悬浮窗的root View
     */
    private FrameLayout mDecorView;

    /**
     * 悬浮窗样式
     */
    private int mFloatingStyle;

    /**
     * 设置悬浮窗的大小
     */
    private int mFloatingWidth = -1, mFloatingHeight = -1;

    /**
     * 请求的数据
     */
    private final List<HttpBean> mData = new ArrayList<>();

    private IFloatingView mIFloatingView;
    private OnRequestListener mOnRequestListener;

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

    @Override
    public Interceptor get(Context context) {
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
     * 更新数据
     *
     * @param bean 更新的数据内容
     */
    public void update(HttpBean bean) {
        if (mOnRequestListener != null) {
            mOnRequestListener.onInterceptor(bean, indexOf(bean));
        }
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
     * 设置悬浮窗的宽度
     *
     * @param width 悬浮窗的宽度(默认为屏幕的3/8)
     */
    public void setWidth(int width) {
        this.mFloatingWidth = width;
    }

    /**
     * 设置悬浮窗的高度
     *
     * @param height 悬浮窗的高度(默认为宽度的3/4)
     */
    public void setHeight(int height) {
        this.mFloatingHeight = height;
    }

    public void setOnRequestListener(OnRequestListener listener) {
        mOnRequestListener = listener;
    }

    public void setIFloatingView(IFloatingView floatingView) {
        this.mIFloatingView = floatingView;
    }

    /**
     * 清空数据
     */
    public void clear() {
        mData.clear();
    }

    /**
     * Http是否请求结束
     *
     * @param status 请求的状态码
     * @return 是否结束Http请求
     */
    public boolean isHttpFinished(int status) {
        return status == STOP;
    }

    /**
     * 如果不在悬浮窗点击进入的Activity里, 将会改变悬浮窗的状态
     *
     * @param visible 悬浮窗是否可见
     */
    public void isVisible(boolean visible) {
        if (!isShowFloatingView()) return;
        Log.d(TAG, "naughty floating view is visible: " + visible);
        if (visible) {
            int index = SettingUtils.getFloatingStyle(mService);
            if (index != mFloatingStyle) {
                mFloatingStyle = index;
                recreateView(mService);
            }
            mWindowManager.addView(mDecorView, mLayoutParams);
        } else {
            mWindowManager.removeView(mDecorView);
        }
    }

    private boolean isShowFloatingView() {
        return isCreatedService
                && SettingUtils.isEnabledFloating(mService)
                && mWindowManager != null
                && mDecorView != null;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(NaughtyService service) {
        this.mService = service;
        isCreatedService = true;

        mWindowManager = (WindowManager) service.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = ViewUtils.createFloatingLogModelViewParams();

        mDecorView = new FrameLayout(mService);
        mDecorView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        mFloatingStyle = SettingUtils.getFloatingStyle(mService);
        recreateView(service);
        mDecorView.setOnTouchListener(new FloatingTouchListener());
    }

    private void recreateView(NaughtyService service) {
        mDecorView.removeAllViews();
        if (mIFloatingView == null) {
            if (mFloatingStyle == 0) {
                mDecorView.addView(ViewUtils.createFloatingView(service));
                mDecorView.setOnClickListener(this);
            } else if (mFloatingStyle == 1) {
                if (mFloatingWidth == -1) {
                    mFloatingWidth = ViewUtils.getWidth() * 3 / 8;
                }
                if (mFloatingHeight == -1) {
                    mFloatingHeight = mFloatingWidth * 4 / 3;
                }

                final LogAdapter adapter = new LogAdapter();
                mDecorView.addView(ViewUtils.createFloatingLogModel(service, adapter, mFloatingWidth, mFloatingHeight));

                final LinearLayout parent = (LinearLayout) mDecorView.getChildAt(0);
                final LinearLayout titleLayout = (LinearLayout) parent.getChildAt(0);
                final RecyclerView recyclerView = (RecyclerView) parent.getChildAt(1);
                recyclerView.setVerticalScrollBarEnabled(true);
                final ImageView network = (ImageView) titleLayout.getChildAt(1);

                LogHelper.getInstance().start(new LogHelper.OnChangeListener() {
                    @Override
                    public void onChanged(String log) {
                        if (mDecorView != null) {
                            mDecorView.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    int size = adapter.getItemCount();
                                    recyclerView.smoothScrollToPosition(size);
                                }
                            });
                        }
                    }
                });
                adapter.setData(LogHelper.getInstance().getData());
                network.setOnClickListener(this);
            }
        } else {
            mDecorView.addView(mIFloatingView.create(mDecorView));
        }
    }

    @Override
    public void onClick(View v) {
        NaughtyBroadcast.startActivity(mService);
    }

    public void onDestroy() {
        isCreatedService = false;
        mService = null;
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

    public interface OnRequestListener {

        void onInterceptor(HttpBean bean, int position);
    }

    public interface IFloatingView {

        View create(ViewGroup parent);
    }
}
