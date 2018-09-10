package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployPicRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployPicActivityView;
import com.sensoro.smartcity.presenter.DeployPicActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployPicActivity extends BaseActivity<IDeployPicActivityView, DeployPicActivityPresenter>
implements IDeployPicActivityView{
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_deploy_pic_save)
    TextView acDeployPicSave;
    @BindView(R.id.ac_deploy_pic_rc_content)
    RecyclerView acDeployPicRcContent;
    private DeployPicRcContentAdapter mContentAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_pic);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText("部署图片");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        mContentAdapter = new DeployPicRcContentAdapter(mActivity);
        GridLayoutManager manager = new GridLayoutManager(mActivity, 5);
        acDeployPicRcContent.setLayoutManager(manager);
        acDeployPicRcContent.setAdapter(mContentAdapter);
    }

    @Override
    protected DeployPicActivityPresenter createPresenter() {
        return new DeployPicActivityPresenter();
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
}
