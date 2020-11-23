package com.dzenm.naughty.ui.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.shared_preferences.SharedPreferencesHelper;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 显示 SharedPreference 文件列表页面
 */
public class ListFragment extends BaseFragment<MainModelActivity> implements
        ListAdapter.OnItemClickListener<File> {

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        ListAdapter adapter = new ListAdapter();
        adapter.setOnItemClickListener(this);
        adapter.setData(SharedPreferencesHelper.getSharedPreferenceFiles(mActivity));

        return ViewUtils.createDecorView(
                mActivity, inflater, adapter, mActivity.getString(R.string.shared_preferences_title)
        );
    }

    @Override
    public void onItemClick(File data, int position) {
        Utils.switchFragment(
                mActivity.getSupportFragmentManager(),
                this,
                ItemFragment.newInstance(data.getAbsolutePath())
        );
    }
}