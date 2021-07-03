package com.dzenm.naughty.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.dzenm.naughty.util.Colors;
import com.dzenm.naughty.util.ViewUtils;

public class TableCellView extends AppCompatTextView {

    /**
     * Use this paint to draw border of table.
     */
    private final Paint borderPaint = new Paint();

    /**
     * Indicate current cell the is first cell of the row or not.
     */
    private boolean isFirstCell = false;

    public TableCellView(@NonNull Context context) {
        this(context, null);
    }

    public TableCellView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableCellView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFirstCell(boolean firstCell) {
        isFirstCell = firstCell;
    }

    private void init() {
        setWillNotDraw(false); // Layout may not call onDraw(), so we need to disable that.
        borderPaint.setColor(Colors.DIVIDE);
        borderPaint.setStrokeWidth(ViewUtils.dp2px(1f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isFirstCell) {
            // We don't draw the left border if it's first cell.
            float height = (float) getHeight();
            canvas.drawLine(0f, 0f, 0, height, borderPaint);
        }
        super.onDraw(canvas);
    }
}
