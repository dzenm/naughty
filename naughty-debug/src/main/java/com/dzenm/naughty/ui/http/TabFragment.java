package com.dzenm.naughty.ui.http;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.http.model.HttpBean;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.util.Colors;
import com.dzenm.naughty.util.Dimens;
import com.dzenm.naughty.util.StringUtils;
import com.dzenm.naughty.util.ViewUtils;
import com.dzenm.naughty.view.JSONViewAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dzenm
 * <p>
 * 显示单个Http请求
 */
public class TabFragment extends BaseFragment<MainActivity> {

    static final int BUNDLE_REQUEST = 1;
    static final int BUNDLE_RESPONSE = 2;

    /**
     * 保存的需要显示的数据
     */
    private Map<String, String> data = new HashMap<>();

    private int which;

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
            which = bundle.getInt(BUNDLE_FLAG);
            if (bean != null) {
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
        NestedScrollView scrollView = ViewUtils.createScrollView(mActivity);
        LinearLayout parent = ViewUtils.createRootView(mActivity);

        parent.addView(ViewUtils.newSubtitle(mActivity, Dimens.PADDING_8, "Headers"));
        parent.addView(ViewUtils.newDivide(mActivity, Colors.DIVIDE));
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

        parent.addView(ViewUtils.newSubtitle(mActivity, Dimens.PADDING_8, "Body"));
        parent.addView(ViewUtils.newDivide(mActivity, Colors.DIVIDE));

        String value = which != BUNDLE_REQUEST ? data.get("Response Body") : data.get("Request Body");
        if (value != null) {
            parent.addView(ViewUtils.newJsonBody(mActivity, StringUtils.formatJson(value)));
        }

        scrollView.addView(parent);
        return scrollView;
    }
}
