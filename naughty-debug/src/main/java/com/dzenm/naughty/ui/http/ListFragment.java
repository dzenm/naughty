package com.dzenm.naughty.ui.http;

import android.content.Intent;
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
import com.dzenm.naughty.service.NaughtyService;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

public class ListFragment extends BaseFragment implements
        Naughty.OnRequestListener, ListAdapter.OnItemClickListener {

    private ListAdapter mAdapter;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c) {
        mAdapter = new ListAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(Naughty.getInstance().get());
        Naughty.getInstance().setOnRequestListener(this);

        // 设置Toolbar
        setHasOptionsMenu(true);
        return ViewUtils.createDecorView(mActivity, i, mAdapter, "Debug Mode");
    }

    @Override
    public void initView() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_fullscreen_white);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.floating_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            final String[] items = new String[]{"N", "V", "D", "I", "W", "E"};
            ViewUtils.createDialog(mActivity, items);
        } else if (item.getItemId() == R.id.cache) {
            com.dzenm.naughty.ui.file.ListFragment fragment = com.dzenm.naughty.ui.file.ListFragment.newInstance();
            Utils.switchFragment(
                    mActivity.getSupportFragmentManager(),
                    this,
                    fragment
            );
        }  else if (item.getItemId() == R.id.clear) {
            Naughty.getInstance().clear();
            mAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.close) {
            Naughty.getInstance().setShowNotification(false);
            mActivity.stopService(new Intent(mActivity, NaughtyService.class));
            mActivity.back(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(HttpBean bean, int position) {
        if (Naughty.getInstance().isHttpFinished(bean.getLoadingState())) {
            ItemFragment fragment = ItemFragment.newInstance(bean);
            Utils.switchFragment(
                    mActivity.getSupportFragmentManager(),
                    this,
                    fragment
            );
        } else {
            Toast.makeText(mActivity, "网络请求正在加载中...请加载结束后再试", Toast.LENGTH_SHORT).show();
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
