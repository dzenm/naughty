package com.dzenm.naughty.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dzenm.naughty.util.Colors;
import com.dzenm.naughty.util.ViewUtils;

public class TableRowLayout extends LinearLayout {

    private final Paint borderPaint = new Paint();

    public TableRowLayout(Context context) {
        this(context, null);
    }

    public TableRowLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableRowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false); // Layout may not call onDraw(), so we need to disable that.
        borderPaint.setColor(Colors.DIVIDE);
        borderPaint.setStrokeWidth(ViewUtils.dp2px(1f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float height = ((float) getHeight()) - 0.5f;
        float width = (float) getWidth();
        canvas.drawLine(0f, height, width, height, borderPaint);
        super.onDraw(canvas);
    }
}
