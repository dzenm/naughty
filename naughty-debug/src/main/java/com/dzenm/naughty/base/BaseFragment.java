package com.dzenm.naughty.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dzenm.naughty.ui.MainModelActivity;

/**
 * @author dzenm
 * 2020/8/4
 */
public class BaseFragment extends Fragment {

    protected MainModelActivity mActivity;

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainModelActivity) getActivity();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = onCreateView(inflater, container);
        initView();
        return view;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }

    public void initView() {

    }
}
