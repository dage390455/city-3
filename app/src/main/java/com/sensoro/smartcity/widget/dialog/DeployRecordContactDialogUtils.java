package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployRecordContactDialogUtilsAdapter;
import com.sensoro.smartcity.adapter.EarlyWarningThresholdDialogUtilsAdapter;

import java.util.List;

public class DeployRecordContactDialogUtils {

    private TextView tvDeployRecordContactDesc;
    private RecyclerView rvDeployRecordContact;
    private DeployRecordContactDialogUtilsAdapter mAdapter;
    private CustomCornerDialog mDialog;
    private ImageView ivCancel;
    private TextView tvTitle;
    private Activity mActivity;

    public DeployRecordContactDialogUtils(Activity activity) {
        mActivity = activity;
        View view = View.inflate(activity, R.layout.item_dialog_deploy_record_contact, null);
        tvTitle = view.findViewById(R.id.tv_deploy_record_contact_title);
        tvDeployRecordContactDesc = view.findViewById(R.id.tv_deploy_record_contact_desc);
        ivCancel = view.findViewById(R.id.iv_cancel);

        rvDeployRecordContact = view.findViewById(R.id.rv_deploy_record_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDeployRecordContact.setLayoutManager(layoutManager);
        mAdapter = new DeployRecordContactDialogUtilsAdapter(activity);
        rvDeployRecordContact.setAdapter(mAdapter);
//        mTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view, 560 / 750f);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }


    public void show(List<DeviceNotificationBean> data) {
        if (mDialog != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(mActivity.getString(R.string.total)).append(data.size()).append(mActivity.getString(R.string.person));
            tvDeployRecordContactDesc.setText(sb.toString());
            mAdapter.updateList(data);
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

}
