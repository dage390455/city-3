package com.sensoro.forestfire.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.CameraListFilterPopupWindow;
import com.sensoro.common.widgets.CustomDivider;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;
import com.sensoro.forestfire.adapter.ForestFireCameraListAdapter;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;
import com.sensoro.forestfire.imainviews.IForestFireCameraListActivityView;
import com.sensoro.forestfire.model.ForestFireCameraBean;
import com.sensoro.forestfire.presenter.ForestFireCameraDetailActivityPresenter;
import com.sensoro.forestfire.presenter.ForestFireListActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理监测点详情>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_CAMERA_LIST)
public class ForestFireCameraDetailActivity extends BaseActivity<IForestFireCameraDetailActivityView, ForestFireCameraDetailActivityPresenter>
        implements IForestFireCameraDetailActivityView, View.OnClickListener {


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forest_fire_camera_detail);
    }

    @Override
    protected ForestFireCameraDetailActivityPresenter createPresenter() {
        return null;
    }
}
