package com.sensoro.smartcity.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.util.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by JL-DDONG on 2017/7/12 0012.
 */

public abstract class BaseFragment<V, P extends BasePresenter<V>> extends Fragment {
    protected P mPrestener;
    protected View mRootView;
    protected Unbinder unbinder;
    protected BaseFragment mRootFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        mPrestener = createPresenter();
        mPrestener.attachView((V) this);
        V view = mPrestener.getView();
        if (view instanceof BaseFragment) {
            mRootFragment = (BaseFragment) view;
        } else {
            LogUtils.loge(this, "当前View转换异常！");
            mRootFragment = this;
        }
        if (mRootView == null) {
            mRootView = inflater.inflate(initRootViewId(), container, false);
        }
        unbinder = ButterKnife.bind(mPrestener.getView(), mRootView);
        LogUtils.logd("onCreateView");
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.logd("onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面埋点
        StatService.onPageStart(getActivity(), this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面埋点
        StatService.onPageEnd(getActivity(), this.getClass().getSimpleName());
    }

    protected abstract void initData(Context activity);

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrestener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.logd("onActivityCreated");
        initData(mRootFragment.getActivity());
    }

    protected abstract int initRootViewId();

    protected abstract P createPresenter();

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        mPrestener.onDestroy();
        mPrestener.detachView();
        super.onDestroyView();
    }
}
