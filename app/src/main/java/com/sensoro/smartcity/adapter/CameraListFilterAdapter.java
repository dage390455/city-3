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
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraListFilterAdapter extends RecyclerView.Adapter<CameraListFilterAdapter.InspectionTaskStateSelectHolder> {
    private final Context mContext;
    private RecycleViewItemClickListener mListener;
    private boolean isMutilSelect;
    private List<InspectionStatusCountModel> mStateCountList = new ArrayList<>();

    public CameraListFilterAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionTaskStateSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_camera_filter, parent, false);
        InspectionTaskStateSelectHolder holder = new InspectionTaskStateSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final InspectionTaskStateSelectHolder holder, int position) {
        Resources resources = mContext.getResources();
        final InspectionStatusCountModel ic = mStateCountList.get(position);
        holder.itemPopTvSelectState.setText(ic.statusTitle);


        holder.itemPopSelectLlRoot.setBackgroundResource(!ic.isSelect ? R.drawable.shape_bg_solid_ff_stroke_df_corner
                : R.drawable.shape_bg_corner_1dbb99_shadow);

        holder.itemPopTvSelectState.setTextColor(resources.getColor(!ic.isSelect ? R.color.c_252525 : R.color.white));

        holder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMutilSelect) {
                    for (int i = 0; i < mStateCountList.size(); i++) {
                        mStateCountList.get(i).isSelect = false;
                    }

                }
                ic.isSelect = !ic.isSelect;
                notifyDataSetChanged();

//                if (mListener != null) {
//                    mListener.onItemClick(v, selectPosition);
//                }

            }
        });


    }

    public InspectionStatusCountModel getItem(int position) {
        return mStateCountList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<InspectionStatusCountModel> list, boolean isMutilSelect) {
        mStateCountList.clear();
        mStateCountList.addAll(list);
        this.isMutilSelect = isMutilSelect;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(InspectionTaskStateSelectHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return mStateCountList.size();
    }


    class InspectionTaskStateSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_pop_tv_select_state)
        TextView itemPopTvSelectState;

        @BindView(R.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;

        InspectionTaskStateSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
