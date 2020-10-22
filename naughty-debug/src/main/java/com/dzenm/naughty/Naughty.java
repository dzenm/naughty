package com.dzenm.naughty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.dzenm.naughty.http.HttpInterceptor;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.http.HttpBean;
import com.dzenm.naughty.ui.log.LogItemAdapter;
import com.dzenm.naughty.util.Utils;
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
public class Naughty extends BaseNaughty {

    private static final String TAG = Naughty.class.getSimpleName();

    public static final int START = 1;
    public static final int RUNNING = 2;
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
    public boolean isCreated = false;

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
     * 设置悬浮窗的大小
     */
    private int mWidth = -1, mHeight = -1;

    /**
     * 请求的数据
     */
    private List<HttpBean> mData = new ArrayList<>();

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
        this.mWidth = width;
    }

    /**
     * 设置悬浮窗的高度
     *
     * @param height 悬浮窗的高度(默认为宽度的3/4)
     */
    public void setHeight(int height) {
        this.mHeight = height;
    }

    /**
     * 清空数据
     */
    public void clear() {
        mData.clear();
    }

    @Override
    public void show() {
        if (isCreated) {
            if (!isShowing && mWindowManager != null && mDecorView != null) {
                isShowing = true;
                mWindowManager.addView(mDecorView, mLayoutParams);
            }
        }
    }

    @Override
    public void dismiss() {
        if (isCreated) {
            if (isShowing && mWindowManager != null && mDecorView != null) {
                isShowing = false;
                mWindowManager.removeView(mDecorView);
            }
        }
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
     * @param isShowing 悬浮窗是否显示
     */
    public void onChanged(boolean isShowing) {
        if (isShowing) {
            show();
        } else {
            dismiss();
        }
    }

    public OnRequestListener getOnRequestListener() {
        return mOnRequestListener;
    }

    public void setOnRequestListener(OnRequestListener listener) {
        this.mOnRequestListener = listener;
    }

    public void setIFloatingView(IFloatingView floatingView) {
        this.mIFloatingView = floatingView;
    }

    /**
     * 启动FloatingActivity
     *
     * @param context 上下文
     */
    public void startActivity(Context context) {
        Intent intent = new Intent(context, MainModelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(final NaughtyService service) {
        this.mService = service;
        isCreated = true;

        if (Utils.checkOverlaysPermission(service)) {
            mWindowManager = (WindowManager) service.getSystemService(Context.WINDOW_SERVICE);
            mLayoutParams = ViewUtils.createFloatingViewParams();

            mDecorView = new FrameLayout(mService);
            mDecorView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            ));

            if (mWidth == -1) {
                mWidth = ViewUtils.getWidth() * 3 / 8;
            }
            if (mHeight == -1) {
                mHeight = mWidth * 4 / 3;
            }

            if (mIFloatingView == null) {
//                mDecorView.addView(ViewUtils.createFloatingView(service));
                final LogItemAdapter adapter = new LogItemAdapter();
                mDecorView.addView(ViewUtils.createFloatingLogModel(service, adapter, mWidth, mHeight));

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

                network.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(mService);
                    }
                });
            } else {
                mDecorView.addView(mIFloatingView.create(mDecorView));
            }
            mDecorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mDecorView.setOnTouchListener(new FloatingTouchListener());
        }
    }

    public void onDestroy() {
        isCreated = false;
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

    public interface OnRequestListener {

        void onInterceptor(HttpBean bean, int position);
    }

    public interface IFloatingView {

        View create(ViewGroup parent);
    }
}
