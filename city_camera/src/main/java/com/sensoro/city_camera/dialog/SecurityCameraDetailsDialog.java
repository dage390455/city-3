package com.sensoro.city_camera.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.common.widgets.dialog.TipDialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wangqinghao
 */
public class SecurityCameraDetailsDialog extends BaseBottomDialog {

    @BindView(R2.id.iv_camera_details_popup_close)
    ImageView mPopCloseIv;
    @BindView(R2.id.security_camera_details_title_tv)
    TextView mCameraNameTv;
    @BindView(R2.id.security_camera_details_type_tv)
    TextView mCameraTypeTv;
    @BindView(R2.id.security_camera_details_status_tv)
    TextView mCameraStatusTv;
    @BindView(R2.id.security_camera_details_sn_tv)
    TextView mCameraSNTv;
    @BindView(R2.id.security_camera_details_brand_tv)
    TextView mCameraBrandTv;
    @BindView(R2.id.security_camera_details_label_rg)
    RadioGroup mCameraLabelRg;
    @BindView(R2.id.security_camera_details_label1)
    RadioButton mCameraLabelRb1;
    @BindView(R2.id.security_camera_details_label2)
    RadioButton mCameraLabelRb2;
    @BindView(R2.id.security_camera_details_verson_tv)
    TextView mCameraVersonTv;
    @BindView(R2.id.security_camera_details_contact_tv)
    TextView mCameraContactTv;

    @BindView(R2.id.security_camera_details_address_tv)
    TextView mCameraAddressTv;


    private TipDialogUtils mCancelConfirmDialog, mUploadConfirmDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_camera_details_dialog_layout, null, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {

    }

    @Override
    protected void onBackPressed() {
        showCancelCommitDialog();
    }

    @OnClick({R2.id.iv_camera_details_popup_close})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_camera_details_popup_close) {
            showCancelCommitDialog();
        }
    }

    private void showCancelCommitDialog() {
        if (mCancelConfirmDialog == null) {
            mCancelConfirmDialog = new TipDialogUtils(getActivity());
            mCancelConfirmDialog.setTipConfirmText(getContext().getString(R.string.security_warn_confirm_dialog_exit_button), ContextCompat.getColor(getContext(), R.color.c_f34a4a));
            mCancelConfirmDialog.setTipMessageText(getContext().getString(R.string.security_warn_confirm_dialog_exit_title));
            mCancelConfirmDialog.setTipDialogUtilsClickListener(new TipDialogUtils.TipDialogUtilsClickListener() {
                @Override
                public void onCancelClick() {
                    mCancelConfirmDialog.dismiss();
                }

                @Override
                public void onConfirmClick() {
                    mCancelConfirmDialog.dismiss();
                    dismiss();
                }
            });
        }
        mCancelConfirmDialog.show();
    }

    private void showUploadConfirmDialog() {
        if (mUploadConfirmDialog == null) {
            mUploadConfirmDialog = new TipDialogUtils(getActivity());
            mUploadConfirmDialog.setTipConfirmText(getContext().getString(R.string.security_warn_confirm_dialog_upload_button), ContextCompat.getColor(getContext(), R.color.c_f34a4a));
            mUploadConfirmDialog.setTipMessageText(getContext().getString(R.string.security_warn_confirm_dialog_upload_title));
            mUploadConfirmDialog.setTipDialogUtilsClickListener(new TipDialogUtils.TipDialogUtilsClickListener() {
                @Override
                public void onCancelClick() {
                    mUploadConfirmDialog.dismiss();
                }

                @Override
                public void onConfirmClick() {
                    mUploadConfirmDialog.dismiss();
                }
            });
        }
        mUploadConfirmDialog.show();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, SecurityWarnConfirmDialog.class.getSimpleName());
    }

}
