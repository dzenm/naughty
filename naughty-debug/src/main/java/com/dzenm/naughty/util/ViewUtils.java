package com.dzenm.naughty.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.log.LogHelper;
import com.dzenm.naughty.R;
import com.dzenm.naughty.db.model.Column;
import com.dzenm.naughty.ui.file.db.DataAdapter;
import com.dzenm.naughty.view.JSONViewAdapter;
import com.dzenm.naughty.view.TableCellView;
import com.dzenm.naughty.view.TableRowLayout;

import java.util.List;

public class ViewUtils {

    //************************************** Float View ***************************************//

    /**
     * 创建悬浮窗(按钮)
     *
     * @param context 上下文
     * @return Floating View
     */
    public static TextView createFloatingView(Context context) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new FrameLayout.LayoutParams(dp2px(64), dp2px(64)));
        textView.setText(context.getText(R.string.floating_debug_mode_title));
        textView.setGravity(Gravity.CENTER);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxLines(1);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        textView.setBackgroundResource(R.drawable.ic_floating);
        return textView;
    }

    /**
     * 创建悬浮窗(带日志调试)
     *
     * @param context 上下文
     * @param adapter RecyclerView适配器
     * @param width   宽度
     * @param height  高度
     * @return Floating View
     */
    public static LinearLayout createFloatingLogModel(Context context, RecyclerView.Adapter<?> adapter,
                                                      int width, int height) {
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        parent.setOrientation(LinearLayout.VERTICAL);

        int padding = dp2px(Dimens.PADDING_4);

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
        recyclerView.addItemDecoration(new WhiteDivideItemDecoration(Colors.SECONDARY_TEXT));
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
     * 创建Floating LogModel View配置信息
     *
     * @return Floating View配置信息
     */
    public static WindowManager.LayoutParams createFloatingLogModelViewParams() {
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
        int padding = dp2px(Dimens.PADDING_4);
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
        int padding = dp2px(Dimens.PADDING_16);
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
    public static void createListDialog(final Context context, final String[] items,
                                        final TextView anchorView) {
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
     * 创建Decor View(仅仅是一个FrameLayout)
     *
     * @param activity      上下文
     * @param frameLayoutId frameLayout Id
     * @return FrameLayout
     */
    public static View createDecorView(AppCompatActivity activity, int frameLayoutId) {
        FrameLayout parent = new FrameLayout(activity);
        parent.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setId(frameLayoutId);
        return parent;
    }

    /**
     * 创建root view(包含一个Toolbar, 和一个RecyclerView, 如果isShowToolbar为false, Toolbar为空,
     * 如果adapter为null, RecyclerView也为空)
     *
     * @param activity      上下文
     * @param isShowToolbar 是否设置Toolbar
     * @param adapter       RecyclerView adapter
     * @param title         Toolbar标题
     * @return LinearLayout
     */
    public static LinearLayout createDecorView(AppCompatActivity activity, boolean isShowToolbar,
                                               RecyclerView.Adapter<?> adapter, CharSequence title) {
        LinearLayout parent = new LinearLayout(activity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        if (isShowToolbar) {
            parent.addView(createToolbar(activity, title));
        }
        if (adapter != null) {
            parent.addView(createRecyclerView(activity, adapter));
        }
        return parent;
    }

    /**
     * 创建一个Toolbar, Toolbar根据主题色colorPrimary设置, 默认显示前置的返回按钮并设置点击事件
     *
     * @param activity 上下文
     * @param title    Toolbar的标题
     * @return 创建的Toolbar
     */
    public static Toolbar createToolbar(final AppCompatActivity activity, CharSequence title) {
        // 获取ActionBar高度
        TypedArray a = activity.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = a.getDimensionPixelOffset(0, 0);
        a.recycle();

        Context context = new ContextThemeWrapper(activity, R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
        Toolbar toolbar = new Toolbar(context);
        toolbar.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, actionBarSize));
        // 获取colorPrimary颜色
        TypedValue color = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, color, true);
        toolbar.setBackgroundColor(color.data);
        toolbar.setPopupTheme(R.style.Theme_AppCompat_Light);

        // 设置Toolbar
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
    public static RecyclerView createRecyclerView(Context context, RecyclerView.Adapter<?> adapter) {
        // 添加并设置RecyclerView
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
        ));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    //************************************** List Item View **************************************//

    /**
     * 创建Http列表的ItemView
     *
     * @param context 上下文
     * @return ItemView
     */
    public static RelativeLayout createHttpItemView(Context context) {
        RelativeLayout parent = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = params.topMargin =
                params.rightMargin = params.bottomMargin = dp2px(8);
        parent.setLayoutParams(params);
        parent.setElevation(Dimens.ELEVATION_4F);

        // 状态标识
        TextView result = new TextView(context);
        RelativeLayout.LayoutParams resultParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, dp2px(16));
        resultParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        result.setLayoutParams(resultParams);
        result.setGravity(Gravity.CENTER);
        result.setPadding(dp2px(4), 0, dp2px(4), 0);
        result.setTextColor(getColor(context, android.R.color.white));
        result.setTextSize(10f);

        // 进度显示
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(View.generateViewId());
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                dp2px(24), dp2px(24));
        progressParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        progressParams.leftMargin = progressParams.rightMargin = dp2px(8);
        progressParams.topMargin = dp2px(16);
        progressBar.setLayoutParams(progressParams);

        // 请求状态
        TextView state = new TextView(context);
        state.setId(View.generateViewId());
        RelativeLayout.LayoutParams stateParams = new RelativeLayout.LayoutParams(dp2px(40),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        stateParams.leftMargin = stateParams.topMargin = stateParams.rightMargin = dp2px(16);
        state.setLayoutParams(stateParams);
        state.setGravity(Gravity.CENTER_VERTICAL);
        state.setTextSize(16f);
        state.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        // URL
        TextView url = new TextView(context);
        url.setId(View.generateViewId());
        RelativeLayout.LayoutParams urlParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        urlParams.topMargin = dp2px(16);
        urlParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        urlParams.addRule(RelativeLayout.START_OF, progressBar.getId());
        urlParams.addRule(RelativeLayout.END_OF, state.getId());
        url.setLayoutParams(urlParams);
        url.setTextSize(16f);
        url.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        url.setTextColor(Colors.PRIMARY_TEXT);

        // baseUrl
        TextView baseUrl = new TextView(context);
        baseUrl.setId(View.generateViewId());
        RelativeLayout.LayoutParams baseUrlParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        baseUrlParams.addRule(RelativeLayout.BELOW, url.getId());
        baseUrlParams.addRule(RelativeLayout.START_OF, progressBar.getId());
        baseUrlParams.addRule(RelativeLayout.END_OF, state.getId());
        baseUrlParams.topMargin = baseUrlParams.bottomMargin = dp2px(8);
        baseUrl.setLayoutParams(baseUrlParams);
        baseUrl.setTextColor(Colors.SECONDARY_TEXT);

        // 请求所用耗时
        TextView usageTime = new TextView(context);
        RelativeLayout.LayoutParams usageTimeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        usageTimeParams.addRule(RelativeLayout.BELOW, baseUrl.getId());
        usageTimeParams.addRule(RelativeLayout.END_OF, state.getId());
        usageTimeParams.bottomMargin = dp2px(16);
        usageTime.setLayoutParams(usageTimeParams);
        usageTime.setTextColor(Colors.SECONDARY_TEXT);

        // 发出请求时的具体时间
        TextView currentTime = new TextView(context);
        RelativeLayout.LayoutParams currentTimeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        currentTimeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        currentTimeParams.addRule(RelativeLayout.BELOW, baseUrl.getId());
        currentTimeParams.bottomMargin = dp2px(16);
        currentTime.setLayoutParams(currentTimeParams);
        currentTime.setTextColor(Colors.SECONDARY_TEXT);

        // 请求返回内容的大小
        TextView size = new TextView(context);
        RelativeLayout.LayoutParams sizeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sizeParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        sizeParams.addRule(RelativeLayout.BELOW, baseUrl.getId());
        sizeParams.rightMargin = sizeParams.bottomMargin = dp2px(16);
        size.setLayoutParams(sizeParams);
        size.setTextColor(Colors.SECONDARY_TEXT);

        parent.addView(result);
        parent.addView(progressBar);
        parent.addView(state);
        parent.addView(url);
        parent.addView(baseUrl);
        parent.addView(usageTime);
        parent.addView(currentTime);
        parent.addView(size);
        return parent;
    }

    /**
     * 创建文件列表的ItemView
     *
     * @param context 上下文
     * @return ItemView
     */
    public static LinearLayout createFileItemView(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dp2px(Dimens.MARGIN_8);
        int padding = dp2px(Dimens.MARGIN_16);
        params.bottomMargin = params.topMargin = params.leftMargin = params.rightMargin = margin;
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setPadding(padding, padding, padding, padding);
        parent.setElevation(Dimens.ELEVATION_4F);

        // 文件名
        TextView fileName = new TextView(context);
        fileName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        fileName.setTextSize(20f);
        fileName.setEllipsize(TextUtils.TruncateAt.END);
        fileName.setMaxLines(1);
        fileName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        fileName.setTextColor(Colors.PRIMARY_TEXT);

        // 文件路径
        TextView filePath = new TextView(context);
        LinearLayout.LayoutParams fileParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fileParams.topMargin = fileParams.bottomMargin = margin;
        filePath.setLayoutParams(fileParams);
        filePath.setTextColor(Colors.SECONDARY_TEXT);

        FrameLayout paramLayout = new FrameLayout(context);
        paramLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 文件大小
        TextView size = new TextView(context);
        size.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        size.setGravity(Gravity.CENTER_VERTICAL);
        size.setTextColor(Colors.SECONDARY_TEXT);

        // 文件最后修改时间
        TextView time = new TextView(context);
        time.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.END;
        time.setLayoutParams(layoutParams);
        time.setTextColor(Colors.SECONDARY_TEXT);

        paramLayout.addView(size);
        paramLayout.addView(time);

        parent.addView(fileName);
        parent.addView(filePath);
        parent.addView(paramLayout);
        return parent;
    }

    /**
     * 创建数据库表的ItemView
     *
     * @param context 上下文
     * @return ItemView
     */
    public static LinearLayout createTableItemView(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dp2px(Dimens.MARGIN_8);
        int padding = dp2px(Dimens.MARGIN_16);
        params.bottomMargin = params.topMargin = params.leftMargin = params.rightMargin = margin;
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setPadding(padding, padding, padding, padding);
        parent.setElevation(Dimens.ELEVATION_4F);

        // 文件名
        TextView fileName = new TextView(context);
        fileName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        fileName.setTextSize(16f);
        fileName.setEllipsize(TextUtils.TruncateAt.END);
        fileName.setMaxLines(1);
        fileName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        fileName.setTextColor(Colors.PRIMARY_TEXT);

        parent.addView(fileName);
        return parent;
    }


    /**
     * 创建root view(包含一个Toolbar, 和一个RecyclerView,)
     *
     * @return LinearLayout
     */
    public static LinearLayout createDataView(AppCompatActivity activity,
                                              HorizontalScrollView scrollView,
                                              ProgressBar progressBar,
                                              TableRowLayout tableRowLayout,
                                              TextView totalCount,
                                              DataAdapter adapter,
                                              String tableName) {
        LinearLayout parent = new LinearLayout(activity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(params);

        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1
        ));
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setVerticalScrollBarEnabled(false);

        totalCount.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        totalCount.setPadding(dp2px(12), dp2px(12), dp2px(12), dp2px(12));
        totalCount.setGravity(Gravity.CENTER);

        tableRowLayout.setBackgroundColor(Colors.GRAY_BACKGROUND);

        LinearLayout dataParent = new LinearLayout(activity);
        dataParent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dataParent.setOrientation(LinearLayout.VERTICAL);

        dataParent.addView(tableRowLayout);
        dataParent.addView(ViewUtils.createRecyclerView(activity, adapter));

        scrollView.addView(dataParent);

        parent.addView(ViewUtils.createToolbar(activity, tableName));
        parent.addView(scrollView);
        parent.addView(progressBar);
        parent.addView(totalCount);
        return parent;
    }

    /**
     * 创建表格布局横行显示的ItemView
     *
     * @param context 上下文
     * @return TableRowLayout
     */
    public static TableRowLayout createColumnItemView(Context context) {
        TableRowLayout parent = new TableRowLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp2px(30)
        ));
        return parent;
    }

    /**
     * Build a TableRowLayout as a row to show the columns of a table as title.
     */
    public static void createColumnItemView(TableRowLayout tableRowLayout,
                                            List<Column> columns, int rowWidth) {
        ViewGroup.LayoutParams param = tableRowLayout.getLayoutParams();
        param.width = rowWidth;
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            TableCellView tableCellView = createRowItemView(tableRowLayout.getContext());
            tableCellView.setFirstCell(i == 0);
            tableCellView.setText(column.getName());
            // We let each column has 20dp extra space, to make it look better.
            LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                    column.getWidth() + dp2px(20),
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            tableRowLayout.addView(tableCellView, layoutParam);
        }
        tableRowLayout.setBackgroundColor(Color.WHITE);
    }

    /**
     * Build a TextView widget as a table cell to show data in a row.
     */
    public static TableCellView createRowItemView(Context context) {
        TableCellView tableCellView = new TableCellView(context);
        tableCellView.setGravity(Gravity.CENTER_VERTICAL);
        // Actually each column has 20dp extra space, but we only use 10 in padding.
        // This makes each column has more space to show their content before be ellipsized.
        tableCellView.setPadding(ViewUtils.dp2px(5), 0, ViewUtils.dp2px(5), 0);
        tableCellView.setSingleLine();
        tableCellView.setEllipsize(TextUtils.TruncateAt.END);
        return tableCellView;
    }

    //************************************** Parent Layout ***************************************//

    /**
     * 创建一个可以滚动的Layout
     *
     * @param context 上下文
     * @return NestedScrollView
     */
    public static NestedScrollView createScrollView(Context context) {
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ));
        return scrollView;
    }

    /**
     * 创建一个线性布局的根View
     *
     * @param context 上下文
     * @return root view
     */
    public static LinearLayout createRootView(Context context) {
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        int padding = dp2px(Dimens.PADDING_16);
        parent.setPadding(padding, padding, padding, padding);
        parent.setOrientation(LinearLayout.VERTICAL);
        return parent;
    }

    //************************************** Content View ****************************************//

    /**
     * 创建小标题
     *
     * @param context  上下文
     * @param padding  内边距
     * @param subtitle 标题文字
     * @return TextView
     */
    public static TextView newSubtitle(Context context, int padding, String subtitle) {
        TextView tvSubtitle = new TextView(context);
        tvSubtitle.setPadding(dp2px(padding), dp2px(padding), dp2px(padding), dp2px(padding));
        tvSubtitle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        tvSubtitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvSubtitle.setTextColor(Colors.PRIMARY_TEXT);
        tvSubtitle.setTextSize(18f);
        tvSubtitle.setText(subtitle);
        return tvSubtitle;
    }

    /**
     * 创建分割线
     *
     * @param context 上下文
     * @param color   分割线颜色值
     * @return 分割线
     */
    public static View newDivide(Context context, @ColorInt int color) {
        View divide = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(1)
        );
        params.bottomMargin = dp2px(Dimens.MARGIN_8);
        divide.setLayoutParams(params);
        divide.setBackgroundColor(color);
        return divide;
    }

    public static LinearLayout newTitleLayout(Context context) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parent.setPadding(Dimens.PADDING_8, Dimens.PADDING_8, Dimens.PADDING_8, Dimens.PADDING_8);
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
        child.setTextColor(Colors.PRIMARY_TEXT);
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
            text = StringUtils.formatJson(text);
        }
        child.setText(getContentViewStyle(context, text));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

    public static SpannableString getContentViewStyle(final Context context, final String text) {
        SpannableString string = new SpannableString(text);
        ForegroundColorSpan contentColor = new ForegroundColorSpan(Colors.SECONDARY_TEXT);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                copy(context, text);
                Toast.makeText(context, context.getString(R.string.toast_copy_text) + text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                // 设置颜色
                ds.setColor(Colors.SECONDARY_TEXT);
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        };
        string.setSpan(contentColor, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    //************************************** Body ************************************************//

    public static TextView newBodyView(Context context, String json) {
        TextView child = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        child.setLayoutParams(params);
        child.setLineSpacing(0f, 1.2f);
        child.setText(getContentViewStyle(context, json));
        // 为TextView设置完Span后，别忘了setMovementMethod
        child.setMovementMethod(LinkMovementMethod.getInstance());
        return child;
    }

    public static View newJsonBody(Context context, String json) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        JSONViewAdapter adapter = new JSONViewAdapter(context);
        recyclerView.setAdapter(adapter);
        adapter.bindData(json);
        return recyclerView;
    }

    //************************************** Tab ************************************************//

    /**
     * 创建一个内容跟随Tab可以左右滑动的TabLayout
     *
     * @param activity 上下文
     * @param title    Tab标题
     * @return TabLayout
     */
    public static LinearLayout createTabLayout(AppCompatActivity activity, String title) {
        // 创建根布局
        LinearLayout parent = new LinearLayout(activity);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        parent.setOrientation(LinearLayout.VERTICAL);

        // 添加并设置TabLayout
        LinearLayout tabLayout = new LinearLayout(activity);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tabLayout.setGravity(Gravity.CENTER);
        tabLayout.setBackgroundColor(resolveColor(activity, R.attr.colorPrimary));

        // 添加并设置ViewPager
        ViewPager viewPager = new ViewPager(activity);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
        ));
        viewPager.setId(View.generateViewId());

        // 添加所有View到根布局
        parent.addView(createToolbar(activity, title));
        parent.addView(tabLayout);
        parent.addView(viewPager);
        return parent;
    }

    public static void initTab(final AppCompatActivity activity, FragmentManager fragmentManager,
                               final ViewPager viewPager, final LinearLayout tabLayout,
                               final String[] titles, Fragment[] fragments) {
        // 初始化Tab显示内容
        for (int i = 0; i < titles.length; i++) {
            final int position = i;
            TextView tab = createTab(activity);
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

    /**
     * 创建一个Tab
     *
     * @param context 上下文
     * @return Tab
     */
    public static TextView createTab(Context context) {
        TextView tab = new TextView(context);
        tab.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp2px(50)));
        tab.setPadding(dp2px(Dimens.PADDING_24), 0, dp2px(Dimens.PADDING_24), 0);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(18);
        tab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tab.setForeground(resolveDrawable(context, android.R.attr.selectableItemBackground));
        }
        return tab;
    }

    //************************************** Setting View ***************************************//

    public static PreferenceScreen createScreen(Context context, PreferenceManager manager) {
        return manager.createPreferenceScreen(context);
    }

    public static PreferenceCategory createCategory(Context context, CharSequence title) {
        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(title);
        return category;
    }

    public static Preference createPreference(Context context, int title, int summary) {
        Preference preference = new Preference(context);
        preference.setCopyingEnabled(true);
        preference.setTitle(title);
        preference.setSummary(summary);
        return preference;
    }

    public static Preference createPreference(Context context, int title) {
        Preference preference = new Preference(context);
        preference.setCopyingEnabled(true);
        preference.setTitle(title);
        return preference;
    }

    public static SwitchPreferenceCompat createSwitch(Context context, boolean defValue,
                                                      int title, int summary) {
        SwitchPreferenceCompat switchButton = new SwitchPreferenceCompat(context);
        switchButton.setSingleLineTitle(true);
        switchButton.setDefaultValue(defValue);
        switchButton.setTitle(title);
        switchButton.setSummary(summary);
        return switchButton;
    }

    public static ListPreference createList(Context context, int defValueId, int titleId,
                                            int dialogTitle, int entriesId) {
        ListPreference list = new ListPreference(context);
        list.setDefaultValue(context.getText(defValueId));
        list.setTitle(titleId);
        list.setDialogTitle(dialogTitle);
        list.setEntries(context.getResources().getStringArray(entriesId));
        list.setEntryValues(context.getResources().getStringArray(entriesId));
        list.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        return list;
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

//        normalDrawable.setStroke(dp2px(Dimens.STROKE_1), resolveColor(context, R.attr.colorButtonNormal));

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

        private final String[] titles;
        private final Fragment[] fragments;

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
     * 复制纯文本
     *
     * @param context 获取系统服务的上下文
     * @param text    复制的文本
     */
    public static void copy(Context context, CharSequence text) {
        // 获取剪切板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符clipData
        ClipData clipData = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, text);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
