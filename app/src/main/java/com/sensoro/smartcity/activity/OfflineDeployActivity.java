package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.OfflineDeployAdapter;
import com.sensoro.smartcity.imainviews.IOfflineDeployActivityView;
import com.sensoro.smartcity.presenter.OfflineDeployPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfflineDeployActivity extends BaseActivity<IOfflineDeployActivityView, OfflineDeployPresenter> implements IOfflineDeployActivityView {


    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    View icNoContent;
    @BindView(R.id.rc_content)
    RecyclerView rcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    OfflineDeployAdapter adapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_offline_deploy_list);

        ButterKnife.bind(this);
        icNoContent = LayoutInflater.from(this).inflate(R.layout.no_content, null);

        includeTextTitleTvTitle.setText("离线上传");
        includeTextTitleTvSubtitle.setText("");
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());


        adapter = new OfflineDeployAdapter(mActivity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcContent.setLayoutManager(linearLayoutManager);

        rcContent.setAdapter(adapter);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
            }
        });
        adapter.setOnContentClickListener(new OfflineDeployAdapter.OnContentItemClickListener() {

            @Override
            public void onUploadClick(View v, int position) {
                DeployAnalyzerModel deployAnalyzerModel = adapter.getData().get(position);

                mPresenter.uploadTask(deployAnalyzerModel, false);

            }

            @Override
            public void onForceUploadClick(View view, int position) {


                mPresenter.doForceUpload(position);
            }

            @Override
            public void onClearClick(View view, int position) {
                DeployAnalyzerModel deployAnalyzerModel = adapter.getData().get(position);
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
                mPresenter.removeTask(deployAnalyzerModel);
            }

            @Override
            public void onItemClick(View v, int position) {
                DeployAnalyzerModel deployAnalyzerModel = adapter.getData().get(position);
                mPresenter.doOfflineTaskDetail(deployAnalyzerModel);
            }
        });
        mPresenter.initData(this);


    }

    @Override
    protected OfflineDeployPresenter createPresenter() {
        return new OfflineDeployPresenter();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.offline_deploy_batch_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;

            case R.id.offline_deploy_batch_tv:

                mPresenter.doBatch();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();

    }

    @Override
    public void dismissProgressDialog() {

        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateAdapter(ArrayList<DeployAnalyzerModel> deviceInfos) {
        if (deviceInfos != null && deviceInfos.size() > 0) {

            adapter.updateData(deviceInfos);
        }
        setNoContentVisible(deviceInfos == null || deviceInfos.size() < 1);

    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();

    }

    @SuppressLint("RestrictedApi")
    public void setNoContentVisible(boolean isVisible) {
        if (isVisible) {
            refreshLayout.setRefreshContent(icNoContent);
        } else {
            refreshLayout.setRefreshContent(rcContent);
        }
    }


    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    @Override
    public void setCurrentTaskIndex(int index) {


        adapter.setCurrentTaskIndex(index);
    }

    @Override
    public void setUploadClickable(boolean canClick) {

        adapter.setUploadClickable(canClick);

    }


}
