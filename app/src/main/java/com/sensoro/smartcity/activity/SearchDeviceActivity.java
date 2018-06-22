package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.IndexGridAdapter;
import com.sensoro.smartcity.adapter.IndexListAdapter;
import com.sensoro.smartcity.adapter.RelationAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISearchDeviceActivityView;
import com.sensoro.smartcity.presenter.SearchDeviceActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXGridLayoutManager;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.popup.SensoroPopupStatusView;
import com.sensoro.smartcity.widget.popup.SensoroPopupTypeView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;
import static com.sensoro.smartcity.constant.Constants.INDEX_STATUS_ARRAY;
import static com.sensoro.smartcity.constant.Constants.INDEX_TYPE_ARRAY;
import static com.sensoro.smartcity.constant.Constants.TYPE_GRID;
import static com.sensoro.smartcity.constant.Constants.TYPE_LIST;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchDeviceActivity extends BaseActivity<ISearchDeviceActivityView, SearchDeviceActivityPresenter>
        implements ISearchDeviceActivityView, View.OnClickListener, TextView
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
    private ProgressUtils mProgressUtils;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelationAdapter mRelationAdapter;
    private IndexListAdapter mListAdapter;
    private IndexGridAdapter mGridAdapter;

    private int switchType = TYPE_LIST;
    private boolean isShowDialog = true;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
        initView();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mKeywordEt.addTextChangedListener(this);
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        initSearchHistory();
        initRelation();
        initIndex();
    }


    @Override
    protected SearchDeviceActivityPresenter createPresenter() {
        return new SearchDeviceActivityPresenter();
    }

    private void initIndex() {
        initListView();
        initGridView();
        mTypeTextView.setOnClickListener(this);
        mStatusTextView.setOnClickListener(this);
        mTypeImageView.setOnClickListener(this);
        mStatusImageView.setOnClickListener(this);
        mSwitchImageView.setOnClickListener(this);
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        setIndexListLayoutVisible(false);
        mKeywordEt.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPrestener.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPrestener.onStop();
    }

    private void initListView() {
        xLinearLayoutManager = new SensoroXLinearLayoutManager(mActivity);
        mListAdapter = new IndexListAdapter(mActivity, this);
        mListRecyclerView.setAdapter(mListAdapter);
        mListRecyclerView.setLayoutManager(xLinearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x15);
        mListRecyclerView.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mListRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                String text = mKeywordEt.getText().toString();
                isShowDialog = false;
                mPrestener.requestWithDirection(DIRECTION_DOWN, text);
            }

            @Override
            public void onLoadMore() {
                isShowDialog = false;
                String text = mKeywordEt.getText().toString();
                mPrestener.requestWithDirection(DIRECTION_UP, text);
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


    private void initGridView() {
        xGridLayoutManager = new SensoroXGridLayoutManager(mActivity, 3);
        mGridAdapter = new IndexGridAdapter(mActivity, this);
        mGridRecyclerView.setAdapter(mGridAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x15);
        mGridRecyclerView.setLayoutManager(xGridLayoutManager);
        mGridRecyclerView.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mGridRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                String text = mKeywordEt.getText().toString();
                mPrestener.requestWithDirection(DIRECTION_DOWN, text);
            }

            @Override
            public void onLoadMore() {
                String text = mKeywordEt.getText().toString();
                mPrestener.requestWithDirection(DIRECTION_UP, text);
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

    //    public void analyseData(DeviceInfoListRsp deviceInfoListRsp) {
//        this.mDataList.clear();
//        this.mRelationLayout.setVisibility(View.GONE);
//        this.mIndexListLayout.setVisibility(VISIBLE);
//        for (int i = 0; i < deviceInfoListRsp.getData().size(); i++) {
//            DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(i);
//            switch (deviceInfo.getStatus()) {
//                case SENSOR_STATUS_ALARM:
//                    deviceInfo.setSort(1);
//                    break;
//                case SENSOR_STATUS_NORMAL:
//                    deviceInfo.setSort(2);
//                    break;
//                case SENSOR_STATUS_LOST:
//                    deviceInfo.setSort(3);
//                    break;
//                case SENSOR_STATUS_INACTIVE:
//                    deviceInfo.setSort(4);
//                    break;
//                default:
//                    break;
//            }
//            mDataList.add(deviceInfo);
//        }
//        refreshData();
//    }
    @Override
    public void refreshData(List<DeviceInfo> dataList) {
        Collections.sort(dataList);
        if (switchType == TYPE_LIST) {
            mListAdapter.setData(dataList);
            mListAdapter.notifyDataSetChanged();
            mListRecyclerView.refreshComplete();
        } else {
            mGridAdapter.setData(dataList);
            mGridAdapter.notifyDataSetChanged();
            mGridRecyclerView.refreshComplete();
        }
    }

    @Override
    public void updateRelationData(List<String> strList) {
        if (strList != null) {
            mRelationAdapter.setData(strList);
        }
        mRelationAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSearchHistoryData() {
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean getSearchDataListVisible() {
        boolean isListVisible = mIndexListLayout.getVisibility() == VISIBLE;
        boolean isSearchHistoryHide = mSearchHistoryLayout.getVisibility() == View.GONE;
        boolean isRelationLayoutHide = mRelationLayout.getVisibility() == View.GONE;
        return isListVisible && isSearchHistoryHide && isRelationLayoutHide;
    }

    @Override
    public void setEditText(String text) {
        if (text != null) {
            mKeywordEt.setText(text);
            mKeywordEt.setSelection(text.length());
        }
    }


    private void initRelation() {
        mRelationAdapter = new RelationAdapter(mActivity, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mClearKeywordIv.setVisibility(View.VISIBLE);
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                mPrestener.clickRelationItem(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRelationRecyclerView.setLayoutManager(linearLayoutManager);
        mRelationRecyclerView.setAdapter(mRelationAdapter);
    }

    private void initSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, mPrestener.getHistoryKeywords(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mPrestener.getHistoryKeywords().get(position);
                        setEditText(text);
                        mClearKeywordIv.setVisibility(View.VISIBLE);
                        mKeywordEt.clearFocus();
                        dismissInputMethodManager(view);
                        mPrestener.requestWithDirection(DIRECTION_DOWN, text);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        updateSearchHistoryData();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    @Override
    public void showListLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.layout_in_anim);
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

    @Override
    public void showGridLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.layout_in_anim);
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
                    mPrestener.setTypeSelectedIndex(position);
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
                    mPrestener.setStatusSelectedIndex(position);
                    filterByStatusWithRequest(position);
                }
            });
        }

    }

    @Override
    public void filterByTypeWithRequest(int position) {
        mTypeTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mTypeImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mTypeImageView.setRotation(0);
        String typeText = INDEX_TYPE_ARRAY[position];
        mTypeTextView.setText(typeText);
        mPrestener.setTypeSelectedIndex(position);
        String text = mKeywordEt.getText().toString();
        mPrestener.requestWithDirection(DIRECTION_DOWN, text);
    }

    @Override
    public void recycleViewRefreshComplete() {
        mListRecyclerView.refreshComplete();
        mGridRecyclerView.refreshComplete();
    }

    @Override
    public void filterByStatusWithRequest(int position) {
        mStatusTextView.setTextColor(getResources().getColor(R.color.c_626262));
        mStatusImageView.setColorFilter(getResources().getColor(R.color.c_626262));
        mStatusImageView.setRotation(0);
        String statusText = INDEX_STATUS_ARRAY[position];
        mStatusTextView.setText(statusText);
        mPrestener.setStatusSelectedIndex(position);
        String text = mKeywordEt.getText().toString();
        mPrestener.requestWithDirection(DIRECTION_DOWN, text);
    }

    @Override
    public void switchToTypeList() {
        switchType = TYPE_LIST;
        String text = mKeywordEt.getText().toString();
        mPrestener.requestWithDirection(DIRECTION_DOWN, text);
        mReturnTopImageView.setVisibility(View.GONE);
        mSwitchImageView.setImageResource(R.mipmap.ic_switch_grid);
        showListLayout();
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void setRelationLayoutVisible(boolean isVisible) {
        mRelationLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void setIndexListLayoutVisible(boolean isVisible) {
        mIndexListLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void setTipsLinearLayoutVisible(boolean isVisible) {
        tipsLinearLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void switchToTypeGrid() {
        switchType = TYPE_GRID;
        String text = mKeywordEt.getText().toString();
        mPrestener.requestWithDirection(DIRECTION_DOWN, text);
        mReturnTopImageView.setVisibility(View.GONE);
        mSwitchImageView.setImageResource(R.mipmap.ic_switch_list);
        showGridLayout();
    }

    @Override
    public void returnTop() {
        if (switchType == TYPE_LIST) {
            mListRecyclerView.smoothScrollToPosition(0);
        } else {
            mGridRecyclerView.smoothScrollToPosition(0);
        }
        mReturnTopImageView.setVisibility(View.GONE);
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }


    @Override
    protected void onDestroy() {
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_device_clear_btn:
                mPrestener.cleanHistory();
                break;
            case R.id.search_device_cancel_tv:
                mKeywordEt.clearFocus();
                finishAc();
                break;
            case R.id.search_device_clear_iv:
                mKeywordEt.getText().clear();
                mClearKeywordIv.setVisibility(View.GONE);
                setTipsLinearLayoutVisible(false);
                updateSearchHistoryData();
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
                mPrestener.switchIndexGridOrList(switchType);
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
            setSearchHistoryLayoutVisible(false);
            setRelationLayoutVisible(true);
            mPrestener.filterDeviceInfo(s.toString());
        } else {
            setSearchHistoryLayoutVisible(true);
            setRelationLayoutVisible(false);
            setIndexListLayoutVisible(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = mKeywordEt.getText().toString();
            if (TextUtils.isEmpty(text)) {
                SensoroToast.makeText(mActivity, "请输入搜索内容", Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
                        .show();
                return true;
            }
            mPrestener.save(text);
            mClearKeywordIv.setVisibility(View.VISIBLE);
            mKeywordEt.clearFocus();
            dismissInputMethodManager(v);
            mPrestener.requestWithDirection(DIRECTION_DOWN, text);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        mPrestener.clickItem(position);
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

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
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
    }
}
