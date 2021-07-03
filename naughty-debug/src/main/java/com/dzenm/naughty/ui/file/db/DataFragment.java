package com.dzenm.naughty.ui.file.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.db.DBHelper;
import com.dzenm.naughty.db.model.Column;
import com.dzenm.naughty.db.model.Row;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.util.Colors;
import com.dzenm.naughty.util.ViewUtils;
import com.dzenm.naughty.view.TableRowLayout;

import java.util.List;

public class DataFragment extends BaseFragment<MainActivity> implements
        BaseAdapter.OnItemClickListener<List<Row>> {

    private String dbName, tableName;
    private DataAdapter adapter;
    private TableRowLayout tableRowLayout;
    private HorizontalScrollView scrollView;
    private TextView totalCount;
    private ProgressBar progressBar;

    public static DataFragment newInstance(String dbPath, String tableName) {
        DataFragment fragment = new DataFragment();
        if (dbPath != null && tableName != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_FLAG, dbPath);
            bundle.putString(BUNDLE_DATA, tableName);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        boolean isNull = bundle == null;
        if (!isNull) {
            dbName = bundle.getString(BUNDLE_FLAG);
            tableName = bundle.getString(BUNDLE_DATA);
            isNull = tableName == null;
        }

        if (isNull) {
            Toast.makeText(mActivity, "Table name is null", Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        tableRowLayout = ViewUtils.createColumnItemView(mActivity);
        scrollView = new HorizontalScrollView(mActivity);
        progressBar = new ProgressBar(mActivity);
        totalCount = new TextView(mActivity);
        adapter = new DataAdapter();
        getData();
        return ViewUtils.createDataView(mActivity, scrollView, progressBar, tableRowLayout,
                totalCount, adapter, tableName);
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();
            }
        }).start();
    }

    private void getDataFromDB() {
        int rowWidth = 0;
        final List<Column> columns = DBHelper.getColumnFromDB(dbName, tableName);
        final List<List<Row>> data = DBHelper.getRowFromDB(dbName, tableName, columns);
        DBHelper.measureColumnsWidth(mActivity, columns, data);
        for (Column column : columns) {
            rowWidth += column.getWidth();
        }
        final int width = rowWidth + columns.size() * ViewUtils.dp2px(20);
        tableRowLayout.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                updateUI(columns, data, width);
            }
        });
    }

    private void updateUI(List<Column> columns, List<List<Row>> data, int rowWidth) {
        rowWidth = Math.max(rowWidth, scrollView.getWidth());
        ViewUtils.createColumnItemView(tableRowLayout, columns, rowWidth);
        adapter.setRowWidth(rowWidth);
        adapter.setColumns(columns);
        adapter.setData(data);
        String totalText = "总共有" + data.size() + "条数据";
        totalCount.setText(totalText);
    }

    @Override
    public void onItemClick(List<Row> row, int position) {

    }
}
