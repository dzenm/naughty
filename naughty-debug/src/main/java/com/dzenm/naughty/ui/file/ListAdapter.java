package com.dzenm.naughty.ui.file;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @author dzenm
 * 2020/8/4
 */
class ListAdapter extends BaseAdapter<File> {

    @Override
    protected View getView() {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = ViewUtils.dp2px(8);
        int padding = ViewUtils.dp2px(16);
        params.bottomMargin = params.topMargin = params.leftMargin = params.rightMargin = margin;
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setPadding(padding, padding, padding, padding);

        TextView fileName = new TextView(context);
        fileName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        fileName.setTextSize(16f);
        fileName.setEllipsize(TextUtils.TruncateAt.END);
        fileName.setMaxLines(1);
        fileName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        fileName.setTextColor(ViewUtils.getColor(context, R.color.primary_text_color));

        TextView filePath = new TextView(context);
        LinearLayout.LayoutParams fileParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fileParams.topMargin = fileParams.bottomMargin = margin;
        filePath.setLayoutParams(fileParams);

        LinearLayout paramLayout = new LinearLayout(context);
        paramLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        paramLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView size = new TextView(context);
        size.setTextSize(12f);
        size.setGravity(Gravity.CENTER_VERTICAL);
        size.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        TextView time = new TextView(context);
        time.setTextSize(12f);
        time.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        time.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));

        paramLayout.addView(size);
        paramLayout.addView(time);

        parent.addView(fileName);
        parent.addView(filePath);
        parent.addView(paramLayout);
        return parent;
    }

    @Override
    protected void onBindData(@NonNull ViewHolder holder, int position) {
        final File bean = data.get(position);

        holder.getTextView(0).setText(bean.getName());
        holder.getTextView(1).setText(bean.getAbsolutePath());
        LinearLayout paramLayout = holder.getLinearLayout(2);

        ((TextView) paramLayout.getChildAt(0)).setText(Utils.formatFileSize(bean.length()));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ((TextView) paramLayout.getChildAt(1)).setText(format.format(bean.lastModified()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.copy(context, bean.getAbsolutePath());
                Toast.makeText(context, context.getString(R.string.toast_copy_text), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        float radius = 4f;
        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, radius));
    }
}
