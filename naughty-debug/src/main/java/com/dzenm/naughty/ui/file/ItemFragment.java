package com.dzenm.naughty.ui.file;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.shared_preferences.SharedPreferencesHelper;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.util.Map;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 显示单个 SharedPreferences 详细数据页面
 */
public class ItemFragment extends BaseFragment<MainModelActivity> {

    private String mFileData;
    private Map<String, ?> data;

    public static ItemFragment newInstance(String data) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取上一个页面传递的文件数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFileData = bundle.getString(BUNDLE_DATA);
            data = SharedPreferencesHelper.getInstance().getSharedPreferenceValue(new File(mFileData));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        LinearLayout parent = ViewUtils.createDecorView(mActivity, inflater, null, mFileData);
        NestedScrollView scrollView = ViewUtils.newScrollView(mActivity);
        LinearLayout content = ViewUtils.newDecorView(mActivity);

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