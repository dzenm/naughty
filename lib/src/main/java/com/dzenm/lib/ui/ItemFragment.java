package com.dzenm.lib.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.lib.R;
import com.dzenm.lib.util.Utils;

public class ItemFragment extends Fragment {

    private HttpActivity mActivity;
    private HttpBean data;

    public ItemFragment() {
    }

    /**
     * 创建一个Fragment, 并传递一些数据
     *
     * @param bean 传递过来的数据
     * @return 创建的Fragment
     */
    static ItemFragment newInstance(HttpBean bean) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(HttpActivity.FLOATING_BEAN, bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ListFragment传递的数据
        Bundle bundle = getArguments();
        mActivity = (HttpActivity) getActivity();
        if (bundle != null) {
            data = bundle.getParcelable(HttpActivity.FLOATING_BEAN);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup c, @Nullable Bundle s) {
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
        Toolbar toolbar = Utils.getToolbar(inflater, parent);
        setHasOptionsMenu(true);
        mActivity.setSupportActionBar(toolbar);
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = data.getMethod() + "   " + Uri.parse(data.getUrl()).getPath();
            mActivity.getSupportActionBar().setTitle(title);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
        }

        // 添加并设置TabLayout
        LinearLayout tabLayout = new LinearLayout(mActivity);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setBackgroundColor(Utils.resolveColor(mActivity, R.attr.colorPrimary));

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

        initTab(viewPager, tabLayout);
        return parent;
    }

    private void initTab(final ViewPager viewPager, final LinearLayout tabLayout) {
        final String[] titles = new String[]{"RESPONSE", "REQUEST"};

        // 初始化Tab显示内容
        for (int i = 0; i < titles.length; i++) {
            final int position = i;
            TextView tab = createTab();
            tab.setText(titles[i]);
            boolean isSelected = i == 0;
            tab.setTextSize(isSelected ? 18 : 14);
            tab.setTextColor(isSelected
                    ? Color.WHITE
                    : Utils.resolveColor(mActivity, R.attr.colorButtonNormal));
            tabLayout.addView(tab);
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(position);
                }
            });
        }

        final TabFragment[] fragments = new TabFragment[]{
                TabFragment.newInstance(data, TabFragment.ARGUMENT_RESPONSE),
                TabFragment.newInstance(data, TabFragment.ARGUMENT_REQUEST)
        };

        // 设置ViewPager
        viewPager.setAdapter(new TabAdapter(getChildFragmentManager(), titles, fragments));
        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 设置选中时的Tab变化
                for (int i = 0; i < titles.length; i++) {
                    TextView tab = (TextView) tabLayout.getChildAt(i);
                    boolean isSelected = position == i;
                    tab.setTextColor(isSelected
                            ? Color.WHITE
                            : Utils.resolveColor(mActivity, R.attr.colorButtonNormal));
                    tab.setTextSize(isSelected ? 18 : 14);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private TextView createTab() {
        TextView tab = new TextView(mActivity);
        tab.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, Utils.dp2px(50))
        );
        tab.setPadding(Utils.dp2px(24), 0, Utils.dp2px(24), 0);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(18);
        tab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tab.setForeground(Utils.resolveDrawable(mActivity, android.R.attr.selectableItemBackground));
        }
        return tab;
    }

    private static class TabAdapter extends FragmentPagerAdapter {

        private String[] titles;
        private Fragment[] fragments;

        private TabAdapter(@NonNull FragmentManager fm, String[] titles, Fragment[] fragments) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.titles = titles;
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
