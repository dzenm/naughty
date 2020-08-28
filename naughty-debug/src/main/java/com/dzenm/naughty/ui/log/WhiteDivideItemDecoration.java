package com.dzenm.naughty.ui.log;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WhiteDivideItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public WhiteDivideItemDecoration(@ColorInt int color) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
    }

    /**
     * 设置ItemView的内嵌偏移长度（inset）
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //只是添加下面这一行代码
        outRect.set(0, 2, 0, 2);
    }

    /**
     * 在子视图上设置绘制范围，并绘制内容, 绘制图层在ItemView以下，所以如果绘制区域与ItemView区域相重叠，会被遮挡
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 获取RecyclerView的Child view的个数
        int childCount = parent.getChildCount();
        // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for (int i = 0; i < childCount - 1; i++) {
            // 获取每个Item的位置
            final View child = parent.getChildAt(i);
            // 设置矩形(分割线)的宽度为10px
            final int mDivider = 2;
            // 矩形左上顶点 = (ItemView的左边界,ItemView的下边界)
            final int left = child.getLeft();
            final int top = child.getBottom();
            // 矩形右下顶点 = (ItemView的右边界,矩形的下边界)
            final int right = child.getRight();
            final int bottom = top + mDivider;
            // 通过Canvas绘制矩形（分割线）
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 同样是绘制内容，但与onDraw（）的区别是：绘制在图层的最上层
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
