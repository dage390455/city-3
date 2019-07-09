package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.common.widgets.CustomDivider;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.UUIDSettingAdapter;
import com.sensoro.smartcity.imainviews.IUuidSettingActivityView;
import com.sensoro.smartcity.model.UuidSettingModel;
import com.sensoro.smartcity.presenter.UuidSettingActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UuidSettingActivity extends BaseActivity<IUuidSettingActivityView, UuidSettingActivityPresenter> implements IUuidSettingActivityView {

    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.tv_current_uuid)
    TextView tvCurrentUuid;
    @BindView(R.id.rv_uuid_normal)
    RecyclerView rvUuidNormal;
    @BindView(R.id.rv_uuid_custom)
    RecyclerView rvUuidCustom;
    @BindView(R.id.tv_add_new_uuid)
    TextView tvAddNewUuid;
    private UUIDSettingAdapter normalUuidSettingAdapter;
    private UUIDSettingAdapter myUuidSettingAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uuid_setting);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        initNormalAdapter();
        initMyAdapter();
    }

    private void initNormalAdapter() {
        normalUuidSettingAdapter = new UUIDSettingAdapter(mActivity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvUuidNormal.setLayoutManager(linearLayoutManager);
        rvUuidNormal.setAdapter(normalUuidSettingAdapter);
        CustomDivider dividerItemDecoration = new CustomDivider(mActivity, DividerItemDecoration.VERTICAL);
        rvUuidNormal.addItemDecoration(dividerItemDecoration);
    }

    private void initMyAdapter() {
        myUuidSettingAdapter = new UUIDSettingAdapter(mActivity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvUuidCustom.setLayoutManager(linearLayoutManager);
        rvUuidCustom.setAdapter(myUuidSettingAdapter);
        CustomDivider dividerItemDecoration = new CustomDivider(mActivity, DividerItemDecoration.VERTICAL);
        rvUuidCustom.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected UuidSettingActivityPresenter createPresenter() {
        return new UuidSettingActivityPresenter();
    }

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.tv_add_new_uuid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doSave();
                break;
            case R.id.tv_add_new_uuid:
                mPresenter.addNewUUID();
                break;
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

    @Override
    public void updateNormalAdapter(List<UuidSettingModel> data) {
        normalUuidSettingAdapter.updateData(data);
    }

    @Override
    public void updateMyAdapter(List<UuidSettingModel> data) {
        myUuidSettingAdapter.updateData(data);
    }
}
