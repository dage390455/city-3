package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.presenter.MalfunctionFragmentPresenter;

public class MalfunctionFragment extends BaseFragment<IMalfunctionFragmentView,MalfunctionFragmentPresenter>
implements IMalfunctionFragmentView{
    @Override
    protected void initData(Context activity) {

    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_malfunction;
    }

    @Override
    protected MalfunctionFragmentPresenter createPresenter() {
        return new MalfunctionFragmentPresenter();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }
}
