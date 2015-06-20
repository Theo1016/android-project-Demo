package com.theo.demo.ui.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theo.demo.R;
import com.theo.sdk.ui.activity.fragment.SDKBaseFragment;

/**
 * Created by Theo on 15/6/19.
 */
public class OneFragment extends SDKBaseFragment{

    /**
     *
     *该fragment的content容器
     */
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one,null);
        return view;
    }
}
