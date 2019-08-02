package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.DeviceCameraFacePic;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraDetailListAdapter extends RecyclerView.Adapter<CameraDetailListAdapter.CameraDetailListViewHolder> {

    private final Context mContext;
    private final List<DeviceCameraFacePic> mList = new ArrayList<>();
    private CameraDetailListClickListener mListener;
    private int mClickPosition = -1;

    public CameraDetailListAdapter(Context context) {
        mContext = context;
    }


    @NonNull
    @Override
    public CameraDetailListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_detail_list, parent, false);
        final CameraDetailListViewHolder holder = new CameraDetailListViewHolder(inflate);
        holder.clRootItemAdapterCameraDetailList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int temp = mClickPosition;
                mClickPosition = (int) holder.clRootItemAdapterCameraDetailList.getTag();
                notifyItemChanged(temp);
                notifyItemChanged(mClickPosition);
                if (mListener != null) {
                    mListener.onItemClick(mClickPosition);
                }
            }
        });

        holder.flAvatarItemAdapterCameraDetailList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAvatarClick((Integer) holder.flAvatarItemAdapterCameraDetailList.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CameraDetailListViewHolder holder, int position) {
        holder.clRootItemAdapterCameraDetailList.setTag(position);
        holder.flAvatarItemAdapterCameraDetailList.setTag(position);

        holder.viewAboveItemAdapterCameraDetailList.setVisibility(position <= 0 ? View.GONE : View.VISIBLE);
        holder.viewBelowItemAdapterCameraDetailList.setVisibility(position >= getItemCount() - 1 ? View.GONE : View.VISIBLE);

        holder.clRootItemAdapterCameraDetailList.setBackgroundResource(mClickPosition == position ? R.drawable.shape_bg_solid_ee_full_corner_4 : R.drawable.shape_bg_solid_white_bottom_left_right_corner);

        DeviceCameraFacePic model = mList.get(position);
        String url = model.getFaceUrl();
        if (!TextUtils.isEmpty(url)) {
            if (!(url.startsWith("https://") || url.startsWith("http://"))) {
                url = Constants.CAMERA_BASE_URL + model.getFaceUrl();
            }
            url = url.trim();
        }
        Uri parse = Uri.parse(url);
        Glide.with(mContext)                             //配置上下文
                .load(parse)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        try {
                            LogUtils.loge("onLoadFailed");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(new RequestOptions().transform(new GlideCircleTransform(mContext)).error(R.drawable.person_locus_placeholder).placeholder(R.drawable.person_locus_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL))

//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                //设置错误图片
                //设置占位图片
                //缓存全尺寸
                .into(holder.ivAvatarItemAdapterCameraDetailList);

        String strTime_hm;
        try {
            long l = Long.parseLong(model.getCaptureTime());
            strTime_hm = DateUtil.getStrTime_MM_dd_hms(l);
        } catch (Exception e) {
            e.printStackTrace();
            strTime_hm = mContext.getString(R.string.unknown);
        }
        holder.tvTimeItemAdapterCameraDetailList.setText(strTime_hm);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(final List<DeviceCameraFacePic> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<DeviceCameraFacePic> getData() {
        return mList;
    }

    public void setOnCameraDetailListClickListener(CameraDetailListClickListener listener) {
        mListener = listener;
    }

    public void clearClickPosition() {
        int temp = mClickPosition;
        mClickPosition = -1;
        notifyItemChanged(temp);
    }

    class CameraDetailListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar_item_adapter_camera_detail_list)
        ImageView ivAvatarItemAdapterCameraDetailList;
        @BindView(R.id.fl_avatar_item_adapter_camera_detail_list)
        FrameLayout flAvatarItemAdapterCameraDetailList;
        @BindView(R.id.view_above_item_adapter_camera_detail_list)
        View viewAboveItemAdapterCameraDetailList;
        @BindView(R.id.view_below_item_adapter_camera_detail_list)
        View viewBelowItemAdapterCameraDetailList;
        @BindView(R.id.tv_time_item_adapter_camera_detail_list)
        TextView tvTimeItemAdapterCameraDetailList;
        @BindView(R.id.cl_root_item_adapter_camera_detail_list)
        ConstraintLayout clRootItemAdapterCameraDetailList;

        public CameraDetailListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CameraDetailListClickListener {
        void onItemClick(int position);

        void onAvatarClick(int position);
    }
}
