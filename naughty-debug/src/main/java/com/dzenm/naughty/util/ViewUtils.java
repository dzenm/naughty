package com.dzenm.naughty.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.naughty.R;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.ui.adapter.TabAdapter;

public class ViewUtils {

    //************************************** Float View ***************************************//

    /**
     * 创建悬浮窗(带日志调试)
     *
     * @param context 上下文
     * @param adapter RecyclerView适配器
     * @param items   选项
     * @return Floating View
     */
    public static LinearLayout createFloatingLogModel(Context context, RecyclerView.Adapter adapter, String[] items) {
        LinearLayout parent = new LinearLayout(context);
        int width = ViewUtils.getWidth() * 3 / 8;
        int height = width * 4 / 3;
        parent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        parent.setOrientation(LinearLayout.VERTICAL);

        int padding = dp2px(4);
        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setPadding(padding, 0, padding, 0);

        TextView title = new TextView(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        title.setText("Log(可拖动)");
        title.setTextColor(context.getResources().getColor(android.R.color.white));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);
        title.setPadding(padding, padding, padding, padding);

        TextView selected = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.rightMargin = params.leftMargin = padding;
        selected.setLayoutParams(params);
        selected.setPadding(padding, 0, padding, 0);
        selected.setGravity(Gravity.CENTER);
        selected.setTextColor(context.getResources().getColor(android.R.color.white));
        selected.setText(items[0]);

        titleLayout.addView(title);
        titleLayout.addView(selected);
        titleLayout.addView(createNetworkView(context, dp2px(16)));

        RecyclerView recyclerView = createRecyclerView(context, adapter);
        titleLayout.setBackgroundColor(0xDD212121);
        recyclerView.setBackgroundColor(0xCC212121);
        parent.addView(titleLayout);
        parent.addView(recyclerView);
        return parent;
    }

    /**
     * 创建Floating NetworkView
     *
     * @param context 上下文
     * @return Floating NetworkView
     */
    public static ImageView createNetworkView(Context context, int size) {
        ImageView network = new ImageView(context);
        network.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        network.setImageResource(R.drawable.ic_network);
        return network;
    }

    /**
     * 创建Floating View配置信息
     *
     * @return Floating View配置信息
     */
    public static WindowManager.LayoutParams createFloatingViewParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        return layoutParams;
    }

    /**
     * 创建Log列表的ItemView
     *
     * @param context 上下文
     * @return ItemView
     */
    public static FrameLayout createLogItemView(Context context) {
        FrameLayout parent = new FrameLayout(context);
        parent.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView item = new TextView(context);
        item.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        int padding = dp2px(4);
        item.setPadding(padding, 0, padding, padding);
        item.setTextSize(12f);

        parent.addView(item);
        return parent;
    }

    /**
     * 创建Log筛选下拉框
     *
     * @param context    上下文
     * @param items      列表内容
     * @param anchorView dialog显示在锚点View的下方
     */
    public static void createDialog(
            Context context, final String[] items, final TextView anchorView
    ) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        popupWindow.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items));
        popupWindow.setAnchorView(anchorView);
        popupWindow.setWidth(dp2px(32));
        popupWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.darker_gray)));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setModal(true);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                anchorView.setText(items[position]);
                popupWindow.dismiss();
            }
        });
        anchorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.show();
            }
        });
    }

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
     * @param context 上下文
     * @param adapter 适配器
     * @return 创建RecyclerView
     */
    public static RecyclerView createRecyclerView(
            final Context context, RecyclerView.Adapter adapter
    ) {
        // 添加并设置RecyclerView
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
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
            final String[] titles, Fragment[] fragments
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
