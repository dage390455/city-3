package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractListAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractManagerActivityView;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.presenter.ContractManagerActivityPresenter;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.sensoro.smartcity.widget.popup.InspectionTaskStatePopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class ContractManagerActivity extends BaseActivity<IContractManagerActivityView, ContractManagerActivityPresenter> implements IContractManagerActivityView, AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener,
        RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.contract_iv_menu_list)
    ImageView contractIvMenuList;
    @BindView(R.id.contract_title)
    TextView contractTitle;
    @BindView(R.id.contract_iv_add)
    ImageView contractIvAdd;
//    @BindView(R.id.rg_contract_select)
//    RadioGroup rgContractSelect;
    @BindView(R.id.contract_ptr_list)
    ListView contractPtrList;
    @BindView(R.id.contract_return_top)
    ImageView contractReturnTop;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
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

    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private ContractListAdapter mContractListAdapter;
    private InspectionTaskStatePopUtils mSelectStatusPop;
    private Drawable blackTriangle;
    private Drawable grayTriangle;
    private InspectionTaskStatePopUtils mSelectTypePop;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_contract_list);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                requestDataByDirection(DIRECTION_DOWN, false);
//                mPresenter.requestTopData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                requestDataByDirection(DIRECTION_UP, false);
            }
        });
//        contractPtrList.setRefreshing(false);
//        contractPtrList.setMode(PullToRefreshBase.Mode.BOTH);
//        contractPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                isShowDialog = false;
//                requestDataByDirection(DIRECTION_DOWN, false);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                isShowDialog = false;
//                requestDataByDirection(DIRECTION_UP, false);
//            }
//        });
        mContractListAdapter = new ContractListAdapter(mActivity);
        contractPtrList.setOnScrollListener(this);
        contractPtrList.setAdapter(mContractListAdapter);
        contractPtrList.setOnItemClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        contractIvAdd.setOnClickListener(this);
        contractReturnTop.setOnClickListener(this);
        tvSelectType.setOnClickListener(this);
        tvSelectStatus.setOnClickListener(this);

        blackTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle);
        blackTriangle.setBounds(0,0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());
        grayTriangle = mActivity.getResources().getDrawable(R.drawable.main_small_triangle_gray);
        grayTriangle.setBounds(0,0, blackTriangle.getMinimumWidth(), blackTriangle.getMinimumHeight());
        initSelectTypePop();
        initSelectStatusPop();
    }

    private void initSelectTypePop() {
        mSelectTypePop = new InspectionTaskStatePopUtils(mActivity);
        mSelectTypePop.setUpAnimation();
        mSelectTypePop.clearAnimation();
        mSelectTypePop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                InspectionStatusCountModel item = mSelectTypePop.getItem(position);
                mPresenter.doSelectTypeDevice(item);
                Resources resources = mActivity.getResources();
                if(position==0){
                    tvSelectType.setText(R.string.all_contracts);
                    tvSelectType.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    tvSelectType.setCompoundDrawables(null,null,grayTriangle,null);
                }else{
                    tvSelectType.setTextColor(resources.getColor(R.color.c_252525));
                    tvSelectType.setCompoundDrawables(null,null,blackTriangle,null);
                    tvSelectType.setText(item.statusTitle);
                }
                mSelectTypePop.dismiss();

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
                InspectionStatusCountModel item = mSelectStatusPop.getItem(position);
                mPresenter.doSelectStatusDevice(item);
                Resources resources = mActivity.getResources();
                if(position==0){
                    tvSelectStatus.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    tvSelectStatus.setCompoundDrawables(null,null,grayTriangle,null);
                    tvSelectStatus.setText(R.string.all_states);
                }else{
                    tvSelectStatus.setTextColor(resources.getColor(R.color.c_252525));
                    tvSelectStatus.setCompoundDrawables(null,null,blackTriangle,null);
                    tvSelectStatus.setText(item.statusTitle);
                }
                mSelectStatusPop.dismiss();

            }
        });
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
        if (mSelectStatusPop!=null) {
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
        if (mSelectTypePop!=null) {
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
            case R.id.contract_iv_add:
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
                if(mSelectStatusPop!=null&&mSelectStatusPop.isShowing()){
                    mSelectStatusPop.dismiss();
                }
                mPresenter.doSelectTypePop();
                break;
            case R.id.contract_tv_select_status:
                mPresenter.doSelectStatusPop();
                break;
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();
        // 通过id实例化选中的这个RadioButton
//        RadioButton choise = (RadioButton) mRootView.findViewById(id);
//        // 获取这个RadioButton的text内容
//        String output = choise.getText().toString();
//        Toast.makeText(MainActivity.this, "你的性别为：" + output, Toast.LENGTH_SHORT).show();
        //恢复没有数据的状态
        refreshLayout.setNoMoreData(false);
//        switch (id) {
//            case R.id.rb_contract_all:
//                mPresenter.requestContractDataAll();
//                break;
//            case R.id.rb_contract_business:
//                mPresenter.requestContractDataBusiness();
//                break;
//            case R.id.rb_contract_person:
//                mPresenter.requestContractDataPerson();
//                break;
//            default:
//                break;
//        }

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
