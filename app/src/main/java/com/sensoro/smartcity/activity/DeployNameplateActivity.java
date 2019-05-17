package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployNameplateActivityView;
import com.sensoro.smartcity.presenter.DeployNameplateActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployNameplateActivity extends BaseActivity<IDeployNameplateActivityView, DeployNameplateActivityPresenter>
        implements IDeployNameplateActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ll_info_ac_deploy_nameplate)
    LinearLayout llInfoAcDeployNameplate;
    @BindView(R.id.tv_name_option_ac_deploy_nameplate)
    TextView tvNameOptionAcDeployNameplate;
    @BindView(R.id.ll_name_ac_deploy_nameplate)
    LinearLayout llNameAcDeployNameplate;
    @BindView(R.id.tv_tag_option_ac_deploy_nameplate)
    TextView tvTagOptionAcDeployNameplate;
    @BindView(R.id.rc_tag_ac_deploy_nameplate)
    TouchRecycleView rcTagAcDeployNameplate;
    @BindView(R.id.ll_tag_ac_deploy_nameplate)
    LinearLayout llTagAcDeployNameplate;
    @BindView(R.id.tv_pic_option_ac_deploy_nameplate)
    TextView tvPicOptionAcDeployNameplate;
    @BindView(R.id.ll_pic_ac_deploy_nameplate)
    LinearLayout llPicAcDeployNameplate;
    @BindView(R.id.ll_content_ac_deploy_nameplate)
    LinearLayout llContentAcDeployNameplate;
    @BindView(R.id.tv_association_sensor_option_ac_deploy_nameplate)
    TextView tvAssociationSensorOptionAcDeployNameplate;
    @BindView(R.id.ll_association_sensor_ac_deploy_nameplate)
    LinearLayout llAssociationSensorAcDeployNameplate;
    @BindView(R.id.tv_upload_ac_deploy_nameplate)
    TextView tvUploadAcDeployNameplate;
    @BindView(R.id.tv_tip_ac_deploy_nameplate)
    TextView tvTipAcDeployNameplate;
    private ProgressUtils mProgressUtils;
    private TagAdapter mTagAdapter;

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

        initRcDeployDeviceTag();

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


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ll_name_ac_deploy_nameplate, R.id.ll_tag_ac_deploy_nameplate, R.id.ll_pic_ac_deploy_nameplate, R.id.ll_association_sensor_ac_deploy_nameplate, R.id.tv_upload_ac_deploy_nameplate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ll_name_ac_deploy_nameplate:
                mPresenter.doName(tvNameOptionAcDeployNameplate.getText().toString());
                break;
            case R.id.ll_tag_ac_deploy_nameplate:
                mPresenter.doTag();
                break;
            case R.id.ll_pic_ac_deploy_nameplate:
                mPresenter.doPic();
                break;
            case R.id.ll_association_sensor_ac_deploy_nameplate:
                mPresenter.doAssociationSensor();
                break;
            case R.id.tv_upload_ac_deploy_nameplate:
                break;
        }
    }

    @Override
    public void setName(String name, int color) {
        tvNameOptionAcDeployNameplate.setText(name);
        tvNameOptionAcDeployNameplate.setTextColor(mActivity.getResources().getColor(color));
    }
}
