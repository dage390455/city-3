package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.EarlyWarningThresholdDialogUtilsAdapter;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;

import java.util.List;

public class RecommendedTransformerDialogUtils {

    private ImageView ivRecommendTransformerClose;
    private TextView tvRecommendTransformerValue;

    private RecyclerView rvRecommendTransformerValue;
    //
    private EarlyWarningThresholdDialogUtilsAdapter mAdapter;
    private CustomCornerDialog mDialog;

    public RecommendedTransformerDialogUtils(Activity activity) {
        this(activity, activity.getString(R.string.warning_threshold));

    }

    public RecommendedTransformerDialogUtils(Activity activity, String title) {
        View view = View.inflate(activity, R.layout.item_dialog_recommend_transformer, null);
        ivRecommendTransformerClose = view.findViewById(R.id.iv_recommend_transformer_close);
        tvRecommendTransformerValue = view.findViewById(R.id.tv_recommend_transformer_value);
        rvRecommendTransformerValue = view.findViewById(R.id.rv_recommend_transformer_value);
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecommendTransformerValue.setLayoutManager(layoutManager);
        mAdapter = new EarlyWarningThresholdDialogUtilsAdapter(activity);
        rvRecommendTransformerValue.setAdapter(mAdapter);
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
        ivRecommendTransformerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ivRecommendTransformerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (listener != null) {
//                    listener.onChangeInfoClick();
//                }
            }
        });

    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void show(List<EarlyWarningthresholdDialogUtilsAdapterModel> data) {
        if (mDialog != null) {
//            updateEarlyWarningThresholdAdapter(data);
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destory() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

}
