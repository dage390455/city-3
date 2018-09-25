package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.adapter.InspectionUploadExceptionTagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionUploadExceptionActivityView;
import com.sensoro.smartcity.presenter.InspectionUploadExceptionActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionUploadExceptionActivity extends BaseActivity<IInspectionUploadExceptionActivityView,
        InspectionUploadExceptionActivityPresenter> implements IInspectionUploadExceptionActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_inspection_upload_exception_rc_tag)
    RecyclerView acInspectionUploadExceptionRcTag;
    @BindView(R.id.ac_inspection_upload_exception_et_remark)
    EditText acInspectionUploadExceptionEtRemark;
    @BindView(R.id.ac_inspection_upload_exception_tv_word_count)
    TextView acInspectionUploadExceptionTvWordCount;
    @BindView(R.id.ac_inspection_upload_exception_rc_pic)
    RecyclerView acInspectionUploadExceptionRcPic;
    @BindView(R.id.ac_inspection_upload_exception_tv_upload)
    TextView acInspectionUploadExceptionTvUpload;
    private InspectionUploadExceptionTagAdapter mRcExceptionTagAdapter;
    private ImagePickerAdapter mRcExceptionPicAdapter;
    private TextView dialogTvException;
    private TextView dialogTvWaite;
    private TextView dialogTvUpload;
    private AlertDialog mExceptionDialog;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_upload_exception);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText("异常上报");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        acInspectionUploadExceptionTvWordCount.setText("0/200");

        initRcTag();

        initRcPicTag();

        initExceptionUploadDialog();

        acInspectionUploadExceptionEtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                updateWordCount(length);
            }
        });
    }

    private void initExceptionUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.item_dialog_inspection_exception_upload, null);
        dialogTvException = view.findViewById(R.id.dialog_tv_exception);
        dialogTvUpload = view.findViewById(R.id.dialog_tv_upload_change_device);
        dialogTvWaite = view.findViewById(R.id.dialog_tv_waite);

        dialogTvException.setOnClickListener(mPresenter);
        dialogTvUpload.setOnClickListener(mPresenter);
        dialogTvWaite.setOnClickListener(mPresenter);
        builder.setView(view);

        mExceptionDialog = builder.create();
        Window window = mExceptionDialog.getWindow();
        if (window !=null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    private void initRcPicTag() {
        mRcExceptionPicAdapter = new ImagePickerAdapter(mActivity, mPresenter.getSelImageList(), 4);
        mRcExceptionPicAdapter.setOnItemClickListener(new ImagePickerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);
        acInspectionUploadExceptionRcPic.setLayoutManager(layoutManager);
        acInspectionUploadExceptionRcPic.setHasFixedSize(true);
        acInspectionUploadExceptionRcPic.setAdapter(mRcExceptionPicAdapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        acInspectionUploadExceptionRcPic.setNestedScrollingEnabled(false);
    }

    private void initRcTag() {
        mRcExceptionTagAdapter = new InspectionUploadExceptionTagAdapter(mActivity);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionUploadExceptionRcTag.setLayoutManager(manager);
        acInspectionUploadExceptionRcTag.setAdapter(mRcExceptionTagAdapter);

    }

    @Override
    protected InspectionUploadExceptionActivityPresenter createPresenter() {
        return new InspectionUploadExceptionActivityPresenter();
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

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExceptionDialog.cancel();
        mExceptionDialog = null;
        mProgressUtils.destroyProgress();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_inspection_upload_exception_tv_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_upload_exception_tv_upload:
                mExceptionDialog.show();
                break;
        }
    }



    @Override
    public void updateExceptionTagAdapter(List<String> exceptionTags) {
        mRcExceptionTagAdapter.updateTags(exceptionTags);
    }

    @Override
    public void updateWordCount(int count) {
        acInspectionUploadExceptionTvWordCount.setText(count + "/200");
    }


    @Override
    public void dismissExceptionDialog() {
        mExceptionDialog.dismiss();
    }

    @Override
    public List<Integer> getSelectTags() {
        return mRcExceptionTagAdapter.getSelectTag();
    }

    @Override
    public String getRemarkMessage() {
       return acInspectionUploadExceptionEtRemark.getText().toString();
    }


}
