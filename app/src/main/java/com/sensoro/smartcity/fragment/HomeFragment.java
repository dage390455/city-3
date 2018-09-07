package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.presenter.HomeFragmentPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment<IHomeFragmentView, HomeFragmentPresenter> implements
        IHomeFragmentView {
    @BindView(R.id.fg_main_home_tv_title)
    TextView fgMainHomeTvTitle;
    @BindView(R.id.fg_main_home_imb_add)
    ImageButton fgMainHomeImbAdd;
    @BindView(R.id.fg_main_home_imb_search)
    ImageButton fgMainHomeImbSearch;
    @BindView(R.id.fg_main_home_rc_type)
    RecyclerView fgMainHomeRcType;
    @BindView(R.id.fg_main_home_rc_content)
    RecyclerView fgMainHomeRcContent;

    @Override
    protected void initData(Context activity) {
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected HomeFragmentPresenter createPresenter() {
        return new HomeFragmentPresenter();
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

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

    @Override
    public void setRcTypeAdapter(MainHomeFragRcTypeAdapter adapter, LinearLayoutManager manager) {
        fgMainHomeRcType.setLayoutManager(manager);
        fgMainHomeRcType.setAdapter(adapter);
    }

    @Override
    public void setRcContentAdapter(MainHomeFragRcContentAdapter adapter, LinearLayoutManager manager) {
        fgMainHomeRcContent.setLayoutManager(manager);
        fgMainHomeRcContent.setAdapter(adapter);
    }

    @Override
    public void setImvAddVisible(boolean isVisible) {
        fgMainHomeImbAdd.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setImvSearchVisible(boolean isVisible) {
        fgMainHomeImbSearch.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    @OnClick({R.id.fg_main_home_imb_add, R.id.fg_main_home_imb_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_home_imb_add:
                addImbRotate();
                break;
            case R.id.fg_main_home_imb_search:
                break;
        }
    }

    private void addImbRotate() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 135, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fgMainHomeImbAdd.startAnimation(rotateAnimation);
    }

    private void showDialog() {
        fgMainHomeImbAdd.clearAnimation();
        MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
        menuDialogFragment.show(getActivity().getSupportFragmentManager(),"mainMenuDialog");
        setImvAddVisible(false);
        setImvSearchVisible(false);
    }
}
