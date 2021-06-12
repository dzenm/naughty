package com.dzenm.naughty.ui.file;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dzenm.naughty.R;
import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.sp.SharedPreferencesHelper;
import com.dzenm.naughty.util.Dimens;
import com.dzenm.naughty.util.StringUtils;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;

/**
 * @author dzenm
 * 2020/8/4
 */
class FileAdapter extends BaseAdapter<File> {

    @Override
    protected View getView() {
        return ViewUtils.createFileItemView(context);
    }

    @Override
    protected boolean isDeleteClickable() {
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final File bean = data.get(position);

        holder.getTextView(0).setText(bean.getName());
        holder.getTextView(1).setText(bean.getAbsolutePath());
        FrameLayout paramLayout = holder.getFrameLayout(2);

        String formatFileSize = StringUtils.formatFileSize(bean.length());
        String fileSize = context.getResources().getString(R.string.file_size) + formatFileSize;
        ((TextView) paramLayout.getChildAt(0)).setText(fileSize);

        String lastModified = StringUtils.formatDate("yyyy/MM/dd HH:mm:ss", bean.lastModified());
        String lastModifiedTime = context.getResources().getString(R.string.file_last_modifier_time)
                + lastModified;
        ((TextView) paramLayout.getChildAt(1)).setText(lastModifiedTime);

        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, Dimens.RADIUS_16F));
    }

    @Override
    protected void deleteBefore(File bean) {
        String name = bean.getName();
        int index = name.lastIndexOf(".");
        SharedPreferencesHelper.clear(context, name.substring(0, index));

        bean.deleteOnExit();
    }
}
