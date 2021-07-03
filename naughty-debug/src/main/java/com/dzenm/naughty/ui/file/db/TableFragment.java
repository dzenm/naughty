package com.dzenm.naughty.ui.file.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dzenm.naughty.base.BaseAdapter;
import com.dzenm.naughty.base.BaseFragment;
import com.dzenm.naughty.db.DBHelper;
import com.dzenm.naughty.db.model.Table;
import com.dzenm.naughty.ui.MainActivity;
import com.dzenm.naughty.ui.file.sp.SPFragment;
import com.dzenm.naughty.util.ViewUtils;

import java.io.File;
import java.util.List;

public class TableFragment extends BaseFragment<MainActivity> implements
        BaseAdapter.OnItemClickListener<Table> {

    private File data;

    public static TableFragment newInstance(String data) {
        TableFragment fragment = new TableFragment();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_DATA, data);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String path = bundle.getString(BUNDLE_DATA);
            if (path != null) {
                data = new File(path);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        if (data == null) {
            mActivity.finish();
        }
        TableAdapter adapter = new TableAdapter();
        adapter.setOnItemClickListener(this);
        List<Table> tables = DBHelper.getTableFromDB(data.getPath());
        adapter.setData(tables);

        return ViewUtils.createDecorView(
                mActivity, true, adapter, data.getName());
    }

    @Override
    public void onItemClick(Table table, int position) {
        String path = data.getPath();
        DataFragment fragment = DataFragment.newInstance(path, table.getName());
        mActivity.switchFragment(this, fragment);
    }
}
