package com.sensoro.smartcity.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AddSensorListAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.model.AddSensorFromListModel;
import com.sensoro.smartcity.presenter.DeployNameplateAddSensorFromListActivityPresenter;
import com.sensoro.smartcity.widget.divider.CustomDrawableDivider;

import java.util.ArrayList;

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
    TextView rbSelectAllAcDeployNameplateSensorList;
    @BindView(R.id.tv_selected_count_ac_deploy_nameplate_sensor_list)
    TextView tvSelectedCountAcDeployNameplateSensorList;
    @BindView(R.id.tv_add_ac_deploy_nameplate_sensor_list)
    TextView tvAddAcDeployNameplateSensorList;
    @BindView(R.id.ll_status_ac_deploy_nameplate_sensor_list)
    LinearLayout llStatusAcDeployNameplateSensorList;
    @BindView(R.id.no_content)
    ImageView noContent;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.rv_list_include)
    RecyclerView rvListInclude;
    @BindView(R.id.refreshLayout_include)
    SmartRefreshLayout refreshLayoutInclude;
    @BindView(R.id.return_top_include)
    ImageView returnTopInclude;
    private AddSensorListAdapter mAddSensorListAdapter;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deplaoy_nameplate_add_sensor_from_list);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        returnTopInclude.setAnimation(returnTopAnimation);
        returnTopInclude.setVisibility(View.GONE);

        initSmartRefresh();

        initRv();

    }

    private void initSmartRefresh() {
        refreshLayoutInclude.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayoutInclude.setEnableLoadMore(true);
        refreshLayoutInclude.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.requestWithDirection(Constants.DIRECTION_DOWN);
            }
        });
        refreshLayoutInclude.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.requestWithDirection(Constants.DIRECTION_UP);
            }
        });
    }

    private void initRv() {
        mAddSensorListAdapter = new AddSensorListAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        CustomDrawableDivider customDivider = new CustomDrawableDivider(mActivity, CustomDrawableDivider.VERTICAL);
        rvListInclude.setLayoutManager(manager);
        rvListInclude.addItemDecoration(customDivider);
        rvListInclude.setAdapter(mAddSensorListAdapter);

        mAddSensorListAdapter.setOnSensorListCheckListener(new AddSensorListAdapter.OnSensorListCheckListener() {
            @Override
            public void onChecked(int position) {
                mPresenter.doChecked(position);
            }
        });

        rvListInclude.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        returnTopInclude.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            returnTopInclude.startAnimation(returnTopAnimation);
                        }
                    } else {
                        returnTopInclude.setVisibility(View.GONE);
                    }
                } else {
                    returnTopInclude.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
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


    @OnClick({R.id.iv_arrow_left_ac_deploy_nameplate_sensor_list, R.id.et_search_ac_deploy_nameplate_sensor_list,
            R.id.iv_clear_ac_deploy_nameplate_sensor_list, R.id.ll_search_ac_deploy_nameplate_sensor_list,
            R.id.tv_search_cancel_ac_deploy_nameplate_sensor_list, R.id.rb_select_all_ac_deploy_nameplate_sensor_list,
            R.id.tv_add_ac_deploy_nameplate_sensor_list})
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
                mPresenter.doSelectAll();
                break;
            case R.id.tv_add_ac_deploy_nameplate_sensor_list:
                break;
            case R.id.return_top_include:
                rvListInclude.smoothScrollToPosition(0);
                returnTopInclude.setVisibility(View.GONE);
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

    @Override
    public void updateData(ArrayList<AddSensorFromListModel> mList) {
        mAddSensorListAdapter.updateData(mList);
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayoutInclude.finishRefresh();
        refreshLayoutInclude.finishLoadMore();
    }

    @Override
    public void setCheckedDrawable(Drawable drawable) {
        rbSelectAllAcDeployNameplateSensorList.setCompoundDrawables(drawable,null,null,null);
    }

    @Override
    public void notifyDataAll() {
        mAddSensorListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectSize(String size) {
        tvSelectedCountAcDeployNameplateSensorList.setText(size);
    }

    @Override
    public void setAddStatus(boolean canAdd) {
        tvAddAcDeployNameplateSensorList.setBackgroundColor(mActivity.getResources().getColor(canAdd ? R.color.c_1DBB99 : R.color.c_dfdfdf));
    }
}
