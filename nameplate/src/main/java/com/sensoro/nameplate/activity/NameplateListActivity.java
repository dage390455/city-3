package com.sensoro.nameplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.common.widgets.dialog.TitleTipDialogUtils;
import com.sensoro.nameplate.IMainViews.INameplateListActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.adapter.NameplateListAdapter;
import com.sensoro.nameplate.model.FilterModel;
import com.sensoro.nameplate.presenter.NameplateListActivityPresenter;
import com.sensoro.nameplate.widget.FilterPopUtils;

import java.util.List;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;

@Route(path = ARouterConstants.ACTIVITY_NAMEPLATE_LIST)
public class NameplateListActivity extends BaseActivity<INameplateListActivityView, NameplateListActivityPresenter>
        implements INameplateListActivityView, View.OnClickListener {


    ImageView ivNameplateListTopBack;
    EditText etNameplateListSearch;
    ImageView ivNameplateListSearchClear;
    LinearLayout llNameplateListSearchTop;
    TextView tvNameplateListSearchCancel;
    ImageView ivNameplateListFilter;
    ImageView ivNameplateListScan;
    LinearLayout llNameplateListTopSearch;
    ImageView noContent;
    TextView noContentTip;
    LinearLayout icNoContent;
    RecyclerView rvNameplateContent;
    SmartRefreshLayout refreshLayout;
    TextView tvSearchClear;
    ImageView btnSearchClear;
    RecyclerView rvSearchHistory;
    LinearLayout llSearchHistory;
    ImageView ivReturnTop;
    RelativeLayout llNameplateListRoot;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private Animation returnTopAnimation;

    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;
    //
    private NameplateListAdapter nameplateListAdapter;
    private TitleTipDialogUtils mDeleteDialog;
    private FilterPopUtils filterPopUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nameplate_list);
        initView();
        ARouter.getInstance().inject(this);
        mPresenter.initData(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {

        filterPopUtils = new FilterPopUtils(this);
        ivNameplateListTopBack = findViewById(R.id.iv_nameplate_list_top_back);
        etNameplateListSearch = findViewById(R.id.et_nameplate_list_search);
        ivNameplateListSearchClear = findViewById(R.id.iv_nameplate_list_search_clear);
        llNameplateListSearchTop = findViewById(R.id.ll_nameplate_list_search_top);
        tvNameplateListSearchCancel = findViewById(R.id.tv_nameplate_list_search_cancel);
        ivNameplateListFilter = findViewById(R.id.iv_nameplate_list_filter);
        ivNameplateListScan = findViewById(R.id.iv_nameplate_list_scan);
        llNameplateListTopSearch = findViewById(R.id.ll_nameplate_list_top_search);
        noContent = findViewById(R.id.no_content);
        noContentTip = findViewById(R.id.no_content_tip);
        icNoContent = findViewById(R.id.ic_no_content);
        rvNameplateContent = findViewById(R.id.rv_nameplate_content);
        refreshLayout = findViewById(R.id.refreshLayout);
        tvSearchClear = findViewById(R.id.tv_search_clear);
        btnSearchClear = findViewById(R.id.btn_search_clear);
        rvSearchHistory = findViewById(R.id.rv_search_history);
        llSearchHistory = findViewById(R.id.ll_search_history);
        ivReturnTop = findViewById(R.id.iv_return_top);
        llNameplateListRoot = findViewById(R.id.ll_nameplate_list_root);
        //
        ivNameplateListTopBack.setOnClickListener(this);
        etNameplateListSearch.setOnClickListener(this);
        ivNameplateListSearchClear.setOnClickListener(this);
        tvNameplateListSearchCancel.setOnClickListener(this);
        ivNameplateListFilter.setOnClickListener(this);
        ivNameplateListScan.setOnClickListener(this);
        btnSearchClear.setOnClickListener(this);
        ivReturnTop.setOnClickListener(this);


        //
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        //
        mDeleteDialog = new TitleTipDialogUtils(mActivity);
        mDeleteDialog.setTipTitleText(mActivity.getString(R.string.is_delete_nameplate));
        mDeleteDialog.setTipCancelText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_252525));
        mDeleteDialog.setTipConfirmText(mActivity.getString(R.string.delete), mActivity.getResources().getColor(R.color.c_f35a58));
        mDeleteDialog.setTipMessageText(mActivity.getString(R.string.redploy_after_delete));
//        mDeleteDialog.setTipDialogUtilsClickListener(this);
        //
//        mDeviceCameraContentAdapter = new DeviceCameraContentAdapter(mActivity);
//        mDeviceCameraContentAdapter.setOnAlarmHistoryLogConfirmListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvNameplateContent.setLayoutManager(linearLayoutManager);
//        acHistoryLogRcContent.setAdapter(mDeviceCameraContentAdapter);
//        CustomDivider dividerItemDecoration = new CustomDivider(mActivity, DividerItemDecoration.VERTICAL);
//        acHistoryLogRcContent.addItemDecoration(dividerItemDecoration);
        //
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        ivReturnTop.setAnimation(returnTopAnimation);
        ivReturnTop.setVisibility(View.GONE);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(DIRECTION_DOWN, getSearchText());
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
        nameplateListAdapter = new NameplateListAdapter(mActivity);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(RecyclerView.VERTICAL);
//        CustomDivider dividerItemDecoration = new CustomDivider(mActivity, DividerItemDecoration.VERTICAL);
//        rvNameplateContent.addItemDecoration(dividerItemDecoration);
        rvNameplateContent.setLayoutManager(manager);
        rvNameplateContent.setAdapter(nameplateListAdapter);

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);

        nameplateListAdapter.setOnClickListener(new NameplateListAdapter.OnNameplateListAdapterClickListener() {
            @Override
            public void onClick(View v, int position) {
                //点击
                mPresenter.doNameplateDetail(position);
            }

            @Override
            public void onDelete(int position) {
                //删除
                if (mDeleteDialog != null) {
                    mDeleteDialog.show();

                    mDeleteDialog.setTipDialogUtilsClickListener(new TitleTipDialogUtils.TitleTipDialogUtilsClickListener() {
                        @Override
                        public void onCancelClick() {
                            mDeleteDialog.dismiss();

                        }

                        @Override
                        public void onConfirmClick() {


                            mDeleteDialog.dismiss();
                            mPresenter.deleteNamePlate(position);
                        }
                    });


                }
            }
        });
        rvNameplateContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (linearLayoutManager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        ivReturnTop.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            ivReturnTop.startAnimation(returnTopAnimation);
                        }
                    } else {
                        ivReturnTop.setVisibility(View.GONE);
                    }
                } else {
                    ivReturnTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
//        mCameraListFilterPopupWindow = new CameraListFilterPopupWindowTest(mActivity);
//        mCameraListFilterPopupWindow.setOnCameraListFilterPopupWindowListener(this);

        etNameplateListSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = getSearchText();
                    etNameplateListSearch.clearFocus();
                    if (!TextUtils.isEmpty(text)) {
                        mPresenter.save(text);
                    }
                    mPresenter.requestDataByFilter(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(NameplateListActivity.this, etNameplateListSearch);
                    setSearchHistoryVisible(false);

                    return true;
                }
                return false;
            }
        });
        etNameplateListSearch.addTextChangedListener(new TextWatcher() {
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

        AppUtils.getInputSoftStatus(llNameplateListRoot, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                etNameplateListSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                etNameplateListSearch.setCursorVisible(true);
            }
        });

        initRcSearchHistory();
        initClearHistoryDialog();

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(NameplateListActivity.this, true);
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
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(NameplateListActivity.this) {
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
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(NameplateListActivity.this, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(NameplateListActivity.this, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String searchText = null;
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            searchText = text;
                            etNameplateListSearch.setText(searchText);
                            etNameplateListSearch.setSelection(etNameplateListSearch.getText().toString().length());
                        }
                        ivNameplateListSearchClear.setVisibility(View.VISIBLE);
                        etNameplateListSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(NameplateListActivity.this, etNameplateListSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.save(searchText);
                        mPresenter.requestDataByFilter(DIRECTION_DOWN, searchText);

                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);


        filterPopUtils.setSelectDeviceTypeItemClickListener(new FilterPopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件

                String deviceFlag = null;
                if (position == 0) {
                    deviceFlag = null;
                } else if (position == 1) {
                    deviceFlag = "true";
                } else if (position == 2) {
                    deviceFlag = "false";


                }

                ivNameplateListFilter.setImageResource(R.mipmap.namepalte_filter_selected);

                mPresenter.requestDataByFilter(DIRECTION_DOWN, getSearchText(), deviceFlag);
                filterPopUtils.dismiss();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }

        if (mDeleteDialog != null) {
            mDeleteDialog.destroy();
        }
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        ivNameplateListSearchClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected NameplateListActivityPresenter createPresenter() {
        return new NameplateListActivityPresenter();
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
    public void updateNameplateAdapter(List<NamePlateInfo> data) {
        if (data != null && data.size() > 0) {

            nameplateListAdapter.updateData(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }


    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rvNameplateContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
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
            tvNameplateListSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
//            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
        } else if (TextUtils.isEmpty(etNameplateListSearch.getText().toString())) {
            tvNameplateListSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }


    @Override
    public void updateDeleteNamePlateStatus(int pos) {


        nameplateListAdapter.getData().remove(pos);
        nameplateListAdapter.notifyDataSetChanged();
        setNoContentVisible(nameplateListAdapter.getData() == null || nameplateListAdapter.getData().size() < 1);

    }

    @Override
    public void updateSelectDeviceStatusList(List<FilterModel> list) {

        filterPopUtils.updateSelectDeviceStatusList(list);
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
    public void onBackPressed() {
        if (filterPopUtils.isShowing()) {
            filterPopUtils.dismiss();
        } else {
            super.onBackPressed();
        }

    }

    private String getSearchText() {
        String text = etNameplateListSearch.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        return text;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_nameplate_list_top_back) {
            finishAc();
            AppUtils.dismissInputMethodManager(mActivity, etNameplateListSearch);
        } else if (i == R.id.et_nameplate_list_search) {
            etNameplateListSearch.requestFocus();
            etNameplateListSearch.setCursorVisible(true);
            setSearchHistoryVisible(true);
        } else if (i == R.id.iv_nameplate_list_search_clear) {
            etNameplateListSearch.setText("");
            etNameplateListSearch.requestFocus();
            AppUtils.openInputMethodManager(NameplateListActivity.this, etNameplateListSearch);
            setSearchHistoryVisible(true);
        } else if (i == R.id.tv_nameplate_list_search_cancel) {
            if (tvNameplateListSearchCancel.getVisibility() == View.VISIBLE) {
                etNameplateListSearch.getText().clear();
            }
            mPresenter.requestDataByFilter(DIRECTION_DOWN, null);
            setSearchHistoryVisible(false);
            AppUtils.dismissInputMethodManager(NameplateListActivity.this, etNameplateListSearch);
        } else if (i == R.id.iv_nameplate_list_filter) {

            if (filterPopUtils.isShowing()) {

                filterPopUtils.dismiss();
            } else {
                filterPopUtils.showAsDropDown(llNameplateListTopSearch);
            }

        } else if (i == R.id.iv_nameplate_list_scan) {

            mPresenter.doScanSearch();






        } else if (i == R.id.btn_search_clear) {//


            showHistoryClearDialog();

        } else if (i == R.id.iv_return_top) {
            rvNameplateContent.smoothScrollToPosition(0);
            ivReturnTop.setVisibility(View.GONE);
            refreshLayout.closeHeaderOrFooter();
        }
    }


//    @Override
//    public void onSave(List<CameraFilterModel> list) {
//        mPresenter.onSaveCameraListFilterPopupWindowDismiss(list, getSearchText());
//    }
//
//    @Override
//    public void onDismiss() {
//        mPresenter.onCameraListFilterPopupWindowDismiss();
//    }
//
//    @Override
//    public void onReset() {
//        mPresenter.onResetCameraListFilterPopupWindowDismiss(getSearchText());
//    }
}
