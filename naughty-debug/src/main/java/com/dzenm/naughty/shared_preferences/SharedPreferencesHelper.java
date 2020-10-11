package com.dzenm.naughty.shared_preferences;

import android.annotation.SuppressLint;
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
        if (mContext == null) {
            mContext = context;
        }
        return this;
    }

    /**
     * 获取{@link SharedPreferences}文件夹下的所有文件
     *
     * @return SharedPreferences所有文件
     */
    public List<File> getSharedPreferenceFiles() {
        List<File> files = Utils.getFiles(getSharedPreferencesDir(), null);
        return files == null ? new ArrayList<File>() : files;
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
    private String getSharedPreferencesDir() {
        return mContext.getFilesDir().getParent() + "/shared_prefs";
    }
}
