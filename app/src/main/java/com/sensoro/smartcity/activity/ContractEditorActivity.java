package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.fragment.BusinessContractFragment;
import com.sensoro.smartcity.fragment.PersonalContractFragment;
import com.sensoro.smartcity.imainviews.IContractEditorView;
import com.sensoro.smartcity.presenter.ContractEditorPresenter;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractEditorActivity extends BaseActivity<IContractEditorView, ContractEditorPresenter>
        implements IContractEditorView ,TipOperationDialogUtils.TipDialogUtilsClickListener{
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

    private PersonalContractFragment mPersonalContractFragment;
    private BusinessContractFragment mBusinessContractFragment;
    private FragmentTransaction mFragmentTransaction;
    private ArrayList<String> sites = new ArrayList<>();
    private TipOperationDialogUtils mCreateContractDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_editor);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
        initCreateContractDilog();
    }

    private void initView() {
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_creattion));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        mPersonalContractFragment = new PersonalContractFragment();
        mBusinessContractFragment = new BusinessContractFragment();

        sites.add(mActivity.getString(R.string.community));
        sites.add(mActivity.getString(R.string.rental_house));
        sites.add(mActivity.getString(R.string.factory));
        sites.add(mActivity.getString(R.string.resident_workshop));
        sites.add(mActivity.getString(R.string.warehouse));
        sites.add(mActivity.getString(R.string.shop_storefront));
        sites.add(mActivity.getString(R.string.the_mall));
        sites.add(mActivity.getString(R.string.the_ohter));

    }

    private void initCreateContractDilog() {
        mCreateContractDialog = new TipOperationDialogUtils(mActivity, true);
        mCreateContractDialog.setTipTitleText(mActivity.getString(R.string.create_contract));
        mCreateContractDialog.setTipMessageText(mActivity.getString(R.string.create_contract_tip_message));
        mCreateContractDialog.setTipCacnleText(mActivity.getString(R.string.cancel),mActivity.getResources().getColor(R.color.c_a6a6a6));
        mCreateContractDialog.setTipConfirmText(mActivity.getString(R.string.dialog_input_confirm),mActivity.getResources().getColor(R.color.c_29c093));
        mCreateContractDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    protected ContractEditorPresenter createPresenter() {
        return new ContractEditorPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
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


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_contract_editor_personal_contract, R.id.ac_contract_editor_business_contract})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_contract_editor_personal_contract:
                showPersonalFragment();
//                mPresenter.doPersonalContract();
                break;
            case R.id.ac_contract_editor_business_contract:
                showBusinessFragment();
//                mPresenter.doBusinessContract();
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
        if(mPersonalContractFragment.isAdded()){
            fragmentTransaction.hide(mBusinessContractFragment).show(mPersonalContractFragment).commit();
        }else{
            fragmentTransaction.add(R.id.ac_contract_editor_fl,mPersonalContractFragment).hide(mBusinessContractFragment).show(mPersonalContractFragment).commit();
        }
    }

    @Override
    public void showBusinessFragment() {
        acContractEditorPersonalContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_left_corner_2_white_bg);
        acContractEditorPersonalContract.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
        acContractEditorCompanyContract.setBackgroundResource(R.drawable.shape_btn_top_bottom_right_corner_2_29c_bg);
        acContractEditorCompanyContract.setTextColor(mActivity.getResources().getColor(R.color.white));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(mBusinessContractFragment.isAdded()){
            fragmentTransaction.hide(mPersonalContractFragment).show(mBusinessContractFragment).commit();
        }else{
            fragmentTransaction.add(R.id.ac_contract_editor_fl,mBusinessContractFragment).hide(mPersonalContractFragment).show(mBusinessContractFragment).commit();
        }
    }

    /**
     * 对话框的点击事件
     */
    @Override
    public void onCancelClick() {
        mCreateContractDialog.dismiss();
    }

    @Override
    public void onConfirmClick(String content) {
        mCreateContractDialog.dismiss();
//        mPersonalContractFragment.is
    }
}
