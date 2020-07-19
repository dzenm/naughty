package com.dzenm.lib.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dzenm.lib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    /**
     * 切换Fragment
     *
     * @param manager FragmentManager
     * @param current 当前所在的Fragment, 即需要隐藏的Fragment
     * @param target  目标Fragment, 即需要切换的Fragment
     */
    public static void switchFragment(int id, FragmentManager manager, Fragment current, Fragment target) {
        FragmentTransaction action = manager.beginTransaction();
        String className = target.getClass().getSimpleName();
        Fragment alreadyFragment = manager.findFragmentByTag(className);
        if (alreadyFragment != null) {
            action.remove(alreadyFragment);
        }
        action.add(id, target, className)
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out
                ).addToBackStack(className);
        if (current != null) {
            action.hide(current);
        }
        action.show(target).commitAllowingStateLoss();
    }

    /**
     * 清空Fragment栈
     *
     * @param manager FragmentManager
     */
    public static void clearStack(FragmentManager manager) {
        int count = manager.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            manager.popBackStack();
        }
    }

    public static int primaryTextColor() {
        return Color.parseColor("#212121");
    }

    public static int secondaryTextColor() {
        return Color.parseColor("#757575");
    }

    public static int resolveColor(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static Drawable resolveDrawable(@NonNull Context context, @AttrRes int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = a.getDrawable(0);
            } else {
                int id = a.getResourceId(0, -1);
                if (id == -1) {
                    drawable = AppCompatResources.getDrawable(context, id);
                }
            }
            return drawable;
        } finally {
            a.recycle();
        }
    }

    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    public static Toolbar getToolbar(LayoutInflater inflater, ViewGroup parent) {
        return (Toolbar) inflater.inflate(R.layout.toolbar, parent, false);
    }

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 复制纯文本
     *
     * @param context 获取系统服务的上下文
     * @param text    复制的文本
     */
    public static void copy(Context context, CharSequence text) {
        // 获取剪切板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符clipData
        ClipData clipData = ClipData.newPlainText("text/plain", text);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    /**
     * 格式化Json字符串
     *
     * @param json      需要格式化的字符串
     * @param isRetouch 是否添加四周装饰的字符
     * @return 格式化好的字符串
     */
    public static String formatJson(String json, boolean isRetouch) {
        StringBuilder sb = new StringBuilder();
        String message;
        // 格式化json字符串
        try {
            if (json.startsWith("{")) {
                // 最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
                message = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                message = new JSONArray(json).toString(4);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }

        // 添加换行并输出字符串
        if (isRetouch) sb.append("╔═════════════");
        String[] lines = message.split("\n");
        for (String line : lines) {
            line = line.replace("\\", "");
            if (isRetouch) {
                sb.append("\n║").append(line);
            } else {
                sb.append(line).append("\n");
            }
        }
        if (isRetouch) sb.append("\n╚═════════════");
        return sb.toString();
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
