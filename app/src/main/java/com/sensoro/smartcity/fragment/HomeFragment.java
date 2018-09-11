package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.adapter.TypeSelectAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.presenter.HomeFragmentPresenter;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class HomeFragment extends BaseFragment<IHomeFragmentView, HomeFragmentPresenter> implements
        IHomeFragmentView, RecycleViewItemClickListener, MenuDialogFragment.OnDismissListener {
    @BindView(R.id.fg_main_home_tv_title)
    TextView fgMainHomeTvTitle;
    @BindView(R.id.fg_main_home_imb_add)
    ImageButton fgMainHomeImbAdd;
    @BindView(R.id.fg_main_home_imb_search)
    ImageButton fgMainHomeImbSearch;
    @BindView(R.id.fg_main_home_rc_type)
    RecyclerView fgMainHomeRcType;
    @BindView(R.id.fg_main_home_rc_content)
    XRecyclerView fgMainHomeRcContent;
    @BindView(R.id.fg_main_home_tv_select_type)
    TextView fgMainHomeTvSelectType;
    @BindView(R.id.fg_main_home_ll_root)
    LinearLayout fgMainHomeLlRoot;

    private MainHomeFragRcContentAdapter mMainHomeFragRcContentAdapter;
    private MainHomeFragRcTypeAdapter mMainHomeFragRcTypeAdapter;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private PopupWindow mPopupWindow;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        initRcType();
        initRcContent();
        initPop();

    }

    private void initPop() {
        View view = LayoutInflater.from(mRootFragment.getActivity()).inflate(R.layout.item_pop_type_select, null);
        RecyclerView mRcTypeSelect = view.findViewById(R.id.pop_type_select_rc);
        TypeSelectAdapter mTypeSelectAdapter = new TypeSelectAdapter(mRootFragment.getActivity());
        GridLayoutManager manager = new GridLayoutManager(mRootFragment.getActivity(), 4);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRootFragment.getActivity(), DividerItemDecoration.VERTICAL);
        mRcTypeSelect.addItemDecoration(dividerItemDecoration);
        mRcTypeSelect.setLayoutManager(manager);
        mRcTypeSelect.setAdapter(mTypeSelectAdapter);
        mTypeSelectAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //选择类型的pop点击事件
                fgMainHomeTvSelectType.setText(Constants.SELECT_TYPE[position]);
                //选择的类型：
                String selectTypeValue = Constants.SELECT_TYPE_VALUES[position];
            }
        });
        mPopupWindow = new PopupWindow(mRootFragment.getActivity());
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setFocusable(true);
    }

    private void initRcContent() {
        mMainHomeFragRcContentAdapter = new MainHomeFragRcContentAdapter(mRootFragment.getActivity());
        mMainHomeFragRcContentAdapter.setOnItemClickLisenter(this);
        //
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainHomeRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainHomeRcContent.setAdapter(mMainHomeFragRcContentAdapter);
        fgMainHomeRcContent.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        fgMainHomeRcContent.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
//        int spacingInPixels = mRootFragment.getResources().getDimensionPixelSize(R.dimen.x8);
//        fgMainHomeRcContent.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        fgMainHomeRcContent.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isShowDialog = false;
                mPresenter.requestWithDirection(DIRECTION_DOWN);
                mPresenter.requestTopData(false);
            }

            @Override
            public void onLoadMore() {
                isShowDialog = false;
                mPresenter.requestWithDirection(DIRECTION_UP);
            }
        });
        fgMainHomeRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
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
    }

    private void initRcType() {
        mMainHomeFragRcTypeAdapter = new MainHomeFragRcTypeAdapter(mRootFragment.getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fgMainHomeRcType.setLayoutManager(linearLayoutManager);
        fgMainHomeRcType.setAdapter(mMainHomeFragRcTypeAdapter);
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
    public void refreshTop(boolean isFirstInit, int alarmCount, int lostCount, int inactiveCount) {

    }

    @Override
    public void returnTop() {
        fgMainHomeRcContent.smoothScrollToPosition(0);
//        mReturnTopImageView.setVisibility(View.GONE);
    }

    @Override
    public void refreshData(List<DeviceInfo> dataList) {
        mMainHomeFragRcContentAdapter.setData(dataList);
        mMainHomeFragRcContentAdapter.notifyDataSetChanged();
        fgMainHomeRcContent.refreshComplete();
//        if (dataList.size() < 5) {
//            mReturnTopImageView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void showTypePopupView() {
        mPopupWindow.showAtLocation(fgMainHomeLlRoot,Gravity.TOP,0,0);
    }

    @Override
    public void setTypeView(String typesText) {

    }

    @Override
    public void recycleViewRefreshComplete() {
        fgMainHomeRcContent.refreshComplete();
    }


    @OnClick({R.id.fg_main_home_imb_add, R.id.fg_main_home_imb_search, R.id.fg_main_home_tv_select_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_home_imb_add:
                addImbRotate();
                break;
            case R.id.fg_main_home_imb_search:
                break;
            case R.id.fg_main_home_tv_select_type:
//                showSelectTypePop();
                showTypePopupView();
                break;
        }
    }

    private void showSelectTypePop() {
//        if (Build.VERSION.SDK_INT < 24) {
//            mPopupWindow.showAsDropDown(fgMainHomeTvSelectType);
//        } else {  // 适配 android 7.0
//            int[] location = new int[2];
//            fgMainHomeTvSelectType.getLocationOnScreen(location);
//            Point point = new Point();
//            mRootFragment.getActivity().getWindowManager().getDefaultDisplay().getSize(point);
//            int tempheight = mPopupWindow.getHeight();
//            if (tempheight == WindowManager.LayoutParams.MATCH_PARENT || point.y <= tempheight) {
//                mPopupWindow.setHeight(point.y - location[1] - fgMainHomeTvSelectType.getHeight());
//            }
//            mPopupWindow.showAtLocation(fgMainHomeTvSelectType, Gravity.NO_GRAVITY, location[0], location[1] + fgMainHomeTvSelectType.getHeight());
//        }
//        mPopupWindow.showAsDropDown();
    }

    private void addImbRotate() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fgMainHomeImbAdd.startAnimation(rotateAnimation);
    }

    private void showDialog() {
        fgMainHomeImbAdd.clearAnimation();
        MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
        menuDialogFragment.setOnDismissListener(this);
        menuDialogFragment.show(getActivity().getSupportFragmentManager(), "mainMenuDialog");
        setImvAddVisible(false);
        setImvSearchVisible(false);
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
            case R.id.dialog_main_home_menu_tv_quick_deploy:
                //TODO 设备部署
                toastShort("设备部署");
                break;
            case R.id.dialog_main_home_menu_new_tv_construction:
                //TODO 合同管理
                toastShort("合同管理");
                break;
            case R.id.dialog_main_home_menu_tv_scan_login:
                //TODO 扫码登录
                mPresenter.doScanLogin();
                toastShort("扫码登录");
                break;
            case R.id.dialog_main_home_menu_rl_root:
                break;
        }
        setImvAddVisible(true);
        setImvSearchVisible(true);
    }

}
