package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.presenter.HomeFragmentPresenter;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerRecyclerView;
import com.sensoro.smartcity.widget.calendar.cardgallery.BannerScaleHelper;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class HomeFragment extends BaseFragment<IHomeFragmentView, HomeFragmentPresenter> implements
        IHomeFragmentView, RecycleViewItemClickListener, MenuDialogFragment.OnDismissListener,
        MainHomeFragRcTypeAdapter.OnTopClickListener,
        MainHomeFragRcContentAdapter.OnItemAlarmInfoClickListener, AppBarLayout.OnOffsetChangedListener {
    @BindView(R.id.fg_main_home_tv_title)
    TextView fgMainHomeTvTitle;
    @BindView(R.id.fg_main_home_imb_add)
    ImageButton fgMainHomeImbAdd;
    @BindView(R.id.fg_main_home_imb_search)
    ImageButton fgMainHomeImbSearch;
    @BindView(R.id.fg_main_home_rc_type)
    BannerRecyclerView fgMainHomeRcType;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.fg_main_home_rc_content)
    RecyclerView fgMainHomeRcContent;
    @BindView(R.id.fg_main_home_tv_select_type)
    TextView fgMainHomeTvSelectType;
    @BindView(R.id.fg_main_home_ll_root)
    CoordinatorLayout fgMainHomeLlRoot;
    @BindView(R.id.tv_detection_point)
    TextView tvDetectionPoint;
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
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
    private MainHomeFragRcContentAdapter mMainHomeFragRcContentAdapter;
    private MainHomeFragRcTypeAdapter mMainHomeFragRcTypeAdapter;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private SelectDeviceTypePopUtils mSelectDeviceTypePop;
    //
    private BannerScaleHelper mBannerScaleHelper;
    private int toolbarDirection = DIRECTION_DOWN;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        appBarLayout.addOnOffsetChangedListener(this);
        initRcType();
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
                mPresenter.requestDataByTypes(position, item);
                //选择类型的pop点击事件
                fgMainHomeTvSelectType.setText(item.name);
                mSelectDeviceTypePop.dismiss();
            }
        });

    }

    private void initRcContent() {
        //
        mMainHomeFragRcContentAdapter = new MainHomeFragRcContentAdapter(mRootFragment.getActivity());
        mMainHomeFragRcContentAdapter.setOnItemClickListener(this);
        mMainHomeFragRcContentAdapter.setOnItemAlarmInfoClickListener(this);
        //
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainHomeRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainHomeRcContent.setAdapter(mMainHomeFragRcContentAdapter);
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

            }
        });
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestWithDirection(DIRECTION_DOWN);
                mPresenter.requestTopData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestWithDirection(DIRECTION_UP);
            }
        });
    }

    private void initRcType() {
        mMainHomeFragRcTypeAdapter = new MainHomeFragRcTypeAdapter(mRootFragment.getActivity());
        mMainHomeFragRcTypeAdapter.setOnTopClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(false);
        fgMainHomeRcType.setLayoutManager(linearLayoutManager);
        fgMainHomeRcType.setAdapter(mMainHomeFragRcTypeAdapter);
        mBannerScaleHelper = new BannerScaleHelper();
        mBannerScaleHelper.attachToRecyclerView(fgMainHomeRcType);
        fgMainHomeRcType.setOnPageChangeListener(new BannerRecyclerView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LogUtils.loge(this, "setOnPageChangeListener --> position = " + position);
                try {
                    HomeTopModel homeTopModel = mMainHomeFragRcTypeAdapter.getData().get(position);
                    mPresenter.requestDataByStatus(homeTopModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //
        /*  mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.e("TAG", "postDelayed scrollToPosition" );
                mBannerScaleHelper.scrollToPosition(4);
            }
        }, 2000);*/
        //
        fgMainHomeRcType.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
////                    int currentItem = mBannerScaleHelper.getCurrentItem();
////                    if (mLastPos == currentItem) return;
////                    int type = mMainHomeFragRcTypeAdapter.getData().get(currentItem).type;
////                    mPresenter.requestDataByStatus(type);
////                    mLastPos = currentItem;
//                }
//            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtils.loge(this, "onScrolled-->> dx = " + dx + ",dy = " + dy);
//                fgMainHomeRcContent.scrollTo(dx, 0);
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
    protected int fragmentStatusBarColor() {
        return R.color.white;
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
    public void refreshTop(boolean isFirstInit, final List<HomeTopModel> data) {
        //如果需要带缩放的scale需要调用一下，否则缩放效果会出现缩放误差
        if (isFirstInit) {
            //如果数据重新加载 添加滚动 防止错位
            mBannerScaleHelper.initWidthData();
        } else {
            mBannerScaleHelper.setCurrentItem(mBannerScaleHelper.getCurrentItem(), true);
        }
        mMainHomeFragRcTypeAdapter.updateData(fgMainHomeRcType, data);
    }

    @Override
    public void returnTop() {
        fgMainHomeRcContent.smoothScrollToPosition(0);
//        mReturnTopImageView.setVisibility(View.GONE);
    }

    @Override
    public void refreshData(List<DeviceInfo> dataList) {
        if (dataList.size() > 0) {
            mMainHomeFragRcContentAdapter.updateData(dataList);
        }

        setNoContentVisible(dataList.size() < 1);

//        if (dataList.size() < 5) {
//            mReturnTopImageView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        fgMainHomeRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
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
    public void recycleViewRefreshComplete() {
//        refreshLayout.computeScroll();
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void recycleViewRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
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

//        if (mGridAdapter != null) {
//            mGridAdapter.getData().clear();
//        }
//        if (mListAdapter != null) {
//            mListAdapter.getData().clear();
//        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
//        if (mListRecyclerView != null) {
//            mListRecyclerView.destroy();
//        }
//        if (mGridRecyclerView != null) {
//            mGridRecyclerView.destroy();
//        }
        if (mMainHomeFragRcTypeAdapter != null) {
            mMainHomeFragRcTypeAdapter.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.clickItem(position);
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
    public void onStatusChange(int status) {
//        mPresenter.requestDataByStatus(status);
    }

    @Override
    public void onAlarmInfoClick(View v, int position) {
        mPresenter.clickAlarmInfo(position);
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
//            LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN");
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//收缩
            toolbarDirection = DIRECTION_UP;
            homeToolbarMonitor.setVisibility(View.GONE);
            homeTopToolbar.setVisibility(View.VISIBLE);
            homeTopToolbar.setAlpha(1.0f);
            refreshLayout.setEnableRefresh(false);
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
                changeStatusBarColorAlpha(R.color.white, mPresenter.getCurrentColor(), bar_alpha);
            } else {
                changeStatusBarColorAlpha(R.color.white, R.color.white, 0);
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_DOWN 0000");
            }

        } else {
            if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange()) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                homeTopToolbar.setAlpha(bar_alpha);
                home_layout_head.setAlpha(1 - bar_alpha);
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP bar_alpha = " + bar_alpha);
                changeStatusBarColorAlpha(mPresenter.getCurrentColor(), R.color.white, 1 - bar_alpha);
            } else {
                changeStatusBarColorAlpha(mPresenter.getCurrentColor(), mPresenter.getCurrentColor(), 0);
//                LogUtils.loge(this, "onOffsetChanged-->> DIRECTION_UP 0000");
            }

        }
    }


    private void changeStatusBarColorAlpha(int preColor, int endColor, float alpha) {
        MainActivity activity = (MainActivity) mRootFragment.getActivity();
        if (activity != null) {
            ImmersionBar immersionBar = activity.immersionBar;
            if (immersionBar != null) {
                if (alpha == 0) {
                    immersionBar.fitsSystemWindows(true, endColor)
                            .statusBarColor(endColor)
                            .statusBarDarkFont(true).init();
                } else {
                    immersionBar.fitsSystemWindows(true, preColor, endColor, alpha)
                            .statusBarColorTransform(endColor)
                            .statusBarAlpha(alpha)
                            .init();
                }

            }
        }
    }


}
