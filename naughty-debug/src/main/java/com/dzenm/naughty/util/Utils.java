package com.dzenm.naughty.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dzenm.naughty.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * 切换Fragment
     *
     * @param manager FragmentManager
     * @param current 当前所在的Fragment, 即需要隐藏的Fragment
     * @param target  目标Fragment, 即需要切换的Fragment
     */
    public static void switchFragment(FragmentManager manager, Fragment current, Fragment target) {
        FragmentTransaction action = manager.beginTransaction();
        String className = target.getClass().getSimpleName();
        Fragment alreadyFragment = manager.findFragmentByTag(className);
        if (alreadyFragment != null) {
            action.remove(alreadyFragment);
        }
        action.add(R.id.frame_layout_id, target, className)
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
        ClipData clipData = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, text);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    /**
     * 格式化Json字符串展示
     *
     * @param json 需要格式化的字符串
     * @return 格式化好的字符串
     */
    public static String formatJson(String json) {
        String message;
        // 格式化json字符串
        try {
            // 最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            if (json.startsWith("{")) {
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
        String[] lines = message.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            line = line.replace("\\", "");
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 根据文件大小格式化为B, KB, MB, GB
     *
     * @param size 文件大小
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        String[] suffixes = new String[]{" B", " KB", " MB", " GB", " TB", "PB"};
        int index = 0;
        // 因为要保存小数点，所以需大于102400以便用于后面进行小数分解的运算，index是判断以哪一个单位结尾
        while (size >= 102400) {
            size /= 1024;
            index++;
        }

        long integer = size / 100;
        long decimal = size % 100;
        boolean isNeedDecimal = integer == 0 && decimal == 0;

        return integer + (isNeedDecimal ? "" : "." + decimal) + suffixes[index];
    }

    /**
     * 读取文件内容
     *
     * @param file 读取的文件
     * @return 文件的文本内容
     */
    public static String readFileText(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            // 构造一个BufferedReader类来读取文件
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取文件夹下的所有文件
     *
     * @param path       文件路径
     * @param filterType 过滤的文件后缀(文件类型)
     * @return 该路径下的所有文件
     */
    public static List<File> getFiles(String path, String filterType) {
        File dir = new File(path);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }

        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                if (TextUtils.isEmpty(filterType)) {
                    fileList.add(f);
                } else if (f.getName().endsWith(filterType)) {
                    fileList.add(f);
                }
            } else if (f.isDirectory()) {
                List<File> childFiles = getFiles(f.getAbsolutePath(), filterType);
                if (childFiles != null) {
                    fileList.addAll(childFiles);
                }
            }
        }
        return fileList;
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

    @SuppressLint("SimpleDateFormat")
    public static String format(String date) {
        return new SimpleDateFormat("HH:mm:ss SSS").format(Long.valueOf(date));
    }
}
