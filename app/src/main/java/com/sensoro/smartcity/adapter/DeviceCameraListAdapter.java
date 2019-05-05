package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

/**
 * camera list有聚合，为防止改回去，先保留
 */
public class DeviceCameraListAdapter extends RecyclerView.Adapter<DeviceCameraListAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeviceCameraFacePicListModel> mList = new ArrayList<>();
    private OnDeviceCameraListClickListener onDeviceCameraListClickListener;
    private DeviceCameraFacePicListModel preModel;
    private boolean liveClick = true;

    public void setLiveState(boolean isLiveStream) {
        liveClick = isLiveStream;
    }

    public void clearPreModel() {
        preModel = null;
    }

    public interface OnDeviceCameraListClickListener {
        void onItemClick(View view, int position);

        void onLiveClick();

        void onAvatarClick(int modelPosition,int avatarPosition);
    }

    public void setOnContentItemClickListener(OnDeviceCameraListClickListener onDeviceCameraListClickListener) {
        this.onDeviceCameraListClickListener = onDeviceCameraListClickListener;
    }

    public DeviceCameraListAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final ArrayList<DeviceCameraFacePicListModel> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

//    public void addData(List<DeviceCameraFacePic> list) {
//        mList.addAll(list);
//    }

    public List<DeviceCameraFacePicListModel> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MyViewHolder holder;
        if (viewType == 1) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_list_top, parent, false);
            holder = new MyViewHolder(inflate);
            holder.clLiveStream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (preModel != null) {
                        preModel.isSelect = false;
                    }
                    liveClick = true;
                    notifyDataSetChanged();
                    if (onDeviceCameraListClickListener != null) {
                        onDeviceCameraListClickListener.onLiveClick();
                    }
                }
            });
        } else if (viewType == 2) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_list_title, parent, false);
            holder = new MyViewHolder(inflate);
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_device_camera_list_adapter, parent, false);
            holder = new MyViewHolder(inflate);
            holder.clPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer position = (Integer) holder.clPicture.getTag();
                    if (preModel != null) {
                        preModel.isSelect = false;
                    }
                    liveClick = false;
                    DeviceCameraFacePicListModel model = mList.get(position - 1);
                    model.isSelect = true;
                    preModel = model;
                    notifyDataSetChanged();
                    if (onDeviceCameraListClickListener != null) {
                        onDeviceCameraListClickListener.onItemClick(v, position);
                    }

                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (getItemViewType(position) == 1) {
            holder.clLiveStream.setTag(position);
            holder.clLiveStream.setBackgroundColor(liveClick ? Color.parseColor("#F3F4F4") :
                    mContext.getResources().getColor(R.color.white));
        } else if (getItemViewType(position) == 2) {
            holder.tvTimeTitle.setTag(position);
            holder.tvTimeTitle.setBackgroundColor(Color.WHITE);
            DeviceCameraFacePicListModel model = mList.get(position - 1);
            holder.tvTimeTitle.setText(model.time);
        } else {
            holder.clPicture.setTag(position);

            final int index = position - 1;
            DeviceCameraFacePicListModel model = mList.get(index);
            holder.clPicture.setBackgroundColor(model.isSelect ? Color.parseColor("#F3F4F4") : mContext.getResources().
                    getColor(R.color.white));
            String captureTime = model.pics.get(0).getCaptureTime();
            String strTime_hm = DateUtil.getStrTime_hm(captureTime);
            //
            holder.tvTimeItemCamera.setText(strTime_hm);
            CameraDetailAvatarAdapter avatarAdapter = new CameraDetailAvatarAdapter(mContext);
            GridLayoutManager manager = new GridLayoutManager(mContext, 4);
            holder.rvPicture.setLayoutManager(manager);
            holder.rvPicture.setAdapter(avatarAdapter);
            holder.rvPicture.setLayoutFrozen(true);
            avatarAdapter.setOnAvatarClickListener(new CameraDetailAvatarAdapter.OnAvatarClickListener() {
                @Override
                public void onAvatar(int position) {
                    if (onDeviceCameraListClickListener != null) {
                        onDeviceCameraListClickListener.onAvatarClick(index,position);
                    }
                }
            });

            //
            if (position - 1 > -1 && getItemViewType(position - 1) == 2) {
                holder.viewAbove.setVisibility(View.INVISIBLE);
            } else if (position + 1 < getItemCount() && getItemViewType(position + 1) == 2) {
                holder.viewBelow.setVisibility(View.INVISIBLE);
            } else {
                holder.viewAbove.setVisibility(View.VISIBLE);
                holder.viewBelow.setVisibility(View.VISIBLE);
            }

            avatarAdapter.updateData(model.pics);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        }
        DeviceCameraFacePicListModel model = mList.get(position - 1);
        if (model.pics == null) {
            return 2;
        }

        return 3;
    }

    private void setBottomVisible(MyViewHolder holder, int position) {
//        if (getItemCount() - 1 == position) {
//            holder.lineBottom.setVisibility(View.INVISIBLE);
//        } else {
//            holder.lineBottom.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.imv_ring)
        ImageView imvRing;
        @Nullable
        @BindView(R.id.tv_live_stream)
        TextView tvLiveStream;
        @Nullable
        @BindView(R.id.cl_live_stream)
        ConstraintLayout clLiveStream;
        @Nullable
        @BindView(R.id.tv_time_item_camera)
        TextView tvTimeItemCamera;
        @Nullable
        @BindView(R.id.tv_time_list_top)
        TextView tvTimeListTop;
        @Nullable
        @BindView(R.id.tv_time_title)
        TextView tvTimeTitle;
        @Nullable
        @BindView(R.id.view_item_camera)
        View viewItemCamera;
        @Nullable
        @BindView(R.id.rv_picture)
        RecyclerView rvPicture;
        @Nullable
        @BindView(R.id.view_below)
        View viewBelow;
        @Nullable
        @BindView(R.id.view_above)
        View viewAbove;
        @Nullable
        @BindView(R.id.cl_picture)
        ConstraintLayout clPicture;


        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
