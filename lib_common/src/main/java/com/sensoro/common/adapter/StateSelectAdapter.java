package com.sensoro.common.adapter;

import android.content.Context;
import android.content.res.Resources;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.common.R;
import com.sensoro.common.R2;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.model.StatusCountModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StateSelectAdapter extends RecyclerView.Adapter<StateSelectAdapter.StateSelectHolder> {
    private final Context mContext;
    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<StatusCountModel> mStateCountList = new ArrayList<>();

    public StateSelectAdapter(Context context) {
        mContext = context;
    }


    @Override
    public StateSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_inspection_task_select_state, parent, false);
        StateSelectHolder holder = new StateSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(StateSelectHolder holder, int position) {
        Resources resources = mContext.getResources();
        StatusCountModel ic = mStateCountList.get(position);
        if (ic.count == -1) {
            holder.itemPopTvSelectCount.setVisibility(View.GONE);
        } else {
            holder.itemPopTvSelectCount.setVisibility(View.VISIBLE);
            holder.itemPopTvSelectCount.setText(ic.count + "");
        }
        holder.itemPopTvSelectState.setText(ic.statusTitle);

        holder.itemPopSelectLlRoot.setBackgroundResource(position != selectPosition ? R.drawable.shape_bg_solid_ff_stroke_df_corner
                : R.drawable.shape_bg_corner_29c_shadow);
        holder.itemPopTvSelectCount.setTextColor(resources.getColor(position != selectPosition ? R.color.c_a6a6a6 : R.color.white));
        holder.itemPopTvSelectState.setTextColor(resources.getColor(position != selectPosition ? R.color.c_252525 : R.color.white));

        holder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldSelectPosition = selectPosition;
                selectPosition = ((StateSelectHolder) v.getTag()).getAdapterPosition();
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onItemClick(v, selectPosition);
                }

            }
        });


    }

    public StatusCountModel getItem(int position) {
        return mStateCountList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<StatusCountModel> list) {
        mStateCountList.clear();
        mStateCountList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(StateSelectHolder holder, int position, List<Object> payloads) {
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


    class StateSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_pop_tv_select_state)
        TextView itemPopTvSelectState;
        @BindView(R2.id.item_pop_tv_select_count)
        TextView itemPopTvSelectCount;
        @BindView(R2.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;

        StateSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
