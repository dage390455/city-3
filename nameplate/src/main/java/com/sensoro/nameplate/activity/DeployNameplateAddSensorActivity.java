package com.sensoro.nameplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.nameplate.IMainViews.IDeployNameplateAddSensorActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.adapter.AddedSensorAdapter;
import com.sensoro.nameplate.presenter.DeployNameplateAddSensorActivityPresenter;
import com.sensoro.nameplate.widget.CustomDrawableDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DeployNameplateAddSensorActivity extends BaseActivity<IDeployNameplateAddSensorActivityView,
        DeployNameplateAddSensorActivityPresenter> implements IDeployNameplateAddSensorActivityView {
    @BindView(R2.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R2.id.ll_from_List_ac_deploy_nameplate_add_sensor)
    LinearLayout llFromListAcDeployNameplateAddSensor;
    @BindView(R2.id.ll_from_scan_ac_deploy_nameplate_add_sensor)
    LinearLayout llFromScanAcDeployNameplateAddSensor;
    @BindView(R2.id.tv_added_count_ac_deploy_nameplate_add_sensor)
    TextView tvAddedCountAcDeployNameplateAddSensor;
    @BindView(R2.id.rv_list_include)
    RecyclerView rvAddedListAcDeployNameplateAddSensor;
    @BindView(R2.id.no_content)
    ImageView noContent;
    @BindView(R2.id.no_content_tip)
    TextView noContentTip;
    @BindView(R2.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R2.id.refreshLayout_include)
    SmartRefreshLayout refreshLayoutInclude;
    @BindView(R2.id.return_top_include)
    ImageView returnTopInclude;
    private AddedSensorAdapter mAddedSensorAdapter;
    private Animation returnTopAnimation;

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

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        returnTopInclude.setAnimation(returnTopAnimation);
        returnTopInclude.setVisibility(GONE);

        initSmartRefresh();

        initRvAddedSensorList();
    }

    private void initSmartRefresh() {
        refreshLayoutInclude.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayoutInclude.setEnableLoadMore(true);
        refreshLayoutInclude.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
//                mPresenter.doRefresh();
            }
        });
        refreshLayoutInclude.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                mPresenter.doLoadMore();
            }
        });

    }

    private void initRvAddedSensorList() {
        mAddedSensorAdapter = new AddedSensorAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
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

        rvAddedListAcDeployNameplateAddSensor.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//

                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        returnTopInclude.setVisibility(VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            returnTopInclude.startAnimation(returnTopAnimation);
                        }
                    } else {
                        returnTopInclude.setVisibility(GONE);
                    }
                } else {
                    returnTopInclude.setVisibility(GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }

    @Override
    protected DeployNameplateAddSensorActivityPresenter createPresenter() {
        return new DeployNameplateAddSensorActivityPresenter();
    }


    @OnClick({R2.id.include_text_title_tv_cancel, R2.id.include_text_title_tv_subtitle,
            R2.id.ll_from_List_ac_deploy_nameplate_add_sensor, R2.id.ll_from_scan_ac_deploy_nameplate_add_sensor,
    R2.id.return_top_include})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.include_text_title_tv_cancel) {
            finishAc();
        } else if (id == R.id.include_text_title_tv_subtitle) {

        } else if (id == R.id.ll_from_List_ac_deploy_nameplate_add_sensor) {
            mPresenter.doAddFromList();
        } else if (id == R.id.ll_from_scan_ac_deploy_nameplate_add_sensor) {

        }else if (id == R.id.return_top_include) {
            rvAddedListAcDeployNameplateAddSensor.smoothScrollToPosition(0);
            returnTopInclude.setVisibility(GONE);
            refreshLayoutInclude.closeHeaderOrFooter();
        }
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayoutInclude.finishRefresh();
        refreshLayoutInclude.finishLoadMore();
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

}
