package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.fragment.BusinessContractFragment;
import com.sensoro.smartcity.fragment.PersonalContractFragment;
import com.sensoro.smartcity.imainviews.IContractEditorView;
import com.sensoro.smartcity.presenter.ContractEditorPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractEditorActivity extends BaseActivity<IContractEditorView, ContractEditorPresenter>
        implements IContractEditorView, TipOperationDialogUtils.TipDialogUtilsClickListener {
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
    @BindView(R.id.ac_contract_editor_personal_contract)
    TextView acContractEditorPersonalContract;
    @BindView(R.id.ac_contract_editor_business_contract)
    TextView acContractEditorCompanyContract;
    @BindView(R.id.ac_contract_editor_fl)
    FrameLayout acContractEditorFl;
    @BindView(R.id.ac_contract_editor_top_tab)
    LinearLayout acContractEditorTopTab;

    private PersonalContractFragment mPersonalContractFragment;
    private BusinessContractFragment mBusinessContractFragment;
    private TipOperationDialogUtils mCreateContractDialog;

    private int mDialogOrigin;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_editor);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
        initCreateContractDialog();
    }

    private void initView() {
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_creattion));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        mPersonalContractFragment = new PersonalContractFragment();
        mBusinessContractFragment = new BusinessContractFragment();

    }

    private void initCreateContractDialog() {
        mCreateContractDialog = new TipOperationDialogUtils(mActivity, true);
        mCreateContractDialog.setTipTitleText(mActivity.getString(R.string.create_contract));
        mCreateContractDialog.setTipMessageText(mActivity.getString(R.string.create_contract_tip_message));
        mCreateContractDialog.setTipCancelText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_a6a6a6));
        mCreateContractDialog.setTipConfirmText(mActivity.getString(R.string.dialog_input_confirm), mActivity.getResources().getColor(R.color.c_1dbb99));
        mCreateContractDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    protected ContractEditorPresenter createPresenter() {
        return new ContractEditorPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.ac_contract_editor_personal_contract, R.id.ac_contract_editor_business_contract})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
            case R.id.include_text_title_tv_subtitle:
                finishAc();
                break;
            case R.id.ac_contract_editor_personal_contract:
                showPersonalFragment();
                break;
            case R.id.ac_contract_editor_business_contract:
                showBusinessFragment();
                break;
        }
    }

    @Override
    public void showPersonalFragment() {
        acContractEditorPersonalContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_left_corner_2_29c_bg);
        acContractEditorPersonalContract.setTextColor(mActivity.getResources().getColor(R.color.white));
        acContractEditorCompanyContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_right_corner_2_white_bg);
        acContractEditorCompanyContract.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mPersonalContractFragment.isAdded()) {
            fragmentTransaction.hide(mBusinessContractFragment).show(mPersonalContractFragment).commitAllowingStateLoss();
        } else {
            fragmentTransaction.add(R.id.ac_contract_editor_fl, mPersonalContractFragment).hide(mBusinessContractFragment).show(mPersonalContractFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void showBusinessFragment() {
        acContractEditorPersonalContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_left_corner_2_white_bg);
        acContractEditorPersonalContract.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
        acContractEditorCompanyContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_right_corner_2_29c_bg);
        acContractEditorCompanyContract.setTextColor(mActivity.getResources().getColor(R.color.white));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mBusinessContractFragment.isAdded()) {
            fragmentTransaction.hide(mPersonalContractFragment).show(mBusinessContractFragment).commitAllowingStateLoss();
        } else {
            fragmentTransaction.add(R.id.ac_contract_editor_fl, mBusinessContractFragment).hide(mPersonalContractFragment).show(mBusinessContractFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void personalFragmentSetArguments(Bundle bundle) {
        mPersonalContractFragment.setArguments(bundle);
    }

    @Override
    public void businessFragmentSetArguments(Bundle bundle) {
        mBusinessContractFragment.setArguments(bundle);
    }

    @Override
    public void setTopTabVisible(boolean isVisible) {
        acContractEditorTopTab.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitleText(String text) {
        includeTextTitleTvTitle.setText(text);
    }

    @Override
    public void setOriginFormList(boolean isFormList) {
        if (isFormList) {
            includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
            includeTextTitleTvSubtitle.setText(mActivity.getResources().getString(R.string.cancel));
            //这里为了要保持边距，所以需要设置invisible，可是不能隐藏掉，所以下面调用绘制的方法，使其隐藏
            includeTextTitleImvArrowsLeft.setVisibility(View.INVISIBLE);
            includeTextTitleImvArrowsLeft.postInvalidate();
        } else {
            includeTextTitleImvArrowsLeft.setVisibility(View.VISIBLE);
            includeTextTitleTvSubtitle.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        if (mCreateContractDialog != null) {
            mCreateContractDialog.dismiss();
            mCreateContractDialog.destroy();
            mCreateContractDialog = null;
        }
        super.onDestroy();
    }

    /**
     * 对话框的点击事件
     */
    @Override
    public void onCancelClick() {
        mCreateContractDialog.dismiss();
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mCreateContractDialog.dismiss();
        if (mDialogOrigin == 1) {
            mPersonalContractFragment.doCreateContract();
        } else if (mDialogOrigin == 2) {
            mBusinessContractFragment.doCreateContract();
        }
    }

    /**
     * @param origin 1是个人合同 2是企业合同
     */
    public void showCreateDialog(int origin) {
        mDialogOrigin = origin;
        mCreateContractDialog.show();
    }
}
