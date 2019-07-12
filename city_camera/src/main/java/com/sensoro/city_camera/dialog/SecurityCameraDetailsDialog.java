package com.sensoro.city_camera.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.adapter.LabelAdapter;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.security.bean.SecurityCameraInfo;
import com.sensoro.common.server.security.bean.SecurityContactsInfo;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SpacesItemDecoration;

import java.util.List;

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
    @BindView(R2.id.label_rv)
    RecyclerView mLabelRv;
    @BindView(R2.id.scroview_camera_details)
    NestedScrollView mNestedScrollView;

    @BindView(R2.id.security_camera_details_verson_tv)
    TextView mCameraVersonTv;
    //联系人
    @BindView(R2.id.layout_camera_details_contact)
    RelativeLayout layoutCameraContact;
    @BindView(R2.id.security_camera_details_contact_tv)
    TextView mCameraContactTv;
    @BindView(R2.id.security_camera_details_contact_amount)
    TextView mCameraContactCountTv;
    //地址
    @BindView(R2.id.layout_camera_details_address)
    RelativeLayout layoutCameraAddress;
    @BindView(R2.id.security_camera_details_address_tv)
    TextView mCameraAddressTv;
    private LabelAdapter mLabelAdapter;

    public static final String EXTRA_KEY_SECURITY_ID = "security_id";
    public static final String EXTRA_KEY_CAMERA_INFO = "camera_info";
    private String id;
    private SecurityCameraInfo mSecurityCameraInfo;
    private int contactCount;

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
        if (bundle != null) {
            id = bundle.getString(EXTRA_KEY_SECURITY_ID);
            mSecurityCameraInfo = (SecurityCameraInfo) bundle.getSerializable(EXTRA_KEY_CAMERA_INFO);
            List<String> labelList = mSecurityCameraInfo.getLabel();
            List<SecurityContactsInfo> constantsList = mSecurityCameraInfo.getContact();
            String contactStr;

            if (constantsList != null && constantsList.size() > 0) {
                SecurityContactsInfo contactsInfo = constantsList.get(0);
                contactStr = TextUtils.isEmpty(contactsInfo.getName()) ? contactsInfo.getMobilePhone()
                        : contactsInfo.getName() + "|" + contactsInfo.getMobilePhone();
                contactCount = constantsList.size();
            } else {
                contactStr = "";
                contactCount = 0;
            }
            int cameraStatus;
            try {
                cameraStatus = Integer.parseInt(mSecurityCameraInfo.getDeviceStatus());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cameraStatus = 0;
            }

            mCameraNameTv.setText(mSecurityCameraInfo.getName());
            mCameraTypeTv.setText(mSecurityCameraInfo.getType());
            mCameraStatusTv.setText(cameraStatus == SecurityConstants.SECURITY_DEVICE_ONLINE ? R.string.offline : R.string.online);
            mCameraStatusTv.setTextColor(cameraStatus == SecurityConstants.SECURITY_DEVICE_ONLINE ? getResources().getColor(R.color.c_1dbb99)
                    : getResources().getColor(R.color.c_a6a6a6));
            mCameraSNTv.setText(mSecurityCameraInfo.getSn());
            mCameraBrandTv.setText(mSecurityCameraInfo.getBrand());
            mCameraVersonTv.setText(mSecurityCameraInfo.getVersion());
            mCameraContactTv.setText(contactStr);
            mCameraContactCountTv.setText(String.format(getString(R.string.contact_count_tip), contactCount));
            mCameraAddressTv.setText(mSecurityCameraInfo.getLocation());

            if (labelList.isEmpty()) {
                mLabelRv.setVisibility(View.INVISIBLE);
            } else {
                mLabelRv.setVisibility(View.VISIBLE);
                mLabelAdapter = new LabelAdapter(getActivity());

                SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                mLabelRv.setLayoutManager(layoutManager);
                mLabelRv.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(getActivity(), 4)));
                mLabelRv.setAdapter(mLabelAdapter);
                mLabelRv.setHasFixedSize(true);
                mLabelRv.setNestedScrollingEnabled(false);
                mLabelAdapter.updateLabelList(labelList);

            }


        }

    }

    @Override
    protected void onBackPressed() {
        dismiss();
    }

    @OnClick({R2.id.iv_camera_details_popup_close, R2.id.layout_camera_details_contact,
            R2.id.layout_camera_details_address})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_camera_details_popup_close) {
            dismiss();
        } else if (i == R.id.layout_camera_details_contact) {
            if (null != mSecurityCameraInfo.getContact() && mSecurityCameraInfo.getContact().size() > 0) {
                //联系人点击事件处理
                mSecurityCameraDetailsCallback.showContactsDetails();
            }

        } else if (i == R.id.layout_camera_details_address) {
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
         * 显示联系人
         */
        void showContactsDetails();
    }

    public void setSecurityCameraDetailsCallback(SecurityCameraDetailsCallback securityCameraDetailsCallback) {
        mSecurityCameraDetailsCallback = securityCameraDetailsCallback;
    }

}
