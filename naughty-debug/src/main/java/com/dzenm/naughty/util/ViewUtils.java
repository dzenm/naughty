package com.dzenm.naughty.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.naughty.R;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.ui.adapter.TabAdapter;
import com.dzenm.naughty.ui.http.TabFragment;

public class ViewUtils {

    //************************************** Parent Layout ***************************************//

    public static LinearLayout createDecorView(
            MainModelActivity activity, LayoutInflater inflater, RecyclerView.Adapter adapter, CharSequence title
    ) {
        LinearLayout parent = new LinearLayout(activity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        if (inflater != null) {
            parent.addView(createToolbar(activity, inflater, parent, title));
        }
        if (adapter != null) {
            parent.addView(createRecyclerView(activity, adapter));
        }
        return parent;
    }

    /**
     * 创建一个Toolbar
     *
     * @param activity 上下文
     * @param inflater 布局加载器
     * @param parent   父布局
     * @return 创建的Toolbar
     */
    public static Toolbar createToolbar(
            final MainModelActivity activity, LayoutInflater inflater, ViewGroup parent, CharSequence title
    ) {
        // 添加并设置Toolbar
        Toolbar toolbar = (Toolbar) inflater.inflate(R.layout.toolbar, parent, false);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
        return toolbar;
    }

    /**
     * 创建一个RecyclerView
     *
     * @param activity 上下文
     * @param adapter  适配器
     * @return 创建RecyclerView
     */
    public static RecyclerView createRecyclerView(
            final AppCompatActivity activity, RecyclerView.Adapter adapter
    ) {
        // 添加并设置RecyclerView
        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(
                activity, LinearLayoutManager.VERTICAL, false
        ));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }


    //************************************** Parent Layout ***************************************//

    public static NestedScrollView newScrollView(Context context) {
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        return scrollView;
    }

    public static LinearLayout newDecorView(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        int padding = dp2px(16);
        parent.setPadding(padding, padding, padding, padding);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        return parent;
    }


    //************************************** Content View ****************************************//

    public static LinearLayout newTitleLayout(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = dp2px(8);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(params);
        return parent;
    }

    public static TextView newTitleView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getWidth() / 3, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        child.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        child.setTextColor(primaryTextColor());
        child.setMaxLines(1);
        child.setEllipsize(TextUtils.TruncateAt.END);
        child.setLayoutParams(params);
        child.setText(text);
        return child;
    }

    public static TextView newContentView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        child.setLayoutParams(params);
        child.setLineSpacing(0f, 1.2f);
        if ((text.startsWith("{") || text.startsWith("[")) && (text.endsWith("}") || text.endsWith("]"))) {
            text = Utils.formatJson(text, false);
        }
        child.setText(getContentViewStyle(context, text));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

    public static SpannableString getContentViewStyle(final Context context, final String text) {
        SpannableString string = new SpannableString(text);
        ForegroundColorSpan contentColor = new ForegroundColorSpan(secondaryTextColor());
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
                Toast.makeText(context, "复制成功: " + string, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(secondaryTextColor());             // 设置颜色
                ds.setUnderlineText(false);                    // 去掉下划线
            }
        };
        string.setSpan(contentColor, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    //************************************** Body ************************************************//

    public static TextView newBodyView(Context context, String text) {
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

    //************************************** Tab ************************************************//

    public static void initTab(
            final AppCompatActivity activity, FragmentManager fragmentManager,
            final ViewPager viewPager, final LinearLayout tabLayout,
            final String[] titles, TabFragment[] fragments
    ) {
        // 初始化Tab显示内容
        for (int i = 0; i < titles.length; i++) {
            final int position = i;
            TextView tab = ViewUtils.createTab(activity);
            tab.setText(titles[i]);
            boolean isSelected = i == 0;
            tab.setTextSize(isSelected ? 18 : 14);
            tab.setTextColor(isSelected
                    ? Color.WHITE
                    : resolveColor(activity, R.attr.colorButtonNormal));
            tabLayout.addView(tab);
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(position);
                }
            });
        }

        // 设置ViewPager
        viewPager.setAdapter(new TabAdapter(fragmentManager, titles, fragments));
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
                            : resolveColor(activity, R.attr.colorButtonNormal));
                    tab.setTextSize(isSelected ? 18 : 14);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static TextView createTab(Context context) {
        TextView tab = new TextView(context);
        tab.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp2px(50))
        );
        tab.setPadding(dp2px(24), 0, dp2px(24), 0);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(18);
        tab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tab.setForeground(ViewUtils.resolveDrawable(context, android.R.attr.selectableItemBackground));
        }
        return tab;
    }

    //************************************** Drawable ********************************************//

    public static int resolveColor(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static Drawable resolveDrawable(@NonNull Context context, @AttrRes int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = a.getDrawable(0);
            } else {
                int id = a.getResourceId(0, -1);
                if (id == -1) {
                    drawable = AppCompatResources.getDrawable(context, id);
                }
            }
            return drawable;
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取水波纹按压背景
     *
     * @param radius 圆角
     * @return 水波纹背景
     */
    public static Drawable getRippleDrawable(Context context, float radius) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(Color.WHITE);
        float[] radiusIIII = new float[]{
                dp2px(radius), dp2px(radius), dp2px(radius), dp2px(radius),
                dp2px(radius), dp2px(radius), dp2px(radius), dp2px(radius)};
        normalDrawable.setCornerRadii(radiusIIII);

        normalDrawable.setStroke(dp2px(1), resolveColor(context, R.attr.colorAccent));

        return new RippleDrawable(
                ColorStateList.valueOf(resolveColor(context, R.attr.colorButtonNormal)),
                normalDrawable, null);
    }

    public static Drawable getStatusDrawable(int color, float radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        float[] radiusII = new float[]{
                0, 0, dp2px(radius), dp2px(radius), 0, 0, dp2px(radius), dp2px(radius)
        };
        gradientDrawable.setCornerRadii(radiusII);
        return gradientDrawable;
    }

    public static int primaryTextColor() {
        return Color.parseColor("#212121");
    }

    public static int secondaryTextColor() {
        return Color.parseColor("#757575");
    }

    //************************************** Size ********************************************//

    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

}
