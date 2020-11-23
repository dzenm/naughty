package com.dzenm.naughty.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.dzenm.naughty.BuildConfig;
import com.dzenm.naughty.R;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.util.ViewUtils;

public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    public static final String SETTING_PREFERENCES = "naughty_setting_preferences";
    private static final String TAG = SettingFragment.class.getSimpleName();

    public static final String KEY_THEME_MODE = "theme_mode";

    public static final String KEY_ENABLED_FLOATING = "enabled_floating";
    public static final String KEY_FLOATING_STYLE = "floating_style";

    public static final String KEY_ENABLED_LOG_DEBUG = "enabled_log_debug";

    public static final String KEY_ENABLED_HTTP = "enabled_http";
    public static final String KEY_ENABLED_NOTIFICATION = "enabled_notification";
    public static final String KEY_NOTIFICATION_LEVEL = "notification_level";
    public static final String KEY_ENABLED_NOTIFICATION_SOUND_VIBRATION = "enabled_notification_sound_vibration";

    public static final String KEY_OTHERS_ISSUE = "issue_address";
    public static final String KEY_OTHERS_VERSION = "current_version";

    private MainModelActivity mActivity;
    private ListPreference mThemeMode;
    private SwitchPreferenceCompat mFloatingState;
    private ListPreference mFloatingStyle;
    private SwitchPreferenceCompat mLogDebugState;
    private SwitchPreferenceCompat mHttpModelState;
    private SwitchPreferenceCompat mHttpNotificationState;
    private ListPreference mNotificationLevel;
    private SwitchPreferenceCompat mHttpNotificationSoundAndVibrationState;

    private Preference mIssue;
    private Preference mVersion;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mActivity = (MainModelActivity) getActivity();
        // 配置 SharedPreferences 名称, 必须在 setPreferencesFromResource() 之前设置
        getPreferenceManager().setSharedPreferencesName(SETTING_PREFERENCES);

        createPreferenceScreen();

        initData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LinearLayout parent = ViewUtils.createDecorView(mActivity, inflater,
                null, getText(R.string.setting_title));
        parent.addView(super.onCreateView(inflater, container, savedInstanceState));

        setDividerHeight(1);
        return parent;
    }

    private void initData() {
        final String githubUrl = "https://github.com/dzenm/naughty";
        mIssue.setSummary(githubUrl);
        mIssue.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(githubUrl));
                startActivity(intent);
                return false;
            }
        });
        mVersion.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mThemeMode) {
            mActivity.getDelegate().setLocalNightMode(mActivity.loadThemeMode((String) newValue));
            mActivity.recreate();
        }
        return true;
    }

    /**
     * 创建Preference Screen
     */
    private void createPreferenceScreen() {
        PreferenceScreen rootScreen = ViewUtils.createScreen(mActivity, getPreferenceManager());

        PreferenceCategory basicPreference = ViewUtils.createCategory(mActivity,
                getText(R.string.setting_basic_preferences));
        rootScreen.addPreference(basicPreference);
        createBasicPreference(basicPreference);

        PreferenceCategory floatingPreference = ViewUtils.createCategory(mActivity,
                getText(R.string.setting_floating_preferences));
        rootScreen.addPreference(floatingPreference);
        createFloatingPreference(floatingPreference);

        PreferenceCategory logPreference = ViewUtils.createCategory(mActivity,
                getText(R.string.setting_log_preferences));
        rootScreen.addPreference(logPreference);
        createLogPreference(logPreference);

        PreferenceCategory httpPreference = ViewUtils.createCategory(mActivity,
                getText(R.string.setting_http_preferences));
        rootScreen.addPreference(httpPreference);
        createHttpPreference(httpPreference);

        PreferenceCategory othersPreference = ViewUtils.createCategory(mActivity,
                getText(R.string.setting_others_preferences));
        rootScreen.addPreference(othersPreference);
        createOthersPreference(othersPreference);

        setPreferenceScreen(rootScreen);

        setDependency();
    }

    /**
     * 基本设置
     *
     * @param basicPreference parent preference
     */
    private void createBasicPreference(PreferenceCategory basicPreference) {
        // 夜间模式开关
        mThemeMode = ViewUtils.createList(mActivity,
                R.string.setting_basic_dark_mode_default_value_preferences,
                R.string.setting_basic_dark_mode_title_preferences,
                R.string.setting_basic_dark_mode_dialog_title_preferences,
                R.array.theme_mode
        );
        mThemeMode.setKey(KEY_THEME_MODE);
        basicPreference.addPreference(mThemeMode);
        // TODO 夜间模式待完善
    }

    /**
     * 悬浮窗设置
     *
     * @param floatingPreference parent preference
     */
    private void createFloatingPreference(PreferenceCategory floatingPreference) {
        // 悬浮窗开关
        mFloatingState = ViewUtils.createSwitch(mActivity, true,
                R.string.setting_floating_title_preferences,
                R.string.setting_floating_summary_preferences
        );
        mFloatingState.setKey(KEY_ENABLED_FLOATING);
        floatingPreference.addPreference(mFloatingState);

        // 悬浮窗样式
        mFloatingStyle = ViewUtils.createList(mActivity,
                R.string.setting_floating_style_default_value_preferences,
                R.string.setting_floating_style_title_preferences,
                R.string.setting_floating_style_dialog_title_preferences,
                R.array.floating_value
        );
        mFloatingStyle.setKey(KEY_FLOATING_STYLE);
        mFloatingStyle.setOnPreferenceChangeListener(this);
        floatingPreference.addPreference(mFloatingStyle);
    }

    /**
     * 日志设置
     *
     * @param logPreference parent preference
     */
    private void createLogPreference(PreferenceCategory logPreference) {
        // 日志Debug开关
        mLogDebugState = ViewUtils.createSwitch(mActivity, true,
                R.string.setting_log_debug_title_preferences,
                R.string.setting_log_debug_summary_preferences
        );
        mLogDebugState.setKey(KEY_ENABLED_LOG_DEBUG);

        logPreference.addPreference(mLogDebugState);
    }

    /**
     * HTTP设置
     *
     * @param httpPreference parent preference
     */
    private void createHttpPreference(PreferenceCategory httpPreference) {
        // HTTP面板开关
        mHttpModelState = ViewUtils.createSwitch(mActivity, true,
                R.string.setting_http_interceptor_title_preferences,
                R.string.setting_http_interceptor_summary_preferences
        );
        mHttpModelState.setKey(KEY_ENABLED_HTTP);
        httpPreference.addPreference(mHttpModelState);

        // HTTP通知开关
        mHttpNotificationState = ViewUtils.createSwitch(mActivity, true,
                R.string.setting_http_notification_title_preferences,
                R.string.setting_http_notification_summary_preferences
        );
        mHttpNotificationState.setKey(KEY_ENABLED_NOTIFICATION);
        httpPreference.addPreference(mHttpNotificationState);

        // HTTP通知优先级
        mNotificationLevel = ViewUtils.createList(mActivity,
                R.string.setting_http_notification_level_default_value_preferences,
                R.string.setting_http_notification_level_title_preferences,
                R.string.setting_http_notification_level_dialog_title_preferences,
                R.array.notification_level
        );
        mNotificationLevel.setKey(KEY_NOTIFICATION_LEVEL);
        mNotificationLevel.setOnPreferenceChangeListener(this);
        httpPreference.addPreference(mNotificationLevel);
        // TODO 通知优先级

        // HTTP通知的声音和振动开关
        mHttpNotificationSoundAndVibrationState = ViewUtils.createSwitch(mActivity, false,
                R.string.setting_http_notification_sound_and_vibration_title_preferences,
                R.string.setting_http_notification_sound_and_vibration_summary_preferences
        );
        mHttpNotificationSoundAndVibrationState.setKey(KEY_ENABLED_NOTIFICATION_SOUND_VIBRATION);
        httpPreference.addPreference(mHttpNotificationSoundAndVibrationState);
        // TODO 声音振动开关待完善
    }

    /**
     * 其他设置
     *
     * @param othersPreference parent preference
     */
    private void createOthersPreference(PreferenceCategory othersPreference) {
        mIssue = ViewUtils.createPreference(mActivity,
                R.string.setting_others_issue_title_preferences);
        mIssue.setKey(KEY_OTHERS_ISSUE);
        othersPreference.addPreference(mIssue);

        mVersion = ViewUtils.createPreference(mActivity,
                R.string.setting_others_version_title_preferences);
        mVersion.setKey(KEY_OTHERS_VERSION);
        othersPreference.addPreference(mVersion);
    }

    /**
     * 设置依赖的Key, 不能在创建时设置
     */
    public void setDependency() {
        mFloatingStyle.setDependency(KEY_ENABLED_FLOATING);
        mHttpNotificationState.setDependency(KEY_ENABLED_HTTP);
        mNotificationLevel.setDependency(KEY_ENABLED_NOTIFICATION);
        mHttpNotificationSoundAndVibrationState.setDependency(KEY_ENABLED_NOTIFICATION);
    }
}
