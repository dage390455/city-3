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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.common.server.security.bean.SecurityDeployPersonInfo;
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

    public static final String EXTRA_KEY_DEPLOY_INFO = "deploy_info";
    public static final String EXTRA_KEY_DEPLOY_IMAGE = "deploy_imageurl";
    private SecurityDeployPersonInfo mSecurityDeployPersonInfo;


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
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSecurityDeployPersonInfo = (SecurityDeployPersonInfo) bundle.getSerializable(EXTRA_KEY_DEPLOY_INFO);
            String imageUrl = bundle.getString(EXTRA_KEY_DEPLOY_IMAGE);
            Glide.with(this)
                    .load(imageUrl)
                    .apply(new RequestOptions().skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                            .dontAnimate())
                    .into(mControlPersonPhotoIv);
            mControlPersonNameTv.setText(mSecurityDeployPersonInfo.getName());
            mControlPersonNationTv.setText(mSecurityDeployPersonInfo.getNationality());
            mControlPersonTelephoneTv.setText(mSecurityDeployPersonInfo.getMobile());
            mControlPersonIdcardTv.setText(mSecurityDeployPersonInfo.getIdentityCardNumber());
            mControlPersonDescribeTv.setText(mSecurityDeployPersonInfo.getDescription());
        }


    }

    @Override
    protected void onBackPressed() {
        dismiss();
    }

    @OnClick({R2.id.iv_alarm_popup_close, R2.id.control_person_describe_tv})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_alarm_popup_close) {
            dismiss();
        }
    }


    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, SecurityWarnConfirmDialog.class.getSimpleName());
    }

}
