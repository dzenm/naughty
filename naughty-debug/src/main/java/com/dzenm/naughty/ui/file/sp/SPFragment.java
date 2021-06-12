package com.dzenm.naughty.ui.file.sp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.sp.SharedPreferencesHelper;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.util.Map;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 显示单个 SharedPreferences 详细数据页面
 */
public class SPFragment extends BaseFragment<MainActivity> {

    private String mFileData;
    private Map<String, ?> data;

    public static SPFragment newInstance(String data) {
        SPFragment fragment = new SPFragment();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_DATA, data);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取上一个页面传递的文件数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFileData = bundle.getString(BUNDLE_DATA);
            if (mFileData != null) {
                data = SharedPreferencesHelper.getSharedPreferenceValue(mActivity, new File(mFileData));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        LinearLayout parent = ViewUtils.createDecorView(mActivity, true, null, mFileData);
        NestedScrollView scrollView = ViewUtils.createScrollView(mActivity);
        LinearLayout content = ViewUtils.createRootView(mActivity);

        for (Map.Entry<String, ?> map : data.entrySet()) {
            String key = map.getKey();
            Object value = map.getValue();

            LinearLayout titleLayout = ViewUtils.newTitleLayout(mActivity);
            titleLayout.addView(ViewUtils.newTitleView(mActivity, key));
            titleLayout.addView(ViewUtils.newContentView(mActivity, value.toString()));
            content.addView(titleLayout);
        }

        scrollView.addView(content);
        parent.addView(scrollView);
        return parent;
    }
}