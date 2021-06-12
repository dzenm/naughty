package com.dzenm.naughty.ui.http;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.dzenm.naughty.Naughty;
import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.service.NaughtyService;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.ui.file.FileFragment;
import com.dzenm.naughty.ui.setting.SettingFragment;
import com.dzenm.naughty.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * 2020/8/4
 * <p>
 * 显示Http请求页面
 */
public class HttpFragment extends BaseFragment<MainActivity> implements
        Naughty.OnRequestListener, HttpAdapter.OnItemClickListener<HttpBean> {

    private HttpAdapter mAdapter;
    private List<HttpBean> data;

    public static HttpFragment newInstance(List<HttpBean> data) {
        HttpFragment fragment = new HttpFragment();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_DATA, (ArrayList<? extends Parcelable>) data);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getParcelableArrayList(BUNDLE_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container) {
        Naughty.getInstance().setOnRequestListener(this);
        mAdapter = new HttpAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(data);

        setHasOptionsMenu(true);
        // 创建并设置Toolbar
        View view = ViewUtils.createDecorView(mActivity, true, mAdapter, mActivity.getString(R.string.main_model_title));
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_fullscreen_white);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.floating_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.db) {
            mActivity.switchFragment(this, FileFragment.newInstance(
                    getString(R.string.dp_title),
                    FileFragment.FLAG_DATA_BASE));
        } else if (item.getItemId() == R.id.sp) {
            mActivity.switchFragment(this, FileFragment.newInstance(
                    getString(R.string.shared_preferences_title),
                    FileFragment.FLAG_SHARED_PREFERENCES));
        } else if (item.getItemId() == R.id.setting) {
//            final String[] items = new String[]{"N", "V", "D", "I", "W", "E"};
//            ViewUtils.createDialog(mActivity, items);
            mActivity.switchFragment(this, SettingFragment.newInstance());
        } else if (item.getItemId() == R.id.clear) {
            Naughty.getInstance().clear();
            mAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.close) {
            mActivity.stopService(new Intent(mActivity, NaughtyService.class));
            mActivity.onBackKeyboard(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(HttpBean bean, int position) {
        if (Naughty.getInstance().isHttpFinished(bean.getLoadingState())) {
            mActivity.switchFragment(this, ItemFragment.newInstance(bean));
        } else {
            Toast.makeText(mActivity, getString(R.string.toast_internet_loading_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInterceptor(HttpBean bean, final int position) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRangeChanged(position, 1);
            }
        });
    }
}
