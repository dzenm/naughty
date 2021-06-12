package com.dzenm.naughty.ui.http;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.util.ViewUtils;

/**
 * 显示单个Http请求详细数据页面
 */
public class ItemFragment extends BaseFragment<MainActivity> {

    private HttpBean data;

    /**
     * 创建一个Fragment, 并传递一些数据
     *
     * @param bean 传递过来的数据
     * @return 创建的Fragment
     */
    static ItemFragment newInstance(HttpBean bean) {
        ItemFragment fragment = new ItemFragment();
        if (bean != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_DATA, bean);
            fragment.setArguments(bundle);
        }
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container) {
        // 添加并设置Toolbar
        setHasOptionsMenu(true);

        String title = data.getMethod() + "   " + Uri.parse(data.getRequestUrl()).getPath();
        final String[] titles = new String[]{
                mActivity.getString(R.string.tab_response_title),
                mActivity.getString(R.string.tab_request_title)
        };
        TabFragment[] fragments = new TabFragment[]{
                TabFragment.newInstance(data, TabFragment.BUNDLE_RESPONSE),
                TabFragment.newInstance(data, TabFragment.BUNDLE_REQUEST)
        };

        LinearLayout parent = ViewUtils.createTabLayout(mActivity, title);
        LinearLayout tabLayout = (LinearLayout) parent.getChildAt(1);
        ViewPager viewPager = (ViewPager) parent.getChildAt(2);
        ViewUtils.initTab(mActivity, getChildFragmentManager(), viewPager, tabLayout, titles, fragments);
        return parent;
    }
}
