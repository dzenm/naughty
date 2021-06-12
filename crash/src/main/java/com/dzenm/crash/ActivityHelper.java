package com.dzenm.crash;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Stack;

/**
 * @author dinzhenyan
 * @date 2019-06-11 12:25
 * Activity栈管理工具类
 * <p>
 * Activity onCreate()
 * ActivityHelper.getInstance().push(this);
 * <p>
 * Activity onDestroy()
 * ActivityHelper.getInstance().pop(this);
 * </p>
 */
public class ActivityHelper {

    private static final String TAG = ActivityHelper.class.getSimpleName();
    private static volatile ActivityHelper sActivityHelper;
    private static Stack<Activity> sActivityStack;

    private ActivityHelper() {
        sActivityStack = new Stack<>();
    }

    /**
     * 单例
     */
    public static ActivityHelper getInstance() {
        if (sActivityHelper == null) {
            synchronized (ActivityHelper.class) {
                if (sActivityHelper == null) {
                    sActivityHelper = new ActivityHelper();
                }
            }
        }
        return sActivityHelper;
    }

    /**
     * 判断Activity是否存活
     *
     * @param clazz 判断的Activity的class
     * @return 是否存活
     */
    public boolean isAlive(Class<?> clazz) {
        if (isEmpty(sActivityStack)) return false;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Log.d(TAG, clazz.getSimpleName() + " isAlive");
                return true;
            }
        }
        Log.d(TAG, clazz.getSimpleName() + " not isAlive");
        return false;
    }

    /**
     * 添加指定的Activity
     */
    public void push(Activity activity) {
        if (sActivityStack.add(activity)) {
            Log.d(TAG, "add activity: " + className(activity)
                    + ", activity stack's size is " + size());
        } else {
            Log.e(TAG, "添加Activity失败");
        }
    }

    /**
     * 移除指定的Activity
     */
    public void pop(Activity activity) {
        if (!isEmpty(sActivityStack)) {
            if (sActivityStack.remove(activity)) {
                Log.d(TAG, "remove activity: " + className(activity)
                        + ", activity stack's size is " + size());
            } else {
                Log.e(TAG, "移除失败" + activity);
            }
        } else {
            Log.e(TAG, "栈内Activity为空");
        }
    }

    /**
     * 获取当前显示Activity（堆栈中最后一个传入的activity）
     */
    public Activity peek() {
        if (isEmpty(sActivityStack)) return null;
        Log.d(TAG, "get top activity: " + className(sActivityStack.lastElement())
                + ", activity stack's size is " + size());
        return sActivityStack.lastElement();
    }

    /**
     * 获取指定的Activity
     */
    public Activity get(Class<?> clazz) {
        if (isEmpty(sActivityStack)) return null;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Log.d(TAG, "get activity: " + clazz.getSimpleName()
                        + ", activity stack's size is " + size());
                return activity;
            }
        }
        return null;
    }

    /**
     * 获取所有Activity
     */
    public Stack<Activity> get() {
        Log.d(TAG, "activity stack's size is " + size());
        return sActivityStack;
    }

    /**
     * 结束指定的Activity
     */
    public void finish(Activity activity) {
        if (isEmpty(sActivityStack) || activity.isFinishing()) return;
        activity.finish();
        Log.d(TAG, "finish activity: " + className(activity)
                + ", activity stack's size is " + size());
    }

    /**
     * 结束指定类名的Activity
     */
    public void finish(Class<?> clazz) {
        if (isEmpty(sActivityStack)) return;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Log.d(TAG, "finish activity: " + className(activity)
                        + ", activity stack's size is " + size());
                finish(activity);
                break;
            }
        }
    }

    /**
     * 结束除当前传入以外所有Activity
     */
    public void finishOthers(Class<?> clazz) {
        if (isEmpty(sActivityStack)) return;
        for (Activity activity : sActivityStack) {
            if (!activity.getClass().equals(clazz)) activity.finish();
        }
        Log.d(TAG, "finish others activity except "
                + clazz.getSimpleName() + ", activity stack's size is " + size());
    }

    /**
     * 结束所有Activity
     */
    public void finish() {
        if (isEmpty(sActivityStack)) return;
        for (Activity activity : sActivityStack) {
            activity.finish();
        }
        sActivityStack.clear();
        Log.d(TAG, "finish all activity, activity stack's size is " + size());
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        try {
            finish();
            Log.d(TAG, "exit app");
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity创建的数量
     *
     * @return Activity在栈内的数量
     */
    private int size() {
        return sActivityStack.size();
    }

    /**
     * 获取一个实例的类名
     *
     * @param clazz 类的实例
     * @return 类的名称
     */
    static String className(Context clazz) {
        return clazz.getClass().getSimpleName();
    }

    /**
     * 判断栈是否为空
     *
     * @param stack 任务栈
     * @return 是否为空
     */
    static boolean isEmpty(Stack<?> stack) {
        if (stack == null)
            throw new NullPointerException("stack is null, please initialize before use");
        return stack.isEmpty();
    }
}
