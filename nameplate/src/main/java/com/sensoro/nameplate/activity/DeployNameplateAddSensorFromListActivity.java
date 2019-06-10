package com.sensoro.nameplate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.sensoro.nameplate.IMainViews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.adapter.AddSensorListAdapter;
import com.sensoro.nameplate.presenter.DeployNameplateAddSensorFromListActivityPresenter;
import com.sensoro.nameplate.widget.AssociationSensorConfirmDialogUtil;
import com.sensoro.nameplate.widget.CustomDrawableDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;

@Route(path = ARouterConstants.ACTIVITY_DEPLOY_ASSOCIATE_SENSOR_FROM_LIST)
public class DeployNameplateAddSensorFromListActivity extends BaseActivity<IDeployNameplateAddSensorFromListActivityView,
        DeployNameplateAddSensorFromListActivityPresenter> implements IDeployNameplateAddSensorFromListActivityView,
        TextView.OnEditorActionListener, View.OnClickListener {

    @BindView(R2.id.iv_arrow_left_ac_deploy_nameplate_sensor_list)
    ImageView ivArrowLeftAcDeployNameplateSensorList;
    @BindView(R2.id.et_search_ac_deploy_nameplate_sensor_list)
    EditText etSearchAcDeployNameplateSensorList;
    @BindView(R2.id.iv_clear_ac_deploy_nameplate_sensor_list)
    ImageView ivClearAcDeployNameplateSensorList;
    @BindView(R2.id.ll_search_ac_deploy_nameplate_sensor_list)
    LinearLayout llSearchAcDeployNameplateSensorList;
    @BindView(R2.id.tv_search_cancel_ac_deploy_nameplate_sensor_list)
    TextView tvSearchCancelAcDeployNameplateSensorList;
    @BindView(R2.id.ll_top_search_ac_deploy_nameplate_sensor_list)
    LinearLayout llTopSearchAcDeployNameplateSensorList;
    @BindView(R2.id.rb_select_all_ac_deploy_nameplate_sensor_list)
    TextView rbSelectAllAcDeployNameplateSensorList;
    @BindView(R2.id.tv_selected_count_ac_deploy_nameplate_sensor_list)
    TextView tvSelectedCountAcDeployNameplateSensorList;
    @BindView(R2.id.tv_add_ac_deploy_nameplate_sensor_list)
    TextView tvAddAcDeployNameplateSensorList;
    @BindView(R2.id.ll_status_ac_deploy_nameplate_sensor_list)
    LinearLayout llStatusAcDeployNameplateSensorList;
    @BindView(R2.id.no_content)
    ImageView noContent;
    @BindView(R2.id.no_content_tip)
    TextView noContentTip;
    @BindView(R2.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R2.id.rv_list_include)
    RecyclerView rvListInclude;
    @BindView(R2.id.refreshLayout_include)
    SmartRefreshLayout refreshLayoutInclude;
    @BindView(R2.id.return_top_include)
    ImageView returnTopInclude;
    @BindView(R2.id.view_divider_ac_deploy_nameplate_sensor_list)
    View viewDividerAcDeployNameplateSensorList;
    @BindView(R2.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R2.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R2.id.ll_search_history)
    LinearLayout llSearchHistory;
    @BindView(R2.id.rl_content_ac_deploy_nameplate_sensor_list)
    RelativeLayout rlContentAcDeployNameplateSensorList;
    @BindView(R2.id.rl_root_ac_deploy_nameplate_sensor_list)
    RelativeLayout rlRootAcDeployNameplateSensorList;
    private AddSensorListAdapter mAddSensorListAdapter;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;
    private AssociationSensorConfirmDialogUtil mConfirmDialog;
    private SearchHistoryAdapter mSearchHistoryAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deplaoy_nameplate_add_sensor_from_list);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mConfirmDialog = new AssociationSensorConfirmDialogUtil(mActivity);
        mConfirmDialog.setOnListener(new AssociationSensorConfirmDialogUtil.OnListener() {
            @Override
            public void onConfirm() {
                mConfirmDialog.dismiss();
                mPresenter.doAssociateSensor();
            }
        });
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        returnTopInclude.setAnimation(returnTopAnimation);
        returnTopInclude.setVisibility(View.GONE);

        setSelectSize(0);

        initOnclick();

        initEditText();

        initSmartRefresh();

        initRv();

        initRcSearchHistory();

    }

    private void initOnclick() {
        ivArrowLeftAcDeployNameplateSensorList.setOnClickListener(this);
        ivClearAcDeployNameplateSensorList.setOnClickListener(this);
        tvSearchCancelAcDeployNameplateSensorList.setOnClickListener(this);
        rbSelectAllAcDeployNameplateSensorList.setOnClickListener(this);
        tvAddAcDeployNameplateSensorList.setOnClickListener(this);
        returnTopInclude.setOnClickListener(this);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {
        etSearchAcDeployNameplateSensorList.setOnEditorActionListener(this);
        etSearchAcDeployNameplateSensorList.addTextChangedListener(new TextWatcher() {
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
        AppUtils.getInputSoftStatus(rlRootAcDeployNameplateSensorList, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                etSearchAcDeployNameplateSensorList.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                etSearchAcDeployNameplateSensorList.setCursorVisible(true);
            }
        });

        etSearchAcDeployNameplateSensorList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setSearchHistoryVisible(true);
                    etSearchAcDeployNameplateSensorList.requestFocus();
                    etSearchAcDeployNameplateSensorList.setCursorVisible(true);
                    AppUtils.openInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
                }
                return false;
            }
        });
    }

    public void setSearchClearImvVisible(boolean isVisible) {
        ivClearAcDeployNameplateSensorList.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity) {
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
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(mActivity, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            etSearchAcDeployNameplateSensorList.setText(text);
                            etSearchAcDeployNameplateSensorList.setSelection(etSearchAcDeployNameplateSensorList.getText().toString().length());
                        }
                        etSearchAcDeployNameplateSensorList.setVisibility(View.VISIBLE);
                        etSearchAcDeployNameplateSensorList.clearFocus();
                        AppUtils.dismissInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
                        setSearchHistoryVisible(false);
                        mPresenter.save(text);
                        mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    private void initSmartRefresh() {
        refreshLayoutInclude.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayoutInclude.setEnableLoadMore(true);
        refreshLayoutInclude.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.requestWithDirection(DIRECTION_DOWN, true);
            }
        });
        refreshLayoutInclude.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.requestWithDirection(Constants.DIRECTION_UP);
            }
        });
    }

    private void initRv() {
        mAddSensorListAdapter = new AddSensorListAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        CustomDrawableDivider customDivider = new CustomDrawableDivider(mActivity, CustomDrawableDivider.VERTICAL);
        rvListInclude.setLayoutManager(manager);
        rvListInclude.addItemDecoration(customDivider);
        rvListInclude.setAdapter(mAddSensorListAdapter);

        mAddSensorListAdapter.setOnSensorListCheckListener(new AddSensorListAdapter.OnSensorListCheckListener() {
            @Override
            public void onChecked(int position) {
                mPresenter.doChecked(position);
            }
        });

        rvListInclude.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }

                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        returnTopInclude.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            returnTopInclude.startAnimation(returnTopAnimation);
                        }
                    } else {
                        returnTopInclude.setVisibility(View.GONE);
                    }
                } else {
                    returnTopInclude.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (manager.findFirstVisibleItemPosition() == 0 && rvListInclude.getChildAt(0).getTop() == 0) {
                    viewDividerAcDeployNameplateSensorList.setVisibility(View.GONE);
                } else {
                    viewDividerAcDeployNameplateSensorList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected DeployNameplateAddSensorFromListActivityPresenter createPresenter() {
        return new DeployNameplateAddSensorFromListActivityPresenter();
    }


    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }

        if (mConfirmDialog != null) {
            mConfirmDialog.destroy();
        }
    }

    @Override
    public void updateData(ArrayList<NamePlateInfo> mList) {
        if (mList == null || mList.size() > 0) {
            icNoContent.setVisibility(View.GONE);
            refreshLayoutInclude.setVisibility(View.VISIBLE);
            mAddSensorListAdapter.updateData(mList);
        } else {
            icNoContent.setVisibility(View.VISIBLE);
            refreshLayoutInclude.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayoutInclude.finishRefresh();
        refreshLayoutInclude.finishLoadMore();
    }

    @Override
    public void setCheckedDrawable(Drawable drawable) {
        rbSelectAllAcDeployNameplateSensorList.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void notifyDataAll() {
        mAddSensorListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectSize(int size) {
        tvSelectedCountAcDeployNameplateSensorList.setTextColor(mActivity.getResources().getColor(size == 0 ? R.color.c_a6a6a6 : R.color.c_1dbb99));
        tvSelectedCountAcDeployNameplateSensorList.setText(size + "");
    }

    @Override
    public void setAddStatus(boolean canAdd) {
        if (canAdd) {
            tvAddAcDeployNameplateSensorList.setBackgroundColor(mActivity.getResources().getColor(R.color.c_1dbb99));
            tvAddAcDeployNameplateSensorList.setClickable(true);
        } else {
            tvAddAcDeployNameplateSensorList.setBackgroundColor(mActivity.getResources().getColor(R.color.c_dfdfdf));
            tvAddAcDeployNameplateSensorList.setClickable(false);
        }

    }

    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rlContentAcDeployNameplateSensorList.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            tvSearchCancelAcDeployNameplateSensorList.setVisibility(View.VISIBLE);
//            setEditTextState(false);
            AppUtils.dismissInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
        } else {
            tvSearchCancelAcDeployNameplateSensorList.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }

    @Override
    public void UpdateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }

    @Override
    public void showConfirmDialog() {
        if (mConfirmDialog != null) {
            mConfirmDialog.show(mPresenter.mSelectList);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = etSearchAcDeployNameplateSensorList.getText().toString();
            mPresenter.save(text);
            etSearchAcDeployNameplateSensorList.clearFocus();
            mPresenter.requestSearchData(DIRECTION_DOWN, text);
            AppUtils.dismissInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
            setSearchHistoryVisible(false);
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.dismissInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_arrow_left_ac_deploy_nameplate_sensor_list) {
            mActivity.finish();
        } else if (id == R.id.iv_clear_ac_deploy_nameplate_sensor_list) {
            etSearchAcDeployNameplateSensorList.getText().clear();
        } else if (id == R.id.tv_search_cancel_ac_deploy_nameplate_sensor_list) {
            etSearchAcDeployNameplateSensorList.getText().clear();
            mPresenter.requestWithDirection(DIRECTION_DOWN);
            setSearchHistoryVisible(false);
            AppUtils.dismissInputMethodManager(mActivity, etSearchAcDeployNameplateSensorList);
        } else if (id == R.id.rb_select_all_ac_deploy_nameplate_sensor_list) {
            mPresenter.doSelectAll();
        } else if (id == R.id.tv_add_ac_deploy_nameplate_sensor_list) {
            mPresenter.doAddSensorList();
        } else if (id == R.id.return_top_include) {
            rvListInclude.smoothScrollToPosition(0);
            returnTopInclude.setVisibility(View.GONE);
        }
    }

    @Override
    public void startAC(Intent intent) {

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
}
