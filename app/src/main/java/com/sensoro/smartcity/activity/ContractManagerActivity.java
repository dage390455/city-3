package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractListAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractManagerActivityView;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.presenter.ContractManagerActivityPresenter;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.popup.InspectionTaskStatePopUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class ContractManagerActivity extends BaseActivity<IContractManagerActivityView, ContractManagerActivityPresenter> implements IContractManagerActivityView,
        AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener {
    @BindView(R.id.contract_iv_menu_list)
    ImageView contractIvMenuList;
    @BindView(R.id.contract_manger_root)
    RelativeLayout contractMangerRoot;
    @BindView(R.id.ac_contract_manger_add)
    RelativeLayout acContractMangerAdd;
    @BindView(R.id.contract_ptr_list)
    ListView contractPtrList;
    @BindView(R.id.contract_return_top)
    ImageView contractReturnTop;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.contract_tv_select_type)
    TextView tvSelectType;
    @BindView(R.id.contract_tv_select_status)
    TextView tvSelectStatus;
    @BindView(R.id.contract_cl_select_root)
    ConstraintLayout clSelectRoot;
    @BindView(R.id.fg_main_top_search_title_root)
    LinearLayout fgMainWarnTitleRoot;
    @BindView(R.id.ac_contract_record_frame_search)
    RelativeLayout fgMainWarnFrameSearch;
    @BindView(R.id.ac_contract_record_et_search)
    EditText fgMainWarnEtSearch;
    @BindView(R.id.ac_contract_record_imv_calendar)
    ImageView fgMainWarnImvCalendar;
    @BindView(R.id.tv_contract_search_cancel)
    TextView tvWarnAlarmSearchCancel;
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R.id.ll_search_history)
    LinearLayout llSearchHistory;
    @BindView(R.id.ac_contract_record_search_imv_clear)
    ImageView fgMainWarnFragmentImvClear;
    @BindView(R.id.fg_main_top_search_tv_date_edit)
    TextView fgMainWarnTvDateEdit;
    @BindView(R.id.fg_main_warn_top_search_date_close)
    ImageView fgMainWarnImvDateClose;
    @BindView(R.id.fg_main_top_search_rl_date_edit)
    RelativeLayout fgMainWarnRlDateEdit;

    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private ContractListAdapter mContractListAdapter;
    private InspectionTaskStatePopUtils mSelectStatusPop;
    private Drawable blackTriangle;
    private Drawable grayTriangle;
    private InspectionTaskStatePopUtils mSelectTypePop;
    private SearchHistoryAdapter mSearchHistoryAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_contract_list);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        fgMainWarnEtSearch.setHint(R.string.legal_representative_name);
        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
////                mPresenter.requestTopData();
                isShowDialog = false;
                String text = fgMainWarnEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = fgMainWarnEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP, text);
            }
        });
        mContractListAdapter = new ContractListAdapter(mActivity);
        contractPtrList.setOnScrollListener(this);
        contractPtrList.setAdapter(mContractListAdapter);
        contractPtrList.setOnItemClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        acContractMangerAdd.setOnClickListener(this);
        contractReturnTop.setOnClickListener(this);
        tvSelectType.setOnClickListener(this);
        tvSelectStatus.setOnClickListener(this);
        fgMainWarnFrameSearch.setOnClickListener(this);
        fgMainWarnEtSearch.setOnClickListener(this);
        tvWarnAlarmSearchCancel.setOnClickListener(this);
        fgMainWarnFragmentImvClear.setOnClickListener(this);
        fgMainWarnImvCalendar.setOnClickListener(this);
        fgMainWarnImvDateClose.setOnClickListener(this);

        blackTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle);
        blackTriangle.setBounds(0, 0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());
        grayTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle_gray);
        grayTriangle.setBounds(0, 0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());
        initSelectTypePop();
        initSelectStatusPop();
        fgMainWarnEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = fgMainWarnEtSearch.getText().toString();
                    mPresenter.save(text);
                    fgMainWarnEtSearch.clearFocus();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                    setSearchHistoryVisible(false);
                    return true;
                }
                return false;
            }
        });
        fgMainWarnEtSearch.addTextChangedListener(new TextWatcher() {
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
        AppUtils.getInputSoftStatus(contractMangerRoot, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                fgMainWarnEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                fgMainWarnEtSearch.setCursorVisible(true);
            }
        });

        //搜索历史的recyclerview
        initRcSearchHistory();
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
                            fgMainWarnEtSearch.setText(text);
                            fgMainWarnEtSearch.setSelection(fgMainWarnEtSearch.getText().toString().length());
                        }
                        fgMainWarnFragmentImvClear.setVisibility(View.VISIBLE);
                        fgMainWarnEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        fgMainWarnFragmentImvClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void initSelectTypePop() {
        mSelectTypePop = new InspectionTaskStatePopUtils(mActivity);
        mSelectTypePop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                InspectionStatusCountModel item = mSelectTypePop.getItem(position);
                mPresenter.doSelectTypeDevice(item);
                Resources resources = mActivity.getResources();
                if (position == 0) {
                    tvSelectType.setText(R.string.all_contracts);
                    tvSelectType.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    tvSelectType.setCompoundDrawables(null, null, grayTriangle, null);
                } else {
                    tvSelectType.setTextColor(resources.getColor(R.color.c_252525));
                    tvSelectType.setCompoundDrawables(null, null, blackTriangle, null);
                    tvSelectType.setText(item.statusTitle);
                }
                mSelectTypePop.dismiss();

            }
        });
    }

    private void initSelectStatusPop() {
        mSelectStatusPop = new InspectionTaskStatePopUtils(mActivity);
        mSelectStatusPop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                InspectionStatusCountModel item = mSelectStatusPop.getItem(position);
                mPresenter.doSelectStatusDevice(item);
                Resources resources = mActivity.getResources();
                if (position == 0) {
                    tvSelectStatus.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    tvSelectStatus.setCompoundDrawables(null, null, grayTriangle, null);
                    tvSelectStatus.setText(R.string.all_states);
                } else {
                    tvSelectStatus.setTextColor(resources.getColor(R.color.c_252525));
                    tvSelectStatus.setCompoundDrawables(null, null, blackTriangle, null);
                    tvSelectStatus.setText(item.statusTitle);
                }
                mSelectStatusPop.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSelectTypePop.isShowing()) {
            mSelectTypePop.dismiss();
        } else if (mSelectStatusPop.isShowing()) {
            mSelectStatusPop.dismiss();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
//        contractPtrList.onRefreshComplete();
    }

//    @Override
//    public PullToRefreshBase.State getPullRefreshState() {
//        return contractPtrList.getState();
//    }

    @Override
    public void requestDataByDirection(int direction, boolean isFirst) {
        mPresenter.requestDataByDirection(direction, isFirst);
    }

    @Override
    public void updateContractList(List<ContractListInfo> data) {
        if (data != null && data.size() > 0) {
            mContractListAdapter.setData(data);
            mContractListAdapter.notifyDataSetChanged();
        }
        setNoContentVisible(data == null || data.size() < 1);
    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        contractPtrList.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void UpdateSelectStatusPopList(List<InspectionStatusCountModel> list) {
        if (mSelectStatusPop != null) {
            mSelectStatusPop.updateSelectDeviceStatusList(list);
        }
    }

    @Override
    public void showSelectStatusPop() {
        if (mSelectStatusPop != null) {
            mSelectStatusPop.showAsDropDown(clSelectRoot);
        }
    }

    @Override
    public void UpdateSelectTypePopList(List<InspectionStatusCountModel> list) {
        if (mSelectTypePop != null) {
            mSelectTypePop.updateSelectDeviceStatusList(list);
        }
    }

    @Override
    public void showSelectStTypePop() {
        if (mSelectTypePop != null) {
            mSelectTypePop.showAsDropDown(clSelectRoot);
        }
    }

    @Override
    public void showSmartRefreshNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void smoothScrollToPosition(int position) {
        contractPtrList.smoothScrollToPosition(position);

    }

    @Override
    public void closeRefreshHeaderOrFooter() {
        refreshLayout.closeHeaderOrFooter();
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.clickItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_contract_manger_add:
                mPresenter.startToAdd();
                break;
            case R.id.contract_iv_menu_list:
                finishAc();
                break;
            case R.id.contract_return_top:
                contractPtrList.smoothScrollToPosition(0);
                closeRefreshHeaderOrFooter();
                break;
            case R.id.contract_tv_select_type:
                if (mSelectStatusPop != null && mSelectStatusPop.isShowing()) {
                    mSelectStatusPop.dismiss();
                }
                AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                mPresenter.doSelectTypePop();
                break;
            case R.id.contract_tv_select_status:
                AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                mPresenter.doSelectStatusPop();
                break;
            case R.id.ac_contract_record_frame_search:
            case R.id.ac_contract_record_et_search:
                fgMainWarnEtSearch.requestFocus();
                fgMainWarnEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
                AppUtils.openInputMethodManager(mActivity, fgMainWarnEtSearch);
//                forceOpenSoftKeyboard();
                break;
            case R.id.ac_contract_record_imv_calendar:
//                mPresenter.doCalendar(fgMainWarnTitleRoot);
                mPresenter.doCalendar(fgMainWarnTitleRoot);
                AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                break;
            case R.id.fg_main_warn_top_search_date_close:
                fgMainWarnRlDateEdit.setVisibility(View.GONE);
                String text = fgMainWarnEtSearch.getText().toString();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
                break;
            case R.id.tv_contract_search_cancel:
                doCancelSearch();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
                break;
            case R.id.ac_contract_record_search_imv_clear:
                fgMainWarnEtSearch.getText().clear();
                fgMainWarnEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mActivity, fgMainWarnEtSearch);
                break;
        }
    }

    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @Override
    public void UpdateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return fgMainWarnRlDateEdit.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean isVisible) {
        fgMainWarnRlDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelectedDateSearchText(String content) {
        fgMainWarnTvDateEdit.setText(content);
    }

    private void doCancelSearch() {
        if (getSearchTextVisible()) {
            fgMainWarnEtSearch.getText().clear();
        }
        mPresenter.doCancelSearch();
    }

    @Override
    public boolean getSearchTextVisible() {
        return tvWarnAlarmSearchCancel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            tvWarnAlarmSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
            AppUtils.dismissInputMethodManager(mActivity, fgMainWarnEtSearch);
        } else {
            tvWarnAlarmSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int tempPos = contractPtrList.getFirstVisiblePosition();
        if (tempPos > 0) {
            contractReturnTop.setVisibility(View.VISIBLE);
        } else {
            contractReturnTop.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }


    @Override
    protected ContractManagerActivityPresenter createPresenter() {
        return new ContractManagerActivityPresenter();
    }
}
