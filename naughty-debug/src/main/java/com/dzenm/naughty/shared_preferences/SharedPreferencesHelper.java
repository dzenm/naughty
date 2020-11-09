package com.dzenm.naughty.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dzenm.naughty.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * SharedPreference 文件工具类
 */
public class SharedPreferencesHelper {

    private static final String TAG = SharedPreferencesHelper.class.getSimpleName();

    /**
     * 获取{@link SharedPreferences}文件夹下的所有文件
     *
     * @param context 上下文
     * @return SharedPreferences所有文件
     */
    public static List<File> getSharedPreferenceFiles(Context context) {
        List<File> files = Utils.getFiles(getSharedPreferencesDir(context), null);
        return files == null ? new ArrayList<File>() : files;
    }

    /**
     * 获取 SharedPreferences 文件内容
     *
     * @param context 上下文
     * @param file    SharedPreferences文件
     * @return 文件内容的键值对
     */
    public static Map<String, ?> getSharedPreferenceValue(Context context, File file) {
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        Log.d(TAG, "SharedPreferences fileName: " + fileName);
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getAll();
    }

    /**
     * 清空SharedPreference文件内容并删除
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 是否执行成功
     */
    public static boolean clear(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 获取{@link SharedPreferences}所在文件夹
     *
     * @param context 上下文
     * @return SharedPreferences文件夹
     */
    private static String getSharedPreferencesDir(Context context) {
        return context.getFilesDir().getParent() + "/shared_prefs";
    }
}
