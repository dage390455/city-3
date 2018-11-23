package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.sensoro.smartcity.adapter.InspectionTaskRcContentAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.presenter.InspectionTaskActivityPresenter;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.dialog.TipBleDialogUtils;
import com.sensoro.smartcity.widget.popup.InspectionTaskStatePopUtils;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskActivity extends BaseActivity<IInspectionTaskActivityView, InspectionTaskActivityPresenter>
        implements IInspectionTaskActivityView, Constants {
    @BindView(R.id.ac_inspection_task_imv_arrows_left)
    ImageView acInspectionTaskImvArrowsLeft;
    @BindView(R.id.ac_inspection_task_ll_search)
    RelativeLayout acInspectionTaskLlSearch;
    @BindView(R.id.fg_main_top_search_imv_clear)
    ImageView acInspectionTaskImvSearchClear;
    @BindView(R.id.ac_inspection_task_fl_state)
    FrameLayout acInspectionTaskFlState;
    @BindView(R.id.ac_inspection_task_fl_type)
    FrameLayout acInspectionTaskFlType;
    @BindView(R.id.ac_inspection_task_rc_content)
    RecyclerView acInspectionTaskRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ac_inspection_task_et_search)
    EditText acInspectionTaskEtSearch;
    @BindView(R.id.tv_inspection_task_search_cancel)
    TextView tvInspectionTaskSearchCancel;
    @BindView(R.id.ac_inspection_task_imv_scan)
    ImageView acInspectionTaskImvScan;
    @BindView(R.id.ac_inspection_task_imv_map)
    ImageView acInspectionTaskImvMode;
    @BindView(R.id.ac_inspection_task_tv_inspection_count)
    TextView acInspectionTaskTvInspectionCount;
    @BindView(R.id.ac_inspection_task_tv_not_inspection_count)
    TextView acInspectionTaskTvNotInspectionCount;
    @BindView(R.id.ac_inspection_task_tv_state)
    TextView acInspectionTaskTvState;
    @BindView(R.id.ac_inspection_task_tv_type)
    TextView acInspectionTaskTvType;
    @BindView(R.id.ac_inspection_task_ll_select)
    LinearLayout acInspectionTaskLlSelect;
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.ac_inspection_task_rl_root)
    RelativeLayout acInspectionTaskRlRoot;
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R.id.ll_search_history)
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

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
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
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
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
                setSearchClearImvVisible(s.length()>0);
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
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        acInspectionTaskImvSearchClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity){
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
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
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
                        AppUtils.dismissInputMethodManager(mActivity,acInspectionTaskEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.requestSearchData(DIRECTION_DOWN,text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        acInspectionTaskEtSearch.setCursorVisible(false);
    }

    public void forceOpenSoftKeyboard() {
//        acInspectionTaskEtSearch.setCursorVisible(true);
//        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
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
        mSelectDeviceTypePop.setUpAnimation();
        mSelectDeviceTypePop.setSelectDeviceTypeItemClickListener(new SelectDeviceTypePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position, DeviceTypeModel deviceTypeModel) {
                //选择类型的pop点击事件
                setSearchHistoryVisible(false);
                dismissInputMethodManager(acInspectionTaskEtSearch);
                mPresenter.doSelectTypeDevice(deviceTypeModel);
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
        mSelectStatusPop.setUpAnimation();
        mSelectStatusPop.clearAnimation();
        mSelectStatusPop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                setSearchHistoryVisible(false);
                dismissInputMethodManager(acInspectionTaskEtSearch);
                InspectionStatusCountModel item = mSelectStatusPop.getItem(position);
                acInspectionTaskTvState.setText(item.statusTitle);
                mPresenter.doSelectStatusDevice(item);
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
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = acInspectionTaskEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP, text);
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
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.ac_inspection_task_imv_arrows_left, R.id.ac_inspection_task_ll_search, R.id.ac_inspection_task_et_search,
            R.id.ac_inspection_task_fl_state, R.id.ac_inspection_task_fl_type, R.id.ac_inspection_task_imv_scan, R.id.tv_inspection_task_search_cancel
            , R.id.ac_inspection_task_imv_map,R.id.fg_main_top_search_imv_clear,R.id.btn_search_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_inspection_task_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_task_ll_search:
            case R.id.ac_inspection_task_et_search:
                //TODO
                acInspectionTaskEtSearch.requestFocus();
                acInspectionTaskEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
//                forceOpenSoftKeyboard();
                break;
            case R.id.ac_inspection_task_fl_state:
                if (mSelectDeviceTypePop!=null&&mSelectDeviceTypePop.isShowing()) {
                    mSelectDeviceTypePop.dismiss();
                }
                mPresenter.doInspectionStatus(true);
                break;
            case R.id.ac_inspection_task_fl_type:
                if(mSelectStatusPop!=null&&mSelectStatusPop.isShowing()){
                    mSelectStatusPop.dismiss();
                }
                mPresenter.doInspectionType(true);
                break;
            case R.id.ac_inspection_task_imv_scan:
                mPresenter.doInspectionScan();
                break;
            case R.id.tv_inspection_task_search_cancel:
                doCancelSearch();
                dismissInputMethodManager(acInspectionTaskEtSearch);
                setSearchHistoryVisible(false);
                break;
            case R.id.ac_inspection_task_imv_map:

                break;
            case R.id.btn_search_clear:
                mPresenter.clearSearchHistory();
                break;
            case R.id.fg_main_top_search_imv_clear:
                acInspectionTaskEtSearch.getText().clear();
                acInspectionTaskEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mActivity,acInspectionTaskEtSearch);
                setSearchHistoryVisible(true);
                break;
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
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
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
            mSelectStatusPop.showAsDropDown(acInspectionTaskLlSelect);
        }
    }

    @Override
    public void updateInspectionTaskDeviceItem(List<InspectionTaskDeviceDetail> data) {
        if (data != null && data.size() > 0) {
            mContentAdapter.updateDevices(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acInspectionTaskRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
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
    public void UpdateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() >0 ? View.VISIBLE : View.GONE);
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


}
