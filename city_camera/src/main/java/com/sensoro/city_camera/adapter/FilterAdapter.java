package com.sensoro.city_camera.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.model.FilterModel;
import com.sensoro.common.callback.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 筛选选项的
 * @author qinghao.wang
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.InspectionTaskStateSelectHolder> {
    private final Context mContext;
    private int selectPosition = -1;
    private int oldSelectPosition = -1;
    private RecycleViewItemClickListener mListener;
    private List<FilterModel> mStateCountList = new ArrayList<>();

    public FilterAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionTaskStateSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_filter, parent, false);
        InspectionTaskStateSelectHolder holder = new InspectionTaskStateSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(InspectionTaskStateSelectHolder holder, int position) {
        if(selectPosition == -1&& oldSelectPosition == -1){
            for (FilterModel filterModel:mStateCountList){
                if(filterModel.isDefault) {
                    selectPosition = mStateCountList.indexOf(filterModel);
                    break;
                }
            }
        }
        Resources resources = mContext.getResources();
        FilterModel ic = mStateCountList.get(position);
        holder.itemPopTvSelectState.setText(ic.statusTitle);
        //设置选择后背景
//        holder.itemPopSelectLlRoot.setBackgroundResource(position != selectPosition ? R.drawable.filter_corner
//                : R.drawable.shape_bg_corner_29c_shadow);
        if(position == selectPosition){
            holder.itemPopTvSelectState.setTextColor(resources.getColor(R.color.c_252525));
            holder.itemPopTvSelectState.getPaint().setFakeBoldText(true);
        }else{
            holder.itemPopTvSelectState.setTextColor(resources.getColor(R.color.c_a6a6a6));
            holder.itemPopTvSelectState.getPaint().setFakeBoldText(false);
        }
        holder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldSelectPosition = selectPosition;
                selectPosition = ((InspectionTaskStateSelectHolder) v.getTag()).getAdapterPosition();
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onItemClick(v, selectPosition);
                }

            }
        });


    }

    public FilterModel getItem(int position) {
        return mStateCountList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<FilterModel> list) {
        mStateCountList.clear();
        mStateCountList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(InspectionTaskStateSelectHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (position == selectPosition || position == oldSelectPosition) {
                onBindViewHolder(holder, position);
            }
        }
    }


    @Override
    public int getItemCount() {
        return mStateCountList.size();
    }


    class InspectionTaskStateSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_pop_tv_select_state)
        TextView itemPopTvSelectState;
        @BindView(R2.id.item_pop_select_ll_root)
        View itemPopSelectLlRoot;

        InspectionTaskStateSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
