package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AddedSensorAdapter;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorActivityView;
import com.sensoro.smartcity.presenter.DeployNameplateAddSensorActivityPresenter;
import com.sensoro.smartcity.widget.divider.CustomDrawableDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployNameplateAddSensorActivity extends BaseActivity<IDeployNameplateAddSensorActivityView,
        DeployNameplateAddSensorActivityPresenter> implements IDeployNameplateAddSensorActivityView{
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ll_from_List_ac_deploy_nameplate_add_sensor)
    LinearLayout llFromListAcDeployNameplateAddSensor;
    @BindView(R.id.ll_from_scan_ac_deploy_nameplate_add_sensor)
    LinearLayout llFromScanAcDeployNameplateAddSensor;
    @BindView(R.id.tv_added_count_ac_deploy_nameplate_add_sensor)
    TextView tvAddedCountAcDeployNameplateAddSensor;
    @BindView(R.id.rv_added_list_ac_deploy_nameplate_add_sensor)
    RecyclerView rvAddedListAcDeployNameplateAddSensor;
    private AddedSensorAdapter mAddedSensorAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deplaoy_nameplate_add_sensor);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText(R.string.add_sensor);
        includeTextTitleTvSubtitle.setText(R.string.save);
        includeTextTitleTvSubtitle.setTextColor(mActivity.getResources().getColor(R.color.c_1dbb99));

        initRvAddedSensorList();
    }

    private void initRvAddedSensorList() {
        mAddedSensorAdapter = new AddedSensorAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        CustomDrawableDivider bottomNoDividerItemDecoration =
                new CustomDrawableDivider(mActivity, CustomDrawableDivider.VERTICAL);
        rvAddedListAcDeployNameplateAddSensor.addItemDecoration(bottomNoDividerItemDecoration);
        rvAddedListAcDeployNameplateAddSensor.setLayoutManager(manager);
        rvAddedListAcDeployNameplateAddSensor.setAdapter(mAddedSensorAdapter);

        mAddedSensorAdapter.setOnDeleteClickListener(new AddedSensorAdapter.onDeleteClickListenre() {
            @Override
            public void onDeleteClick(int position) {
                toastShort("点击了");
            }
        });

    }

    @Override
    protected DeployNameplateAddSensorActivityPresenter createPresenter() {
        return new DeployNameplateAddSensorActivityPresenter();
    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle, R.id.ll_from_List_ac_deploy_nameplate_add_sensor, R.id.ll_from_scan_ac_deploy_nameplate_add_sensor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                break;
            case R.id.ll_from_List_ac_deploy_nameplate_add_sensor:
                mPresenter.doAddFromList();
                break;
            case R.id.ll_from_scan_ac_deploy_nameplate_add_sensor:
                break;
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
        mActivity.startActivityForResult(intent,requestCode);
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
}
