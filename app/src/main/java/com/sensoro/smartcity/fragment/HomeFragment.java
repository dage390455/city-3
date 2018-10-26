package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapterHorizontal;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.presenter.HomeFragmentPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerRecyclerView;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerScaleHelper;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class HomeFragment extends BaseFragment<IHomeFragmentView, HomeFragmentPresenter> implements
        IHomeFragmentView, MenuDialogFragment.OnDismissListener, AppBarLayout.OnOffsetChangedListener, MainHomeFragRcContentAdapterHorizontal.OnLoadInnerListener {
    @BindView(R.id.fg_main_home_tv_title)
    TextView fgMainHomeTvTitle;
    @BindView(R.id.fg_main_home_imb_add)
    ImageButton fgMainHomeImbAdd;
    @BindView(R.id.fg_main_home_imb_search)
    ImageButton fgMainHomeImbSearch;
    @BindView(R.id.fg_main_home_rc_type)
    BannerRecyclerView fgMainHomeRcTypeHeader;
    @BindView(R.id.fg_main_home_rc_content)
    BannerRecyclerView fgMainHomeRcContent;
    @BindView(R.id.fg_main_home_tv_select_type)
    TextView fgMainHomeTvSelectType;
    @BindView(R.id.fg_main_home_ll_root)
    CoordinatorLayout fgMainHomeLlRoot;
    @BindView(R.id.tv_detection_point)
    TextView tvDetectionPoint;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.fl_main_home_select_type)
    FrameLayout flMainHomeSelectType;
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
    @BindView(R.id.home_iv_top_add)
    ImageButton homeIvTopAdd;
    private MainHomeFragRcContentAdapterHorizontal mMainHomeFragRcContentAdapter;
    private MainHomeFragRcTypeAdapter mMainHomeFragRcTypeHeaderAdapter;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private SelectDeviceTypePopUtils mSelectDeviceTypePop;
    //
    private BannerScaleHelper mBannerScaleHeaderHelper;
    private BannerScaleHelper mBannerScaleContentHelper;
    private int toolbarDirection = DIRECTION_DOWN;
    private int currentPosition = 0;
    private double currentPercent;

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
        homeIvTopSearch.setColorFilter(Color.WHITE);
        homeIvTopAdd.setColorFilter(Color.WHITE);
    }

    private void initPop() {
        mSelectDeviceTypePop = new SelectDeviceTypePopUtils(mRootFragment.getActivity());
        mSelectDeviceTypePop.setTypeStyle(2);
        mSelectDeviceTypePop.setSelectDeviceTypeItemClickListener(new SelectDeviceTypePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position, DeviceTypeModel item) {
                HomeTopModel homeTopModel = mMainHomeFragRcContentAdapter.getData().get(currentPosition);
                mPresenter.requestDataByTypes(position, homeTopModel);
                //选择类型的pop点击事件
                Resources resources = Objects.requireNonNull(mRootFragment.getActivity()).getResources();
                if ("全部".equals(item.name)) {
                    fgMainHomeTvSelectType.setText("全部类型");
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

    private void initRcTypeHeader() {
        mMainHomeFragRcTypeHeaderAdapter = new MainHomeFragRcTypeAdapter(mRootFragment.getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(false);
        fgMainHomeRcTypeHeader.setLayoutManager(linearLayoutManager);
        fgMainHomeRcTypeHeader.setAdapter(mMainHomeFragRcTypeHeaderAdapter);
        mBannerScaleHeaderHelper = new BannerScaleHelper();
        mBannerScaleHeaderHelper.attachToRecyclerView(fgMainHomeRcTypeHeader);
        fgMainHomeRcTypeHeader.setOnPageChangeListener(new BannerRecyclerView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LogUtils.loge(this, "setOnPageChangeListener mBannerScaleContentHelper-->fgMainHomeRcTypeHeader position = " + position);
                try {
                    currentPosition = position;
                    HomeTopModel homeTopModel = mMainHomeFragRcTypeHeaderAdapter.getData().get(position);
                    mPresenter.requestDataByStatus(homeTopModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        fgMainHomeRcTypeHeader.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    if (currentPercent == 0) {
                        try {
                            double widthHeader = recyclerView.getChildAt(0).getWidth();
                            double widthContent = fgMainHomeRcContent.getChildAt(0).getWidth();
                            currentPercent = widthHeader / widthContent;
                        } catch (Exception e) {
                            currentPercent = 1;
                        } finally {
                            if (currentPercent == 0) {
                                currentPercent = 1;
                            }
                        }
                    }
                    if (dx > 0) {
                        dx = (int) (dx / currentPercent + 0.5d);
                    } else if (dx < 0) {
                        dx = (int) (dx / currentPercent - 0.5d);
                    }
                    fgMainHomeRcContent.scrollBy(dx, dy);
                }
            }
        });
    }


    private void initRcContent() {
        //
        mMainHomeFragRcContentAdapter = new MainHomeFragRcContentAdapterHorizontal(mRootFragment.getActivity());
        mMainHomeFragRcContentAdapter.setOnLoadInnerListener(this);
        //
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fgMainHomeRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainHomeRcContent.setAdapter(mMainHomeFragRcContentAdapter);
        mBannerScaleContentHelper = new BannerScaleHelper();
        mBannerScaleContentHelper.setScale(1);
        mBannerScaleContentHelper.attachToRecyclerView(fgMainHomeRcContent);
        fgMainHomeRcContent.setOnPageChangeListener(new BannerRecyclerView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LogUtils.loge(this, "setOnPageChangeListener mBannerScaleContentHelper--> position = " + position);
                try {
                    currentPosition = position;
                    HomeTopModel homeTopModel = mMainHomeFragRcTypeHeaderAdapter.getData().get(position);
                    mPresenter.requestDataByStatus(homeTopModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //
        fgMainHomeRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == RecyclerView.SCROLL_STATE_IDLE &&
                        toolbarDirection == DIRECTION_DOWN) {
//                    mListRecyclerView.setre
                }
                if (xLinearLayoutManager.findFirstVisibleItemPosition() > 4) {
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
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {

                    if (currentPercent == 0) {
                        try {
                            double widthContent = recyclerView.getChildAt(0).getWidth();
                            double widthHeader = fgMainHomeRcTypeHeader.getChildAt(0).getWidth();
                            currentPercent = widthHeader / widthContent;
                        } catch (Exception e) {
                            currentPercent = 1;
                        } finally {
                            if (currentPercent == 0) {
                                currentPercent = 1;
                            }
                        }

                    }
                    if (dx > 0) {
                        dx = (int) (currentPercent * dx + 0.5d);
                    } else if (dx < 0) {
                        dx = (int) (currentPercent * dx - 0.5d);
                    }
                    fgMainHomeRcTypeHeader.scrollBy(dx, dy);
                }
            }
        });
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
        mPresenter.checkUpgrade();
    }

    @Override
    public void onFragmentStop() {

    }


    @Override
    public void startAC(Intent intent) {
        mRootFragment.getActivity().startActivity(intent);
    }

    @Override
    public void finishAc() {
        mRootFragment.getActivity().finish();
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
        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
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
    public void refreshHeaderData(final boolean isFirstInit, final List<HomeTopModel> data) {
        //如果需要带缩放的scale需要调用一下，否则缩放效果会出现缩放误差
        if (fgMainHomeRcTypeHeader.isComputingLayout()) {
            fgMainHomeRcTypeHeader.post(new Runnable() {
                @Override
                public void run() {
                    if (isFirstInit) {
                        //如果数据重新加载 添加滚动 防止错位
                        mBannerScaleHeaderHelper.initWidthData();
                    } else {
                        mBannerScaleHeaderHelper.setCurrentItem(mBannerScaleHeaderHelper.getCurrentItem(), true);
                    }
                    mMainHomeFragRcTypeHeaderAdapter.updateData(fgMainHomeRcTypeHeader, data);
                }
            });
            return;
        }
        if (isFirstInit) {
            //如果数据重新加载 添加滚动 防止错位
            mBannerScaleHeaderHelper.initWidthData();
        } else {
            mBannerScaleHeaderHelper.setCurrentItem(mBannerScaleHeaderHelper.getCurrentItem(), true);
        }
        mMainHomeFragRcTypeHeaderAdapter.updateData(fgMainHomeRcTypeHeader, data);
    }

    @Override
    public void returnTop() {
        fgMainHomeRcContent.smoothScrollToPosition(0);
    }

    @Override
    public void refreshContentData(final boolean isFirstInit, final List<HomeTopModel> dataList) {
        if (fgMainHomeRcContent.isComputingLayout()) {
            fgMainHomeRcContent.post(new Runnable() {
                @Override
                public void run() {
                    if (isFirstInit) {
                        mBannerScaleContentHelper.initWidthData();
                    } else {
                        mBannerScaleContentHelper.setCurrentItem(mBannerScaleContentHelper.getCurrentItem(), true);
                    }
                    mMainHomeFragRcContentAdapter.updateData(dataList);
                }
            });
            return;
        }
        if (isFirstInit) {
            mBannerScaleContentHelper.initWidthData();
        } else {
            mBannerScaleContentHelper.setCurrentItem(mBannerScaleContentHelper.getCurrentItem(), true);
        }
        mMainHomeFragRcContentAdapter.updateData(dataList);
    }

    @Override
    public void updateSelectDeviceTypePopAndShow(List<String> devicesTypes) {
        mSelectDeviceTypePop.updateSelectDeviceTypeList(devicesTypes);
        mSelectDeviceTypePop.showAtLocation(fgMainHomeLlRoot, Gravity.TOP);
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


    @OnClick({R.id.fg_main_home_imb_add, R.id.fg_main_home_imb_search, R.id.fg_main_home_tv_select_type, R.id.fl_main_home_select_type, R.id.home_iv_top_search, R.id.home_iv_top_add})
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
            case R.id.fl_main_home_select_type:
                boolean expand = toolbarDirection == DIRECTION_UP;
                appBarLayout.setExpanded(expand, true);
                break;
        }
    }


    private void showDialog() {
        MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
        menuDialogFragment.setOnDismissListener(this);
        menuDialogFragment.show(mRootFragment.getActivity().getSupportFragmentManager(), "mainMenuDialog");
        setImvAddVisible(false);
//        setImvSearchVisible(false);
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
        if (mMainHomeFragRcTypeHeaderAdapter != null) {
            mMainHomeFragRcTypeHeaderAdapter.onDestroy();
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
//        setImvSearchVisible(true);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {//张开
            toolbarDirection = DIRECTION_DOWN;
            homeToolbarMonitor.setVisibility(View.VISIBLE);
            homeTopToolbar.setVisibility(View.GONE);
            homeToolbarMonitor.setAlpha(1.0f);
            home_layout_head.setAlpha(1.0f);
            handleRefreshLayoutState(true);
//            LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN");
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//收缩
            toolbarDirection = DIRECTION_UP;
            homeToolbarMonitor.setVisibility(View.GONE);
            homeTopToolbar.setVisibility(View.VISIBLE);
            homeTopToolbar.setAlpha(1.0f);
            handleRefreshLayoutState(false);
//            LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP");
        }
        if (toolbarDirection == DIRECTION_DOWN) {
            if (Math.abs(verticalOffset) > 0) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                homeTopToolbar.setAlpha(bar_alpha);
                home_layout_head.setAlpha(1 - bar_alpha);
                homeTopToolbar.setVisibility(View.VISIBLE);
                homeToolbarMonitor.setVisibility(View.GONE);
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN bar_alpha = " + bar_alpha);
            }
//            else {
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN 0000");
//            }

        } else {
            if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange()) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                homeTopToolbar.setAlpha(bar_alpha);
                home_layout_head.setAlpha(1 - bar_alpha);
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP bar_alpha = " + bar_alpha);
            }
//            else {
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP 0000");
//            }

        }
    }

    //处理滑动逻辑
    private void handleRefreshLayoutState(boolean enableRefresh) {
        try {
            List<HomeTopModel> data = mMainHomeFragRcContentAdapter.getData();
            HomeTopModel homeTopModel = data.get(currentPosition);
            homeTopModel.smartRefreshLayout.setEnableRefresh(enableRefresh);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean onBackPressed() {
        if (mSelectDeviceTypePop.isShowing()) {
            mSelectDeviceTypePop.dismiss();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout, int position) {
        try {
            HomeTopModel homeTopModel = mMainHomeFragRcContentAdapter.getData().get(currentPosition);
            mPresenter.requestWithDirection(DIRECTION_DOWN, false, homeTopModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout, int position) {
        try {
            HomeTopModel homeTopModel = mMainHomeFragRcContentAdapter.getData().get(currentPosition);
            mPresenter.requestWithDirection(DIRECTION_UP, false, homeTopModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlarmInfoClick(View v, int position) {
        try {
            HomeTopModel homeTopModel = mMainHomeFragRcContentAdapter.getData().get(currentPosition);
            mPresenter.clickAlarmInfo(position, homeTopModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            HomeTopModel homeTopModel = mMainHomeFragRcContentAdapter.getData().get(currentPosition);
            mPresenter.clickItem(position, homeTopModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
