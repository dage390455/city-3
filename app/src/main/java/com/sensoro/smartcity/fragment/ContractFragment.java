package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.adapter.ContractListAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IContractFragmentView;
import com.sensoro.smartcity.presenter.ContractFragmentPresenter;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;
import static com.sensoro.smartcity.constant.Constants.INPUT;

public class ContractFragment extends BaseFragment<IContractFragmentView, ContractFragmentPresenter> implements
        IContractFragmentView, AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener,
        RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.contract_iv_menu_list)
    ImageView contractIvMenuList;
    @BindView(R.id.contract_title)
    TextView contractTitle;
    @BindView(R.id.contract_iv_add)
    ImageView contractIvAdd;
    @BindView(R.id.rg_contract_select)
    RadioGroup rgContractSelect;
    @BindView(R.id.contract_ptr_list)
    PullToRefreshListView contractPtrList;
    @BindView(R.id.contract_return_top)
    ImageView contractReturnTop;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private ContractListAdapter mContractListAdapter;

    public static ContractFragment newInstance(String input) {
        ContractFragment contractFragment = new ContractFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        contractFragment.setArguments(args);
        return contractFragment;
    }

    @Override
    protected void initData(Context activity) {
        mPrestener.initData(activity);
        initView();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        rgContractSelect.setOnCheckedChangeListener(this);
        contractPtrList.setRefreshing(false);
        contractPtrList.setMode(PullToRefreshBase.Mode.BOTH);
        contractPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                requestDataByDirection(DIRECTION_DOWN, false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                requestDataByDirection(DIRECTION_UP, false);
            }
        });
        mContractListAdapter = new ContractListAdapter(mRootFragment.getActivity());
        contractPtrList.setOnScrollListener(this);
        contractPtrList.setAdapter(mContractListAdapter);
        contractPtrList.setOnItemClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        contractIvMenuList.setOnClickListener(this);
        contractIvAdd.setOnClickListener(this);
        contractReturnTop.setOnClickListener(this);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_contract_list;
    }

    @Override
    protected ContractFragmentPresenter createPresenter() {
        return new ContractFragmentPresenter();
    }

    @Override
    public void onPullRefreshComplete() {
        contractPtrList.onRefreshComplete();
    }

    @Override
    public PullToRefreshBase.State getPullRefreshState() {
        return contractPtrList.getState();
    }

    @Override
    public void requestDataByDirection(int direction, boolean isFirst) {
        mPrestener.requestDataByDirection(direction, isFirst);
    }

    @Override
    public void updateContractList(List<ContractListInfo> data) {
        mContractListAdapter.setData(data);
        mContractListAdapter.notifyDataSetChanged();
    }

    @Override
    public void startAC(Intent intent) {
        mRootFragment.getActivity().startActivity(intent);
    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mRootFragment.getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {

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
        SensoroToast.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onDestroyView() {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPrestener.clickItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contract_iv_add:
                mPrestener.startToAdd();
                break;
            case R.id.contract_iv_menu_list:
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.contract_return_top:
                contractPtrList.getRefreshableView().smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int tempPos = contractPtrList.getRefreshableView().getFirstVisiblePosition();
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
        switch (id) {
            case R.id.rb_contract_all:
                mPrestener.requestContractDataAll();
                break;
            case R.id.rb_contract_business:
                mPrestener.requestContractDataBusiness();
                break;
            case R.id.rb_contract_person:
                mPrestener.requestContractDataPerson();
                break;
            default:
                break;
        }
    }
}
