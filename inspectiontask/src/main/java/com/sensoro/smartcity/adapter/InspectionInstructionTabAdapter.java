package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.inspectiontask.R;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.inspectiontask.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionInstructionTabAdapter extends RecyclerView.Adapter<InspectionInstructionTabAdapter.InspectionInstructionTabHolder> {
    private final Context mContext;
    private int selectPosition;
    private List<String> tabs = new ArrayList<>();
    private RecycleViewItemClickListener listener;


    public InspectionInstructionTabAdapter(Context context) {
        mContext = context;
    }

    @Override
    public InspectionInstructionTabHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_instruction_tab, parent, false);
        return new InspectionInstructionTabHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionInstructionTabHolder holder, final int position) {
        if (position == selectPosition) {
            holder.itemAdapterInspectionInstructionTv.setBackgroundResource(R.drawable.shape_bg_inspectiontask_solid_29c_full_corner);
            holder.itemAdapterInspectionInstructionTv.setTextColor(Color.WHITE);
        } else {
            holder.itemAdapterInspectionInstructionTv.setBackgroundResource(R.drawable.shape_bg_inspectiontask_solid_transparent_full_corner);
            holder.itemAdapterInspectionInstructionTv.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
        }

        holder.itemAdapterInspectionInstructionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });
        //查找巡检内容的时候，根据deviceType查找，所以tabs存的就是devicetype,后期这部分代码还是要改的
        String deviceType = tabs.get(position);
        holder.itemAdapterInspectionInstructionTv.setText(WidgetUtil.getInspectionDeviceName(deviceType));
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return tabs.size();
    }

    public void updateTagDataList(List<String> deviceTypes) {
        tabs.clear();
        tabs.addAll(deviceTypes);
        notifyDataSetChanged();
    }

    public String getItem(int position) {

        return tabs.get(position);
    }

    class InspectionInstructionTabHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_adapter_inspection_instruction_tv)
        TextView itemAdapterInspectionInstructionTv;

        InspectionInstructionTabHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
