package com.dzenm.naughty.ui.log;

import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dzenm.log.LogHelper;
import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.util.Utils;
import com.dzenm.naughty.util.ViewUtils;

public class LogItemAdapter extends BaseAdapter<String> {

    @Override
    protected View getView() {
        return ViewUtils.createLogItemView(context);
    }

    @Override
    protected void onBindData(@NonNull ViewHolder holder, int position) {
        final String log = data.get(position);
        holder.getTextView(0).setText(Html.fromHtml(LogHelper.getInstance().format(log)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = log.indexOf("):");
                if (index != -1) {
                    String text = log.substring(index + 2);
                    Utils.copy(context, text);
                    Toast.makeText(context, "复制成功: " + text, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
