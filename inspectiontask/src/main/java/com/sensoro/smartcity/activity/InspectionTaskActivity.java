package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.common.widgets.dialog.TipBleDialogUtils;
import com.sensoro.inspectiontask.R;
import com.sensoro.inspectiontask.R2;
import com.sensoro.smartcity.adapter.InspectionTaskRcContentAdapter;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.presenter.InspectionTaskActivityPresenter;
import com.sensoro.smartcity.widget.popup.InspectionTaskStatePopUtils;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskActivity extends BaseActivity<IInspectionTaskActivityView, InspectionTaskActivityPresenter>
        implements IInspectionTaskActivityView, Constants, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R2.id.ac_inspection_task_imv_arrows_left)
    ImageView acInspectionTaskImvArrowsLeft;
    @BindView(R2.id.ac_inspection_task_ll_search)
    RelativeLayout acInspectionTaskLlSearch;
    @BindView(R2.id.fg_main_top_search_imv_clear)
    ImageView acInspectionTaskImvSearchClear;
    @BindView(R2.id.ac_inspection_task_rc_content)
    RecyclerView acInspectionTaskRcContent;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R2.id.ac_inspection_task_et_search)
    EditText acInspectionTaskEtSearch;
    @BindView(R2.id.tv_inspection_task_search_cancel)
    TextView tvInspectionTaskSearchCancel;
    @BindView(R2.id.ac_inspection_task_imv_scan)
    ImageView acInspectionTaskImvScan;
    @BindView(R2.id.ac_inspection_task_imv_map)
    ImageView acInspectionTaskImvMode;
    @BindView(R2.id.ac_inspection_task_tv_inspection_count)
    TextView acInspectionTaskTvInspectionCount;
    @BindView(R2.id.ac_inspection_task_tv_not_inspection_count)
    TextView acInspectionTaskTvNotInspectionCount;
    @BindView(R2.id.ac_inspection_task_tv_state)
    TextView acInspectionTaskTvState;
    @BindView(R2.id.ac_inspection_task_tv_type)
    TextView acInspectionTaskTvType;
    @BindView(R2.id.ac_inspection_task_rl_select)
    RelativeLayout acInspectionTaskLlSelect;
    View icNoContent;
    @BindView(R2.id.ac_inspection_task_rl_root)
    RelativeLayout acInspectionTaskRlRoot;
    @BindView(R2.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R2.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R2.id.ll_search_history)
    LinearLayout llSearchHistory;

    private InspectionTaskRcContentAdapter mContentAdapter;
    private SelectDeviceTypePopUtils mSelectDeviceTypePop;
    private InspectionTaskStatePopUtils mSelectStatusPop;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private TipBleDialogUtils tipBleDialogUtils;
    private Drawable blackTriangle;
    private Drawable grayTriangle;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        icNoContent = LayoutInflater.from(this).inflate(R.layout.no_content, null);

        tipBleDialogUtils = new TipBleDialogUtils(mActivity);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
//地图模式切换，图片 map_list_mode map_mode,已经准备好了

        acInspectionTaskEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = acInspectionTaskEtSearch.getText().toString();
                    mPresenter.save(text);
                    mPresenter.requestSearchData(Constants.DIRECTION_DOWN, text);
                    dismissInputMethodManager(acInspectionTaskEtSearch);
                    setSearchHistoryVisible(false);
                    return true;
                }
                return false;
            }
        });

        acInspectionTaskEtSearch.addTextChangedListener(new TextWatcher() {
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

        AppUtils.getInputSoftStatus(acInspectionTaskRlRoot, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                acInspectionTaskEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                acInspectionTaskEtSearch.setCursorVisible(true);
            }
        });
        initRcContent();

        initRcSearchHistory();

        blackTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle);
        blackTriangle.setBounds(0, 0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());
        grayTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle_gray);
        grayTriangle.setBounds(0, 0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());

        initSelectDeviceTypePop();

        initSelectStatusPop();
        acInspectionTaskEtSearch.setCursorVisible(false);
        acInspectionTaskEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                acInspectionTaskEtSearch.requestFocus();
                acInspectionTaskEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
                return false;
            }
        });
        initClearHistoryDialog();
    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        acInspectionTaskImvSearchClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity) {
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

        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(mActivity, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            acInspectionTaskEtSearch.setText(text);
                            acInspectionTaskEtSearch.setSelection(acInspectionTaskEtSearch.getText().toString().length());
                        }
                        acInspectionTaskImvSearchClear.setVisibility(View.VISIBLE);
                        acInspectionTaskEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(mActivity, acInspectionTaskEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.save(text);
                        mPresenter.requestSearchData(Constants.DIRECTION_DOWN, text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        }
        acInspectionTaskEtSearch.setCursorVisible(false);
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }

        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void initSelectDeviceTypePop() {
        mSelectDeviceTypePop = new SelectDeviceTypePopUtils(mActivity);
        mSelectDeviceTypePop.setTitleVisible(false);
//        mSelectDeviceTypePop.setUpAnimation();
        mSelectDeviceTypePop.setSelectDeviceTypeItemClickListener(new SelectDeviceTypePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position, DeviceTypeModel deviceTypeModel) {
                //选择类型的pop点击事件
                setSearchHistoryVisible(false);
                dismissInputMethodManager(acInspectionTaskEtSearch);
                mPresenter.doSelectTypeDevice(deviceTypeModel, acInspectionTaskEtSearch.getText().toString());
                acInspectionTaskTvType.setText(mSelectDeviceTypePop.getItem(position).name);
                mSelectDeviceTypePop.dismiss();
                Resources resources = mActivity.getResources();
                if (position == 0) {
                    acInspectionTaskTvType.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    acInspectionTaskTvType.setCompoundDrawables(null, null, grayTriangle, null);
                } else {
                    acInspectionTaskTvType.setTextColor(resources.getColor(R.color.c_252525));
                    acInspectionTaskTvType.setCompoundDrawables(null, null, blackTriangle, null);
                }
//                mPresenter.requestDataByDirection(DIRECTION_DOWN);

            }
        });
    }

    private void initSelectStatusPop() {
        mSelectStatusPop = new InspectionTaskStatePopUtils(mActivity);
        mSelectStatusPop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                setSearchHistoryVisible(false);
                dismissInputMethodManager(acInspectionTaskEtSearch);
                InspectionStatusCountModel item = mSelectStatusPop.getItem(position);
                acInspectionTaskTvState.setText(item.statusTitle);
                mPresenter.doSelectStatusDevice(item, acInspectionTaskEtSearch.getText().toString());
                Resources resources = mActivity.getResources();
                if (position == 0) {
                    acInspectionTaskTvState.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    acInspectionTaskTvState.setCompoundDrawables(null, null, grayTriangle, null);
                } else {
                    acInspectionTaskTvState.setTextColor(resources.getColor(R.color.c_252525));
                    acInspectionTaskTvState.setCompoundDrawables(null, null, blackTriangle, null);
                }
                mSelectStatusPop.dismiss();

            }
        });
    }

    private void initRcContent() {
        mContentAdapter = new InspectionTaskRcContentAdapter(mActivity);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acInspectionTaskRcContent.setLayoutManager(manager);
//        acInspectionTaskRcContent.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        acInspectionTaskRcContent.setAdapter(mContentAdapter);

        mContentAdapter.setOnRecycleViewItemClickListener(new InspectionTaskRcContentAdapter.InspectionTaskRcItemClickListener() {
            @Override
            public void onInspectionTaskInspectionClick(int position, int status) {
                mPresenter.doItemClick(position, status);
            }

            @Override
            public void onInspectionTaskNavigationClick(int position) {
                mPresenter.doNavigation(position);
            }

        });

        acInspectionTaskRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
//                        mReturnTopImageView.setVisibility(VISIBLE);
//                        if (returnTopAnimation.hasEnded()) {
//                            mReturnTopImageView.startAnimation(returnTopAnimation);
//                        }
                    } else {
//                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
//                    mReturnTopImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = acInspectionTaskEtSearch.getText().toString();
                mPresenter.requestSearchData(Constants.DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = acInspectionTaskEtSearch.getText().toString();
                mPresenter.requestSearchData(Constants.DIRECTION_UP, text);
            }
        });
    }

    @Override
    protected InspectionTaskActivityPresenter createPresenter() {
        return new InspectionTaskActivityPresenter();
    }

    @Override
    public void onBackPressed() {
        try {
            if (mSelectDeviceTypePop.isShowing()) {
                mSelectDeviceTypePop.dismiss();
            } else if (mSelectStatusPop.isShowing()) {
                mSelectStatusPop.dismiss();
            } else {
                super.onBackPressed();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    protected void onPause() {
        AppUtils.dismissInputMethodManager(mActivity, acInspectionTaskEtSearch);
        super.onPause();
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R2.id.ac_inspection_task_imv_arrows_left, R2.id.ac_inspection_task_tv_state, R2.id.ac_inspection_task_tv_type, R2.id.ac_inspection_task_imv_scan, R2.id.tv_inspection_task_search_cancel
            , R2.id.ac_inspection_task_imv_map, R2.id.fg_main_top_search_imv_clear, R2.id.btn_search_clear})
    public void onViewClicked(View view) {
        int viewID=view.getId();
        if(viewID==R.id.ac_inspection_task_imv_arrows_left){
            finishAc();
        }else if(viewID==R.id.ac_inspection_task_tv_state){
            if (mSelectDeviceTypePop != null) {
                if (mSelectStatusPop.isData()) {
                    showSelectDeviceStatusPop();
                } else {
                    mPresenter.doInspectionStatus(true);
                }
            }
        }else if(viewID==R.id.ac_inspection_task_tv_type){
            if (mSelectStatusPop != null && mSelectStatusPop.isShowing()) {
                mSelectStatusPop.dismiss();
            }
            mPresenter.doInspectionType(true);
        }else if(viewID==R.id.ac_inspection_task_imv_scan){
            mPresenter.doInspectionScan();
        }else if(viewID==R.id.tv_inspection_task_search_cancel){
            doCancelSearch();
            dismissInputMethodManager(acInspectionTaskEtSearch);
            setSearchHistoryVisible(false);
        }else if(viewID==R.id.ac_inspection_task_imv_map){

        }else if(viewID==R.id.btn_search_clear){
            showHistoryClearDialog();
        }else if(viewID==R.id.fg_main_top_search_imv_clear){
            acInspectionTaskEtSearch.getText().clear();
            acInspectionTaskEtSearch.requestFocus();
            AppUtils.openInputMethodManager(mActivity, acInspectionTaskEtSearch);
            setSearchHistoryVisible(true);
        }

    }

    private void doCancelSearch() {
        if (getSearchTextVisible()) {
            acInspectionTaskEtSearch.getText().clear();
        }
        mPresenter.doCancelSearch();
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            tvInspectionTaskSearchCancel.setVisibility(View.VISIBLE);
//            dismissInputMethodManager(acInspectionTaskEtSearch);
        } else {
            tvInspectionTaskSearchCancel.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean getSearchTextVisible() {
        return tvInspectionTaskSearchCancel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void updateSelectDeviceTypeList(List<String> deviceTypes) {
        if (mSelectDeviceTypePop != null) {
            mSelectDeviceTypePop.updateSelectDeviceTypeList(deviceTypes);
//            mSelectDeviceTypePop.showAsDropDown(acInspectionTaskLlSelect);
        }

    }

    @Override
    public void setBottomInspectionStateTitle(String finish, String unFinish) {
        acInspectionTaskTvNotInspectionCount.setText(unFinish);
        acInspectionTaskTvInspectionCount.setText(finish);
    }

    @Override
    public void showSelectDeviceTypePop() {
        if (mSelectDeviceTypePop != null) {
            dismissInputMethodManager(acInspectionTaskEtSearch);
            mSelectDeviceTypePop.showAsDropDown(acInspectionTaskLlSelect);
        }

    }

    @Override
    public void updateSelectDeviceStatusList(List<InspectionStatusCountModel> data) {
        if (mSelectStatusPop != null) {
            dismissInputMethodManager(acInspectionTaskEtSearch);
            mSelectStatusPop.updateSelectDeviceStatusList(data);
        }
    }

    @Override
    public void updateInspectionTaskDeviceItem(List<InspectionTaskDeviceDetail> data) {
        if (data != null && data.size() > 0) {
            mContentAdapter.updateDevices(data);
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
            refreshLayout.setRefreshContent(acInspectionTaskRcContent);
        }
    }

    @Override
    public void showBleTips() {
        if (tipBleDialogUtils != null && !tipBleDialogUtils.isShowing()) {
            tipBleDialogUtils.show();
        }
    }

    @Override
    public void hideBleTips() {
        if (tipBleDialogUtils != null && tipBleDialogUtils.isShowing()) {
            tipBleDialogUtils.dismiss();
        }
    }

    @Override
    public void showSelectDeviceStatusPop() {
        if (mSelectStatusPop != null) {
            if (mSelectStatusPop.isShowing()) {
                mSelectStatusPop.dismiss();
            }
            mSelectStatusPop.showAsDropDown(acInspectionTaskLlSelect);
        }
    }

    @Override
    public void UpdateSearchHistoryList(List<String> data) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (tipBleDialogUtils != null) {
            tipBleDialogUtils.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearSearchHistory();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }
    }
}
