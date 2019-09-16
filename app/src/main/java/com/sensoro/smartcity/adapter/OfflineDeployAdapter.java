package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfflineDeployAdapter extends RecyclerView.Adapter<OfflineDeployAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeployAnalyzerModel> mList = new ArrayList<>();

    private int currentTaskIndex = -1;
    private boolean canClick = true;//批量上传不能点击单个
    private OnContentItemClickListener onContentItemClickListener;

    public interface OnContentItemClickListener {

//        void onUploadClick(View view, int position);

        void onForceUploadClick(View view, int position);

        void onClearClick(View view, int position);

        void onItemClick(View v, int position);
    }

    public void setOnContentClickListener(OnContentItemClickListener onContentItemClickListener) {
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

    public void setUploadClickable(boolean canClick) {
        this.canClick = canClick;
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
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_offline_deploy2, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DeployAnalyzerModel deviceInfo = mList.get(position);
        //
        String type = "";
        switch (deviceInfo.deployType) {
            case Constants.TYPE_SCAN_DEPLOY_STATION:
                type = mContext.getResources().getString(R.string.base_station_deployment);
                break;
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                type = mContext.getResources().getString(R.string.deployment_device);
                break;
            case Constants.TYPE_SCAN_DEPLOY_CAMERA:
                type = mContext.getResources().getString(R.string.deployment_device);
                break;
            default:
                type = mContext.getResources().getString(R.string.deployment_replacement);
                break;
        }

        holder.typeTv.setText(type);
        String deviceType = deviceInfo.deviceType;
        StringBuilder stringBuilder = new StringBuilder();
        holder.itemOfflineDeployAdapterSnTv.setText(stringBuilder.append(" SN:").append(deviceInfo.sn).toString());


        holder.itemOfflineDeployAdapterClearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContentItemClickListener != null) {
                    onContentItemClickListener.onClearClick(v, position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContentItemClickListener != null) {
                    onContentItemClickListener.onItemClick(v, position);
                }
            }
        });
//        holder.tvUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onContentItemClickListener != null && canClick) {
//                    onContentItemClickListener.onUploadClick(v, position);
//                }
//            }
//        });
        holder.tvForceLoad.setOnClickListener(v -> {
            if (onContentItemClickListener != null && canClick) {
                onContentItemClickListener.onForceUploadClick(v, position);
            }
        });
        if (position == currentTaskIndex) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        if (deviceInfo.isShowForce) {
            holder.tvForceLoad.setVisibility(View.VISIBLE);
        } else {
            holder.tvForceLoad.setVisibility(View.GONE);

        }


        // TODO: 2019-09-16 是否已部署显示 
        if (deviceInfo.hasDeployed) {

            holder.tvHasdeployed.setText(mContext.getResources().getString(R.string.deployed));
            holder.tvdeployedtime.setText(mContext.getResources().getString(R.string.deployed));
        } else {
            holder.tvHasdeployed.setText("");
            holder.tvdeployedtime.setText("");

        }

        String deviceTypeStr = WidgetUtil.getDeviceMainTypeName(deviceType);
        StringBuilder sb = new StringBuilder();
        sb.append(deviceTypeStr);
        if (deviceInfo.updatedTime == 0) {
            deviceInfo.updatedTime = deviceInfo.lastOperateTime;
            sb.append(DateUtil.getStrTimeToday(mContext, deviceInfo.lastOperateTime, 0));

        } else {
            sb.append(DateUtil.getStrTimeToday(mContext, deviceInfo.updatedTime, 0));


        }
        // TODO: 2019-09-16 追加已部署时间
        sb.append("部署时间：" + DateUtil.getDate(deviceInfo.lastOperateTime));
        holder.timeTv.setText(sb.toString());

        if (!TextUtils.isEmpty(deviceInfo.getStateErrorMsg)) {
            holder.itemOfflineDeployAdapterErrorMsgTv.setText(deviceInfo.getStateErrorMsg);


        } else {
            holder.itemOfflineDeployAdapterErrorMsgTv.setText("");


        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_offline_deploy_adapter_tv_errormsg)
        TextView itemOfflineDeployAdapterErrorMsgTv;
        @BindView(R.id.item_offline_deploy_adapter_tv_sn)
        TextView itemOfflineDeployAdapterSnTv;
        @BindView(R.id.item_offline_deploy_adapter_clear_tv)
        TextView itemOfflineDeployAdapterClearTv;
        @BindView(R.id.item_offline_deploy_adapter_tiem_tv)
        TextView timeTv;
        @BindView(R.id.item_offline_deploy_adapter_tv_type)
        TextView typeTv;
        @BindView(R.id.item_offline_deploy_adapter_tv_force_upload)
        TextView tvForceLoad;
        //        @BindView(R.id.item_offline_deploy_tv_upload)
//        TextView tvUpload;
        @BindView(R.id.item_offline_deploy_adapter_tv_hasdeployed)
        TextView tvHasdeployed;
        @BindView(R.id.item_offline_deploy_adapter_tv_deployedtime)
        TextView tvdeployedtime;
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
