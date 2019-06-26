package com.sensoro.city_camera.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class SecurityControlPersonDetailsDialog extends BaseBottomDialog {

    @BindView(R2.id.iv_alarm_popup_close)
    ImageView mPopCloseIv;

    @BindView(R2.id.iv_control_person_photo)
    ImageView mControlPersonPhotoIv;

    @BindView(R2.id.control_person_name_tv)
    TextView mControlPersonNameTv;
    @BindView(R2.id.control_person_nation_tv)
    TextView mControlPersonNationTv;
    @BindView(R2.id.control_person_telephone_tv)
    TextView mControlPersonTelephoneTv;
    @BindView(R2.id.control_person_idcard_tv)
    TextView mControlPersonIdcardTv;
    @BindView(R2.id.control_person_describe_tv)
    TextView mControlPersonDescribeTv;


    private TipDialogUtils mCancelConfirmDialog, mUploadConfirmDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_warn_controlperson_details_dialog_layout, null, false);
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

    @OnClick({R2.id.iv_alarm_popup_close,R2.id.control_person_describe_tv})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_alarm_popup_close) {
            showCancelCommitDialog();
        }else if(i == R.id.control_person_describe_tv){

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
