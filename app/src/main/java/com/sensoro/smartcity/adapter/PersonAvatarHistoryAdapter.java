package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonAvatarHistoryAdapter extends RecyclerView.Adapter<PersonAvatarHistoryAdapter.PersonAvatarHistoryViewHolder> {
    private final Context mContext;
    private List<DeviceCameraPersonFaceRsp.DataBean> mList = new ArrayList<>();
    private RecycleViewItemClickListener mListener;


    public PersonAvatarHistoryAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public PersonAvatarHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_person_avatar_history, parent, false);
        PersonAvatarHistoryViewHolder holder = new PersonAvatarHistoryViewHolder(inflate);
        holder.llRootItemAdapterPersonAvatarHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, (Integer) v.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAvatarHistoryViewHolder holder, int position) {
        holder.llRootItemAdapterPersonAvatarHistory.setTag(position);

        DeviceCameraPersonFaceRsp.DataBean dataBean = mList.get(position);
        Glide.with(mContext)                             //配置上下文
                .load(Constants.CAMERA_BASE_URL + dataBean.getFaceUrl())
                .bitmapTransform(new GlideCircleTransform(mContext))
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.person_locus_placeholder)           //设置错误图片
                .placeholder(R.drawable.person_locus_placeholder)//设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(holder.ivAvatarItemAdapterPersonAvatarHistory);

        try {
            long l = Long.parseLong(dataBean.getCaptureTime());
            holder.tvTimeItemAdapterPersonAvatarHistory.setText(DateUtil.getStrTime_ymd_hm_ss(l));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            holder.tvTimeItemAdapterPersonAvatarHistory.setText(mContext.getString(R.string.time_parse_error));
        }


        holder.viewDividerItemAdapterPersonAvatarHistory.setVisibility(position == getItemCount()-1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<DeviceCameraPersonFaceRsp.DataBean> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public List<DeviceCameraPersonFaceRsp.DataBean> getData() {
        return mList;
    }

    public void setOnRecycleViewItemClickListener(RecycleViewItemClickListener listener){
        mListener = listener;
    }

    class PersonAvatarHistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar_item_adapter_person_avatar_history)
        ImageView ivAvatarItemAdapterPersonAvatarHistory;
        @BindView(R.id.tv_time_item_adapter_person_avatar_history)
        TextView tvTimeItemAdapterPersonAvatarHistory;
        @BindView(R.id.view_divider_item_adapter_person_avatar_history)
        View viewDividerItemAdapterPersonAvatarHistory;
        @BindView(R.id.ll_root_item_adapter_person_avatar_history)
        LinearLayout llRootItemAdapterPersonAvatarHistory;
        public PersonAvatarHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
