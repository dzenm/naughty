package com.dzenm.naughty.ui.http;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

public class ItemFragment extends BaseFragment {

    private static final String BUNDLE_DATA = "BUNDLE_DATA";
    private HttpBean data;

    /**
     * 创建一个Fragment, 并传递一些数据
     *
     * @param bean 传递过来的数据
     * @return 创建的Fragment
     */
    static ItemFragment newInstance(HttpBean bean) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_DATA, bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ListFragment传递的数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getParcelable(BUNDLE_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup c) {
        return createView(inflater);
    }

    private View createView(LayoutInflater inflater) {
        // 创建根布局
        LinearLayout parent = new LinearLayout(mActivity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        // 添加并设置Toolbar
        setHasOptionsMenu(true);
        String title = data.getMethod() + "   " + Uri.parse(data.getUrl()).getPath();
        Toolbar toolbar = ViewUtils.createToolbar(mActivity, inflater, parent, title);

        // 添加并设置TabLayout
        LinearLayout tabLayout = new LinearLayout(mActivity);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setBackgroundColor(ViewUtils.resolveColor(mActivity, R.attr.colorPrimary));

        // 添加并设置ViewPager
        ViewPager viewPager = new ViewPager(mActivity);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
        ));
        viewPager.setId(View.generateViewId());

        // 添加所有View到根布局
        parent.addView(toolbar);
        parent.addView(tabLayout);
        parent.addView(viewPager);

        final String[] titles = new String[]{"RESPONSE", "REQUEST"};
        TabFragment[] fragments = new TabFragment[]{
                TabFragment.newInstance(data, TabFragment.BUNDLE_RESPONSE),
                TabFragment.newInstance(data, TabFragment.BUNDLE_REQUEST)
        };
        ViewUtils.initTab(mActivity, getChildFragmentManager(), viewPager, tabLayout, titles, fragments);
        return parent;
    }


}
