package com.sensoro.smartcity.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployPhotoView;
import com.sensoro.smartcity.presenter.DeployPhotoActivityPresenter;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployPhotoActivity extends BaseActivity<IDeployPhotoView, DeployPhotoActivityPresenter> implements
        IDeployPhotoView, ImagePickerAdapter.OnRecyclerViewItemClickListener {
    @BindView(R.id.rv_deploy_photo)
    RecyclerView rvDeployPhoto;
    private ImagePickerAdapter adapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_setting_photo);
        ButterKnife.bind(mActivity);
        initView();
        mPrestener.initData(mActivity);
    }

    private void initView() {
        adapter = new ImagePickerAdapter(mActivity, mPrestener.getSelImageList(), 4);
        adapter.setOnItemClickListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);
        rvDeployPhoto.setLayoutManager(layoutManager);
        rvDeployPhoto.setHasFixedSize(true);
        rvDeployPhoto.setAdapter(adapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        rvDeployPhoto.setNestedScrollingEnabled(false);
    }

    @Override
    public void showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
    }

    @Override
    protected DeployPhotoActivityPresenter createPresenter() {
        return new DeployPhotoActivityPresenter();
    }


    @OnClick({R.id.deploy_setting_photo_back, R.id.deploy_setting_photo_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deploy_setting_photo_back:
                finishAc();
                break;
            case R.id.deploy_setting_photo_finish:
                mPrestener.doFinish();
                break;
        }
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
        mActivity.startActivityForResult(intent,requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPrestener.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        List<ImageItem> images = adapter.getImages();
        mPrestener.clickItem(id, position,images);
    }

    @Override
    public void updateImageList(ArrayList<ImageItem> imageList) {
        adapter.setImages(imageList);
    }
}
