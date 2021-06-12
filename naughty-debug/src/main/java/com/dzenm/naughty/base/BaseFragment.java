package com.dzenm.naughty.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dzenm.naughty.R;

/**
 * @author dzenm
 * 2020/8/4
 */
public abstract class BaseFragment<T extends AppCompatActivity> extends Fragment {

    protected static final String BUNDLE_DATA = "bundle_data";
    protected static final String BUNDLE_FLAG = "bundle_flag";

    protected T mActivity;

    public BaseFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 防止 getActivity() 空指针异常
        mActivity = (T) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return onCreateView(inflater, container);
    }


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }
}
