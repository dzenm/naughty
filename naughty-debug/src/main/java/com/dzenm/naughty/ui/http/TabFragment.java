package com.dzenm.naughty.ui.http;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

public class TabFragment extends BaseFragment {

    private static final String BUNDLE_DATA = "BUNDLE_DATA";
    private static final String BUNDLE_FLAG = "BUNDLE_FLAG";
    static final int BUNDLE_REQUEST = 1;
    static final int BUNDLE_RESPONSE = 2;

    // 保存的需要显示的数据
    private Map<String, String> data;

    // 根布局
    private LinearLayout mParent;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestFragment.
     */
    static TabFragment newInstance(HttpBean bean, int which) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_DATA, bean);
        args.putInt(BUNDLE_FLAG, which);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int which = bundle.getInt(BUNDLE_FLAG);
            HttpBean bean = bundle.getParcelable(BUNDLE_DATA);
            if (bean != null) {
                if (which == BUNDLE_REQUEST) {
                    data = bean.getRequest();
                } else if (which == BUNDLE_RESPONSE) {
                    data = bean.getResponse();
                } else {
                    data = new HashMap<>();
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c) {
        NestedScrollView scrollView = ViewUtils.newScrollView(mActivity);
        mParent = ViewUtils.newDecorView(mActivity);
        addView();
        scrollView.addView(mParent);
        return scrollView;
    }

    private void addView() {
        for (Map.Entry<String, String> map : data.entrySet()) {
            String key = map.getKey();
            String value = map.getValue();
            boolean isBody = key.contains("Body");

            LinearLayout titleLayout = ViewUtils.newTitleLayout(mActivity);
            titleLayout.addView(ViewUtils.newTitleView(mActivity, key));
            if (!isBody) titleLayout.addView(ViewUtils.newContentView(mActivity, value));
            mParent.addView(titleLayout);
            if (isBody) {
                mParent.addView(ViewUtils.newBodyView(mActivity, Utils.formatJson(value, false)));
            }
        }
    }

}
