package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.IndexGridAdapter;
import com.sensoro.smartcity.adapter.IndexListAdapter;
import com.sensoro.smartcity.adapter.RelationAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroXGridLayoutManager;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.popup.SensoroPopupStatusView;
import com.sensoro.smartcity.widget.popup.SensoroPopupTypeView;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchDeviceActivity extends BaseActivity implements View.OnClickListener, Constants, TextView
        .OnEditorActionListener, TextWatcher, RecycleViewItemClickListener {
    @BindView(R.id.search_device_et)
    EditText mKeywordEt;
    @BindView(R.id.search_device_cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.search_device_clear_iv)
    ImageView mClearKeywordIv;
    @BindView(R.id.search_device_history_ll)
    LinearLayout mSearchHistoryLayout;
    @BindView(R.id.search_device_clear_btn)
    ImageView mClearBtn;
    @BindView(R.id.search_device_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.search_device_relation_rv)
    RecyclerView mRelationRecyclerView;
    @BindView(R.id.search_device_tips)
    LinearLayout tipsLinearLayout;
    @BindView(R.id.search_device_relation_layout)
    LinearLayout mRelationLayout;
    @BindView(R.id.layout_index_list)
    LinearLayout mListLayout;
    @BindView(R.id.layout_index_grid)
    LinearLayout mGridLayout;
    @BindView(R.id.index_rv_list)
    XRecyclerView mListRecyclerView;
    @BindView(R.id.index_rv_grid)
    XRecyclerView mGridRecyclerView;
    @BindView(R.id.index_type_shadow)
    SensoroShadowView mTypeShadowLayout;
    @BindView(R.id.index_status_shadow)
    SensoroShadowView mStatusShadowLayout;
    @BindView(R.id.index_iv_type)
    ImageView mTypeImageView;
    @BindView(R.id.index_iv_status)
    ImageView mStatusImageView;
    @BindView(R.id.index_return_top)
    ImageView mReturnTopImageView;
    @BindView(R.id.index_iv_switch)
    ImageView mSwitchImageView;
    @BindView(R.id.index_type_popup)
    SensoroPopupTypeView mTypePopupView;
    @BindView(R.id.index_status_popup)
    SensoroPopupStatusView mStatusPopupView;
    @BindView(R.id.index_tv_type)
    TextView mTypeTextView;
    @BindView(R.id.index_tv_status)
    TextView mStatusTextView;
    @BindView(R.id.index_layout_list)
    RelativeLayout mIndexListLayout;
    private SensoroXLinearLayoutManager xLinearLayoutManager;
    private SensoroXGridLayoutManager xGridLayoutManager;
    private Animation returnTopAnimation;
    private SharedPreferences mPref;
    private Editor mEditor;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelationAdapter mRelationAdapter;
    private IndexListAdapter mListAdapter;
    private IndexGridAdapter mGridAdapter;
    private int switchType = TYPE_LIST;
    private int mTypeSelectedIndex = 0;
    private int mStatusSelectedIndex = 0;
    private int page = 1;
    private List<DeviceInfo> mDataList = new ArrayList<>();
    private final List<DeviceInfo> orginList = new ArrayList<>();
    private List<String> searchStrList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mPref = getSharedPreferences(PREFERENCE_DEVICE_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mKeywordEt.addTextChangedListener(this);
        mKeywordEt.requestFocus();
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        initSearchHistory();
        initRelation();
        StatusBarCompat.setStatusBarColor(this);
        initIndex();
        orginList.addAll(SensoroCityApplication.getInstance().getData());
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void initIndex() {
        initListView();
        initGridView();
        mIndexListLayout.setVisibility(View.GONE);
        mTypeTextView.setOnClickListener(this);
        mStatusTextView.setOnClickListener(this);
        mTypeImageView.setOnClickListener(this);
        mStatusImageView.setOnClickListener(this);
        mSwitchImageView.setOnClickListener(this);
        returnTopAnimation = AnimationUtils.loadAnimation(this, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        switchType = TYPE_LIST;
    }

    private void initListView() {
        xLinearLayoutManager = new SensoroXLinearLayoutManager(this);
        mListAdapter = new IndexListAdapter(this, this);
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
        mListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE) {
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

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void initGridView() {
        xGridLayoutManager = new SensoroXGridLayoutManager(this, 3);
        mGridAdapter = new IndexGridAdapter(this, this);
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
                        && newState == SCROLL_STATE_IDLE) {
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

    public void analyseData(DeviceInfoListRsp deviceInfoListRsp) {
        this.mDataList.clear();
        this.mRelationLayout.setVisibility(View.GONE);
        this.mIndexListLayout.setVisibility(VISIBLE);
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
    }

    public void refreshCacheData() {
        this.mDataList.clear();
        this.mRelationLayout.setVisibility(View.GONE);
        this.mIndexListLayout.setVisibility(VISIBLE);
        for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
            DeviceInfo deviceInfo = SensoroCityApplication.getInstance().getData().get(i);
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

    private boolean isMatcher(DeviceInfo deviceInfo) {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
            return true;
        } else {
            boolean isMatcherType = false;
            boolean isMatcherStatus = false;
            String unionType = deviceInfo.getUnionType();
            if (unionType != null) {
                String[] unionTypeArray = unionType.split("\\|");
                List<String> unionTypeList = Arrays.asList(unionTypeArray);
                String[] menuTypeArray = SENSOR_MENU_ARRAY[mTypeSelectedIndex].split("\\|");
                if (mTypeSelectedIndex == 0) {
                    isMatcherType = true;
                } else {
                    for (int j = 0; j < menuTypeArray.length; j++) {
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


    private void initRelation() {
        mRelationAdapter = new RelationAdapter(this, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String s = searchStrList.get(position);
////            List<DeviceInfo> data = SensoroCityApplication.getInstance().getData();
////            for(int i=0;i<data.size();i++){
////                String tempStr =data.get(i).getName();
////                if (tempStr!=null&&tempStr.equalsIgnoreCase(s)){
////                    position=i;
////                }
////            }
                save(s);
                mClearKeywordIv.setVisibility(View.VISIBLE);
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                requestWithDirection(DIRECTION_DOWN, s);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRelationRecyclerView.setLayoutManager(linearLayoutManager);
        mRelationRecyclerView.setAdapter(mRelationAdapter);
    }

    private void initSearchHistory() {
        String history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mHistoryKeywords, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mKeywordEt.setText(mHistoryKeywords.get(position));
                mClearKeywordIv.setVisibility(View.VISIBLE);
                mProgressDialog.show();
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                requestWithDirection(DIRECTION_DOWN);
            }
        });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
        mKeywordEt.requestFocus();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    public void showListLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.layout_in_anim);
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
        Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.layout_in_anim);
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

    private void filterByTypeWithRequest(int position) {
        mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mTypeImageView.setRotation(0);
        String typeText = INDEX_TYPE_ARRAY[position];
        mTypeTextView.setText(typeText);
        mTypeSelectedIndex = position;
        requestWithDirection(DIRECTION_DOWN);
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

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    public void filterDeviceInfo(String filter) {
        List<DeviceInfo> originDeviceInfoList = new ArrayList<>();
        originDeviceInfoList.addAll(orginList);
        ArrayList<DeviceInfo> deleteDeviceInfoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (deviceInfo.getName() != null) {
                if (!(deviceInfo.getName().contains(filter.toUpperCase()))) {
                    deleteDeviceInfoList.add(deviceInfo);
                }
            } else {
                deleteDeviceInfoList.add(deviceInfo);
            }

        }
        originDeviceInfoList.removeAll(deleteDeviceInfoList);
        List<String> tempList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (deviceInfo.getName() != null) {
                tempList.add(deviceInfo.getName());
            }
        }
        searchStrList.clear();
        searchStrList.addAll(tempList);
        mRelationAdapter.setData(tempList);
        mRelationAdapter.notifyDataSetChanged();
        originDeviceInfoList.clear();
        tempList.clear();
        deleteDeviceInfoList.clear();


    }

    public void save(String text) {
//        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                List<String> list = new ArrayList<String>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                mHistoryKeywords = list;
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    if (i == (list.size() - 1)) {
                        stringBuffer.append(list.get(i));
                    } else {
                        stringBuffer.append(list.get(i) + ",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
        }
    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        mSearchHistoryAdapter.notifyDataSetChanged();
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    public void requestWithDirection(int direction) {
        mProgressDialog.show();
        String type = mTypeSelectedIndex == 0 ? null : INDEX_TYPE_VALUES[mTypeSelectedIndex];
        Integer status = mStatusSelectedIndex == 0 ? null : INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
        String text = mKeywordEt.getText().toString();
        if (direction == DIRECTION_DOWN) {
            page = 1;
            SensoroCityApplication.getInstance().smartCityServer.getDeviceBriefInfoList(page, type, status, text, new Response
                    .Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        if (deviceBriefInfoRsp.getData().size() == 0) {
                            tipsLinearLayout.setVisibility(View.VISIBLE);
                            mDataList.clear();
                            refreshData();
                        } else {
                            SensoroCityApplication.getInstance().setData(deviceBriefInfoRsp.getData());
                            refreshCacheData();
                        }

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
                        Toast.makeText(SearchDeviceActivity.this, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            page++;
            SensoroCityApplication.getInstance().smartCityServer.getDeviceBriefInfoList(page, type, status, text, new Response
                    .Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        if (deviceBriefInfoRsp.getData().size() == 0) {
                            page--;
                        } else {
                            SensoroCityApplication.getInstance().addData(deviceBriefInfoRsp.getData());
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
                        Toast.makeText(SearchDeviceActivity.this, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void requestWithDirection(int direction, String seacherStr) {
        mProgressDialog.show();
        String type = mTypeSelectedIndex == 0 ? null : INDEX_TYPE_VALUES[mTypeSelectedIndex];
        Integer status = mStatusSelectedIndex == 0 ? null : INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
        String text = seacherStr;
        if (direction == DIRECTION_DOWN) {
            page = 1;
            SensoroCityApplication.getInstance().smartCityServer.getDeviceBriefInfoList(page, type, status, text, new Response
                    .Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        if (deviceBriefInfoRsp.getData().size() == 0) {
                            tipsLinearLayout.setVisibility(View.VISIBLE);
                            mDataList.clear();
                            refreshData();
                        } else {
                            SensoroCityApplication.getInstance().setData(deviceBriefInfoRsp.getData());
                            refreshCacheData();
                        }

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
                        Toast.makeText(SearchDeviceActivity.this, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            page++;
            SensoroCityApplication.getInstance().smartCityServer.getDeviceBriefInfoList(page, type, status, text, new Response
                    .Listener<DeviceInfoListRsp>() {
                @Override
                public void onResponse(DeviceInfoListRsp deviceBriefInfoRsp) {
                    try {
                        if (deviceBriefInfoRsp.getData().size() == 0) {
                            page--;
                        } else {
                            SensoroCityApplication.getInstance().addData(deviceBriefInfoRsp.getData());
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
                        Toast.makeText(SearchDeviceActivity.this, new String(data), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_device_clear_btn:
                cleanHistory();
                break;
            case R.id.search_device_cancel_tv:
                mKeywordEt.clearFocus();
                finish();
                break;
            case R.id.search_device_clear_iv:
                mKeywordEt.setText("");
                mClearKeywordIv.setVisibility(View.GONE);
                tipsLinearLayout.setVisibility(View.GONE);
                mSearchHistoryAdapter.notifyDataSetChanged();
                break;
            case R.id.index_tv_type:
            case R.id.index_iv_type:
                showTypePopupView();
                break;
            case R.id.index_iv_status:
            case R.id.index_tv_status:
                showStatusPopupView();
                break;
            case R.id.index_return_top:
                returnTop();
                break;
            case R.id.index_iv_switch:
                if (switchType == TYPE_LIST) {
                    switchToTypeGrid();
                } else {
                    switchToTypeList();
                }
                returnTop();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s.toString())) {
            mSearchHistoryLayout.setVisibility(View.GONE);
            mRelationLayout.setVisibility(View.VISIBLE);
            filterDeviceInfo(s.toString());

        } else {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
            mRelationLayout.setVisibility(View.GONE);
            mIndexListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            save(mKeywordEt.getText().toString());
            mClearKeywordIv.setVisibility(View.VISIBLE);
            mKeywordEt.clearFocus();
            dismissInputMethodManager(v);
            requestWithDirection(DIRECTION_DOWN);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
//            Log.e(TAG, "onItemClick: ", );
        int index = position - 1;
        if (position >= 0) {
//            int size = mDataList.size();
////            Log.e("", "onItemClick: "+mDataList.size());
            DeviceInfo deviceInfo = mDataList.get(index);
            Intent intent = new Intent(this, SensorDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat());
            startActivity(intent);

        }
//
//        String searchText = mRelationAdapter.getData().get(position);
//        List<DeviceInfo> tempList = new ArrayList<>();
//        for (DeviceInfo deviceInfo : sensoroCityApplication.getData()) {
//            if (deviceInfo.getName() != null) {
//                if (searchText.equals(deviceInfo.getName())) {
//                    tempList.add(deviceInfo);
//                    Intent data = new Intent();
//                    data.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
//                    setResult(RESULT_CODE_SEARCH_DEVICE, data);
//                    finish();
//                    break;
//                }
//
//            }
//        }

    }
}
