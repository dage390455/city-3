package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.PersonAvatarHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraPersonAvatarHistoryActivityView;
import com.sensoro.smartcity.presenter.CameraPersonAvatarHistoryActivityPresenter;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.smartcity.widget.GlideRoundTransform;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraPersonAvatarHistoryActivity extends BaseActivity<ICameraPersonAvatarHistoryActivityView, CameraPersonAvatarHistoryActivityPresenter>
implements ICameraPersonAvatarHistoryActivityView{
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.iv_title_avatar_ac_camera_person_avatar_history)
    ImageView ivTitleAvatarAcCameraPersonAvatarHistory;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.rv_content_ac_camera_person_avatar_history)
    RecyclerView rvContentAcCameraPersonAvatarHistory;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_move_locus_ac_camera_person_avatar_history)
    LinearLayout llMoveLocusAcCameraPersonAvatarHistory;
    private PersonAvatarHistoryAdapter rvContentAdapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera_person_avatar_history);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        includeImvTitleTvTitle.setText(mActivity.getString(R.string.person_avatar_history));
        includeImvTitleImvSubtitle.setVisibility(View.GONE);

        initRvContent();

        initRefreshLayout();

    }

    private void initRefreshLayout() {
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.doRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.doLoadMore();
            }
        });


    }

    private void initRvContent() {
        rvContentAdapter = new PersonAvatarHistoryAdapter(mActivity);
        rvContentAdapter.setOnRecycleViewItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPresenter.doItemClick(rvContentAdapter.getData().get(position));
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContentAcCameraPersonAvatarHistory.setLayoutManager(manager);
        rvContentAcCameraPersonAvatarHistory.setAdapter(rvContentAdapter);

    }

    @Override
    protected CameraPersonAvatarHistoryActivityPresenter createPresenter() {
        return new CameraPersonAvatarHistoryActivityPresenter();
    }


    @OnClick({R.id.include_imv_title_imv_arrows_left, R.id.ll_move_locus_ac_camera_person_avatar_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_imv_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ll_move_locus_ac_camera_person_avatar_history:
                mPresenter.doPersonLocus();
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

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

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
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void updateData(List<DeviceCameraPersonFaceRsp.DataBean> data) {
        rvContentAdapter.updateData(data);
    }

    @Override
    public List<DeviceCameraPersonFaceRsp.DataBean> getAdapterData() {
        return rvContentAdapter.getData();
    }

    @Override
    public void loadTitleAvatar(String faceUrl) {
        Glide.with(mActivity).load(Constants.CAMERA_BASE_URL+faceUrl)
                .bitmapTransform(new GlideRoundTransform(mActivity))
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivTitleAvatarAcCameraPersonAvatarHistory);
    }
}
