package com.dzenm.lib.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.dzenm.lib.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class TabFragment extends Fragment {

    private static final String ARGUMENT_DATA = "ARGUMENT_DATA";
    private static final String ARGUMENT_FLAG = "ARGUMENT_FLAG";
    static final int ARGUMENT_REQUEST = 1;
    static final int ARGUMENT_RESPONSE = 2;

    private HttpActivity mActivity;

    // 保存的需要显示的数据
    private Map<String, String> data;

    // 根布局
    private LinearLayout mParent;

    public TabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestFragment.
     */
    static TabFragment newInstance(HttpBean bean, int which) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_DATA, bean);
        args.putInt(ARGUMENT_FLAG, which);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (HttpActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            int which = bundle.getInt(ARGUMENT_FLAG);
            HttpBean bean = bundle.getParcelable(ARGUMENT_DATA);
            if (bean != null) {
                if (which == ARGUMENT_REQUEST) {
                    data = bean.getRequest();
                } else if (which == ARGUMENT_RESPONSE) {
                    data = bean.getResponse();
                } else {
                    data = new HashMap<>();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        NestedScrollView scrollView = newScrollView(mActivity);
        mParent = newDecorView(mActivity);
        addView();
        scrollView.addView(mParent);
        return scrollView;
    }

    private void addView() {
        for (Map.Entry<String, String> map : data.entrySet()) {
            String key = map.getKey();
            String value = map.getValue();
            boolean isBody = key.contains("Body");

            LinearLayout titleLayout = newTitleLayout(mActivity);
            titleLayout.addView(newTitleView(mActivity, key));
            if (!isBody) titleLayout.addView(newContentView(mActivity, value));
            mParent.addView(titleLayout);
            if (isBody) {
                mParent.addView(newBodyView(mActivity, Utils.formatJson(value, false)));
            }
        }
    }

    //************************************** Parent Layout ***************************************//

    private NestedScrollView newScrollView(Context context) {
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        return scrollView;
    }

    private LinearLayout newDecorView(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        int padding = Utils.dp2px(16);
        parent.setPadding(padding, padding, padding, padding);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        return parent;
    }

    //***************************************** Style 1 ******************************************//

    private LinearLayout newTitleLayout(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = Utils.dp2px(8);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(params);
        return parent;
    }

    private TextView newTitleView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Utils.getWidth() / 3, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        child.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        child.setTextColor(Utils.primaryTextColor());
        child.setLayoutParams(params);
        child.setText(text);
        return child;
    }

    private TextView newContentView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        child.setLayoutParams(params);
        child.setLineSpacing(0f, 1.2f);
        child.setText(getContentViewStyle(context, text));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

    private SpannableString getContentViewStyle(final Context context, final String text) {
        SpannableString string = new SpannableString(text);
        ForegroundColorSpan contentColor = new ForegroundColorSpan(Utils.secondaryTextColor());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String string = text;
                if (text.contains("═") || text.contains("╔")
                        || text.contains("╚") || text.contains("║")) {
                    string = text.replace("═", "")
                            .replace("╔", "")
                            .replace("╚", "")
                            .replace("║", "");
                }
                Utils.copy(context, string);
                Toast.makeText(mActivity, "复制成功: " + string, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(Utils.secondaryTextColor());       // 设置颜色
                ds.setUnderlineText(false);                    // 去掉下划线
            }
        };
        string.setSpan(contentColor, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    //***************************************** Body ******************************************//

    private TextView newBodyView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        child.setLayoutParams(params);
        child.setLineSpacing(0f, 1.2f);
        child.setText(getContentViewStyle(context, text));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

}
