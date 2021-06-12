package com.dzenm.naughty.ui.file.db;

import android.view.View;

import androidx.annotation.NonNull;

import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.db.model.Table;
import com.dzenm.naughty.util.Dimens;
import com.dzenm.naughty.util.ViewUtils;

class TableAdapter extends BaseAdapter<Table> {

    @Override
    protected View getView() {
        return ViewUtils.createTableItemView(context);
    }

    @Override
    protected boolean isDeleteClickable() {
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Table bean = data.get(position);

        holder.getTextView(0).setText(bean.getName());

        holder.itemView.setBackground(ViewUtils.getRippleDrawable(context, Dimens.RADIUS_16F));
    }
}
