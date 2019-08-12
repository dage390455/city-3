package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.model.SortConditionModel;
import com.sensoro.smartcity.presenter.HomeFragmentPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.CustomVRecyclerView;
import com.sensoro.smartcity.widget.SensoroHomeAlarmView;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerRecyclerView;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerScaleHelper;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;
import com.sensoro.smartcity.widget.popup.SelectSortConditionPopUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

public class HomeFragment extends BaseFragment<IHomeFragmentView, HomeFragmentPresenter> implements
        IHomeFragmentView, MenuDialogFragment.OnDismissListener, AppBarLayout.OnOffsetChangedListener, SensoroHomeAlarmView.OnSensoroHomeAlarmViewListener, OnRefreshListener, OnLoadMoreListener, MainHomeFragRcContentAdapter.OnContentItemClickListener {
    @BindView(R.id.fg_main_home_tv_title)
    TextView fgMainHomeTvTitle;
    @BindView(R.id.fg_main_home_imb_add)
    ImageButton fgMainHomeImbAdd;
    @BindView(R.id.fg_main_home_imb_search)
    ImageButton fgMainHomeImbSearch;
    @BindView(R.id.fg_main_home_rc_type)
    BannerRecyclerView fgMainHomeRcTypeHeader;
    @BindView(R.id.fg_main_home_rc_content)
    CustomVRecyclerView fgMainHomeRcContent;
    @BindView(R.id.fg_main_home_tv_select_type)
    TextView fgMainHomeTvSelectType;
    @BindView(R.id.fg_main_home_ll_root)
    CoordinatorLayout fgMainHomeLlRoot;
    @BindView(R.id.tv_detection_point)
    TextView tvDetectionPoint;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;


    @BindView(R.id.fg_main_home_tv_select_sort_condition)
    TextView fgMainHomeTvSelectSortCondition;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.home_layout_head)
    View home_layout_head;
    @BindView(R.id.home_top_toolbar)
    View homeTopToolbar;
    @BindView(R.id.home_toolbar_monitor)
    View homeToolbarMonitor;
    @BindView(R.id.home_tv_title_count)
    TextView homeTvTitleCount;
    @BindView(R.id.home_iv_top_search)
    ImageButton homeIvTopSearch;
    @BindView(R.id.iv_header_title_left)
    ImageView ivHeaderTitleLeft;
    @BindView(R.id.iv_header_title_right)
    ImageView ivHeaderTitleRight;
    @BindView(R.id.home_iv_top_add)
    ImageButton homeIvTopAdd;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.shav_home_alarm_tip)
    SensoroHomeAlarmView shavHomeAlarmTip;
    @BindView(R.id.nsv_no_content)
    LinearLayout noContent;
    private MainHomeFragRcTypeAdapter mMainHomeFragRcTypeHeaderAdapter;

    private MainHomeFragRcContentAdapter mMainHomeFragContentAdapter;//首页设备列表适配器
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private SelectDeviceTypePopUtils mSelectDeviceTypePop;
    //排序条件选择弹框工具类
    private SelectSortConditionPopUtils mSelectSortConditionPopUtils;

    private BannerScaleHelper mBannerScaleHeaderHelper;
    private int toolbarDirection = DIRECTION_DOWN;
    private int currentPosition = 0;
    private Parcelable recycleViewState;
    private LinearLayoutManager xLinearLayoutManager;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        appBarLayout.addOnOffsetChangedListener(this);
        initRcTypeHeader();
        initRcContent();
        initPop();
        initSortConditionPop();
    }

    private void initPop() {
        mSelectDeviceTypePop = new SelectDeviceTypePopUtils(mRootFragment.getActivity());
        mSelectDeviceTypePop.setTypeStyle(2);
        mSelectDeviceTypePop.setSelectDeviceTypeItemClickListener(new SelectDeviceTypePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position, DeviceTypeModel item) {
                HomeTopModel homeTopModel = ((HomeFragmentPresenter) mPresenter).getCurrentHomeModel();
                //数据容错
                if (homeTopModel != null) {

                    mPresenter.requestDataByTypes(position, homeTopModel);
                } else {
                    //尝试刷新所有数据
                    mPresenter.requestInitData(true, true);
                }
                //选择类型的pop点击事件
                Resources resources = Objects.requireNonNull(mRootFragment.getActivity()).getResources();
//                if ("全部".equals(item.name)) {
                if (position == 0) {
                    fgMainHomeTvSelectType.setText(R.string.all_types);
                    fgMainHomeTvSelectType.setTextColor(resources.getColor(R.color.c_a6a6a6));
                    Drawable drawable = resources.getDrawable(R.drawable.main_small_triangle_gray);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    fgMainHomeTvSelectType.setCompoundDrawables(null, null, drawable, null);
                } else {
                    fgMainHomeTvSelectType.setText(item.name);
                    Drawable drawable = resources.getDrawable(R.drawable.main_small_triangle);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    fgMainHomeTvSelectType.setTextColor(resources.getColor(R.color.c_252525));
                    fgMainHomeTvSelectType.setCompoundDrawables(null, null, drawable, null);
                }
                mSelectDeviceTypePop.dismiss();
            }
        });


    }


    private void initSortConditionPop() {

        mSelectSortConditionPopUtils = new SelectSortConditionPopUtils(mRootFragment.getActivity());
        mSelectSortConditionPopUtils.setmSelectFilterConditionItemClickListener(new SelectSortConditionPopUtils.SelectSortConditionItemClickListener() {
            @Override
            public void onSelectSortConditionItemClick(View view, int position, SortConditionModel sortCondition) {
                fgMainHomeTvSelectSortCondition.setText(sortCondition.title);
                mPresenter.setSelectedCondition(sortCondition);
                if(mPresenter.getCurrentHomeModel()!=null){
                    mPresenter.freshContentView(mPresenter.getCurrentHomeModel(), false);
                }
                mSelectSortConditionPopUtils.dismiss();
            }
        });

    }

    /**
     * 切换商户重置类型和排序条件
     */
    public  void resetTypeAndSortCondition(){

        //重置类型
        Resources resources = Objects.requireNonNull(mRootFragment.getActivity()).getResources();
        fgMainHomeTvSelectType.setText(R.string.all_types);
        fgMainHomeTvSelectType.setTextColor(resources.getColor(R.color.c_a6a6a6));
        Drawable drawable = resources.getDrawable(R.drawable.main_small_triangle_gray);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        fgMainHomeTvSelectType.setCompoundDrawables(null, null, drawable, null);


        //重置排序
        fgMainHomeTvSelectSortCondition.setText(R.string.sortcondition_def);
        mPresenter.resetConditionList();
    }


    private void initRcTypeHeader() {
        mMainHomeFragRcTypeHeaderAdapter = new MainHomeFragRcTypeAdapter(mRootFragment.getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fgMainHomeRcTypeHeader.setLayoutManager(linearLayoutManager);
        fgMainHomeRcTypeHeader.setAdapter(mMainHomeFragRcTypeHeaderAdapter);
        fgMainHomeRcTypeHeader.setFocusableInTouchMode(false);
        fgMainHomeRcTypeHeader.setFocusable(false);
        fgMainHomeRcTypeHeader.setHasFixedSize(true);
        mBannerScaleHeaderHelper = new BannerScaleHelper();
        mBannerScaleHeaderHelper.setNeedInitScrollToPosition(false);
        mBannerScaleHeaderHelper.attachToRecyclerView(fgMainHomeRcTypeHeader);
        fgMainHomeRcTypeHeader.setOnPageChangeListener(new BannerRecyclerView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                try {
                    currentPosition = position;
                    List<HomeTopModel> homeTopModels = mPresenter.getHomeTopModels();
                    if (homeTopModels.size() > position) {
                        HomeTopModel homeTopModel = homeTopModels.get(position);
                        mPresenter.requestDataByStatus(homeTopModel);
                        mPresenter.freshContentView(homeTopModel, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO 可以考虑异常刷新所有 并重置
                }

            }
        });

    }

    @Override
    public int getFirstVisibleItemPosition() {
        try {
            return xLinearLayoutManager.findFirstVisibleItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void startAnimation(View view, int animResID) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), animResID);
        view.setAnimation(animation);
        animation.start();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = fgMainHomeRcTypeHeader.getLayoutManager();
        if (layoutManager != null) {
            recycleViewState = layoutManager.onSaveInstanceState();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (recycleViewState != null) {
            RecyclerView.LayoutManager layoutManager = fgMainHomeRcTypeHeader.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(recycleViewState);
            }
        }
    }

    private void initRcContent() {
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

        //changqi添加
        mMainHomeFragContentAdapter = new MainHomeFragRcContentAdapter(mRootFragment.getActivity());
        mMainHomeFragContentAdapter.setHasStableIds(true);

        xLinearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        //changqi添加
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainHomeRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainHomeRcContent.setNestedScrollingEnabled(true);
        fgMainHomeRcContent.setAdapter(mMainHomeFragContentAdapter);
        mMainHomeFragContentAdapter.setOnContentItemClickListener(this);
        fgMainHomeRcContent.setOverScrollMode(View.OVER_SCROLL_NEVER);


    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected HomeFragmentPresenter createPresenter() {
        return new HomeFragmentPresenter();
    }

    @Override
    public void onFragmentStart() {
        //检查更新
//        mPresenter.checkUpgrade();
    }

    @Override
    public void onFragmentStop() {

    }


    @Override
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
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
            if (mProgressUtils != null) {
                mProgressUtils.showProgress();
            }
        }
        isShowDialog = true;
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @Override
    public void updateRcTypeAdapter(List<String> data) {
    }

    @Override
    public void setImvAddVisible(boolean isVisible) {
        fgMainHomeImbAdd.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setImvSearchVisible(boolean isVisible) {
        fgMainHomeImbSearch.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void refreshHeaderData(final boolean isResetHeaderPosition, final boolean isFirstInit, final List<HomeTopModel> data) {
        //防止大量数据刷新导致
        if (fgMainHomeRcTypeHeader.isComputingLayout()) {
            fgMainHomeRcTypeHeader.post(new Runnable() {
                @Override
                public void run() {
                    freshHeader(isResetHeaderPosition, isFirstInit, data);
                }
            });
            return;
        }
        freshHeader(isResetHeaderPosition, isFirstInit, data);
    }

    private void freshHeader(boolean isResetHeaderPosition, boolean isFirstInit, List<HomeTopModel> data) {
        if (isResetHeaderPosition) {
            if (mPresenter.getCurrentHomeModel() != null) {
                mBannerScaleHeaderHelper.scrollToPosition(0);
            }
        }
        if (isFirstInit) {
            mBannerScaleHeaderHelper.initWidthData();
            mMainHomeFragRcTypeHeaderAdapter.updateData(fgMainHomeRcTypeHeader, data);

        } else {
            mBannerScaleHeaderHelper.setCurrentItem(mBannerScaleHeaderHelper.getCurrentItem(), true);
            mMainHomeFragRcTypeHeaderAdapter.updateData(fgMainHomeRcTypeHeader, data);
        }

        //如果需要带缩放的scale需要调用一下，否则缩放效果会出现缩放误差

    }

    @Override
    public void returnTop() {
        fgMainHomeRcContent.smoothScrollToPosition(0);
    }


    @Override
    public synchronized void refreshContentData(final boolean isFirstInit, final boolean isPageChanged, List<DeviceInfo> deviceInfoList) {
        synchronized (deviceInfoList) {

            if (deviceInfoList.size() > 0) {
                fgMainHomeRcContent.setVisibility(View.VISIBLE);
                noContent.setVisibility(View.GONE);
                if (fgMainHomeRcContent.isComputingLayout()) {
//                查找当前显示的进行更新操作
                    fgMainHomeRcContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPosition >= 0) {
                                //TODO 所有的postdeleay操作没有生命周期管理的都需要对控件判空
                                if (fgMainHomeRcContent != null) {
                                    mMainHomeFragContentAdapter.updateData(deviceInfoList);
                                    if (isPageChanged) {
                                        startAnimation(fgMainHomeRcContent, R.anim.anim_recycleview_item);
                                    }
                                }

                            }

                        }
                    }, 50);
                    return;
                }
                if (currentPosition >= 0) {
                    mMainHomeFragContentAdapter.updateData(deviceInfoList);
                    if (isPageChanged) {
                        startAnimation(fgMainHomeRcContent, R.anim.anim_recycleview_item);
                    }
                }
            } else {

                if (fgMainHomeRcContent.isComputingLayout()) {
//                查找当前显示的进行更新操作
                    fgMainHomeRcContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //TODO 所有的postdeleay操作没有生命周期管理的都需要对控件判空
                            if (fgMainHomeRcContent != null && noContent != null) {
                                mMainHomeFragContentAdapter.updateData(deviceInfoList);
                                fgMainHomeRcContent.setVisibility(View.GONE);
                                noContent.setVisibility(View.VISIBLE);
                            }


                        }
                    }, 50);
                    return;
                }
                mMainHomeFragContentAdapter.updateData(deviceInfoList);
                fgMainHomeRcContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fgMainHomeRcContent != null && noContent != null) {
                            fgMainHomeRcContent.setVisibility(View.GONE);
                            noContent.setVisibility(View.VISIBLE);
                        }

                    }
                }, 50);


            }
        }
    }


    @Override
    public void updateSelectDeviceTypePopAndShow(List<String> devicesTypes) {
        mSelectDeviceTypePop.updateSelectDeviceTypeList(devicesTypes);
        mSelectDeviceTypePop.showAtLocation(fgMainHomeLlRoot, Gravity.TOP);
    }


    @Override
    public void updateSelectFilterConditionPopAndShow(List mSortConditionList, SortConditionModel selectedCondition) {
        mSelectSortConditionPopUtils.updateSortConditionList(mSortConditionList, selectedCondition);
        mSelectSortConditionPopUtils.showAtLocation(fgMainHomeLlRoot, Gravity.TOP);
    }

    @Override
    public void updateSelectFilterCondition(List mSortConditionList, SortConditionModel selectedCondition) {
        mSelectSortConditionPopUtils.updateSortConditionList(mSortConditionList, selectedCondition);
    }

    @Override
    public void setToolbarTitleCount(String text) {
        homeTvTitleCount.setText(text);
    }

    @Override
    public void setToolbarTitleBackgroundColor(int color) {
        homeTopToolbar.setBackgroundColor(mRootFragment.getActivity().getResources().getColor(color));
    }

    @Override
    public void setDetectionPoints(String count) {
        tvDetectionPoint.setText(count);
    }


    //    @OnClick({R.id.fg_main_home_imb_add, R.id.fg_main_home_imb_search, R.id.fg_main_home_tv_select_type, R.id.fl_main_home_select_type, R.id.home_iv_top_search, R.id.home_iv_top_add, R.id.iv_header_title_left, R.id.iv_header_title_right})
    @OnClick({R.id.fg_main_home_imb_add, R.id.fg_main_home_imb_search, R.id.fg_main_home_tv_select_type, R.id.fg_main_home_tv_select_sort_condition, R.id.home_iv_top_search, R.id.home_iv_top_add, R.id.iv_header_title_left, R.id.iv_header_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_iv_top_add:
            case R.id.fg_main_home_imb_add:
                showDialog();
                break;
            case R.id.home_iv_top_search:
            case R.id.fg_main_home_imb_search:
                mPresenter.doSearch();
                break;
            case R.id.fg_main_home_tv_select_type:
                mPresenter.updateSelectDeviceTypePopAndShow();
                break;
            case R.id.fg_main_home_tv_select_sort_condition:
                mPresenter.updateSelectSortConditionPopAndShow();
                break;
            case R.id.iv_header_title_left:
                try {
                    int index = currentPosition - 1;
                    setHeaderTitleLeftArrow(index);
                    currentPosition = index;
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO 这个一般需要设置一下 例如重新回归position为0
                }
                break;
            case R.id.iv_header_title_right:
                try {
                    int index = currentPosition + 1;
                    setHeaderTitleRightArrow(index);
                    currentPosition = index;

                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO 这个一般需要设置一下 例如重新回归position为0
                }
                break;
        }
    }

    private void setHeaderTitleRightArrow(int index) {
        int itemCount = mMainHomeFragRcTypeHeaderAdapter.getItemCount();
        try {
            LogUtils.loge("iv_header_title --->> right currentPosition = " + currentPosition + ",itemCount = " + itemCount);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (index >= 0 && index <= itemCount - 1) {
            if (index == itemCount - 1) {
                setImvHeaderRightVisible(false);
            } else {
                setImvHeaderRightVisible(true);
            }
            if (index == 0) {
                setImvHeaderLeftVisible(false);
            } else {
                setImvHeaderLeftVisible(true);
            }
            mBannerScaleHeaderHelper.setCurrentItem(index, true);
        }
    }

    private void setHeaderTitleLeftArrow(int index) {
        int itemCount = mMainHomeFragRcTypeHeaderAdapter.getItemCount();
        try {
            LogUtils.loge("iv_header_title --->> left currentPosition = " + currentPosition + ",itemCount = " + itemCount);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (index >= 0 && index <= itemCount - 1) {
            if (index == 0) {
                setImvHeaderLeftVisible(false);
            } else {
                setImvHeaderLeftVisible(true);
            }
            if (index == itemCount - 1) {
                setImvHeaderRightVisible(false);
            } else {
                setImvHeaderRightVisible(true);
            }
            mBannerScaleHeaderHelper.setCurrentItem(index, true);
        }
    }

    private void setAlarmScrolled(int index) {
        int itemCount = mMainHomeFragRcTypeHeaderAdapter.getItemCount();
        try {
            LogUtils.loge("iv_header_title --->> left currentPosition = " + currentPosition + ",itemCount = " + itemCount);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (index >= 0 && index <= itemCount - 1) {

            mBannerScaleHeaderHelper.setCurrentItem(index, true);
            if (index == 0) {
                setImvHeaderLeftVisible(false);
            } else {
                setImvHeaderLeftVisible(true);
            }
            if (index == itemCount - 1) {
                setImvHeaderRightVisible(false);
            } else {
                setImvHeaderRightVisible(true);
            }
        }
    }


    private void showDialog() {
        MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
        menuDialogFragment.setOnDismissListener(this);
        menuDialogFragment.show(mRootFragment.getActivity().getSupportFragmentManager(), "mainMenuDialog");
        setImvAddVisible(false);
        setImvTopAddVisible(false);
    }

    @Override
    public void setImvTopAddVisible(boolean b) {
        homeIvTopAdd.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setImvHeaderLeftVisible(boolean isVisible) {
        ivHeaderTitleLeft.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setImvHeaderRightVisible(boolean isVisible) {
        ivHeaderTitleRight.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        if (mMainHomeFragRcTypeHeaderAdapter != null) {
            mMainHomeFragRcTypeHeaderAdapter.onDestroy();
        }
        if (shavHomeAlarmTip != null) {
            shavHomeAlarmTip.onDestroyPop();
        }
        super.onDestroyView();
    }


    @Override
    public void onMenuDialogFragmentDismiss(int resId) {
        switch (resId) {
            case R.id.dialog_main_home_menu_imv_close:
                break;
            case R.id.rl_fast_deploy:
                mPresenter.doScanDeploy();
                break;
            case R.id.rl_fast_contract:
                mPresenter.doContract();
                break;
            case R.id.rl_fast_scan_login:
                mPresenter.doScanLogin();
                break;
            case R.id.dialog_main_home_menu_rl_root:
                break;
        }
        setImvAddVisible(true);
        setImvTopAddVisible(true);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {//张开
            toolbarDirection = DIRECTION_DOWN;
            homeToolbarMonitor.setVisibility(View.VISIBLE);
            homeTopToolbar.setVisibility(View.GONE);
            homeToolbarMonitor.setAlpha(1.0f);
            home_layout_head.setAlpha(1.0f);
            refreshLayout.setEnableRefresh(true);
            refreshLayout.setEnableLoadMore(false);
            try {
                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//收缩
            toolbarDirection = DIRECTION_UP;
            homeToolbarMonitor.setVisibility(View.GONE);
            homeTopToolbar.setVisibility(View.VISIBLE);
            homeTopToolbar.setAlpha(1.0f);
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadMore(true);
            try {
                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        try {
            LogUtils.loge(this, "onOffsetChanged-->> 执行 ---- ");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (toolbarDirection == DIRECTION_DOWN) {
            if (Math.abs(verticalOffset) > 0) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                homeTopToolbar.setAlpha(bar_alpha);
                home_layout_head.setAlpha(1 - bar_alpha);
                homeTopToolbar.setVisibility(View.VISIBLE);
                homeToolbarMonitor.setVisibility(View.GONE);
            }

        } else {
            if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange()) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                homeTopToolbar.setAlpha(bar_alpha);
                home_layout_head.setAlpha(1 - bar_alpha);
                try {
                    LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP bar_alpha = " + bar_alpha);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

        }
    }


    public boolean onBackPressed() {
        try {
            if (mSelectDeviceTypePop.isShowing()) {
                mSelectDeviceTypePop.dismiss();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAlarmInfoClick(View v, int position) {
        try {
            mPresenter.clickAlarmInfo(position, mPresenter.getCurrentHomeModel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            mPresenter.clickItem(position, mPresenter.getCurrentHomeModel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recycleViewRefreshComplete() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }

    }

    @Override
    public void recycleViewRefreshCompleteNoMoreData() {
        if (refreshLayout != null) {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

    }

    @Override
    public void showAlarmInfoView() {
        try {
            LogUtils.loge("shoAlarmWindow", "showAlarmInfoView");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (shavHomeAlarmTip != null) {
            shavHomeAlarmTip.show(this);
        }
    }

    @Override
    public void dismissAlarmInfoView() {
        if (shavHomeAlarmTip != null) {
            shavHomeAlarmTip.dismiss();
        }
    }

    @Override
    public void onAlarmCheckClick() {
        try {
            HomeTopModel homeTopModel = mMainHomeFragRcTypeHeaderAdapter.getData().get(0);
            if (homeTopModel.status == 0) {
                setAlarmScrolled(0);
                mPresenter.updateHeaderTop(homeTopModel);
                currentPosition = 0;
            } else {
                toastShort(mRootFragment.getString(R.string.device_alert_removed));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        try {
            if (mPresenter.getCurrentHomeModel() == null) {
                mPresenter.requestInitData(false, true);
                return;
            }
            mPresenter.requestWithDirection(DIRECTION_DOWN, false, mPresenter.getCurrentHomeModel());
        } catch (Exception e) {
            e.printStackTrace();
            mPresenter.requestInitData(false, true);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        try {
            if (mPresenter.getCurrentHomeModel() == null) {
                recycleViewRefreshComplete();
                return;
            }
            mPresenter.requestWithDirection(DIRECTION_UP, false, mPresenter.getCurrentHomeModel());
        } catch (Exception e) {
            e.printStackTrace();
            recycleViewRefreshComplete();
        }
    }
}
