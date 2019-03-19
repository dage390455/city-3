package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployRepairInstructionView;
import com.sensoro.smartcity.presenter.DeployRepairInstructionPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployRepairInstructionActivity extends BaseActivity<IDeployRepairInstructionView, DeployRepairInstructionPresenter> {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.wv_deploy_repair_instruction_preview)
    WebView wvDeployRepairInstructionPreview;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_repair_instruction);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {

    }

    @Override
    protected DeployRepairInstructionPresenter createPresenter() {
        return new DeployRepairInstructionPresenter();
    }
    

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.wv_deploy_repair_instruction_preview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                break;
            case R.id.wv_deploy_repair_instruction_preview:
                break;
        }
    }
}
