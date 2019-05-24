package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.Guideline;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.model.SecurityRisksAdapterModel;
import com.sensoro.common.model.SecurityRisksTagModel;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;
import com.sensoro.smartcity.adapter.SecurityRisksReferTagAdapter;
import com.sensoro.smartcity.adapter.touchHelper.SecurityRiskContentTouchHelper;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.presenter.SecurityRisksPresenter;
import com.sensoro.smartcity.widget.dialog.TagDialogUtils;
import com.sensoro.smartcity.widget.dialog.TipDialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecurityRisksActivity extends BaseActivity<ISecurityRisksActivityView, SecurityRisksPresenter> implements ISecurityRisksActivityView
, TipDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.rc_content_ac_security_risk)
    RecyclerView rcContentAcSecurityRisk;
//    @BindView(R.id.guideline1)
//    Guideline guideline1;
    @BindView(R.id.tv_name_ac_security_risks)
    TextView tvNameAcSecurityRisks;
    @BindView(R.id.view_tag_ac_security_risks)
    View viewTagAcSecurityRisks;
    @BindView(R.id.iv_close_ac_security_risks)
    ImageView ivCloseAcSecurityRisks;
    @BindView(R.id.tv_manger_ac_security_risks)
    TextView tvMangerAcSecurityRisks;
    @BindView(R.id.rv_tag_ac_security_risks)
    RecyclerView rvTagAcSecurityRisks;
    @BindView(R.id.sv_tag_ac_security_risks)
    NestedScrollView svTagAcSecurityRisks;
    @BindView(R.id.cl_tag_ac_security_risks)
    ConstraintLayout clTagAcSecurityRisks;
    private SecurityRisksContentAdapter securityRisksContentAdapter;
    private SecurityRisksReferTagAdapter securityRisksReferTagAdapter;
    private TagDialogUtils tagDialogUtils;
    private TipDialogUtils mCancelDialog;
    private TranslateAnimation dismissTagAnimation;
    private TranslateAnimation showTagAnimation;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_security_risks);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.security_risks));
        includeTextTitleTvSubtitle.setText(mActivity.getString(R.string.save));
        includeTextTitleTvSubtitle.setTextColor(mActivity.getResources().getColor(R.color.c_1dbb99));

        tagDialogUtils = new TagDialogUtils(mActivity);
        tagDialogUtils.registerListener(mPresenter);

        showTagAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        showTagAnimation.setDuration(300);
        showTagAnimation.setInterpolator(new LinearInterpolator());

        dismissTagAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        dismissTagAnimation.setDuration(300);
        dismissTagAnimation.setInterpolator(new LinearInterpolator());

        initCancelDialog();
        initContentAdapter();
        initTagAdapter();

    }

    private void initCancelDialog() {
        mCancelDialog = new TipDialogUtils(mActivity);
        mCancelDialog.setTipMessageText(mActivity.getString(R.string.confirm_exit_security_risk));
        mCancelDialog.setTipCacnleText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_a6a6a6));
        mCancelDialog.setTipConfirmText(mActivity.getString(R.string.confirm_exit), mActivity.getResources().getColor(R.color.c_f34a4a));
        mCancelDialog.setTipDialogUtilsClickListener(this);
    }

    private void initTagAdapter() {
        securityRisksReferTagAdapter = new SecurityRisksReferTagAdapter(mActivity);
        securityRisksReferTagAdapter.setOnTagClickListener(mPresenter);
        SensoroLinearLayoutManager linearLayoutManager = new SensoroLinearLayoutManager(mActivity,false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTagAcSecurityRisks.setLayoutManager(linearLayoutManager);
        rvTagAcSecurityRisks.setAdapter(securityRisksReferTagAdapter);
    }

    private void initContentAdapter() {
        securityRisksContentAdapter = new SecurityRisksContentAdapter(mActivity);
        securityRisksContentAdapter.setOnSecurityRisksItemClickListener(mPresenter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcContentAcSecurityRisk.setLayoutManager(linearLayoutManager);
        rcContentAcSecurityRisk.setAdapter(securityRisksContentAdapter);

        SecurityRiskContentTouchHelper callback = new SecurityRiskContentTouchHelper(securityRisksContentAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rcContentAcSecurityRisk);
    }

    @Override
    protected SecurityRisksPresenter createPresenter() {
        return new SecurityRisksPresenter();
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
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle,R.id.iv_close_ac_security_risks,R.id.tv_manger_ac_security_risks})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                if (mCancelDialog != null) {
                    mCancelDialog.show();
                }
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doSave();
                break;
            case R.id.iv_close_ac_security_risks:
                setConstraintTagVisible(false);
                securityRisksContentAdapter.clearFocus();
                break;
            case R.id.tv_manger_ac_security_risks:
                Intent intent = new Intent(mActivity, SecurityRiskTagManagerActivity.class);
                startAC(intent);
                break;
        }
    }

    @Override
    public void setConstraintTagVisible(boolean isVisible) {
        if (isVisible) {
            if (clTagAcSecurityRisks.getVisibility() != View.VISIBLE) {
                clTagAcSecurityRisks.setVisibility(View.VISIBLE);
                clTagAcSecurityRisks.clearAnimation();
                clTagAcSecurityRisks.startAnimation(showTagAnimation);
            }

        }else{
            if(clTagAcSecurityRisks.getVisibility() == View.VISIBLE){
                clTagAcSecurityRisks.setVisibility(View.GONE);
                clTagAcSecurityRisks.clearAnimation();
                clTagAcSecurityRisks.startAnimation(dismissTagAnimation);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(clTagAcSecurityRisks.getVisibility() == View.VISIBLE){
            clTagAcSecurityRisks.setVisibility(View.GONE);
            clTagAcSecurityRisks.clearAnimation();
            clTagAcSecurityRisks.startAnimation(dismissTagAnimation);
        }else{
            if (mCancelDialog != null) {
                mCancelDialog.show();
            }else{
                super.onBackPressed();
            }
        }

    }

    @Override
    public void updateSecurityRisksTag(ArrayList<SecurityRisksTagModel> list, boolean isLocation) {
        securityRisksReferTagAdapter.updateData(list,isLocation);
    }

    @Override
    public void changLocationOrBehaviorColor(int position, boolean isLocation) {
        securityRisksContentAdapter.changLocationOrBehaviorColor(position,isLocation);
    }

    @Override
    public void updateLocationTag(String tag, boolean check, int mAdapterPosition) {
        securityRisksContentAdapter.updateLocationTag(tag,check);
    }

    @Override
    public void setTvName(String name) {
        tvNameAcSecurityRisks.setText(name);
    }

    @Override
    public void showAddTagDialog(boolean mIsLocation) {
        tagDialogUtils.setTitle(mIsLocation ? mActivity.getString(R.string.add_new_location_tag) : mActivity.getString(R.string.add_new_behavior_tag));
        tagDialogUtils.show();
    }

    @Override
    public void dismissTagDialog() {
        if (tagDialogUtils != null) {
            tagDialogUtils.dismissDialog();
        }
    }

    @Override
    public boolean getIsLocation() {
        return securityRisksReferTagAdapter.getIsLocation();
    }

    @Override
    public void rvContentScrollBottom(final int position) {
            rcContentAcSecurityRisk.post(new Runnable() {
                @Override
                public void run() {
                    rcContentAcSecurityRisk.smoothScrollToPosition(position);
                }
            });

    }

    @Override
    public void tagScrollBottom() {
        svTagAcSecurityRisks.post(new Runnable() {
            @Override
            public void run() {
                svTagAcSecurityRisks.fullScroll(NestedScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void updateSecurityRisksContent(List<SecurityRisksAdapterModel> data) {
        securityRisksContentAdapter.updateData(data);
    }

    @Override
    protected void onDestroy() {
        if (tagDialogUtils != null) {
            tagDialogUtils.unregisterListener();
            tagDialogUtils = null;
        }

        if(mCancelDialog != null){
            mCancelDialog.destory();
            mCancelDialog = null;
        }

        if (showTagAnimation != null) {
            showTagAnimation.cancel();
        }

        if (dismissTagAnimation != null) {
            dismissTagAnimation.cancel();
        }
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCancelClick() {
        mCancelDialog.dismiss();
    }

    @Override
    public void onConfirmClick() {
        finishAc();
    }
}
