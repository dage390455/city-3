package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.RecommendedTransformerDialogUtilsAdapter;
import com.sensoro.common.model.RecommendedTransformerValueModel;

import java.util.List;

public class RecommendedTransformerDialogUtils {

    private ImageView ivRecommendTransformerClose;
    private TextView tvRecommendTransformerValue;

    private RecyclerView rvRecommendTransformerValue;
    //
    private RecommendedTransformerDialogUtilsAdapter mAdapter;
    private CustomCornerDialog mDialog;
    private OnRecommendedTransformerDialogUtilsListener listener;

    public RecommendedTransformerDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_dialog_recommend_transformer, null);
        ivRecommendTransformerClose = view.findViewById(R.id.iv_recommend_transformer_close);
        tvRecommendTransformerValue = view.findViewById(R.id.tv_recommend_transformer_value);
        rvRecommendTransformerValue = view.findViewById(R.id.rv_recommend_transformer_value);
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecommendTransformerValue.setLayoutManager(layoutManager);
        mAdapter = new RecommendedTransformerDialogUtilsAdapter(activity);
        rvRecommendTransformerValue.setAdapter(mAdapter);
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view, 560 / 750f);
        ivRecommendTransformerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
                dismiss();
            }
        });
        mAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listener != null) {
                    RecommendedTransformerValueModel recommendedTransformerValueModel = mAdapter.getData().get(position);
                    listener.onItemChose(recommendedTransformerValueModel);
                }

            }
        });
    }

    public void show(List<RecommendedTransformerValueModel> data, String recommendValue) {
        if (mDialog != null) {
            updateRecommendedTransformerAdapter(data);
            tvRecommendTransformerValue.setText(recommendValue);
            mDialog.show();

        }
    }

    private void updateRecommendedTransformerAdapter(List<RecommendedTransformerValueModel> data) {
        if (data != null) {
            mAdapter.updateList(data);
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

    public void setOnRecommendedTransformerDialogUtilsListener(OnRecommendedTransformerDialogUtilsListener onRecommendedTransformerDialogUtilsListener) {
        this.listener = onRecommendedTransformerDialogUtilsListener;
    }

    public interface OnRecommendedTransformerDialogUtilsListener {
        void onCancel();

        void onItemChose(RecommendedTransformerValueModel recommendedTransformerValueModel);
    }

}
