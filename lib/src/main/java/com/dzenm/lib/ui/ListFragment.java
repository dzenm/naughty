package com.dzenm.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.lib.Naughty;
import com.dzenm.lib.R;
import com.dzenm.lib.service.NaughtyService;
import com.dzenm.lib.util.Utils;

public class ListFragment extends Fragment implements
        Naughty.OnRequestListener, ListAdapter.OnItemClickListener {

    private HttpActivity mActivity;
    private ListAdapter mAdapter;

    public ListFragment() {

    }

    static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (HttpActivity) getActivity();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, @Nullable Bundle s) {
        return createView(i);
    }

    private LinearLayout createView(LayoutInflater inflater) {
        LinearLayout parent = new LinearLayout(mActivity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        // 添加并设置Toolbar
        Toolbar toolbar = (Toolbar) inflater.inflate(R.layout.toolbar, parent, false);
        setHasOptionsMenu(true);
        mActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_fullscreen_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Debug Mode");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
        }

        // 添加并设置RecyclerView
        RecyclerView recyclerView = new RecyclerView(mActivity);
        recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(
                mActivity, LinearLayoutManager.VERTICAL, false
        ));
        Naughty.getInstance().setOnRequestListener(this);
        mAdapter = new ListAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setData(Naughty.getInstance().get());
        recyclerView.setAdapter(mAdapter);

        parent.addView(toolbar);
        parent.addView(recyclerView);
        return parent;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.floating_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Naughty.getInstance().setShowNotification(false);
            mActivity.stopService(new Intent(mActivity, NaughtyService.class));
            mActivity.back(true);
        } else if (item.getItemId() == R.id.clear) {
            Naughty.getInstance().clear();
            mAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.setting) {
            // TODO Somethings
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(HttpBean bean, int position) {
        if (Naughty.getInstance().isHttpFinished(bean.getLoadingState())) {
            ItemFragment fragment = ItemFragment.newInstance(bean);
            Utils.switchFragment(
                    mActivity.mFrameLayoutId,
                    mActivity.getSupportFragmentManager(),
                    mActivity.mFragment,
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
