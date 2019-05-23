package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AddSensorListAdapter;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.presenter.DeployNameplateAddSensorFromListActivityPresenter;
import com.sensoro.smartcity.widget.divider.CustomDrawableDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployNameplateAddSensorFromListActivity extends BaseActivity<IDeployNameplateAddSensorFromListActivityView,
        DeployNameplateAddSensorFromListActivityPresenter> implements IDeployNameplateAddSensorFromListActivityView {

    @BindView(R.id.iv_arrow_left_ac_deploy_nameplate_sensor_list)
    ImageView ivArrowLeftAcDeployNameplateSensorList;
    @BindView(R.id.et_search_ac_deploy_nameplate_sensor_list)
    EditText etSearchAcDeployNameplateSensorList;
    @BindView(R.id.iv_clear_ac_deploy_nameplate_sensor_list)
    ImageView ivClearAcDeployNameplateSensorList;
    @BindView(R.id.ll_search_ac_deploy_nameplate_sensor_list)
    LinearLayout llSearchAcDeployNameplateSensorList;
    @BindView(R.id.tv_search_cancel_ac_deploy_nameplate_sensor_list)
    TextView tvSearchCancelAcDeployNameplateSensorList;
    @BindView(R.id.ll_top_search_ac_deploy_nameplate_sensor_list)
    LinearLayout llTopSearchAcDeployNameplateSensorList;
    @BindView(R.id.rb_select_all_ac_deploy_nameplate_sensor_list)
    RadioButton rbSelectAllAcDeployNameplateSensorList;
    @BindView(R.id.tv_selected_count_ac_deploy_nameplate_sensor_list)
    TextView tvSelectedCountAcDeployNameplateSensorList;
    @BindView(R.id.rv_list_ac_deploy_nameplate_sensor_list)
    RecyclerView rvListAcDeployNameplateSensorList;
    @BindView(R.id.tv_add_ac_deploy_nameplate_sensor_list)
    TextView tvAddAcDeployNameplateSensorList;
    private AddSensorListAdapter mAddSensorListAdapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deplaoy_nameplate_add_sensor_from_list);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mAddSensorListAdapter = new AddSensorListAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        CustomDrawableDivider customDivider = new CustomDrawableDivider(mActivity, CustomDrawableDivider.VERTICAL);
        rvListAcDeployNameplateSensorList.setLayoutManager(manager);
        rvListAcDeployNameplateSensorList.addItemDecoration(customDivider);
        rvListAcDeployNameplateSensorList.setAdapter(mAddSensorListAdapter);
    }

    @Override
    protected DeployNameplateAddSensorFromListActivityPresenter createPresenter() {
        return new DeployNameplateAddSensorFromListActivityPresenter();
    }


    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.iv_arrow_left_ac_deploy_nameplate_sensor_list, R.id.et_search_ac_deploy_nameplate_sensor_list, R.id.iv_clear_ac_deploy_nameplate_sensor_list, R.id.ll_search_ac_deploy_nameplate_sensor_list, R.id.tv_search_cancel_ac_deploy_nameplate_sensor_list, R.id.rb_select_all_ac_deploy_nameplate_sensor_list, R.id.tv_add_ac_deploy_nameplate_sensor_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_arrow_left_ac_deploy_nameplate_sensor_list:
                mActivity.finish();
                break;
            case R.id.et_search_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.iv_clear_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.ll_search_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.tv_search_cancel_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.rb_select_all_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.tv_add_ac_deploy_nameplate_sensor_list:
                break;
        }
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
}
