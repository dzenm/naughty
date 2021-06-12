package com.dzenm.naughty.ui.file;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.db.DBHelper;
import com.dzenm.naughty.sp.SharedPreferencesHelper;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.ui.file.db.TableFragment;
import com.dzenm.naughty.ui.file.sp.SPFragment;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 显示文件列表页面
 */
public class FileFragment extends BaseFragment<MainActivity>
        implements FileAdapter.OnItemClickListener<File> {

    public static final int FLAG_DATA_BASE = 1;
    public static final int FLAG_SHARED_PREFERENCES = 2;

    private String mTitle;
    private int mFlag;

    public static FileFragment newInstance(String title, int flag) {
        FileFragment fragment = new FileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_DATA, title);
        bundle.putInt(BUNDLE_FLAG, flag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(BUNDLE_DATA);
            mFlag = bundle.getInt(BUNDLE_FLAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        FileAdapter adapter = new FileAdapter();
        adapter.setOnItemClickListener(this);

        List<File> files = new ArrayList<>();
        if (mFlag == FLAG_DATA_BASE) {
            files.addAll(DBHelper.getDBFiles(mActivity));
        } else if (mFlag == FLAG_SHARED_PREFERENCES) {
            files.addAll(SharedPreferencesHelper.getSharedPreferenceFiles(mActivity));
        }
        adapter.setData(files);

        return ViewUtils.createDecorView(mActivity, true, adapter, mTitle);
    }

    @Override
    public void onItemClick(File data, int position) {
        Fragment fragment = new Fragment();
        String path = data.getPath();
        if (mFlag == FLAG_DATA_BASE) {
            fragment = TableFragment.newInstance(path);
        } else if (mFlag == FLAG_SHARED_PREFERENCES) {
            fragment = SPFragment.newInstance(path);
        }
        mActivity.switchFragment(this, fragment);
    }
}