package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.manger.MaxHeightLinearLayoutManager;
import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.widgets.MaxHeightRecyclerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.WarningContactDialogAdapter;

import java.util.List;

public class WarningContactDialogUtil {
    private final Activity mActivity;
    private final ImageView mIvClose;
    private final MaxHeightRecyclerView mRvContent;
    private final CustomCornerDialog mDialog;
    private final TextView mTvCount;
    private WarningContactDialogAdapter mAdapter;

    public WarningContactDialogUtil(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public WarningContactDialogUtil(Activity activity) {
        mActivity = activity;
        final View view = View.inflate(activity, R.layout.dialog_warning_contact, null);
        WindowManager m = mActivity.getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        int maxHeight = (int) (d.getHeight() * 0.35);
        mIvClose = view.findViewById(R.id.iv_close_warning_contact);
        mTvCount = view.findViewById(R.id.tv_count_warning_contact);
        mRvContent = view.findViewById(R.id.rv_content_item_dialog_warning_contact);

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view, true);

        MaxHeightLinearLayoutManager manager = new MaxHeightLinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        mAdapter = new WarningContactDialogAdapter(mActivity);
        mRvContent.setLayoutManager(manager);
        mRvContent.setAdapter(mAdapter);
        mRvContent.setMaxHeight(maxHeight);


        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public void show(List<DeviceNotificationBean> data) {
        if (mDialog != null) {
            mAdapter.updateData(data);
            StringBuilder sb = new StringBuilder();
            sb.append(data.size()).append(mActivity.getString(R.string.warning_contact));
            mTvCount.setText(sb.toString());
            mDialog.show();
        }
    }

//    public void show() {
//        if (mDialog != null) {
//            mDialog.show();
//        }
//    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
        }
    }
}
