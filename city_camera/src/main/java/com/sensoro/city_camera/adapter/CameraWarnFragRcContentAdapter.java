package com.sensoro.city_camera.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.DpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qinghao.wang
 */
public class CameraWarnFragRcContentAdapter extends RecyclerView.Adapter<CameraWarnFragRcContentAdapter.MyViewHolder> implements Constants {

    private final Context mContext;
    private CameraWarnConfirmStatusClickListener mListener;
    private final List<SecurityAlarmInfo> mList = new ArrayList<>();

    private int mWidth, mHeight;

    public CameraWarnFragRcContentAdapter(Context context) {
        mContext = context;
        initImageViewSize();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camerawarn_rc_content, parent, false);
        return new MyViewHolder(inflate);
    }

    public void setAlarmConfirmStatusClickListener(CameraWarnConfirmStatusClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        if(position==1){
//            changeStrokeColor(holder.mainWarnRcContentTvTag,R.color.c_ff8d34);
//            holder.mainWarnRcContentTvTag.setText("误报");
//        }boolean isReConfirm = false;
        SecurityAlarmInfo securityAlarmInfo = mList.get(position);
        if (securityAlarmInfo != null) {

            int warnType = securityAlarmInfo.getAlarmType();
            String capturePhotoUrl = securityAlarmInfo.getFaceUrl();
            String focusPhotoUrl = securityAlarmInfo.getImageUrl();
            String focusMatchRate = (int) securityAlarmInfo.getScore() + "%";
            long warnTime = securityAlarmInfo.getAlarmTime();

            holder.tvTaskName.setText(securityAlarmInfo.getTaskName());
            holder.tvWarnDeviceName.setText(securityAlarmInfo.getDeviceName());
            holder.tvWarnTime.setText(DateUtil.getStrTimeToday(mContext, warnTime, 0));
            boolean isShowInValidCover;//是否设置无效半透明
            //预警是否有效 处理
            if (securityAlarmInfo.getIsHandle() > 0) {
                boolean isWarnValid = (securityAlarmInfo.getIsHandle() != SecurityConstants.SECURITY_IS_NOT_HANDLE && securityAlarmInfo.getIsEffective() > 0);
                //已经处理 隐藏处理按钮/显示是否有效
                holder.btnWarnConfirm.setVisibility(View.INVISIBLE);
                holder.tvCameraWarnValid.setVisibility(View.VISIBLE);
                holder.tvCameraWarnValid.setBackgroundResource(isWarnValid ? R.drawable.shape_camera_warn_valid : R.drawable.shape_camera_warn_unvalid);
                holder.tvCameraWarnValid.setText(isWarnValid ? R.string.word_valid : R.string.word_unvalid);
                isShowInValidCover = !isWarnValid;
            } else {
                //未处理 显示处理按钮/隐藏是否有效标签
                holder.btnWarnConfirm.setVisibility(View.VISIBLE);
                holder.tvCameraWarnValid.setVisibility(View.GONE);
                isShowInValidCover = false;
            }
            holder.btnWarnConfirm.setVisibility(securityAlarmInfo.getIsHandle() == 0 ? View.VISIBLE : View.INVISIBLE);
            //根据预警类型设置UI
            switch (warnType) {
                //1-重点人员/2-外来人员/3-人员入侵
                case SecurityConstants.SECURITY_TYPE_FOCUS:
                    holder.tvWarnType.setText(R.string.focus_type);
                    holder.tvWarnType.setBackgroundResource(R.drawable.security_type_focus_bg);
                    holder.mSpace.setVisibility(View.VISIBLE);
                    holder.layoutMultiLeftContent.setVisibility(View.VISIBLE);
                    holder.layoutMultiRightContent.setVisibility(View.VISIBLE);
                    holder.tvRightMatchRate.setVisibility(View.VISIBLE);
                    //加载布控 抓拍 照片
                    Glide.with(mContext)
                            .load(focusPhotoUrl)
                            .apply(new RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_port_default_white)
                                    .centerCrop()
                                    .dontAnimate())
                            .into(holder.ivLeftPhoto);
                    Glide.with(mContext)
                            .load(capturePhotoUrl)
                            .apply(new RequestOptions().skipMemoryCache(false)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_port_default_white)
                                    .centerCrop()
                                    .dontAnimate())
                            .into(holder.ivRightPhoto);
                    holder.tvRightMatchRate.setText(focusMatchRate);
                    holder.ivLeftPhoto.setAlpha(isShowInValidCover ? 0.5f : 1f);
                    holder.ivRightPhoto.setAlpha(isShowInValidCover ? 0.5f : 1f);
                    break;
                case SecurityConstants.SECURITY_TYPE_FOREIGN:
                    holder.tvWarnType.setText(R.string.external_type);
                    holder.tvWarnType.setBackgroundResource(R.drawable.security_type_foreign_bg);
                    holder.mSpace.setVisibility(View.GONE);
                    holder.layoutMultiLeftContent.setVisibility(View.GONE);
                    holder.layoutMultiRightContent.setVisibility(View.VISIBLE);
                    holder.tvRightMatchRate.setVisibility(View.GONE);
                    //加载抓拍图片
                    Glide.with(mContext)
                            .load(capturePhotoUrl)
                            .apply(new RequestOptions().skipMemoryCache(false)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_port_default_white)
                                    .centerCrop()
                                    .dontAnimate())
                            .into(holder.ivRightPhoto);
                    holder.ivRightPhoto.setAlpha(isShowInValidCover ? 0.5f : 1f);
                    break;
                case SecurityConstants.SECURITY_TYPE_INVADE:
                    holder.tvWarnType.setText(R.string.invade_type);
                    holder.tvWarnType.setBackgroundResource(R.drawable.security_type_invade_bg);
                    holder.mSpace.setVisibility(View.GONE);
                    holder.layoutMultiLeftContent.setVisibility(View.GONE);
                    holder.layoutMultiRightContent.setVisibility(View.VISIBLE);
                    holder.tvRightMatchRate.setVisibility(View.GONE);
                    //加载抓拍照片
                    Glide.with(mContext)
                            .load(capturePhotoUrl)
                            .apply(new RequestOptions().skipMemoryCache(false)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .dontAnimate()
                                    .placeholder(R.drawable.ic_port_default_white))
                            .into(holder.ivRightPhoto);
                    holder.ivRightPhoto.setAlpha(isShowInValidCover ? 0.5f : 1f);
                    break;
                default:
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            }
        });
        holder.btnWarnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirmStatusClick(v, position);
                }
            }
        });
    }

    public void setData(List<SecurityAlarmInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<SecurityAlarmInfo> getData() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.left_image_rl)
        RelativeLayout layoutMultiLeftContent;

        @BindView(R2.id.right_image_rl)
        RelativeLayout layoutMultiRightContent;

        @BindView(R2.id.space_center)
        View mSpace;

        @BindView(R2.id.iv_left_photo)
        ImageView ivLeftPhoto;

        @BindView(R2.id.iv_right_photo)
        ImageView ivRightPhoto;

        @BindView(R2.id.tv_right_matchrate)
        TextView tvRightMatchRate;

        @BindView(R2.id.tv_camera_warn_type)
        TextView tvWarnType;
        @BindView(R2.id.tv_camera_task_name)
        TextView tvTaskName;
        @BindView(R2.id.tv_camera_warn_device_name)
        TextView tvWarnDeviceName;
        @BindView(R2.id.tv_camera_warn_time)
        TextView tvWarnTime;
        @BindView(R2.id.btn_camerawarn_confim)
        TextView btnWarnConfirm;

        @BindView(R2.id.tv_camerawarn_valid)
        TextView tvCameraWarnValid;


        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ViewGroup.LayoutParams leftParams = ivLeftPhoto.getLayoutParams();
            leftParams.height = mHeight;
            leftParams.width = mWidth;
            ivLeftPhoto.setLayoutParams(leftParams);

            ViewGroup.LayoutParams rightParams = ivRightPhoto.getLayoutParams();
            rightParams.height = mHeight;
            rightParams.width = mWidth;
            ivRightPhoto.setLayoutParams(rightParams);

        }
    }

    public interface CameraWarnConfirmStatusClickListener {
        void onConfirmStatusClick(View view, int position);

        void onItemClick(View view, int position);
    }

    private void initImageViewSize() {
        WindowManager w = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        mWidth = (metrics.widthPixels - DpUtils.dp2px(mContext, 52)) / 2;
        mHeight = mWidth * 380 / 323;

    }
}
