package com.dzenm.naughty.util;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.log.LogHelper;
import com.dzenm.naughty.R;
import com.dzenm.naughty.ui.MainModelActivity;
import com.dzenm.naughty.ui.log.WhiteDivideItemDecoration;

public class ViewUtils {

    //************************************** Float View ***************************************//

    /**
     * 创建悬浮窗(带日志调试)
     *
     * @param context 上下文
     * @param adapter RecyclerView适配器
     * @param width   宽度
     * @param height  高度
     * @return Floating View
     */
    public static LinearLayout createFloatingLogModel(
            Context context, RecyclerView.Adapter adapter, int width, int height
    ) {
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        parent.setOrientation(LinearLayout.VERTICAL);

        int padding = dp2px(4);

        // 标题栏
        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setPadding(padding, 0, padding, 0);

        // 标题
        TextView title = new TextView(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));

        title.setText(context.getString(R.string.floating_log_title));
        title.setTextColor(getColor(context, android.R.color.white));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);
        title.setPadding(padding, padding, padding, padding);

        titleLayout.addView(title);
        titleLayout.addView(createNetworkView(context, dp2px(16)));

        // 显示日志的RecyclerView
        RecyclerView recyclerView = createRecyclerView(context, adapter);
        titleLayout.setBackgroundColor(getColor(context, R.color.primary_transparent_color));
        recyclerView.setBackgroundColor(getColor(context, R.color.secondary_transparent_color));
        recyclerView.addItemDecoration(new WhiteDivideItemDecoration(
                getColor(context, R.color.secondary_text_color)
        ));
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
        layoutParams.gravity = Gravity.END | Gravity.TOP;
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
        parent.setForeground(resolveDrawable(context, android.R.attr.selectableItemBackground));

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
     * 创建日志过滤Dialog
     *
     * @param context 上下文
     * @param items   日志等级
     */
    public static void createDialog(final Context context, final String[] items) {
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        int padding = dp2px(16);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        contentLayout.setPadding(padding, 0, padding, 0);

        final EditText editText = new EditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));

        final TextView levelView = new TextView(context);
        levelView.setLayoutParams(new LinearLayout.LayoutParams(
                dp2px(100), LinearLayout.LayoutParams.MATCH_PARENT
        ));
        String text = context.getString(R.string.log_level_title) + items[0];
        levelView.setText(text);
        levelView.setGravity(Gravity.CENTER);
        levelView.setPadding(padding, 0, padding, 0);

        levelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListDialog(context, items, levelView);
            }
        });

        contentLayout.addView(editText);
        contentLayout.addView(levelView);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_log_title))
                .setView(contentLayout)
                .setNegativeButton(context.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = levelView.getText().toString();
                        String l = text.substring(text.length() - 2).trim();
                        int level = 3;
                        for (int i = 0; i < items.length; i++) {
                            if (l.equals(items[i])) {
                                level = i + 1;
                                break;
                            }
                        }
                        String tag = editText.getText().toString().trim();
                        LogHelper.getInstance().setTag(tag)
                                .setLevel(level)
                                .reset();
                    }
                })
                .create()
                .show();
    }

    /**
     * 创建Log筛选下拉框
     *
     * @param context    上下文
     * @param items      列表内容
     * @param anchorView dialog显示在锚点View的下方
     */
    public static void createListDialog(
            final Context context, final String[] items, final TextView anchorView
    ) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        popupWindow.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items));
        popupWindow.setAnchorView(anchorView);
        popupWindow.setWidth(dp2px(32));
        popupWindow.setBackgroundDrawable(new ColorDrawable(getColor(context, android.R.color.darker_gray)));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setModal(true);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = context.getString(R.string.log_level_title) + items[position];
                anchorView.setText(text);
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

    /**
     * 创建root view(包含一个Toolbar, 和一个RecyclerView, 如果 inflater 为null, Toolbar也为空,
     * 如果adapter为null, RecyclerView也为空)
     *
     * @param activity 上下文
     * @param inflater 加载一个xml的layout
     * @param adapter  RecyclerView adapter
     * @param title    Toolbar标题
     * @return LinearLayout
     */
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

    public static LinearLayout newSubtitle(Context context, int topMargin, int bottomMargin, String subtitle) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp2px(topMargin);
        params.bottomMargin = dp2px(bottomMargin);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.VERTICAL);

        TextView tvSubtitle = new TextView(context);
        LinearLayout.LayoutParams subTitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subTitleParams.bottomMargin = dp2px(bottomMargin);
        tvSubtitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvSubtitle.setTextColor(getColor(context, R.color.subtitle_text_color));
        tvSubtitle.setLayoutParams(subTitleParams);
        tvSubtitle.setTextSize(18f);
        tvSubtitle.setText(subtitle);

        View divide = new View(context);
        divide.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(1)
        ));
        divide.setBackgroundColor(getColor(context, R.color.divide_color));

        parent.addView(tvSubtitle);
        parent.addView(divide);
        return parent;
    }

    public static LinearLayout newTitleLayout(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = dp2px(8);
        parent.setLayoutParams(params);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        return parent;
    }

    public static TextView newTitleView(Context context, String text) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getWidth() / 3, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        child.setLayoutParams(params);
        child.setText(text);
        child.setTextColor(getColor(context, R.color.primary_text_color));
        child.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
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
            text = Utils.formatJson(text);
        }
        child.setText(getContentViewStyle(context, text));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

    public static SpannableString getContentViewStyle(final Context context, final String text) {
        SpannableString string = new SpannableString(text);
        ForegroundColorSpan contentColor = new ForegroundColorSpan(
                getColor(context, R.color.secondary_text_color)
        );
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Utils.copy(context, text);
                Toast.makeText(context, context.getString(R.string.toast_copy_text) + text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                // 设置颜色
                ds.setColor(getColor(context, R.color.secondary_text_color));
                // 去掉下划线
                ds.setUnderlineText(false);
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

        normalDrawable.setStroke(dp2px(1), resolveColor(context, R.attr.colorButtonNormal));

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

    //************************************** Adapter *****************************************//

    public static class TabAdapter extends FragmentPagerAdapter {

        private String[] titles;
        private Fragment[] fragments;

        public TabAdapter(@NonNull FragmentManager fm, String[] titles, Fragment[] fragments) {
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

    //************************************** Utils *******************************************//

    public static int getColor(Context context, @ColorRes int id) {
        return context.getResources().getColor(id);
    }

    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics()
        );
    }

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
