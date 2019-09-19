package com.sensoro.forestfire.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.bean.ForestFireCameraBean;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.CameraListFilterPopupWindow;
import com.sensoro.common.widgets.CustomDivider;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;
import com.sensoro.forestfire.adapter.ForestFireCameraListAdapter;
import com.sensoro.forestfire.imainviews.IForestFireCameraListActivityView;
import com.sensoro.forestfire.presenter.ForestFireListActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理摄像头列表首页>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_CAMERA_LIST)
public class ForestFireCameraListActivity extends BaseActivity<IForestFireCameraListActivityView, ForestFireListActivityPresenter>
        implements IForestFireCameraListActivityView, ForestFireCameraListAdapter.OnDeviceCameraContentClickListener, View.OnClickListener, CameraListFilterPopupWindow.OnCameraListFilterPopupWindowListener {


    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R2.id.fg_history_log_rc_content)
    RecyclerView acHistoryLogRcContent;
    @BindView(R2.id.alarm_return_top)
    ImageView mReturnTopImageView;
    View icNoContent;
    @BindView(R2.id.camera_list_ll_top_search)
    LinearLayout cameraListLlTopSearch;
    @BindView(R2.id.camera_list_iv_top_back)
    ImageView cameraListIvTopBack;
    @BindView(R2.id.camera_list_iv_search_clear)
    ImageView cameraListIvSearchClear;
    @BindView(R2.id.camera_list_et_search)
    EditText cameraListEtSearch;
    @BindView(R2.id.camera_list_tv_search_cancel)
    TextView cameraListTvSearchCancel;
    @BindView(R2.id.camera_list_iv_filter)
    ImageView cameraListIvFilter;
    @BindView(R2.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R2.id.camera_list_ll_root)
    View mRootView;
    @BindView(R2.id.ll_search_history)
    LinearLayout llSearchHistory;

    @BindView(R2.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R2.id.camera_list_search_top)
    LinearLayout cameraListSearchTop;
    @BindView(R2.id.associated_camera_list_tv)
    TextView associatedCameraListTv;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private ForestFireCameraListAdapter mDeviceCameraContentAdapter;
    private Animation returnTopAnimation;

    private CameraListFilterPopupWindow mCameraListFilterPopupWindow;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forestfire_list);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        icNoContent = LayoutInflater.from(this).inflate(R.layout.no_content, null);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mDeviceCameraContentAdapter = new ForestFireCameraListAdapter(mActivity);
        mDeviceCameraContentAdapter.setOnAlarmHistoryLogConfirmListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        acHistoryLogRcContent.setLayoutManager(linearLayoutManager);
        acHistoryLogRcContent.setAdapter(mDeviceCameraContentAdapter);
        CustomDivider dividerItemDecoration = new CustomDivider(mActivity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setSideWith(0);
        acHistoryLogRcContent.addItemDecoration(dividerItemDecoration);
        //
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        cameraListIvFilter.setOnClickListener(this);
        cameraListIvSearchClear.setOnClickListener(this);
        cameraListEtSearch.setOnClickListener(this);
        cameraListTvSearchCancel.setOnClickListener(this);
        btnSearchClear.setOnClickListener(this);
        cameraListIvTopBack.setOnClickListener(this);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(Constants.DIRECTION_DOWN, getSearchText());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(Constants.DIRECTION_UP, getSearchText());
            }
        });
        //
        acHistoryLogRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (linearLayoutManager.findFirstVisibleItemPosition() > 4) {
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
        mCameraListFilterPopupWindow = new CameraListFilterPopupWindow(mActivity,CameraListFilterPopupWindow.FILL_MODE_WRAPCONTENT,0);
        mCameraListFilterPopupWindow.setOnCameraListFilterPopupWindowListener(this);

        cameraListEtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = getSearchText();
                cameraListEtSearch.clearFocus();
                if (!TextUtils.isEmpty(text)) {
                    mPresenter.save(text);
                }
                mPresenter.requestDataByFilter(Constants.DIRECTION_DOWN, text);
                AppUtils.dismissInputMethodManager(ForestFireCameraListActivity.this, cameraListEtSearch);
                setSearchHistoryVisible(false);

                return true;
            }
            return false;
        });

        cameraListEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mCameraListFilterPopupWindow.isShowing()) {

                    mCameraListFilterPopupWindow.dismiss();

                }
                cameraListEtSearch.requestFocus();
                cameraListEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
                return false;
            }
        });

        cameraListEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSearchClearImvVisible(s.length() > 0);
            }
        });

        AppUtils.getInputSoftStatus(mRootView, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                cameraListEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                cameraListEtSearch.setCursorVisible(true);
            }
        });

        initRcSearchHistory();
        initClearHistoryDialog();

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(ForestFireCameraListActivity.this, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(new TipOperationDialogUtils.TipDialogUtilsClickListener() {
            @Override
            public void onCancelClick() {
                historyClearDialog.dismiss();

            }

            @Override
            public void onConfirmClick(String content, String diameter) {
                mPresenter.clearSearchHistory();
                historyClearDialog.dismiss();
            }
        });
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(ForestFireCameraListActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSearchHistory.setLayoutManager(layoutManager);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(ForestFireCameraListActivity.this, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(ForestFireCameraListActivity.this, (view, position) -> {
            String searchText = null;
            String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
            if (!TextUtils.isEmpty(text)) {
                searchText = text;
                cameraListEtSearch.setText(searchText);
                cameraListEtSearch.setSelection(cameraListEtSearch.getText().toString().length());
            }
            cameraListIvSearchClear.setVisibility(View.VISIBLE);
            cameraListEtSearch.clearFocus();
            AppUtils.dismissInputMethodManager(ForestFireCameraListActivity.this, cameraListEtSearch);
            setSearchHistoryVisible(false);
            mPresenter.save(searchText);
            mPresenter.requestDataByFilter(Constants.DIRECTION_DOWN, searchText);

        });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        cameraListIvSearchClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected ForestFireListActivityPresenter createPresenter() {
        return new ForestFireListActivityPresenter();
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
        if (isShowDialog) {
            mProgressUtils.showProgress();
        }
        isShowDialog = true;
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onItemClick(View v, int position) {
        ForestFireCameraBean mForestFireCameraBean = mDeviceCameraContentAdapter.getData().get(position);
        mPresenter.onClickDeviceCamera(mForestFireCameraBean);
    }

    @Override
    public void updateDeviceCameraAdapter(List<ForestFireCameraBean> data) {
        if (data != null && data.size() > 0) {
            mDeviceCameraContentAdapter.updateAdapter(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }



    @SuppressLint("RestrictedApi")
    @Override
    public void setNoContentVisible(boolean isVisible) {

        RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
        if (refreshHeader != null) {
            if (isVisible) {
                refreshHeader.setPrimaryColors(getResources().getColor(R.color.c_f4f4f4));
            } else {
                refreshHeader.setPrimaryColors(getResources().getColor(R.color.white));
            }
        }
        if (isVisible) {
            refreshLayout.setRefreshContent(icNoContent);
        } else {
            refreshLayout.setRefreshContent(acHistoryLogRcContent);
        }
    }

    @Override
    public void setSmartRefreshEnable(boolean enable) {
        refreshLayout.setEnableLoadMore(enable);
        refreshLayout.setEnableRefresh(enable);
    }

    @Override
    public void updateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }


    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            cameraListTvSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
//            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
        } else if (TextUtils.isEmpty(cameraListEtSearch.getText().toString())) {
            cameraListTvSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }



    @Override
    public void setTopTitleState() {
        associatedCameraListTv.setVisibility(View.VISIBLE);
        cameraListSearchTop.setVisibility(View.GONE);
        cameraListIvFilter.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showCameraListFilterPopupWindow(List<CameraFilterModel> data) {
        if (!mCameraListFilterPopupWindow.isShowing()) {
            mCameraListFilterPopupWindow.showAsDropDown(cameraListLlTopSearch, data);
        }
    }

    @Override
    public void dismissCameraListFilterPopupWindow() {
        mCameraListFilterPopupWindow.dismiss();

    }

    @Override
    public void updateCameraListFilterPopupWindowStatusList(List<CameraFilterModel> list) {
        mCameraListFilterPopupWindow.updateSelectDeviceStatusList(list);
    }

    @Override
    public void setCameraListFilterPopupWindowSelectState(boolean hasSelect) {
        if (hasSelect) {
            cameraListIvFilter.setImageResource(R.drawable.camera_filter_selected);
        } else {
            cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);
        }
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }


    @Override
    public void onClick(View v) {
        int  viewID=v.getId();
        if(viewID==R.id.iv_alarm_log_date_close){

        }else if(viewID==R.id.iv_alarm_log_date_close){
            acHistoryLogRcContent.smoothScrollToPosition(0);
            mReturnTopImageView.setVisibility(View.GONE);
            refreshLayout.closeHeaderOrFooter();
        }else if(viewID==R.id.camera_list_iv_top_back){
            finishAc();
            AppUtils.dismissInputMethodManager(mActivity, cameraListEtSearch);
        }else if(viewID==R.id.camera_list_iv_filter){
            mPresenter.doShowCameraListFilterPopupWindow(mCameraListFilterPopupWindow.isShowing());
        }else if(viewID==R.id.camera_list_et_search){
//            forceOpenSoftKeyboard();
        }else if(viewID==R.id.camera_list_iv_search_clear){
            cameraListEtSearch.setText("");
            cameraListEtSearch.requestFocus();
            AppUtils.openInputMethodManager(ForestFireCameraListActivity.this, cameraListEtSearch);
            setSearchHistoryVisible(true);
        }else if(viewID==R.id.camera_list_tv_search_cancel){
            if (cameraListTvSearchCancel.getVisibility() == View.VISIBLE) {
                cameraListEtSearch.getText().clear();
            }
            mPresenter.requestDataByFilter(Constants.DIRECTION_DOWN, null);
            setSearchHistoryVisible(false);
            AppUtils.dismissInputMethodManager(ForestFireCameraListActivity.this, cameraListEtSearch);
        }else if(viewID==R.id.btn_search_clear){
            showHistoryClearDialog();
        }
    }


    @Override
    public void onBackPressed() {
        if (mCameraListFilterPopupWindow.isShowing()) {
            mPresenter.onCameraListFilterPopupWindowDismiss();
        } else {
            super.onBackPressed();
        }

    }

    private String getSearchText() {
        String text = cameraListEtSearch.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        return text;
    }


    @Override
    public void onSave(List<CameraFilterModel> list) {
        mPresenter.onSaveCameraListFilterPopupWindowDismiss(list, getSearchText());
    }

    @Override
    public void onDismiss() {
        mPresenter.onCameraListFilterPopupWindowDismiss();
    }

    @Override
    public void onReset() {
        mPresenter.onResetCameraListFilterPopupWindowDismiss(getSearchText());
    }

}
