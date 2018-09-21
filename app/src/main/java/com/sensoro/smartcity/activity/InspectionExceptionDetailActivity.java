package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionExceptionThumbnailAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionExceptionDetailActivityView;
import com.sensoro.smartcity.presenter.InspectionExceptionDetailActivityPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionExceptionDetailActivity extends BaseActivity<IInspectionExceptionDetailActivityView,
        InspectionExceptionDetailActivityPresenter> implements IInspectionExceptionDetailActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_inspection_exception_detail_tv_name)
    TextView acInspectionExceptionDetailTvName;
    @BindView(R.id.ac_inspection_exception_detail_tv_sn)
    TextView acInspectionExceptionDetailTvSn;
    @BindView(R.id.ac_inspection_exception_detail_rc_tag)
    RecyclerView acInspectionExceptionDetailRcTag;
    @BindView(R.id.ac_inspection_exception_detail_tv_state)
    TextView acInspectionExceptionDetailTvState;
    @BindView(R.id.ac_inspection_exception_detail_rc_exception_tag)
    RecyclerView acInspectionExceptionDetailRcExceptionTag;
    @BindView(R.id.ac_inspection_exception_detail_tv_remark)
    TextView acInspectionExceptionDetailTvRemark;
    @BindView(R.id.ac_inspection_exception_detail_rc_photo)
    RecyclerView acInspectionExceptionDetailRcPhoto;
    @BindView(R.id.ac_inspection_exception_detail_rc_camera)
    RecyclerView acInspectionExceptionDetailRcCamera;
    private TagAdapter mTagAdapter;
    private TagAdapter mExceptionTagAdapter;
    private InspectionExceptionThumbnailAdapter mPhotoAdapter;
    private InspectionExceptionThumbnailAdapter mCameraAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_exception_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText("监测点异常详情");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        acInspectionExceptionDetailTvName.setText("小刘是个鬼啊");
        acInspectionExceptionDetailTvSn.setText("小刘的温度 97987");

        acInspectionExceptionDetailTvRemark.setText("在她生命垂危之际，“众星之子”索拉卡试图将她那灵魂从消散的边缘稳定下来。" +
                "在不愿放弃家园的强大意志支撑下，艾瑞莉娅苏醒了过来。就在此时，她父亲的传世神兵竟凌空悬立于她身旁。艾瑞莉娅对传世神兵的异举并不为奇，转身投入战场。" +
                "传世神兵环绕着她轻灵起舞，在诺克萨斯人的惊恐中将他们一一斩杀。伤亡惨重的入侵者被迫从普雷希典撤军。经此一役，艾瑞莉娅被任命为艾欧尼亚护卫队长");

       initRcTag();

       initRcExceptionTag();

       initRcPhoto();

       initRcCamera();
    }

    private void initRcCamera() {
        mCameraAdapter = new InspectionExceptionThumbnailAdapter(mActivity);
        GridLayoutManager manager = new GridLayoutManager(mActivity, 3);
        acInspectionExceptionDetailRcPhoto.setLayoutManager(manager);
        acInspectionExceptionDetailRcPhoto.setAdapter(mCameraAdapter);
    }

    private void initRcPhoto() {
        mPhotoAdapter = new InspectionExceptionThumbnailAdapter(mActivity);
        GridLayoutManager manager = new GridLayoutManager(mActivity, 3);
        acInspectionExceptionDetailRcCamera.setLayoutManager(manager);
        acInspectionExceptionDetailRcCamera.setAdapter(mPhotoAdapter);

    }

    private void initRcExceptionTag() {
        mExceptionTagAdapter = new TagAdapter(mActivity,R.color.c_ff8d34);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionExceptionDetailRcExceptionTag.setLayoutManager(manager);
        acInspectionExceptionDetailRcExceptionTag.setAdapter(mExceptionTagAdapter);
    }

    private void initRcTag() {
        mTagAdapter = new TagAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionExceptionDetailRcTag.setLayoutManager(manager);
        acInspectionExceptionDetailRcTag.setAdapter(mTagAdapter);
    }

    @Override
    protected InspectionExceptionDetailActivityPresenter createPresenter() {
        return new InspectionExceptionDetailActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
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


    @OnClick({R.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
        }
    }

    @Override
    public void updateTagsData(ArrayList<String> list) {
        mTagAdapter.updateTags(list);
    }

    @Override
    public void updateExceptionTagsData(ArrayList<String> list) {
        mExceptionTagAdapter.updateTags(list);
    }
}
