package com.sensoro.smartcity.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchDeviceActivity;
import com.sensoro.smartcity.activity.SensorDetailActivity;
import com.sensoro.smartcity.adapter.IndexGridAdapter;
import com.sensoro.smartcity.adapter.IndexListAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.Character;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroXGridLayoutManager;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.popup.SensoroPopupStatusView;
import com.sensoro.smartcity.widget.popup.SensoroPopupTypeView;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;
import com.sensoro.volleymanager.NumberDeserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.view.View.VISIBLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by sensoro on 17/11/6.
 */

public class IndexFragment extends Fragment implements Runnable, Constants, View.OnClickListener, RecycleViewItemClickListener, ViewPager.OnPageChangeListener, AppBarLayout.OnOffsetChangedListener {

    private View rootView;
    private ImageView mSwitchImageView;
    private ImageView mSearchImageView;
    private ImageView mTypeImageView;
    private ImageView mStatusImageView;
    private ImageView mReturnTopImageView;
    private SensoroPopupTypeView mTypePopupView;
    private SensoroPopupStatusView mStatusPopupView;
    private IndexListAdapter mListAdapter;
    private IndexGridAdapter mGridAdapter;
    private AppBarLayout mAppBarLayout = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mToolbar1 = null;
    private View mToolbar2 = null;
    private TextView mTypeTextView;
    private TextView mStatusTextView;
    private TextView mTitleTextView;
    private TextView mHeadAlarmNumTextView;
    private TextView mHeadLostNumTextView;
    private TextView mHeadInactiveNumTextView;
    private TextView mHeadAlarmTitleTextView;
    private TextView mHeadLostTitleTextView;
    private TextView mHeadInactiveTitleTextView;
    private XRecyclerView mListRecyclerView;
    private XRecyclerView mGridRecyclerView;
    private SensoroXLinearLayoutManager xLinearLayoutManager;
    private SensoroXGridLayoutManager xGridLayoutManager;
    private Toolbar mToolbar = null;
    private int toolbarDirection = DIRECTION_DOWN;
    private int switchType = TYPE_LIST;
    private int mTypeSelectedIndex = 0;
    private int mStatusSelectedIndex = 0;
    private LinearLayout alarmLayout;
    private LinearLayout mListLayout;
    private LinearLayout mGridLayout;
    private LinearLayout mHeadLayout;
    private SensoroShadowView mTypeShadowLayout;
    private SensoroShadowView mStatusShadowLayout;
    private Animation returnTopAnimation;
    private ProgressDialog mProgressDialog;
    private List<DeviceInfo> mDataList = new ArrayList<>();
    private SensoroCityApplication cityApplication;
    private Handler mHandler;
    private Gson gson;
    private SoundPool soundPool;
    private int soundId = 0;
    private int page = 1;
    private volatile boolean isAlarmPlay = false;

    public static IndexFragment newInstance(Character input) {
        IndexFragment indexFragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putSerializable(INPUT, input);
        indexFragment.setArguments(args);
        return indexFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面埋点
        StatService.onPageStart(getActivity(), "IndexFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面埋点
        StatService.onPageEnd(getActivity(), "IndexFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_index, container, false);
            init();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
        }
        if (mGridAdapter != null) {
            mGridAdapter.getData().clear();
        }
        if (mListAdapter != null) {
            mListAdapter.getData().clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void init() {
        try {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(double.class, new NumberDeserializer())
                    .registerTypeAdapter(int.class, new NumberDeserializer())
                    .registerTypeAdapter(Number.class, new NumberDeserializer());
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load(this.getContext(), R.raw.alarm, 1);
            mHandler = new Handler();
            mHandler.postDelayed(this, 3000);
            gson = gsonBuilder.create();
            cityApplication = (SensoroCityApplication) getActivity().getApplication();
            mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
            mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
            mToolbar1 = rootView.findViewById(R.id.index_toolbar1);
            mToolbar2 = rootView.findViewById(R.id.index_toolbar2);
            mAppBarLayout.addOnOffsetChangedListener(this);
            mTitleTextView = (TextView) rootView.findViewById(R.id.index_tv_title);
            mHeadLayout = (LinearLayout) rootView.findViewById(R.id.index_layout_head1);
            alarmLayout = (LinearLayout) rootView.findViewById(R.id.index_head_alarm_layout) ;
            mHeadAlarmNumTextView = (TextView) rootView.findViewById(R.id.index_head_alarm_num);
            mHeadLostNumTextView = (TextView) rootView.findViewById(R.id.index_head_lost_num);
            mHeadInactiveNumTextView = (TextView) rootView.findViewById(R.id.index_head_inactive_num);
            mHeadAlarmTitleTextView = (TextView) rootView.findViewById(R.id.index_head_alarm_num_title);
            mHeadLostTitleTextView = (TextView) rootView.findViewById(R.id.index_head_lost_num_title);
            mHeadInactiveTitleTextView = (TextView) rootView.findViewById(R.id.index_head_inactive_num_title);
            mSwitchImageView = (ImageView) rootView.findViewById(R.id.index_iv_switch);
            mSearchImageView = (ImageView) rootView.findViewById(R.id.index_iv_search);

            mTypeTextView = (TextView) rootView.findViewById(R.id.index_tv_type);
            mStatusTextView = (TextView) rootView.findViewById(R.id.index_tv_status);
            mTypeImageView = (ImageView) rootView.findViewById(R.id.index_iv_type);
            mStatusImageView = (ImageView) rootView.findViewById(R.id.index_iv_status);
            mReturnTopImageView = (ImageView) rootView.findViewById(R.id.index_return_top);
            mReturnTopImageView.setOnClickListener(this);
            mTypeTextView.setOnClickListener(this);
            mStatusTextView.setOnClickListener(this);
            mTypeImageView.setOnClickListener(this);
            mStatusImageView.setOnClickListener(this);
            mSwitchImageView.setOnClickListener(this);
            mSearchImageView.setOnClickListener(this);
            mHeadAlarmNumTextView.setOnClickListener(this);
            mHeadAlarmTitleTextView.setOnClickListener(this);
            mHeadInactiveNumTextView.setOnClickListener(this);
            mHeadInactiveTitleTextView.setOnClickListener(this);
            mHeadLostNumTextView.setOnClickListener(this);
            mHeadLostTitleTextView.setOnClickListener(this);
            rootView.findViewById(R.id.index_iv_menu_list).setOnClickListener(this);
            rootView.findViewById(R.id.index_iv_menu_list_reverse).setOnClickListener(this);
            rootView.findViewById(R.id.index_iv_search_reverse).setOnClickListener(this);
            mSearchImageView.setColorFilter(Color.WHITE);
            mTypeShadowLayout = (SensoroShadowView) rootView.findViewById(R.id.index_type_shadow);
            mTypePopupView = (SensoroPopupTypeView) rootView.findViewById(R.id.index_type_popup);
            mStatusShadowLayout = (SensoroShadowView) rootView.findViewById(R.id.index_status_shadow);
            mStatusPopupView = (SensoroPopupStatusView) rootView.findViewById(R.id.index_status_popup);
            mListLayout = (LinearLayout) rootView.findViewById(R.id.layout_index_list);
            mGridLayout = (LinearLayout) rootView.findViewById(R.id.layout_index_grid);
            returnTopAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.return_top_in_anim);
            mReturnTopImageView.setAnimation(returnTopAnimation);
            mReturnTopImageView.setVisibility(View.GONE);
            requestDeviceTypeCountData(true);
            switchType = TYPE_LIST;
            initListView();
            initGridView();
            Character character = (Character)getArguments().getSerializable(INPUT);
            refreshCityInfo(character);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }


    }

    private void initListView() {
        mListRecyclerView = (XRecyclerView) rootView.findViewById(R.id.index_rv_list);
        xLinearLayoutManager = new SensoroXLinearLayoutManager(getContext());
        mListAdapter = new IndexListAdapter(getContext(), this);
        mListRecyclerView.setAdapter(mListAdapter);
        mListRecyclerView.setLayoutManager(xLinearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x15);
        mListRecyclerView.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mListRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                requestWithDirection(DIRECTION_DOWN);
            }

            @Override
            public void onLoadMore() {
                requestWithDirection(DIRECTION_UP);
            }
        });
        requestWithDirection(DIRECTION_DOWN);
        mListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE && toolbarDirection == DIRECTION_DOWN) {
//                    mListRecyclerView.setre
                }
                if (xLinearLayoutManager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        mReturnTopImageView.setVisibility(VISIBLE);
                        if (returnTopAnimation.hasEnded()) {
                            mReturnTopImageView.startAnimation(returnTopAnimation);
                        }
                    } else {
                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
                    mReturnTopImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }


    private void initGridView() {
        mGridRecyclerView = (XRecyclerView) rootView.findViewById(R.id.index_rv_grid);
        xGridLayoutManager = new SensoroXGridLayoutManager(getContext(), 3);
        mGridAdapter = new IndexGridAdapter(getContext(), this);
        mGridRecyclerView.setAdapter(mGridAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x15);
        mGridRecyclerView.setLayoutManager(xGridLayoutManager);
        mGridRecyclerView.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mGridRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                requestWithDirection(DIRECTION_DOWN);
            }

            @Override
            public void onLoadMore() {
                requestWithDirection(DIRECTION_UP);
            }
        });
        mGridRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (xGridLayoutManager.findFirstVisibleItemPosition() == 0
                        && newState == SCROLL_STATE_IDLE
                        && toolbarDirection == DIRECTION_DOWN) {
//                    mGridRecyclerView.setRefresh(true);
                }
                if (xGridLayoutManager.findFirstVisibleItemPosition() > 3) {
                    if (newState == 0) {
                        mReturnTopImageView.setVisibility(VISIBLE);
                        if (returnTopAnimation.hasEnded()) {
                            mReturnTopImageView.startAnimation(returnTopAnimation);
                        }
                    } else {
                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
                    mReturnTopImageView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void playSound() {
        String roles = ((MainActivity)getActivity()).getRoles();
        if (roles != null && !roles.equals("admin")) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }

    }

    public void showListLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.layout_in_anim);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mListLayout.setVisibility(View.VISIBLE);
                mGridLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mListLayout.setAnimation(inAnimation);
        mListLayout.startAnimation(inAnimation);
    }

    public void showGridLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.layout_in_anim);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mListLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mGridLayout.setVisibility(View.VISIBLE);
        mGridLayout.setAnimation(inAnimation);
        mGridLayout.startAnimation(inAnimation);
    }

    private void requestDeviceTypeCountData(final boolean isShowAnimation) {
        cityApplication.smartCityServer.getDeviceTypeCount(new Response.Listener<DeviceTypeCountRsp>() {
            @Override
            public void onResponse(DeviceTypeCountRsp response) {
                int alarmCount = response.getData().getAlarm();
                int lostCount = response.getData().getOffline();
                int inactiveCount = response.getData().getInactive();
                refreshTop(isShowAnimation, alarmCount, lostCount, inactiveCount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
//                    byte[] data = volleyError.networkResponse.data;
//                    Toast.makeText(cityApplication, new String(data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void refreshTop(boolean isShowAnimation, int alarmCount, int lostCount, int inactiveCount) {

        mHeadAlarmNumTextView.setText(String.valueOf(alarmCount));
        mHeadLostNumTextView.setText(String.valueOf(lostCount));
        mHeadInactiveNumTextView.setText(String.valueOf(inactiveCount));
        if (isShowAnimation) {
            playFlipAnimation(mHeadAlarmNumTextView);
            playFlipAnimation(mHeadLostNumTextView);
            playFlipAnimation(mHeadInactiveNumTextView);
        }

        if (alarmCount > 0) {
            alarmLayout.setVisibility(View.VISIBLE);
            mHeadAlarmNumTextView.setVisibility(VISIBLE);
            mHeadLostNumTextView.setVisibility(VISIBLE);
            mHeadInactiveNumTextView.setVisibility(VISIBLE);
            mHeadLostTitleTextView.setVisibility(VISIBLE);
            mHeadInactiveTitleTextView.setVisibility(VISIBLE);
            mHeadAlarmTitleTextView.setText(R.string.today_alarm);
            mHeadAlarmNumTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x50));
            mHeadAlarmTitleTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x20));
            mHeadLayout.setBackgroundColor(getResources().getColor(R.color.sensoro_alarm));
            mToolbar1.setBackgroundColor(getResources().getColor(R.color.sensoro_alarm));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.sensoro_alarm));
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.sensoro_alarm));
        } else {
            alarmLayout.setVisibility(View.GONE);
            mHeadAlarmTitleTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x50));
            mHeadAlarmNumTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x20));
            mHeadAlarmNumTextView.setVisibility(View.INVISIBLE);
            mHeadLostNumTextView.setVisibility(View.INVISIBLE);
            mHeadInactiveNumTextView.setVisibility(View.INVISIBLE);
            mHeadLostTitleTextView.setVisibility(View.INVISIBLE);
            mHeadInactiveTitleTextView.setVisibility(View.INVISIBLE);
            mHeadAlarmTitleTextView.setText(R.string.tips_no_alarm);
            mHeadLayout.setBackgroundColor(getResources().getColor(R.color.sensoro_normal));
            mToolbar1.setBackgroundColor(getResources().getColor(R.color.sensoro_normal));
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.sensoro_normal));
            StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.sensoro_normal));
        }
    }

    public void requestWithDirection(int direction) {
        mProgressDialog.show();
        String type = mTypeSelectedIndex == 0 ? null: INDEX_TYPE_VALUES[mTypeSelectedIndex];
        Integer status = mStatusSelectedIndex == 0 ? null : INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
        if (direction == DIRECTION_DOWN) {
            page = 1;
            cityApplication.smartCityServer.getDeviceBriefInfoList(page, type, status, null, new Response.Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        cityApplication.setData(deviceBriefInfoRsp.getData());
                        refreshCacheData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mListRecyclerView.refreshComplete();
                        mGridRecyclerView.refreshComplete();
                        mProgressDialog.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mListRecyclerView.refreshComplete();
                    mGridRecyclerView.refreshComplete();
                    mProgressDialog.dismiss();
                    if (volleyError.networkResponse != null) {
                        byte[] data = volleyError.networkResponse.data;
                        Toast.makeText(cityApplication, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            page++;
            cityApplication.smartCityServer.getDeviceBriefInfoList(page, type, status, null, new Response.Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        if (deviceBriefInfoRsp.getData().size() == 0) {
                            page--;
                        } else {
                            cityApplication.addData(deviceBriefInfoRsp.getData());
                            refreshCacheData();
                        }
                    } catch (Exception e) {
                        page--;
                        e.printStackTrace();
                    } finally {
                        mListRecyclerView.loadMoreComplete();
                        mGridRecyclerView.loadMoreComplete();
                        mProgressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    page--;
                    mProgressDialog.dismiss();
                    mListRecyclerView.loadMoreComplete();
                    mGridRecyclerView.loadMoreComplete();
                    if (volleyError.networkResponse != null) {
                        byte[] data = volleyError.networkResponse.data;
                        Toast.makeText(cityApplication, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void refreshWithJsonData(String json) {
        DeviceInfo data = gson.fromJson(json, DeviceInfo.class);
        if (data != null && !this.isHidden()) {
            boolean isContains = false;
            for (int i = 0; i < cityApplication.getData().size(); i++) {
                DeviceInfo deviceInfo = cityApplication.getData().get(i);
                if (deviceInfo.getSn().equals(data.getSn())) {
                    if (data.getStatus() == SENSOR_STATUS_ALARM && deviceInfo.getStatus() != SENSOR_STATUS_ALARM) {
                        isAlarmPlay = true;
                    }
                    data.setPushDevice(true);
                    cityApplication.getData().set(i, data);
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                if (data.getStatus() == SENSOR_STATUS_ALARM) {
                    isAlarmPlay = true;
                }
                data.setNewDevice(true);
                data.setPushDevice(true);
                cityApplication.getData().add(data);
            }
        }
    }

    public void refreshWithSearch(DeviceInfoListRsp deviceInfoListRsp) {
        this.mDataList.clear();
        for (int i = 0; i < deviceInfoListRsp.getData().size(); i++) {
            DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;

                default:
                    break;
            }
            mDataList.add(deviceInfo);
        }
        filterBySearch();
        refreshData();
    }


    public void refreshCacheData() {
        this.mDataList.clear();
        for (int i = 0; i < cityApplication.getData().size(); i++) {
            DeviceInfo deviceInfo = cityApplication.getData().get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;
                default:
                    break;
            }
            if (isMatcher(deviceInfo)) {
                mDataList.add(deviceInfo);
            }
        }
        refreshData();
    }

    public void refreshData() {
        Collections.sort(mDataList);
        if (switchType == TYPE_LIST) {
            mListAdapter.setData(mDataList);
            mListAdapter.notifyDataSetChanged();
            mListRecyclerView.refreshComplete();
        } else {
            mGridAdapter.setData(mDataList);
            mGridAdapter.notifyDataSetChanged();
            mGridRecyclerView.refreshComplete();
        }
        if (mDataList.size() < 5) {
            mReturnTopImageView.setVisibility(View.GONE);
        }
    }

    private void playFlipAnimation(View targetView) {
        AnimatorSet animatorSetOut = (AnimatorSet) AnimatorInflater
                .loadAnimator(getContext(), R.animator.card_flip_left_out);

        final AnimatorSet animatorSetIn = (AnimatorSet) AnimatorInflater
                .loadAnimator(getContext(), R.animator.card_flip_left_in);

        animatorSetOut.setTarget(targetView);
        animatorSetIn.setTarget(targetView);

        animatorSetOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {// 翻转90度之后，换图
                animatorSetIn.start();
            }
        });

        animatorSetIn.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO
            }
        });
        animatorSetOut.start();
    }


    public void refreshCityInfo(Character character) {
        if (character != null) {
            if (character.getShortName() != null) {
                mTitleTextView.setText(character.isApply() ? character.getShortName() : getString(R.string.city_name));
            }
        }
    }

    public void refreshDeviceInfo(DeviceInfo deviceInfo) {
        for (int i = 0; i < cityApplication.getData().size(); i++) {
            DeviceInfo tempDeviceInfo = cityApplication.getData().get(i);
            if (deviceInfo.getSn().equals(tempDeviceInfo.getSn())) {
                cityApplication.getData().set(i, deviceInfo);
                break;
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void showTypePopupView() {
        if (mTypePopupView.getVisibility() == VISIBLE) {
            mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
            mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
            mTypeImageView.setRotation(0);
            mTypePopupView.dismiss();
        } else {
            mStatusTextView.setTextColor(getResources().getColor(R.color.c_626262));
            mStatusImageView.setColorFilter(getResources().getColor(R.color.c_626262));
            mStatusImageView.setRotation(0);
            mStatusPopupView.dismiss();

            mTypeTextView.setTextColor(getResources().getColor(R.color.popup_selected_text_color));
            mTypeImageView.setColorFilter(getResources().getColor(R.color.popup_selected_text_color));
            mTypeImageView.setRotation(180);
            mTypeShadowLayout.setVisibility(VISIBLE);
            mTypeShadowLayout.setAlpha(0.5f);
            mTypeShadowLayout.setBackgroundColor(getResources().getColor(R.color.c_626262));
            mTypePopupView.show(mTypeShadowLayout, new SensoroPopupTypeView.OnTypePopupItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mTypeSelectedIndex = position;
                    filterByTypeWithRequest(position);
                }
            });
        }

    }

    private void showStatusPopupView() {
        if (mStatusPopupView.getVisibility() == VISIBLE) {
            mStatusTextView.setTextColor(getResources().getColor(R.color.c_626262));
            mStatusImageView.setColorFilter(getResources().getColor(R.color.c_626262));
            mStatusImageView.setRotation(0);
            mStatusPopupView.dismiss();
        } else {
            mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
            mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
            mTypeImageView.setRotation(0);
            mTypePopupView.dismiss();

            mStatusTextView.setTextColor(getResources().getColor(R.color.popup_selected_text_color));
            mStatusImageView.setColorFilter(getResources().getColor(R.color.popup_selected_text_color));
            mStatusImageView.setRotation(180);
            mStatusShadowLayout.setVisibility(VISIBLE);
            mStatusShadowLayout.setAlpha(0.5f);
            mStatusShadowLayout.setBackgroundColor(getResources().getColor(R.color.c_626262));
            mStatusPopupView.show(mStatusShadowLayout, new SensoroPopupStatusView.OnStatusPopupItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mStatusSelectedIndex = position;
                    filterByStatusWithRequest(position);
                }
            });
        }

    }

    private void filterBySearch() {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
        } else {
            List<DeviceInfo> tempTypeList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                DeviceInfo deviceInfo = mDataList.get(i);
                String unionType = deviceInfo.getUnionType();
                if (unionType != null) {
                    if (unionType.equalsIgnoreCase(SENSOR_MENU_ARRAY[mTypeSelectedIndex]) || mTypeSelectedIndex == 0) {
                        tempTypeList.add(deviceInfo);
                    }
                }
            }

            List<DeviceInfo> tempStatusList = new ArrayList<>();
            if (mStatusSelectedIndex != 0) {
                for (int i = 0; i < tempTypeList.size(); i++) {
                    DeviceInfo deviceInfo = tempTypeList.get(i);
                    int status = INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
                    if (deviceInfo.getStatus() == status) {
                        tempStatusList.add(deviceInfo);
                    }
                }
            } else {
                tempStatusList.addAll(tempTypeList);
            }
            mDataList.clear();
            mDataList.addAll(tempStatusList);
        }
    }

    private boolean isMatcher(DeviceInfo deviceInfo) {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
             return true;
        } else {
            boolean isMatcherType = false;
            boolean isMatcherStatus = false;
            String unionType = deviceInfo.getUnionType();
            if (unionType != null) {
                String []unionTypeArray = unionType.split("\\|");
                List<String> unionTypeList = Arrays.asList(unionTypeArray);
                String []menuTypeArray = SENSOR_MENU_ARRAY[mTypeSelectedIndex].split("\\|");
                if (mTypeSelectedIndex == 0) {
                    isMatcherType = true;
                } else {
                    for (int j = 0 ; j < menuTypeArray.length; j++) {
                        String menuType = menuTypeArray[j];
                        if (unionTypeList.contains(menuType)) {
                            isMatcherType = true;
                            break;
                        }
                    }
                }
            }
            if (mStatusSelectedIndex != 0) {
                int status = INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
                if (deviceInfo.getStatus() == status) {
                    isMatcherStatus = true;
                }
            } else {
                isMatcherStatus = true;
            }
            return isMatcherStatus && isMatcherType;
        }
    }

    private void filterByStatusWithRequest(int position) {
        mStatusTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mStatusImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mStatusImageView.setRotation(0);
        String statusText = INDEX_STATUS_ARRAY[position];
        mStatusTextView.setText(statusText);
        mStatusSelectedIndex = position;
        requestWithDirection(DIRECTION_DOWN);
    }

    @Deprecated
    private void filterByStatus(int position) {
        mStatusTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mStatusImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mStatusImageView.setRotation(0);

        String statusText = INDEX_STATUS_ARRAY[position];
        mStatusTextView.setText(statusText);
        if (position == 0) {
            mDataList.clear();
            mDataList.addAll(cityApplication.getData());
        } else {
            List<DeviceInfo> tempList = new ArrayList<>();
            for (int i = 0; i < cityApplication.getData().size(); i++) {
                DeviceInfo deviceInfo = cityApplication.getData().get(i);
                int status = INDEX_STATUS_VALUES[position - 1];
                if (deviceInfo.getStatus() == status) {
                    tempList.add(deviceInfo);
                }
            }
            mDataList.clear();
            mDataList.addAll(tempList);

        }
        refreshData();
    }

    private void filterByTypeWithRequest(int position) {
        mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mTypeImageView.setRotation(0);
        String typeText = INDEX_TYPE_ARRAY[position];
        mTypeTextView.setText(typeText);
        mTypeSelectedIndex = position;
        requestWithDirection(DIRECTION_DOWN);
    }

    private void filterByType(int position) {
        mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mTypeImageView.setRotation(0);
        String typeText = INDEX_TYPE_ARRAY[position];
        mTypeTextView.setText(typeText);
        if (position == 0) {
            mDataList.clear();
            mDataList.addAll(cityApplication.getData());

        } else {
            List<DeviceInfo> tempList = new ArrayList<>();
            for (int i = 0; i < cityApplication.getData().size(); i++) {
                DeviceInfo deviceInfo = cityApplication.getData().get(i);
                String unionType = deviceInfo.getUnionType();
                if (unionType != null) {
                    if (unionType.equalsIgnoreCase(SENSOR_MENU_ARRAY[position])) {
                        tempList.add(deviceInfo);
                    }
                }
            }
            mDataList.clear();
            mDataList.addAll(tempList);
        }
        refreshData();
    }

    private void switchToTypeList() {
        switchType = TYPE_LIST;
        page = 1;
        requestWithDirection(DIRECTION_DOWN);
        mReturnTopImageView.setVisibility(View.GONE);
        mSwitchImageView.setImageResource(R.mipmap.ic_switch_grid);
        showListLayout();
    }

    private void switchToTypeGrid() {
        switchType = TYPE_GRID;
        page = 1;
        requestWithDirection(DIRECTION_DOWN);
        mReturnTopImageView.setVisibility(View.GONE);
        mSwitchImageView.setImageResource(R.mipmap.ic_switch_list);
        showGridLayout();
    }

    private void returnTop() {
        if (switchType == TYPE_LIST) {
            mListRecyclerView.smoothScrollToPosition(0);
        } else {
            mGridRecyclerView.smoothScrollToPosition(0);
        }
        mReturnTopImageView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.index_iv_type:
            case R.id.index_tv_type:
                showTypePopupView();
                break;
            case R.id.index_iv_status:
            case R.id.index_tv_status:
                showStatusPopupView();
                break;
            case R.id.index_return_top:
                returnTop();
                break;
            case R.id.index_iv_menu_list_reverse:
            case R.id.index_iv_menu_list:
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.index_iv_search:
            case R.id.index_iv_search_reverse:
                Intent intent = new Intent(getActivity(), SearchDeviceActivity.class);
                intent.putExtra(EXTRA_FRAGMENT_INDEX, 1);
                int size = mDataList.size();
//                intent.putExtra("", value);
                Bundle bundle = new Bundle();
                startActivityForResult(intent, REQUEST_CODE_SEARCH_DEVICE);
                break;
            case R.id.index_iv_switch:
                if (switchType == TYPE_LIST) {
                    switchToTypeGrid();
                } else {
                    switchToTypeList();
                }
                returnTop();
                break;
            case R.id.index_head_alarm_num:
            case R.id.index_head_alarm_num_title:
                break;
            case R.id.index_head_inactive_num:
            case R.id.index_head_inactive_num_title:
                mAppBarLayout.setExpanded(true, true);
                break;
            case R.id.index_head_lost_num:
            case R.id.index_head_lost_num_title:
                mAppBarLayout.setExpanded(true, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int index = position - 1;
        if (index >= 0) {
            DeviceInfo deviceInfo = mDataList.get(index);
            Intent intent = new Intent(getContext(), SensorDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat());
            getActivity().startActivity(intent);
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {//张开
            toolbarDirection = DIRECTION_DOWN;
            mToolbar1.setVisibility(View.VISIBLE);
            mToolbar2.setVisibility(View.GONE);
            mToolbar1.setAlpha(1.0f);
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//收缩
            toolbarDirection = DIRECTION_UP;
            mToolbar1.setVisibility(View.GONE);
            mToolbar2.setVisibility(View.VISIBLE);
            mToolbar2.setAlpha(1.0f);

        }
        if (toolbarDirection == DIRECTION_DOWN) {
            if (Math.abs(verticalOffset) > 0) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                mToolbar2.setAlpha(bar_alpha);
                mToolbar2.setVisibility(VISIBLE);
                mToolbar1.setVisibility(View.GONE);
            }
        } else {
            if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange()) {
                float bar_alpha = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                mToolbar2.setAlpha(bar_alpha);
            }
        }
    }

    public void scheduleRefresh() {
        for (int i = 0; i < cityApplication.getData().size(); i++) {
            DeviceInfo deviceInfo = cityApplication.getData().get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;
                default:
                    break;
            }
            for (int j = 0 ; j < mDataList.size(); j++) {
                DeviceInfo currentDeviceInfo = mDataList.get(j);
                if (currentDeviceInfo.getSn().equals(deviceInfo.getSn())) {
                    mDataList.set(j, deviceInfo);
                }
            }
            if (deviceInfo.isNewDevice() && isMatcher(deviceInfo)) {
                deviceInfo.setNewDevice(false);
                mDataList.add(deviceInfo);
            }
        }
        if (isAlarmPlay) {
            playSound();
            isAlarmPlay = false;
        }
        refreshData();
        if (isVisible() && this.isResumed()) {
            requestDeviceTypeCountData(false);
        }

    }

    @Override
    public void run() {
        mHandler.postDelayed(this, 3000);
        scheduleRefresh();
    }
}
