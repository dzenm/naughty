package com.dzenm.naughty.ui.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * 2020/8/4
 */
class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private List<File> data = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    void setData(List<File> data) {
        this.data = data;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return ViewHolder.createViewHolder(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final File bean = data.get(position);

        holder.fileName.setText(bean.getName());
        holder.filePath.setText(bean.getAbsolutePath());
        holder.size.setText(Utils.formatFileSize(bean.length()));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        holder.time.setText(format.format(bean.lastModified()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.copy(context, bean.getAbsolutePath());
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        float radius = 4f;
        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, radius));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(bean, position);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView filePath;
        TextView time;
        TextView size;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            LinearLayout parent = (LinearLayout) itemView;
            fileName = (TextView) parent.getChildAt(0);
            filePath = (TextView) parent.getChildAt(1);
            LinearLayout paramLayout = (LinearLayout) parent.getChildAt(2);
            size = (TextView) paramLayout.getChildAt(0);
            time = (TextView) paramLayout.getChildAt(1);
        }

        static ViewHolder createViewHolder(Context context) {
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
            fileName.setTextColor(ViewUtils.primaryTextColor());

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
            return new ViewHolder(parent);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(File data, int position);
    }
}
