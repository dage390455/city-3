package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployPicAdapter;
import com.sensoro.smartcity.server.bean.DeployPicInfo;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorDeployPicView;
import com.sensoro.smartcity.presenter.DeployMonitorDeployPicPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.dialog.DeployPicExampleDialogUtils;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorDeployPicActivity extends BaseActivity<IDeployMonitorDeployPicView, DeployMonitorDeployPicPresenter>
        implements IDeployMonitorDeployPicView {
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_deploy_pic_tv_device_pic)
    TextView acDeployPicTvDevicePic;
    @BindView(R.id.ac_deploy_pic_tv_device_pic_tip)
    TextView acDeployPicTvDevicePicTip;
    @BindView(R.id.tv_add_content)
    TextView tvAddContent;
    @BindView(R.id.ac_deploy_pic_ll_add_device_pic)
    LinearLayout acDeployPicLlAddDevicePic;
    @BindView(R.id.ac_deploy_pic_imv_device_pic)
    ImageView acDeployPicImvDevicePic;
    @BindView(R.id.ac_deploy_pic_imv_device_pic_delete)
    ImageView acDeployPicImvDevicePicDelete;
    @BindView(R.id.ac_deploy_pic_rl_device_pic)
    RelativeLayout acDeployPicRlDevicePic;
    @BindView(R.id.ac_deploy_pic_tv_installation_site)
    TextView acDeployPicTvInstallationSite;
    @BindView(R.id.ac_deploy_pic_ll_installation_site)
    LinearLayout acDeployPicLlInstallationSite;
    @BindView(R.id.ac_deploy_pic_imv_installation_site_pic)
    ImageView acDeployPicImvInstallationSitePic;
    @BindView(R.id.ac_deploy_pic_imv_installation_site_pic_delete)
    ImageView acDeployPicImvInstallationSitePicDelete;
    @BindView(R.id.ac_deploy_pic_rl_installation_site_pic)
    RelativeLayout acDeployPicRlInstallationSitePic;
    @BindView(R.id.ac_deploy_pic_tv_shop_pic)
    TextView acDeployPicTvShopPic;
    @BindView(R.id.ac_deploy_pic_tv_shop_pic_tip)
    TextView acDeployPicTvShopPicTip;
    @BindView(R.id.ac_deploy_pic_ll_shop_pic)
    LinearLayout acDeployPicLlShopPic;
    @BindView(R.id.ac_deploy_pic_imv_shop_pic)
    ImageView acDeployPicImvShopPic;
    @BindView(R.id.ac_deploy_pic_imv_shop_pic_delete)
    ImageView acDeployPicImvShopPicDelete;
    @BindView(R.id.ac_deploy_pic_rl_shop_pic)
    RelativeLayout acDeployPicRlShopPic;
    @BindView(R.id.ac_deploy_pic_tv_installation_site_tip)
    TextView acDeployPicTvInstallationSiteTip;
    @BindView(R.id.ac_deploy_pic_rc)
    RecyclerView acDeployPicRc;
    private DeployPicAdapter mDeployPicAdapter;
    private DeployPicExampleDialogUtils mPicExampleDialogUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_pic);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mPicExampleDialogUtils = new DeployPicExampleDialogUtils(mActivity);
        mPicExampleDialogUtils.setDeployPicExampleClickListener(mPresenter);
        initTitle();
        initRC();
    }

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.deploy_photo);
        includeTextTitleTvCancel.setVisibility(View.VISIBLE);
        includeTextTitleTvCancel.setTextColor(getResources().getColor(R.color.c_b6b6b6));
        includeTextTitleTvCancel.setText(R.string.cancel);
        includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
        includeTextTitleTvSubtitle.setText(getString(R.string.save));
        updateSaveStatus(true);
    }

    @Override
    public void updateSaveStatus(boolean isEnable) {
        includeTextTitleTvSubtitle.setEnabled(isEnable);
        includeTextTitleTvSubtitle.setTextColor(isEnable ? getResources().getColor(R.color.c_29c093) : getResources().getColor(R.color.c_dfdfdf));

    }

    private void initRC() {
        mDeployPicAdapter = new DeployPicAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acDeployPicRc.setLayoutManager(manager);
        acDeployPicRc.setAdapter(mDeployPicAdapter);

        mDeployPicAdapter.setDeployPicClickListener(new DeployPicAdapter.DeployPicClickListener() {
            @Override
            public void onTakePhotoClick(int position) {
//                mPresenter.doAddPic(position);
                mPresenter.doTakePhoto(position);
            }

            @Override
            public void onDeletePhotoClick(int position) {
                mPresenter.deletePic(position);
            }

            @Override
            public void onPreviewPhoto(int position) {
                mPresenter.doPreviewPic(position);
            }
        });
    }

    @Override
    protected DeployMonitorDeployPicPresenter createPresenter() {
        return new DeployMonitorDeployPicPresenter();
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
        mActivity.startActivityForResult(intent, requestCode);
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.ac_deploy_pic_ll_add_device_pic,
            R.id.ac_deploy_pic_imv_device_pic, R.id.ac_deploy_pic_imv_device_pic_delete, R.id.ac_deploy_pic_ll_installation_site,
            R.id.ac_deploy_pic_imv_installation_site_pic, R.id.ac_deploy_pic_imv_installation_site_pic_delete, R.id.ac_deploy_pic_ll_shop_pic,
            R.id.ac_deploy_pic_imv_shop_pic, R.id.ac_deploy_pic_imv_shop_pic_delete, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                finishAc();
                break;
            case R.id.ac_deploy_pic_ll_add_device_pic:
                mPresenter.doAddPic(0);
                break;
            case R.id.ac_deploy_pic_imv_device_pic:
                mPresenter.doPreviewPic(0);
                break;
            case R.id.ac_deploy_pic_imv_device_pic_delete:
                mPresenter.deletePic(0);
                acDeployPicRlDevicePic.setVisibility(View.GONE);
                acDeployPicImvDevicePic.setImageDrawable(null);
                acDeployPicLlAddDevicePic.setVisibility(View.VISIBLE);
                break;
            case R.id.ac_deploy_pic_ll_installation_site:
                mPresenter.doAddPic(1);
                break;
            case R.id.ac_deploy_pic_imv_installation_site_pic:
                mPresenter.doPreviewPic(1);
                break;
            case R.id.ac_deploy_pic_imv_installation_site_pic_delete:
                mPresenter.deletePic(1);
                acDeployPicRlInstallationSitePic.setVisibility(View.GONE);
                acDeployPicImvInstallationSitePic.setImageDrawable(null);
                acDeployPicLlInstallationSite.setVisibility(View.VISIBLE);
                break;
            case R.id.ac_deploy_pic_ll_shop_pic:
                mPresenter.doAddPic(2);
                break;
            case R.id.ac_deploy_pic_imv_shop_pic:
                mPresenter.doPreviewPic(2);
                break;
            case R.id.ac_deploy_pic_imv_shop_pic_delete:
                mPresenter.deletePic(2);
                acDeployPicRlShopPic.setVisibility(View.GONE);
                acDeployPicImvShopPic.setImageDrawable(null);
                acDeployPicLlShopPic.setVisibility(View.VISIBLE);
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doSave();
                break;
        }
    }

    @Override
    public void showSelectDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
    }



    @Override
    public void displayPic(ImageItem[] selImages, int index) {
        DrawableRequestBuilder<String> builder = Glide.with((Activity) mActivity)                             //配置上下文
                .load(selImages[index].path)
                .error(R.drawable.ic_default_image)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)//设置占位图片
                .thumbnail(0.01f)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        switch (index) {
            case 0:
                acDeployPicLlAddDevicePic.setVisibility(View.GONE);
                acDeployPicRlDevicePic.setVisibility(View.VISIBLE);
                builder.into(acDeployPicImvDevicePic);
                break;
            case 1:
                acDeployPicLlInstallationSite.setVisibility(View.GONE);
                acDeployPicRlInstallationSitePic.setVisibility(View.VISIBLE);
                builder.into(acDeployPicImvInstallationSitePic);
                break;
            case 2:
                acDeployPicLlShopPic.setVisibility(View.GONE);
                acDeployPicRlShopPic.setVisibility(View.VISIBLE);
                builder.into(acDeployPicImvShopPic);
                break;
        }


    }

    @Override
    protected void onDestroy() {
        mPicExampleDialogUtils.destroy();
        super.onDestroy();

    }


    @Override
    public void setDeployPicTvInstallationSiteTipVisible(boolean isVisible) {
        acDeployPicTvInstallationSiteTip.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateData(List<DeployPicInfo> data) {
        mDeployPicAdapter.updateData(data);
    }

    @Override
    public DeployPicInfo getDeployPicItem(int position) {
        return mDeployPicAdapter.getItem(position);
    }

    @Override
    public void showDeployPicExampleDialog(DeployPicInfo item, int position) {
        mPicExampleDialogUtils.show(item.imgUrl,item.title,item.description,position);
    }

    @Override
    public void dismissDeployPicExampleDialog() {
        mPicExampleDialogUtils.dismiss();
    }

    @Override
    public List<DeployPicInfo> getDeployPicData() {
        return mDeployPicAdapter.getData();
    }

    @Override
    public void updateIndexData(ImageItem imageItem, int mAddPicIndex) {
        mDeployPicAdapter.updateIndexData(imageItem,mAddPicIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleActivityResult(requestCode, resultCode, data);
    }
}
