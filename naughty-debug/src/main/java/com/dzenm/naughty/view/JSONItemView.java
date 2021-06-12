package com.dzenm.naughty.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.dzenm.naughty.R;
import com.dzenm.naughty.util.ViewUtils;

public class JSONItemView extends LinearLayout {

    private TextView tvKey;
    private ImageView ivExpand;
    private TextView tvValue;

    private int mTextSize = 16;

    public JSONItemView(Context context) {
        this(context, null);
    }

    public JSONItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JSONItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        tvKey = new TextView(getContext());
        tvKey.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        tvKey.setTextSize(mTextSize);
        tvKey.setVisibility(GONE);

        ivExpand = new ImageView(getContext());
        LayoutParams imageParams = new LayoutParams(
                ViewUtils.dp2px(mTextSize), ViewUtils.dp2px(mTextSize));
        imageParams.gravity = Gravity.CENTER;
        imageParams.leftMargin = imageParams.rightMargin = ViewUtils.dp2px(4);
        ivExpand.setLayoutParams(imageParams);
        ivExpand.setVisibility(GONE);
        ivExpand.setAdjustViewBounds(true);
        ivExpand.setContentDescription(getResources().getString(R.string.json_expand));
        int padding = ViewUtils.dp2px(2);
        ivExpand.setPadding(padding, padding, padding, padding);
        ivExpand.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivExpand.setImageResource(R.drawable.ic_json_viewer_expand);
        ivExpand.setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));

        tvValue = new TextView(getContext());
        LayoutParams rightParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightParams.leftMargin = rightParams.rightMargin = ViewUtils.dp2px(1);
        tvValue.setLayoutParams(rightParams);
        tvValue.setTextSize(mTextSize);
        tvValue.setVisibility(GONE);

        LinearLayout parent = new LinearLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.addView(tvKey);
        parent.addView(ivExpand);
        parent.addView(tvValue);

        setPadding(padding, padding, padding, padding);
        setOrientation(VERTICAL);
        addView(parent);
    }

    public void setTextSize(int textSize) {
        // 范围是[12.0F,30.0F]
        mTextSize = textSize;
        mTextSize = mTextSize < 12f ? 12 : Math.min(mTextSize, 30);
        // 设置左边文本的文字大小
        tvKey.setTextSize(mTextSize);
        // 设置展示展开和收缩图标的大小
        int size = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mTextSize,
                getResources().getDisplayMetrics()
        );

        LayoutParams params = ((LayoutParams) ivExpand.getLayoutParams());
        params.width = params.height = size;
        ivExpand.setLayoutParams(params);
        // 设置右边文本的文字大小
        tvValue.setTextSize(mTextSize);
    }

    public void setKeyTextColor(@ColorInt int textColor) {
        tvValue.setTextColor(textColor);
    }

    public void hideKey() {
        tvKey.setVisibility(GONE);
    }

    public void setKeyText(@Nullable CharSequence text) {
        tvKey.setVisibility(VISIBLE);
        if (text != null) {
            tvKey.setText(text);
        }
    }

    public void hideIcon() {
        ivExpand.setVisibility(GONE);
    }

    public void showIcon(boolean isExpand) {
        ivExpand.setVisibility(VISIBLE);
        ivExpand.setImageResource(isExpand ? R.drawable.ic_json_viewer_expand : R.drawable.ic_json_viewer_collapse);
        ivExpand.setContentDescription(getResources().getString(isExpand ? R.string.json_expand : R.string.json_shrink));
    }

    public void hideValue() {
        tvValue.setVisibility(GONE);
    }

    public void setValueText(@Nullable CharSequence text) {
        tvValue.setVisibility(VISIBLE);
        if (text != null) {
            tvValue.setText(text);
        }
    }

    public @Nullable
    CharSequence getRightText() {
        return tvValue.getText();
    }

    public void setRightTextClickListener(OnClickListener listener) {
        tvValue.setOnClickListener(listener);
    }

    public void addViewNoInvalidate(View childView) {
        LayoutParams params = (LayoutParams) childView.getLayoutParams();
        if (params != null) {
            // 调用addViewInLayout方法，使其在布局（layout）的时候添加View，并且添加到View树的最后一个位置
            addViewInLayout(childView, -1, params);
        } else {
            // 如果子View没有LayoutParams，就创建一个默认的LayoutParams，宽度是MATCH_PARENT，高度是WRAP_CONTENT
            params = generateDefaultLayoutParams();
            if (params != null) {
                addViewInLayout(childView, -1, params);
            }
        }
    }

}
