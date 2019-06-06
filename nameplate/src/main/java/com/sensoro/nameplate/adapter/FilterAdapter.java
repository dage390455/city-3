package com.sensoro.nameplate.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.model.FilterModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.InspectionTaskStateSelectHolder> {
    private final Context mContext;
    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<FilterModel> mStateCountList = new ArrayList<>();

    public FilterAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionTaskStateSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_filter, parent, false);
        InspectionTaskStateSelectHolder holder = new InspectionTaskStateSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(InspectionTaskStateSelectHolder holder, int position) {
        Resources resources = mContext.getResources();
        FilterModel ic = mStateCountList.get(position);
        holder.itemPopTvSelectState.setText(ic.statusTitle);

        holder.itemPopSelectLlRoot.setBackgroundResource(position != selectPosition ? R.drawable.filter_corner
                : R.drawable.shape_bg_corner_29c_shadow);
        holder.itemPopTvSelectState.setTextColor(resources.getColor(position != selectPosition ? R.color.c_252525 : R.color.white));

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
