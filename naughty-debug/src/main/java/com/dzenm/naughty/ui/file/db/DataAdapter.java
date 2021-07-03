package com.dzenm.naughty.ui.file.db;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.db.model.Column;
import com.dzenm.naughty.db.model.Row;
import com.dzenm.naughty.util.Colors;
import com.dzenm.naughty.util.ViewUtils;
import com.dzenm.naughty.view.TableCellView;
import com.dzenm.naughty.view.TableRowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据表的数据显示
 */
public class DataAdapter extends BaseAdapter<List<Row>> {

    List<Column> columns = new ArrayList<>();
    private int rowWidth;

    public void setRowWidth(int rowWidth) {
        this.rowWidth = rowWidth;
        notifyDataSetChanged();
    }

    public void setColumns(List<Column> columns) {
        this.columns.clear();
        this.columns = columns;
        notifyDataSetChanged();
    }

    @Override
    protected View getView() {
        TableRowLayout tableRowLayout = ViewUtils.createColumnItemView(context);
        ViewUtils.createColumnItemView(tableRowLayout, columns, rowWidth);
        return tableRowLayout;
    }

    @Override
    protected boolean isDeleteClickable() {
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int adapterPosition = holder.getAdapterPosition();
        List<Row> row = data.get(adapterPosition);
        if (row != null) {
            TableRowLayout tableRowLayout = (TableRowLayout) holder.itemView;
            int color = position % 2 == 0 ? Colors.WHITE_BACKGROUND : Colors.GRAY_BACKGROUND;
            tableRowLayout.setBackgroundColor(color);
            for (int i = 0; i < tableRowLayout.getChildCount(); i++) {
                TableCellView tableCellView = (TableCellView) tableRowLayout.getChildAt(i);
                tableCellView.setFirstCell(i == 0);
                Object value = row.get(i).getValue();
                String text = value == null ? "null" : value.toString();
                tableCellView.setText(text);
            }
        }
    }
}
