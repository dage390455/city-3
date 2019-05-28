package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrequencyPointAdapter extends RecyclerView.Adapter<FrequencyPointAdapter.FrequencyPointHolder> {
    private final Context mContext;
    private List<String> mStateCountList = new ArrayList<>();

    public FrequencyPointAdapter(Context context) {
        mContext = context;
    }


    @Override
    public FrequencyPointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_frequency_point, parent, false);
        FrequencyPointHolder holder = new FrequencyPointHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FrequencyPointHolder holder, int position) {


        if (null != mStateCountList.get(position)) {
            final String ic = mStateCountList.get(position);
            holder.itemPointTv.setText(ic);

        }


    }


    public void updateDeviceTypList(List<String> list) {
        mStateCountList.clear();
        mStateCountList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(FrequencyPointHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return mStateCountList.size();
    }


    class FrequencyPointHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_poit_tv)
        TextView itemPointTv;


        FrequencyPointHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
