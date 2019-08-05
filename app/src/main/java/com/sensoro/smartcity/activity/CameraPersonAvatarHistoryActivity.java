package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.DeviceCameraPersonFaceBean;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.PersonAvatarHistoryAdapter;
import com.sensoro.smartcity.imainviews.ICameraPersonAvatarHistoryActivityView;
import com.sensoro.smartcity.presenter.CameraPersonAvatarHistoryActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraPersonAvatarHistoryActivity extends BaseActivity<ICameraPersonAvatarHistoryActivityView, CameraPersonAvatarHistoryActivityPresenter>
        implements ICameraPersonAvatarHistoryActivityView {
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
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    View icNoContent;
    @BindView(R.id.ll_move_locus_ac_camera_person_avatar_history)
    LinearLayout llMoveLocusAcCameraPersonAvatarHistory;
    private PersonAvatarHistoryAdapter rvContentAdapter;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera_person_avatar_history);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        icNoContent = LayoutInflater.from(this).inflate(R.layout.no_content, null);

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        includeImvTitleTvTitle.setText(mActivity.getString(R.string.person_avatar_history));
        includeImvTitleImvSubtitle.setVisibility(View.GONE);

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);

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
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContentAcCameraPersonAvatarHistory.setLayoutManager(manager);
        rvContentAcCameraPersonAvatarHistory.setAdapter(rvContentAdapter);

        rvContentAcCameraPersonAvatarHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        mReturnTopImageView.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            mReturnTopImageView.startAnimation(returnTopAnimation);
                        }
                    } else {
                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
                    mReturnTopImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    @Override
    protected CameraPersonAvatarHistoryActivityPresenter createPresenter() {
        return new CameraPersonAvatarHistoryActivityPresenter();
    }


    @OnClick({R.id.include_imv_title_imv_arrows_left, R.id.ll_move_locus_ac_camera_person_avatar_history, R.id.alarm_return_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_imv_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ll_move_locus_ac_camera_person_avatar_history:
                mPresenter.doPersonLocus();
                break;
            case R.id.alarm_return_top:
                rvContentAcCameraPersonAvatarHistory.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                refreshLayout.closeHeaderOrFooter();
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
    public void updateData(List<DeviceCameraPersonFaceBean> data) {
        if (data != null) {
            rvContentAdapter.updateData(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }

    @SuppressLint("RestrictedApi")
    public void setNoContentVisible(boolean isVisible) {

        RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
        if (refreshHeader != null) {
            refreshHeader.setPrimaryColors(getResources().getColor(R.color.white));
        }

        if (isVisible) {
            refreshLayout.setRefreshContent(icNoContent);
        } else {
            refreshLayout.setRefreshContent(rvContentAcCameraPersonAvatarHistory);
        }

    }

    @Override
    public List<DeviceCameraPersonFaceBean> getAdapterData() {
        return rvContentAdapter.getData();
    }

    @Override
    public void loadTitleAvatar(String faceUrl) {
        if (!TextUtils.isEmpty(faceUrl) && !(faceUrl.startsWith("https://") || faceUrl.startsWith("http://"))) {
            faceUrl = Constants.CAMERA_BASE_URL + faceUrl;
        }
        Glide.with(mActivity).load(faceUrl)
                .apply(new RequestOptions()
//                        .transform(new GlideCircleTransform(mActivity))
                        .circleCrop()
                        .placeholder(R.drawable.person_locus_placeholder)
                        .error(R.drawable.person_locus_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .skipMemoryCache(false)
                        .dontAnimate()
                .into(ivTitleAvatarAcCameraPersonAvatarHistory);
    }
}
