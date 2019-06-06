package com.sensoro.nameplate.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TouchRecycleView;
import com.sensoro.nameplate.IMainViews.IDeployNameplateActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.presenter.DeployNameplateActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARouterConstants.ACTIVITY_DEPLOY_NAMEPLATE)
public class DeployNameplateActivity extends BaseActivity<IDeployNameplateActivityView, DeployNameplateActivityPresenter>
        implements IDeployNameplateActivityView {
    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R2.id.ll_info_ac_deploy_nameplate)
    LinearLayout llInfoAcDeployNameplate;
    @BindView(R2.id.tv_name_option_ac_deploy_nameplate)
    TextView tvNameOptionAcDeployNameplate;
    @BindView(R2.id.ll_name_ac_deploy_nameplate)
    LinearLayout llNameAcDeployNameplate;
    @BindView(R2.id.tv_tag_option_ac_deploy_nameplate)
    TextView tvTagOptionAcDeployNameplate;
    @BindView(R2.id.rc_tag_ac_deploy_nameplate)
    TouchRecycleView rcTagAcDeployNameplate;
    @BindView(R2.id.ll_tag_ac_deploy_nameplate)
    LinearLayout llTagAcDeployNameplate;
    @BindView(R2.id.tv_pic_option_ac_deploy_nameplate)
    TextView tvPicOptionAcDeployNameplate;
    @BindView(R2.id.ll_pic_ac_deploy_nameplate)
    LinearLayout llPicAcDeployNameplate;
    @BindView(R2.id.ll_content_ac_deploy_nameplate)
    LinearLayout llContentAcDeployNameplate;
    @BindView(R2.id.tv_association_sensor_option_ac_deploy_nameplate)
    TextView tvAssociationSensorOptionAcDeployNameplate;
    @BindView(R2.id.ll_association_sensor_ac_deploy_nameplate)
    LinearLayout llAssociationSensorAcDeployNameplate;
    @BindView(R2.id.tv_upload_ac_deploy_nameplate)
    TextView tvUploadAcDeployNameplate;
    @BindView(R2.id.tv_tip_ac_deploy_nameplate)
    TextView tvTipAcDeployNameplate;
    @BindView(R2.id.tv_nameplate_id_ac_deploy_nameplate)
    TextView tvNameplateIdAcDeployNameplate;
    private ProgressUtils mProgressUtils;
    private TagAdapter mTagAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_nameplate);
        ButterKnife.bind(this);
        initView();

        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.nameplate_deploy));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        initUploadDialog();

        initRcDeployDeviceTag();

    }

    private void initUploadDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    private void initRcDeployDeviceTag() {
        rcTagAcDeployNameplate.setIntercept(true);
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        //
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        rcTagAcDeployNameplate.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        rcTagAcDeployNameplate.setLayoutManager(layoutManager);
        rcTagAcDeployNameplate.setAdapter(mTagAdapter);
    }

    @Override
    protected DeployNameplateActivityPresenter createPresenter() {
        return new DeployNameplateActivityPresenter();
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        if (tagList.size() > 0) {
            tvTagOptionAcDeployNameplate.setVisibility(View.GONE);
            rcTagAcDeployNameplate.setVisibility(View.VISIBLE);
            mTagAdapter.updateTags(tagList);
        } else {
            tvTagOptionAcDeployNameplate.setVisibility(View.VISIBLE);
            rcTagAcDeployNameplate.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAssociateSensorSize(int size) {
        if (size == 0) {
            tvAssociationSensorOptionAcDeployNameplate.setText(mActivity.getString(R.string.optional));
            tvAssociationSensorOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
        }else{
            tvAssociationSensorOptionAcDeployNameplate.setText(mActivity.getString(R.string.selected_device_size)+size);
            tvAssociationSensorOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
        }

    }


    @Override
    public void setDeployPhotoTextSize(int size) {
        if(size > 0){
            StringBuilder sb = new StringBuilder();
            sb.append(mActivity.getString(R.string.added)).append(size).append(mActivity.getString(R.string.images));
            tvPicOptionAcDeployNameplate.setText(sb.toString());
            tvPicOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
        }else{
            tvPicOptionAcDeployNameplate.setText(mActivity.getString(R.string.optional));
            tvPicOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
        }
    }

    @Override
    public void setUploadStatus(boolean isUpload) {
        tvUploadAcDeployNameplate.setBackgroundResource(isUpload ? R.drawable.shape_bg_corner_4_29c_shadow : R.drawable.shape_bg_corner_4_dfdf_shadow);
    }

    @Override
    public void showStartUploadProgressDialog() {
        progressDialog.setTitle(mActivity.getString(R.string.please_wait));
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    public void dismissUploadProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showUploadProgressDialog(String content, double percent) {
        if (progressDialog != null) {
//            String title = "正在上传第" + currentNum + "张，总共" + count + "张";
            progressDialog.setProgress((int) (percent * 100));
            progressDialog.setTitle(content);
            progressDialog.show();
        }
    }

    @Override
    public void setNameplateId(String mNameplateId) {
        tvNameplateIdAcDeployNameplate.setText(mNameplateId);
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
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
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.ll_name_ac_deploy_nameplate, R2.id.ll_tag_ac_deploy_nameplate, R2.id.ll_pic_ac_deploy_nameplate, R2.id.ll_association_sensor_ac_deploy_nameplate, R2.id.tv_upload_ac_deploy_nameplate})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.include_text_title_imv_arrows_left) {
            finishAc();
        }else if (id == R.id.ll_name_ac_deploy_nameplate) {
            mPresenter.doName(tvNameOptionAcDeployNameplate.getText().toString());

        }else if (id == R.id.ll_tag_ac_deploy_nameplate) {
            mPresenter.doTag();

        }else if (id == R.id.ll_pic_ac_deploy_nameplate) {
            mPresenter.doPic();

        }else if (id == R.id.ll_association_sensor_ac_deploy_nameplate) {
            mPresenter.doAssociationSensor();

        }else if (id == R.id.tv_upload_ac_deploy_nameplate) {
            mPresenter.doUpload();
        }

    }

    @Override
    public void setName(String name, int color) {
        tvNameOptionAcDeployNameplate.setText(name);
        tvNameOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(color));
    }
}
