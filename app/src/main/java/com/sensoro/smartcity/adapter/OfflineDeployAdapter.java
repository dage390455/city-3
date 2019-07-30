package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfflineDeployAdapter extends RecyclerView.Adapter<OfflineDeployAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeployAnalyzerModel> mList = new ArrayList<>();

    private int currentTaskIndex = -1;
    private OnContentItemClickListener onContentItemClickListener;

    public interface OnContentItemClickListener {

        void onItemClick(View view, int position);

        void onClearClick(View view, int position);
    }

    public void setOnContentItemClickListener(OnContentItemClickListener onContentItemClickListener) {
        this.onContentItemClickListener = onContentItemClickListener;
    }

    public OfflineDeployAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final List<DeployAnalyzerModel> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setCurrentTaskIndex(int index) {

        this.currentTaskIndex = index;
        notifyDataSetChanged();
    }

    public void addData(List<DeployAnalyzerModel> list) {
        mList.addAll(list);
    }

    public List<DeployAnalyzerModel> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_offline_deploy, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DeployAnalyzerModel deviceInfo = mList.get(position);
        //
        String sn = deviceInfo.sn;

        holder.itemOfflineDeployAdapterSnTv.setText(sn);
        holder.itemOfflineDeployAdapterClearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContentItemClickListener != null) {

                    onContentItemClickListener.onClearClick(v, position);

                }
            }
        });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContentItemClickListener != null) {

                    onContentItemClickListener.onItemClick(v, position);

                }
            }
        });
        if (position == currentTaskIndex) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {

            holder.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_offline_deploy_adapter_sn_tv)
        TextView itemOfflineDeployAdapterSnTv;
        @BindView(R.id.item_offline_deploy_adapter_clear_tv)
        TextView itemOfflineDeployAdapterClearTv;
        @BindView(R.id.oading_prgbar)
        ProgressBar progressBar;
        @BindView(R.id.item_offline_deploy_root)
        View root;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
