package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.adapter.InspectionUploadExceptionTagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionUploadExceptionActivityView;
import com.sensoro.smartcity.presenter.InspectionUploadExceptionActivityPresenter;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.smartcity.widget.ScrollFrameLayout;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.widgets.SelectDialog;

import java.util.ArrayList;
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
    @BindView(R.id.ac_inspection_upload_exception_scroll_view)
    NestedScrollView acInspectionUploadExceptionScrollView;
    @BindView(R.id.ac_inspection_upload_exception_sf)
    ScrollFrameLayout acInspectionUploadExceptionSF;
    private InspectionUploadExceptionTagAdapter mRcExceptionTagAdapter;
    private ImagePickerAdapter mRcExceptionPicAdapter;
    private TextView dialogTvException;
    private TextView dialogTvWaite;
    private TextView dialogTvUpload;
    private CustomCornerDialog mExceptionDialog;
    private ProgressUtils mProgressUtils;
    private ProgressDialog progressDialog;
    private int initEtRemarkWidth = -1;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_upload_exception);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        acInspectionUploadExceptionScrollView.setNestedScrollingEnabled(false);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        includeTextTitleTvTitle.setText(R.string.abnormal_reporting);
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
        acInspectionUploadExceptionEtRemark.post(new Runnable() {
            @Override
            public void run() {
                initEtRemarkWidth = acInspectionUploadExceptionEtRemark.getWidth();
            }
        });
        acInspectionUploadExceptionEtRemark.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                if(initEtRemarkWidth!=-1){
                int diff = acInspectionUploadExceptionEtRemark.getHeight() - initEtRemarkWidth;
                initEtRemarkWidth = acInspectionUploadExceptionEtRemark.getHeight();
                if (diff > 0 && !TextUtils.isEmpty(acInspectionUploadExceptionEtRemark.getText().toString())) {
//                        acInspectionUploadExceptionScrollView.setScrollY(diff);
                    acInspectionUploadExceptionScrollView.smoothScrollBy(0, diff);
                }
//                }
            }
        });
    }

    private void initExceptionUploadDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.item_dialog_inspection_exception_upload, null);
        dialogTvException = view.findViewById(R.id.dialog_tv_exception);
        dialogTvUpload = view.findViewById(R.id.dialog_tv_upload_change_device);
        dialogTvWaite = view.findViewById(R.id.dialog_tv_waite);

        dialogTvException.setOnClickListener(mPresenter);
        dialogTvUpload.setOnClickListener(mPresenter);
        dialogTvWaite.setOnClickListener(mPresenter);
//        builder.setView(view);

//        mExceptionDialog = new ;
        mExceptionDialog = new CustomCornerDialog(mActivity, R.style.CustomCornerDialogStyle, view);
//        Window window = mExceptionDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

    }

    private void initRcPicTag() {
        mRcExceptionPicAdapter = new ImagePickerAdapter(mActivity, mPresenter.getSelImageList());
        mRcExceptionPicAdapter.setMaxImgCount(4);
        mRcExceptionPicAdapter.setAddTipText(mActivity.getString(R.string.photo_recording));
        mRcExceptionPicAdapter.setOnItemClickListener(new ImagePickerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                List<ImageItem> images = mRcExceptionPicAdapter.getImages();
                mPresenter.clickItem(id, position, images);
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
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
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
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        mExceptionDialog.cancel();
        mExceptionDialog = null;
        mProgressUtils.destroyProgress();
        super.onDestroy();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_inspection_upload_exception_tv_upload, R.id.ac_inspection_upload_exception_sf})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_upload_exception_tv_upload:
                List<Integer> selectTags = getSelectTags();
                if (selectTags.size() == 0) {
                    toastShort(mActivity.getString(R.string.must_select_a_tag_type));
                    return;
                }
                if (mPresenter.selImageList.size() > 0) {
                    mExceptionDialog.show();
                } else {
                    toastShort(mActivity.getString(R.string.upload_at_least_one_photo_or_a_video));
                }

                break;
            case R.id.ac_inspection_upload_exception_sf:
                acInspectionUploadExceptionEtRemark.setFocusable(true);
                acInspectionUploadExceptionEtRemark.setFocusableInTouchMode(true);
                acInspectionUploadExceptionEtRemark.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) mActivity
                        .getSystemService(mActivity.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(acInspectionUploadExceptionEtRemark, 0);
                break;
        }
    }

    @Override
    public void dismissUploadProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void initUploadProgressDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
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

    @Override
    public void updateImageList(ArrayList<ImageItem> imageList) {
        mRcExceptionPicAdapter.setImages(imageList);
    }

    @Override
    public void showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showUploadProgressDialog(String content, double percent) {
        if (progressDialog != null) {
            progressDialog.setProgress((int) (percent * 100));
            progressDialog.setTitle(content);
            progressDialog.show();
        }
    }
}
