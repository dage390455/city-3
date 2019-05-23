package com.sensoro.nameplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SelectDialog;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TouchRecycleView;
import com.sensoro.nameplate.IMainViews.INameplateDetailActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.adapter.AddedSensorAdapter;
import com.sensoro.nameplate.presenter.NameplateDetailActivityPresenter;
import com.sensoro.nameplate.widget.CustomDrawableDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameplateDetailActivity extends BaseActivity<INameplateDetailActivityView, NameplateDetailActivityPresenter> implements INameplateDetailActivityView {
    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R2.id.tv_nameplate_detail_sn)
    TextView tvNameplateDetailSn;
    @BindView(R2.id.tv_nameplate_qrcode)
    TextView tvNameplateQrcode;
    @BindView(R2.id.tv_nameplate_edit)
    TextView tvNameplateEdit;
    @BindView(R2.id.tv_nameplate_name)
    TextView tvNameplateName;
    @BindView(R2.id.trv_nameplate_tag)
    TouchRecycleView trvNameplateTag;
    @BindView(R2.id.tv_nameplate_associated_sensor)
    TextView tvNameplateAssociatedSensor;
    @BindView(R2.id.rv_nameplate_associated_sensor)
    RecyclerView rvNameplateAssociatedSensor;
    @BindView(R2.id.tv_nameplate_associated_new_sensor)
    TextView tvNameplateAssociatedNewSensor;
    private AddedSensorAdapter mAddedSensorAdapter;
    private TagAdapter tagAdapter;
    private final List<String> options = new ArrayList<>();

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nameplate_detail);
        ButterKnife.bind(this);
        includeTextTitleTvTitle.setText(R.string.nameplate_manager_detail);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        options.add("扫码关联");
        options.add("传感器列表中关联");
        initTag();
        initRvAddedSensorList();
        mPresenter.initData(mActivity);
    }

    private void initTag() {
        tagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        trvNameplateTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        trvNameplateTag.setIntercept(true);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trvNameplateTag.setLayoutManager(layoutManager);
//        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.x10);
//        holder.rvItemAdapterNameplateTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        trvNameplateTag.setAdapter(tagAdapter);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            strings.add("标签 " + i);
        }
        tagAdapter.updateTags(strings);
    }

    private void initRvAddedSensorList() {
        mAddedSensorAdapter = new AddedSensorAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        CustomDrawableDivider bottomNoDividerItemDecoration =
                new CustomDrawableDivider(mActivity, CustomDrawableDivider.VERTICAL);
        rvNameplateAssociatedSensor.addItemDecoration(bottomNoDividerItemDecoration);
        rvNameplateAssociatedSensor.setLayoutManager(manager);
        rvNameplateAssociatedSensor.setAdapter(mAddedSensorAdapter);

        mAddedSensorAdapter.setOnDeleteClickListener(new AddedSensorAdapter.onDeleteClickListenre() {
            @Override
            public void onDeleteClick(int position) {
//                toastShort("点击了");
            }
        });

    }

    @Override
    protected NameplateDetailActivityPresenter createPresenter() {
        return new NameplateDetailActivityPresenter();
    }


    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.tv_nameplate_qrcode, R2.id.tv_nameplate_edit, R2.id.tv_nameplate_associated_new_sensor})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (R.id.include_text_title_imv_arrows_left == id) {

        } else if (R.id.tv_nameplate_qrcode == id) {

        } else if (R.id.tv_nameplate_edit == id) {
            mPresenter.doEditNameplate();
        } else if (R.id.tv_nameplate_associated_new_sensor == id) {
            AppUtils.showDialog(mActivity, new SelectDialog.SelectDialogListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.doNesSensor(position);
                }
            }, options).show();
        } else {

        }
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
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }
}
