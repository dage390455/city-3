package com.sensoro.city_camera.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.dialog.TipDialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public class SecurityWarnConfirmDialog extends BaseBottomDialog {

    @BindView(R2.id.iv_alarm_popup_close)
    ImageView mPopCloseIv;
    @BindView(R2.id.security_warn_type_tv)
    TextView mSecurityWarnTypeTv;
    @BindView(R2.id.security_warn_title_tv)
    TextView mSecurityWarnTitleTv;
    @BindView(R2.id.security_warn_time_tv)
    TextView mSecurityWarnTimeTv;
    @BindView(R2.id.security_warn_type_invalid_rb)
    RadioButton mSecurityWarnTypeInvalidRb;
    @BindView(R2.id.security_warn_type_valid_rb)
    RadioButton mSecurityWarnTypeValidRb;
    @BindView(R2.id.security_warn_des_et)
    EditText mSecurityWarnDesEt;
    @BindView(R2.id.security_warn_commit_btn)
    Button mSecurityWarnCommitBtn;

    private TipDialogUtils mCancelConfirmDialog, mUploadConfirmDialog;

    public static final String EXTRA_KEY_SECURITY_ID = "security_id";
    public static final String EXTRA_KEY_SECURITY_TITLE = "security_title";
    public static final String EXTRA_KEY_SECURITY_TIME = "security_time";
    public static final String EXTRA_KEY_SECURITY_TYPE = "security_type";
    private String id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableSlideDismiss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_warn_confirm_dialog_layout, null, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString(EXTRA_KEY_SECURITY_ID);
            String title = bundle.getString(EXTRA_KEY_SECURITY_TITLE);
            String time = bundle.getString(EXTRA_KEY_SECURITY_TIME);
            int type = bundle.getInt(EXTRA_KEY_SECURITY_TYPE);

            if(getContext() != null){
                mSecurityWarnTitleTv.setText(getContext().getString(R.string.start_include_backspace_text, title));
            }
            mSecurityWarnTimeTv.setText(DateUtil.getStrTimeToday(getContext(), Long.parseLong(time), 0));
            switch (type) {
                case SecurityConstants.SECURITY_TYPE_FOCUS:
                    mSecurityWarnTypeTv.setText(R.string.focus_type);
                    mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_focus_bg);
                    break;
                case SecurityConstants.SECURITY_TYPE_FOREIGN:
                    mSecurityWarnTypeTv.setText(R.string.external_type);
                    mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_foreign_bg);
                    break;
                case SecurityConstants.SECURITY_TYPE_INVADE:
                    mSecurityWarnTypeTv.setText(R.string.invade_type);
                    mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_invade_bg);
                    break;
                default:
            }
        }

        mSecurityWarnTypeValidRb.setOnCheckedChangeListener((buttonView, isChecked) ->
                mSecurityWarnCommitBtn.setBackgroundResource(isChecked
                        ? R.drawable.security_warn_valid_commit_btn_bg
                        : R.drawable.security_warn_invalid_commit_btn_bg));
    }

    @Override
    protected void onBackPressed() {
        showCancelCommitDialog();
    }

    @OnClick({R2.id.iv_alarm_popup_close, R2.id.security_warn_commit_btn})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_alarm_popup_close) {
            showCancelCommitDialog();
        } else if (i == R.id.security_warn_commit_btn) {
            if (!mSecurityWarnTypeValidRb.isChecked() && !mSecurityWarnTypeInvalidRb.isChecked()) {
                SensoroToast.getInstance().makeText(getString(R.string.please_complete_the_required_fields_first), Toast.LENGTH_SHORT).show();
            } else {
                showUploadConfirmDialog();
            }
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
                    mSecurityWarnDesEt.getText().clear();
                    dismiss();
                }
            });
        }
        mCancelConfirmDialog.show();
    }

    private void showUploadConfirmDialog() {
        if (mUploadConfirmDialog == null) {
            mUploadConfirmDialog = new TipDialogUtils(getActivity());
            mUploadConfirmDialog.setTipDialogUtilsClickListener(new TipDialogUtils.TipDialogUtilsClickListener() {
                @Override
                public void onCancelClick() {
                    mUploadConfirmDialog.dismiss();
                }

                @Override
                public void onConfirmClick() {
                    if (mSecurityConfirmCallback != null) {
                        mSecurityConfirmCallback.onConfirmClick(id,
                                mSecurityWarnTypeValidRb.isChecked()
                                        ? SecurityConstants.SECURITY_VALID
                                        : SecurityConstants.SECURITY_INVALID,
                                String.valueOf(mSecurityWarnDesEt.getText()));
                    }
                    dismiss();
                    mUploadConfirmDialog.dismiss();
                }
            });
        }
        mUploadConfirmDialog.setTipConfirmText(getContext().getString(R.string.security_warn_confirm_dialog_upload_button), ContextCompat.getColor(getContext(), R.color.c_f34a4a));
        mUploadConfirmDialog.setTipMessageText(
                getContext().getString(R.string.security_warn_confirm_dialog_upload_title,
                        mSecurityWarnTypeValidRb.isChecked()
                                ? getContext().getString(R.string.word_valid)
                                : getContext().getString(R.string.word_unvalid)));
        mUploadConfirmDialog.show();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, SecurityWarnConfirmDialog.class.getSimpleName());
    }

    private SecurityConfirmCallback mSecurityConfirmCallback;

    public interface SecurityConfirmCallback {

        /**
         * 预警确认弹窗回调
         *
         * @param id
         * @param isEffective
         * @param operationDetail
         */
        void onConfirmClick(String id, int isEffective, String operationDetail);
    }

    public void setSecurityConfirmCallback(SecurityConfirmCallback securityConfirmCallback) {
        mSecurityConfirmCallback = securityConfirmCallback;
    }
}
