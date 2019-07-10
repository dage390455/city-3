package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IUUIDAddMySettingActivityView;
import com.sensoro.smartcity.presenter.UUIDAddMySettingActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UUIDAddMySettingActivity extends BaseActivity<IUUIDAddMySettingActivityView, UUIDAddMySettingActivityPresenter> implements IUUIDAddMySettingActivityView {


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
    @BindView(R.id.et_add_uuid)
    EditText etAddUuid;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uuidadd_my_setting);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setText("添加");
        includeTextTitleTvSubtitle.setTextColor(getResources().getColor(R.color.c_1DBB99));
        includeTextTitleTvTitle.setText("添加UUID");
    }

    @Override
    protected UUIDAddMySettingActivityPresenter createPresenter() {
        return new UUIDAddMySettingActivityPresenter();
    }

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.et_add_uuid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doAddNewUUID(etAddUuid.getText().toString());
                break;
            case R.id.et_add_uuid:
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
}
