package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionTaskStateSelectAdapter extends RecyclerView.Adapter<InspectionTaskStateSelectAdapter.InspectionTaskStateSelectHolder> {
    private final Context mContext;
    private String[] types = Constants.SELECT_TYPE;
    private Integer[] typeIcons = Constants.SELECT_TYPE_RESOURCE;
    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<InspectionStatusCountModel> mStateCountList = new ArrayList<>();

    public InspectionTaskStateSelectAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionTaskStateSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_inspection_task_select_state, parent, false);
        InspectionTaskStateSelectHolder holder = new InspectionTaskStateSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(InspectionTaskStateSelectHolder holder, int position) {
        Resources resources = mContext.getResources();
        InspectionStatusCountModel inspectionStatusCountModel = mStateCountList.get(position);
        holder.itemPopTvSelectCount.setText(inspectionStatusCountModel.count+"");
        holder.itemPopTvSelectState.setText(inspectionStatusCountModel.state);

        holder.itemPopSelectLlRoot.setBackgroundResource(position != selectPosition ? R.drawable.shape_bg_solid_ff_stroke_df_corner
        : R.drawable.shape_bg_corner_29c_shadow);
        holder.itemPopTvSelectCount.setTextColor(resources.getColor(position != selectPosition ?R.color.c_a6a6a6:R.color.white));
        holder.itemPopTvSelectState.setTextColor(resources.getColor(position != selectPosition ?R.color.c_252525:R.color.white));

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

    public InspectionStatusCountModel getItem(int position) {
        return mStateCountList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<InspectionStatusCountModel> list) {
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
        @BindView(R.id.item_pop_tv_select_state)
        TextView itemPopTvSelectState;
        @BindView(R.id.item_pop_tv_select_count)
        TextView itemPopTvSelectCount;
        @BindView(R.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;
        InspectionTaskStateSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
