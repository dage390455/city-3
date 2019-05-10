package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraLiveDetailAdapter extends RecyclerView.Adapter<CameraLiveDetailAdapter.CameraLiveDetailViewHolder> {
    private final Context mContext;

    List mList = new ArrayList();
    private AlarmCameraLiveItemClickListener mListenre;

    public CameraLiveDetailAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public CameraLiveDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_alarm_camera_live_detail, parent, false);
        final CameraLiveDetailViewHolder holder = new CameraLiveDetailViewHolder(inflate);
        holder.clRootItemAdapterAlarmCameraLiveDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.clRootItemAdapterAlarmCameraLiveDetail.getTag();
                if (mListenre != null) {
                    mListenre.OnAlarmCameraLiveItemClick(position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CameraLiveDetailViewHolder holder, int position) {
        holder.clRootItemAdapterAlarmCameraLiveDetail.setTag(position);

    }

    public void updateData(List data){
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnAlarmCameraLiveItemClickListener(AlarmCameraLiveItemClickListener listener){
        mListenre = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface AlarmCameraLiveItemClickListener{
        void OnAlarmCameraLiveItemClick(int position);
    }

    class CameraLiveDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_pic_item_adapter_alarm_camera_live_detail)
        ImageView ivPicItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.iv_live_item_adapter_alarm_camera_live_detail)
        ImageView ivLiveItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_watch_state_item_adapter_alarm_camera_live_detail)
        TextView tvWatchStateItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_name_item_adapter_alarm_camera_live_detail)
        TextView tvNameItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_status_item_adapter_alarm_camera_live_detail)
        TextView tvStatusItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.cl_root_item_adapter_alarm_camera_live_detail)
        ConstraintLayout clRootItemAdapterAlarmCameraLiveDetail;
        public CameraLiveDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
