package com.sensoro.nameplate.widget;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.adapter.AssociationSensorDialogAdapter;
import com.sensoro.nameplate.model.AddSensorFromListModel;

import java.util.List;

public class AssociationSensorConfirmDialogUtil {
    private final Activity mActivity;
    private final ImageView mIvClose;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final RecyclerView mRvContent;
    private final CustomCornerDialog mDialog;
    private final TextView mTvCount;
    private AssociationSensorDialogAdapter mAdapter;
    private OnListener mListener;

    public AssociationSensorConfirmDialogUtil(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public AssociationSensorConfirmDialogUtil(Activity activity) {
        mActivity = activity;
        final View view = View.inflate(activity, R.layout.item_dialog_association_sensor_confirm, null);

        mIvClose = view.findViewById(R.id.iv_close_item_dialog_associate_sensor_confirm);
        mTvCancel = view.findViewById(R.id.tv_cancel_item_dialog_associate_sensor_confirm);
        mTvConfirm = view.findViewById(R.id.tv_confirm_item_dialog_associate_sensor_confirm);
        mTvCount = view.findViewById(R.id.tv_count_item_dialog_associate_sensor_confirm);
        mRvContent = view.findViewById(R.id.rv_content_item_dialog_associate_sensor_confirm);

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view, true);

        MaxHeightLinearLayoutManager manager = new MaxHeightLinearLayoutManager(mActivity, RecyclerView.VERTICAL,false);
        mAdapter = new AssociationSensorDialogAdapter(mActivity);
        mRvContent.setLayoutManager(manager);
        mRvContent.setAdapter(mAdapter);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm();
                }
            }
        });
    }

   public void setOnListener(OnListener listener){
        mListener = listener;
   }

   public interface OnListener{
        void onConfirm();
   }
    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public void show(List<AddSensorFromListModel> data) {
        if (mDialog != null) {
            mAdapter.updateData(data);
            StringBuilder sb = new StringBuilder();
            sb.append(mActivity.getString(R.string.selectede)).append(data.size()).append(mActivity.getString(R.string.sensor));
            mTvCount.setText(sb.toString());
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
        }
    }
}
