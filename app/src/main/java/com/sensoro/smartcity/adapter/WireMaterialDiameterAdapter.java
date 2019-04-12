package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WireMaterialDiameterAdapter extends RecyclerView.Adapter<WireMaterialDiameterAdapter.WireMaterialDiameterViewHolder> {

    private final Context mContext;
    private final Drawable noClickBgDrawable;
    private ArrayList<WireMaterialDiameterModel> mList = new ArrayList<>(5);
    private onItemClickListener mListener;
    private final Drawable bgDrawable;
    private final int flBgGreenColor;
    private final int flBgWhiteColor;
    private final int flBgBlackColor;
    private final Drawable addDrawable;

    public WireMaterialDiameterAdapter(Context context) {
        mContext = context;
        Resources resources = mContext.getResources();
        bgDrawable = resources.getDrawable(R.drawable.select_wire_diameter_stroke_fafa_ee);
        noClickBgDrawable = resources.getDrawable(R.drawable.shape_bg_solid_df_corner_2);
        flBgGreenColor = resources.getColor(R.color.c_29c093);
        flBgWhiteColor = resources.getColor(R.color.white);
        flBgBlackColor = resources.getColor(R.color.c_252525);
        addDrawable = mContext.getDrawable(R.drawable.wire_add);
        if (addDrawable != null) {
            addDrawable.setBounds(0,0,addDrawable.getMinimumWidth(),addDrawable.getMinimumHeight());
        }
    }


    @NonNull
    @Override
    public WireMaterialDiameterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_wire_material_diameter_add, parent, false);
        final WireMaterialDiameterViewHolder holder = new WireMaterialDiameterViewHolder(view);
        holder.flTittleItemAdapterWireMaterialDiameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Integer position = (Integer) holder.flTittleItemAdapterWireMaterialDiameter.getTag();
                    mListener.onItemClick(position, position == 0);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WireMaterialDiameterViewHolder holder, int position) {
        holder.flTittleItemAdapterWireMaterialDiameter.setTag(position);
        if(position == 0){
            holder.flTittleItemAdapterWireMaterialDiameter.setBackground(null);
            if(getItemCount() == 6){
                holder.tvTitleItemAdapterWireMaterialDiameter.setText(mContext.getString(R.string.add_wire));
                holder.flWrapItemAdapterWireMaterialDiameter.setBackground(noClickBgDrawable);
                holder.tvTitleItemAdapterWireMaterialDiameter.setClickable(false);
                holder.flTittleItemAdapterWireMaterialDiameter.setClickable(false);
                holder.tvTitleItemAdapterWireMaterialDiameter.setTextColor(Color.WHITE);
                addDrawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                holder.tvTitleItemAdapterWireMaterialDiameter.setCompoundDrawables(addDrawable,null,null,null);

            }else{
                holder.tvTitleItemAdapterWireMaterialDiameter.setText(mContext.getString(R.string.add_wire));
                holder.tvTitleItemAdapterWireMaterialDiameter.setTextColor(flBgBlackColor);
                holder.flWrapItemAdapterWireMaterialDiameter.setBackground(bgDrawable);
                addDrawable.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                holder.tvTitleItemAdapterWireMaterialDiameter.setCompoundDrawables(addDrawable,null,null,null);
            }
        }else{
            holder.tvTitleItemAdapterWireMaterialDiameter.setCompoundDrawables(null,null,null,null);
            WireMaterialDiameterModel model = mList.get(position - 1);
            holder.flTittleItemAdapterWireMaterialDiameter.setBackgroundColor(model.isSelected ? flBgGreenColor : flBgWhiteColor);
            holder.tvTitleItemAdapterWireMaterialDiameter.setTextColor( model.isSelected ? flBgWhiteColor : flBgBlackColor);
            StringBuilder sb;
            if (model.material == 1) {
                sb = new StringBuilder(mContext.getString(R.string.al));
            } else {
                sb = new StringBuilder(mContext.getString(R.string.cu));
            }
            sb.append(" ").append(model.diameter).append("mm² × ").append(model.count);
            holder.tvTitleItemAdapterWireMaterialDiameter.setText(sb.toString());
//            holder.flTittleItemAdapterWireMaterialDiameter.setBackground(flBgDrawable);
        }

    }


    @Override
    public int getItemCount() {
        return mList.size()+1;
    }


    public void updateData(ArrayList<WireMaterialDiameterModel> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    class WireMaterialDiameterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title_item_adapter_wire_material_diameter)
        TextView tvTitleItemAdapterWireMaterialDiameter;
        @BindView(R.id.fl_tittle_item_adapter_wire_material_diameter)
        FrameLayout flTittleItemAdapterWireMaterialDiameter;
        @BindView(R.id.fl_wrap_item_adapter_wire_material_diameter)
        FrameLayout flWrapItemAdapterWireMaterialDiameter;

        public WireMaterialDiameterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position, boolean isAction);
    }
}
