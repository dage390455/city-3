package com.sensoro.city_camera.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.city_camera.IMainViews.ICameraListFragmentView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.SecurityWarnDetailActivity;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.presenter.CameraListFragmentPresenter;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.constant.ARouterConstants;

/**
 * @author bin.tian
 */
@Route(path = ARouterConstants.FRAGMENT_CAMERA_LIST)
public class CameraListFragment extends BaseFragment<ICameraListFragmentView, CameraListFragmentPresenter> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void initData(Context activity) {

    }

    @Override
    protected int initRootViewId() {
        return 0;
    }

    @Override
    protected CameraListFragmentPresenter createPresenter() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SecurityWarnConfirmDialog().show(getChildFragmentManager());
                startActivity(new Intent(getContext(), SecurityWarnDetailActivity.class));
            }
        });
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }
}
