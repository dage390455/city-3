package com.sensoro.city_camera.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.common.widgets.dialog.TipDialogUtils;

import java.util.ArrayList;

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
    //联系人
    @BindView(R.id.layout_camera_details_contact)
    RelativeLayout layoutCameraContact;
    @BindView(R2.id.security_camera_details_contact_tv)
    TextView mCameraContactTv;
    @BindView(R2.id.security_camera_details_contact_amount)
    TextView mCameraContactCountTv;
    //地址
    @BindView(R.id.layout_camera_details_address)
    RelativeLayout layoutCameraAddress;
    @BindView(R2.id.security_camera_details_address_tv)
    TextView mCameraAddressTv;

    public static final String EXTRA_KEY_SECURITY_ID = "security_id";
    public static final String EXTRA_KEY_CAMERA_NAME = "camera_name";
    public static final String EXTRA_KEY_CAMERA_TYPE = "camera_type";
    public static final String EXTRA_KEY_CAMERA_SATUS = "camera_status";
    public static final String EXTRA_KEY_CAMERA_SN = "camera_sn";
    public static final String EXTRA_KEY_CAMERA_BRAND = "camera_brand";
    public static final String EXTRA_KEY_CAMERA_LABEL = "camera_label";
    public static final String EXTRA_KEY_CAMERA_VERSION = "camera_version";
    public static final String EXTRA_KEY_CAMERA_CONTACT = "camera_contect";
    public static final String EXTRA_KEY_CAMERA_CONTACT_COUNT = "camera_contect_count";
    public static final String EXTRA_KEY_CAMERA_ADDRESS = "camera_address";
    private String id;

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
        Bundle bundle = getArguments();
        if (bundle != null){
            id = bundle.getString(EXTRA_KEY_SECURITY_ID);
            String name = bundle.getString(EXTRA_KEY_CAMERA_NAME);
            String type = bundle.getString(EXTRA_KEY_CAMERA_TYPE);
            int deviceStatus = bundle.getInt(EXTRA_KEY_CAMERA_SATUS);
            String sn = bundle.getString(EXTRA_KEY_CAMERA_SN);
            String brand = bundle.getString(EXTRA_KEY_CAMERA_BRAND);
            ArrayList<String> labelList = bundle.getStringArrayList(EXTRA_KEY_CAMERA_LABEL);
            String version = bundle.getString(EXTRA_KEY_CAMERA_VERSION);
            String contactStr = bundle.getString(EXTRA_KEY_CAMERA_CONTACT);
            int contactCount = bundle.getInt(EXTRA_KEY_CAMERA_CONTACT_COUNT);
            String address = bundle.getString(EXTRA_KEY_CAMERA_ADDRESS);

            mCameraNameTv.setText(name);
            mCameraTypeTv.setText(type);
            mCameraStatusTv.setText(deviceStatus == 0?R.string.offline:R.string.online);
            mCameraStatusTv.setTextColor(deviceStatus == 0?getResources().getColor(R.color.c_1dbb99)
                    :getResources().getColor(R.color.c_f35a58));
            mCameraSNTv.setText(sn);
            mCameraBrandTv.setText(brand);
            mCameraVersonTv.setText(version);
            mCameraContactTv.setText(contactStr);
            mCameraContactCountTv.setText(String.format(getString(R.string.contact_count_tip),contactCount));
            mCameraAddressTv.setText(address);
            //add Camera label


        }

    }

    @Override
    protected void onBackPressed() {
        dismiss();
    }

    @OnClick({R2.id.iv_camera_details_popup_close,R2.id.layout_camera_details_contact,
    R2.id.layout_camera_details_address})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_camera_details_popup_close) {
            dismiss();
        }else if(i == R.id.layout_camera_details_contact){
            //联系人点击事件处理
            mSecurityCameraDetailsCallback.showContactsDetails();
        }else if(i == R.id.layout_camera_details_address){
            //地址点击事件处理
            mSecurityCameraDetailsCallback.onNavi();
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, SecurityWarnConfirmDialog.class.getSimpleName());
    }
    private SecurityCameraDetailsCallback mSecurityCameraDetailsCallback;
    public interface SecurityCameraDetailsCallback {

        /**
         * 导航
         */
        void onNavi();
        /**
         *
         */
        void showContactsDetails();
    }

    public void setSecurityCameraDetailsCallback(SecurityCameraDetailsCallback securityCameraDetailsCallback) {
        mSecurityCameraDetailsCallback = securityCameraDetailsCallback;
    }

}
