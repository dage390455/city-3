package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraListFilterAdapter extends RecyclerView.Adapter<CameraListFilterAdapter.InspectionTaskStateSelectHolder> {
    private final Context mContext;
    private RecycleViewItemClickListener mListener;
    private boolean isMutilSelect;
    private List<CameraFilterModel.ListBean> mStateCountList = new ArrayList<>();

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


        if (null != mStateCountList.get(position)) {
            final CameraFilterModel.ListBean ic = mStateCountList.get(position);

            if (!TextUtils.isEmpty(ic.getName())) {

                String name = ic.getName().replace("（", "(").replace("）", ")");
                holder.itemPopTvSelectState.setText(name);
            }
            boolean select = ic.isSelect();
            holder.itemPopSelectLlRoot.setBackgroundResource(!select ? R.drawable.shape_bg_solid_ff_corner
                    : R.drawable.shape_bg_corner_1dbb99_shadow);

            if (select) {
                holder.selected_tick_iv.setVisibility(View.VISIBLE);
            } else {
                holder.selected_tick_iv.setVisibility(View.GONE);

            }

            holder.itemPopTvSelectState.setTextColor(resources.getColor(!select ? R.color.c_252525 : R.color.white));

            holder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isMutilSelect) {
                        for (int i = 0; i < mStateCountList.size(); i++) {
                            mStateCountList.get(i).setSelect(false);
                        }

                    }

                    ic.setSelect(!ic.isSelect());
                    notifyDataSetChanged();


                }
            });
        }


    }

    public CameraFilterModel.ListBean getItem(int position) {
        return mStateCountList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<CameraFilterModel.ListBean> list, boolean isMutilSelect) {
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
        @BindView(R.id.selected_tick_iv)
        ImageView selected_tick_iv;

        @BindView(R.id.item_pop_select_ll_root)
        RelativeLayout itemPopSelectLlRoot;

        InspectionTaskStateSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
