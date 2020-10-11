package com.dzenm.naughty.ui.http;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.http.HttpBean;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dzenm
 * <p>
 * 显示单个Http请求
 */
public class TabFragment extends BaseFragment<MainModelActivity> {

    static final int BUNDLE_REQUEST = 1;
    static final int BUNDLE_RESPONSE = 2;

    /**
     * 保存的需要显示的数据
     */
    private Map<String, String> data = new HashMap<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestFragment.
     */
    public static TabFragment newInstance(HttpBean bean, int which) {
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
            HttpBean bean = bundle.getParcelable(BUNDLE_DATA);
            if (bean != null) {
                int which = bundle.getInt(BUNDLE_FLAG);
                if (which == BUNDLE_REQUEST) {
                    data = bean.getRequest();
                } else if (which == BUNDLE_RESPONSE) {
                    data = bean.getResponse();
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container) {
        // 根布局
        NestedScrollView scrollView = ViewUtils.newScrollView(mActivity);
        LinearLayout parent = ViewUtils.newDecorView(mActivity);

        parent.addView(ViewUtils.newSubtitle(mActivity, 8, 8, "Headers"));
        for (Map.Entry<String, String> map : data.entrySet()) {
            String key = map.getKey();
            String value = map.getValue();
            boolean isBody = key.equals("Response Body") || key.equals("Request Body");

            if (isBody) continue;
            LinearLayout titleLayout = ViewUtils.newTitleLayout(mActivity);
            titleLayout.addView(ViewUtils.newTitleView(mActivity, key));
            titleLayout.addView(ViewUtils.newContentView(mActivity, value));
            parent.addView(titleLayout);
        }

        parent.addView(ViewUtils.newSubtitle(mActivity, 16, 8, "Body"));
        Bundle bundle = getArguments();
        if (bundle != null) {
            int which = bundle.getInt(BUNDLE_FLAG);
            String value = which != BUNDLE_REQUEST ? data.get("Response Body") : data.get("Request Body");
            if (value != null) {
                parent.addView(ViewUtils.newBodyView(mActivity, Utils.formatJson(value)));
            }
        }

        scrollView.addView(parent);
        return scrollView;
    }
}
