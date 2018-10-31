package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployRecordContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployRecordActivityView;
import com.sensoro.smartcity.presenter.DeployRecordActivityPresenter;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class DeployRecordActivity extends BaseActivity<IDeployRecordActivityView, DeployRecordActivityPresenter> implements
        IDeployRecordActivityView {

    @BindView(R.id.ac_deploy_record_imv_finish)
    ImageView acDeployRecordImvFinish;
    @BindView(R.id.ac_deploy_record_et_search)
    EditText acDeployRecordEtSearch;
    @BindView(R.id.ac_deploy_record_frame_search)
    FrameLayout acDeployRecordFrameSearch;
    @BindView(R.id.tv_warn_alarm_search_cancel)
    TextView tvWarnAlarmSearchCancel;
    @BindView(R.id.ac_deploy_record_imv_calendar)
    ImageView acDeployRecordImvCalendar;
    @BindView(R.id.ac_deploy_record_tv_date_edit)
    TextView acDeployRecordTvDateEdit;
    @BindView(R.id.ac_deploy_record_imv_date_close)
    ImageView acDeployRecordImvDateClose;
    @BindView(R.id.ac_deploy_record_rl_date_edit)
    RelativeLayout acDeployRecordRlDateEdit;
    @BindView(R.id.ac_deploy_record_title_root)
    LinearLayout acDeployRecordTitleRoot;
    @BindView(R.id.ac_deploy_record_deploy_rl_new_device)
    RelativeLayout acDeployRecordDeployRlNewDevice;
    @BindView(R.id.ac_deploy_record_rc_content)
    RecyclerView acDeployRecordRcContent;
    @BindView(R.id.no_content)
    ImageView noContent;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    private DeployRecordContentAdapter mContentAdapter;
    private Animation returnTopAnimation;
    private ProgressUtils mProgressDialog;
    private boolean isShowDialog = true;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_record);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressDialog = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        acDeployRecordEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = getSearchText();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mActivity,acDeployRecordEtSearch);
                    return true;
                }
                return false;
            }
        });
        initRcContent();

        initRefreshLayout();
    }

    private void initRefreshLayout() {
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = getSearchText();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = getSearchText();
                mPresenter.requestSearchData(DIRECTION_UP, text);
            }
        });
        //

    }

    private void initRcContent() {
        mContentAdapter = new DeployRecordContentAdapter(mActivity);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        acDeployRecordRcContent.addItemDecoration(dividerItemDecoration);
        acDeployRecordRcContent.setLayoutManager(manager);
        acDeployRecordRcContent.setAdapter(mContentAdapter);

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);

        mContentAdapter.setOnClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPresenter.doRecorDetail(mContentAdapter.getItem(position));
            }
        });
        acDeployRecordRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        mReturnTopImageView.setAnimation(returnTopAnimation);
                        if (returnTopAnimation.hasEnded()) {
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
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.destroyProgress();
        }
    }

    @Override
    protected DeployRecordActivityPresenter createPresenter() {
        return new DeployRecordActivityPresenter();
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
        if(isShowDialog){
            mProgressDialog.showProgress();
        }
        isShowDialog = true;
    }

    @Override
    public void dismissProgressDialog() {
        mProgressDialog.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg,Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.ac_deploy_record_imv_finish, R.id.ac_deploy_record_imv_calendar, R.id.ac_deploy_record_deploy_rl_new_device
    ,R.id.alarm_return_top,R.id.ac_deploy_record_imv_date_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_deploy_record_imv_finish:
                finishAc();
                break;
            case R.id.ac_deploy_record_imv_calendar:
                mPresenter.doCalendar(acDeployRecordTitleRoot);
                break;
            case R.id.ac_deploy_record_deploy_rl_new_device:
                mPresenter.doDeployNewDevice();
                break;
            case R.id.alarm_return_top:
                acDeployRecordRcContent.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                break;
            case R.id.ac_deploy_record_imv_date_close:
                setSelectedDateLayoutVisible(false);
                mPresenter.requestSearchData(DIRECTION_DOWN,getSearchText());
                break;
        }
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return acDeployRecordRlDateEdit.getVisibility() == View.VISIBLE;
    }

    @Override
    public void updateRcContentData(List<DeployRecordInfo> data) {
        if (data.size() > 0) {
            mContentAdapter.setData(data);
            mContentAdapter.notifyDataSetChanged();
        }
        setNoContentVisible(data.size()<1);
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean isVisible) {
        acDeployRecordRlDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelectedDateSearchText(String s) {
        acDeployRecordTvDateEdit.setText(s);
    }

    @Override
    public String getSearchText() {
        return acDeployRecordEtSearch.getText().toString();
    }

    private void setNoContentVisible(boolean isVisible) {
        acDeployRecordRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
