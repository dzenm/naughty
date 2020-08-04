package com.dzenm.naughty.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.dzenm.naughty.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dzenm
 * 2020/8/4
 */
public class SharedPreferencesHelper {

    private static final String TAG = SharedPreferencesHelper.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static SharedPreferencesHelper sInstance;
    private Context mContext;

    private SharedPreferencesHelper() {
    }

    public static SharedPreferencesHelper getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferencesHelper();
                }
            }
        }
        return sInstance;
    }

    public SharedPreferencesHelper init(Context context) {
        mContext = context;
        return this;
    }

    /**
     * 获取{@link SharedPreferences}文件夹下的所有文件
     *
     * @return SharedPreferences所有文件
     */
    public List<File> getSharedPreferenceFiles() {
        List<File> files = Utils.getFiles(getSharedPreferencesPath(), null);
        if (files == null) {
            files = new ArrayList<>();
        }
        return files;
    }

    /**
     * 获取 SharedPreferences 文件内容
     *
     * @param file SharedPreferences文件
     * @return 文件内容的键值对
     */
    public Map<String, ?> getSharedPreferenceValue(File file) {
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        Log.d(TAG, "SharedPreferences fileName: " + fileName);
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 获取{@link SharedPreferences}所在文件夹
     *
     * @return SharedPreferences文件夹
     */
    private String getSharedPreferencesPath() {
        String dir = mContext.getFilesDir().getParent();
        return dir + "/shared_prefs";
    }
}
