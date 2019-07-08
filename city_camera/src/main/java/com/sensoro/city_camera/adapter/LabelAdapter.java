package com.sensoro.city_camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qinghao.wang
 */
public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelHolder> {
    private final Context mContext;
    List<String> mLabelTitleList = new ArrayList<>();

    public LabelAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_label, parent, false);
        LabelHolder holder = new LabelHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LabelHolder holder, int position) {
        holder.rbLabel.setText(mLabelTitleList.get(position));
        holder.rbLabel.setClickable(false);
    }

    public void updateLabelList(List<String> list) {
        mLabelTitleList.clear();
        mLabelTitleList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mLabelTitleList.size();
    }

    class LabelHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.security_camera_details_label)
        RadioButton rbLabel;


        LabelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
