package com.dzenm.naughty.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.dzenm.naughty.R;
import com.dzenm.naughty.shared_preferences.SharedPreferencesHelper;
import com.dzenm.naughty.ui.setting.SettingFragment;

public class SettingUtils {

    /**
     * 获取设置中的Boolean值
     *
     * @param context 上下文
     * @param key     值对应的键
     * @return Boolean
     */
    public static Object getValue(Context context, String key, Object defValue) {
        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferences(
                context, SettingFragment.SETTING_PREFERENCES);
        if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        }
        return null;
    }

    /**
     * 获取设置中的Boolean值
     *
     * @param context 上下文
     * @param key     值对应的键
     * @return Boolean
     */
    public static boolean getBooleanValue(Context context, String key, boolean defValue) {
        return (boolean) getValue(context, key, defValue);
    }

    public static String getStringValue(Context context, String key, String defValue) {
        return (String) getValue(context, key, defValue);
    }

    public static String getThemeMode(Context context) {
        String[] values = context.getResources().getStringArray(R.array.theme_mode);
        return (String) getValue(context, SettingFragment.KEY_THEME_MODE, values[0]);
    }

    public static boolean isEnabledFloating(Context context) {
        return getBooleanValue(context, SettingFragment.KEY_ENABLED_FLOATING, true);
    }

    public static int getFloatingStyle(Context context) {
        String[] values = context.getResources().getStringArray(R.array.floating_value);
        String defValue = (String) getValue(context, SettingFragment.KEY_FLOATING_STYLE, values[0]);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(defValue)) return i;
        }
        return 0;
    }

    public static boolean isEnabledFloatingLogView(Context context) {
        return getBooleanValue(context, SettingFragment.KEY_ENABLED_FLOATING, true);
    }

    public static boolean getLogState(Context context) {
        return getBooleanValue(context, SettingFragment.KEY_ENABLED_LOG_DEBUG, true);
    }

    public static boolean isEnabledHttpInterceptor(Context context) {
        return getBooleanValue(context, SettingFragment.KEY_ENABLED_HTTP, true);
    }

    public static boolean isEnabledNotification(Context context) {
        return getBooleanValue(context, SettingFragment.KEY_ENABLED_NOTIFICATION, true);
    }

    public static int getNotificationLevel(Context context) {
        String[] values = context.getResources().getStringArray(R.array.notification_level);
        String defValue = (String) getValue(context,
                SettingFragment.KEY_NOTIFICATION_LEVEL, values[0]);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(defValue)) return i;
        }
        return 0;
    }

    public static boolean isEnabledNotificationSoundAndVibration(Context context) {
        return getBooleanValue(context,
                SettingFragment.KEY_ENABLED_NOTIFICATION_SOUND_VIBRATION, false);
    }

}
