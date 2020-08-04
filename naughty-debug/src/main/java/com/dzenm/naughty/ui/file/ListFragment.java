package com.dzenm.naughty.ui.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.shared.SharedPreferencesHelper;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;

/**
 * @author dzenm
 * 2020/8/4
 */
public class ListFragment extends BaseFragment implements ListAdapter.OnItemClickListener {

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        ListAdapter adapter = new ListAdapter();
        adapter.setOnItemClickListener(this);
        adapter.setData(SharedPreferencesHelper.getInstance().init(mActivity).getSharedPreferenceFiles());

        return ViewUtils.createDecorView(mActivity, inflater, adapter, "SharedPreferences File");
    }

    @Override
    public void onItemClick(File data, int position) {
        ItemFragment fragment = ItemFragment.newInstance(data.getAbsolutePath());
        Utils.switchFragment(
                mActivity.getSupportFragmentManager(),
                this,
                fragment
        );
    }
}