package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.util.DpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagAdapter extends RecyclerView.
        Adapter<TagAdapter.TagHolder> {
    private final Context mContext;
    private final List<String> tags = new ArrayList<>();
    private  int mTextColor = -1;
    private  int mStrokeColor = -1;
    private int mStrokeWidth;

    public TagAdapter(Context context, @ColorRes int textColorId,@ColorRes int strokeColorId) {
        mContext = context;
        mTextColor = textColorId;
        mStrokeColor = strokeColorId;
        mStrokeWidth = DpUtils.dp2px(context, 1);
    }

    public void updateTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_text_view, parent, false);

        return new TagHolder(view);
    }

    @Override
    public void onBindViewHolder(TagHolder holder, int position) {
        holder.itemAdapterTv.setText(tags.get(position));
        changeColor(holder);
    }

    private void changeColor(TagHolder holder) {
        Resources resources = mContext.getResources();
        holder.itemAdapterTv.setTextColor(resources.getColor(mTextColor));
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_bg_solid_ff_stroke_df_full_corner);
        gd.setBounds(0,0,gd.getMinimumWidth(),gd.getMinimumHeight());
        gd.setStroke(mStrokeWidth,resources.getColor(mStrokeColor));
        holder.itemAdapterTv.setBackground(gd);


    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_tv)
        TextView itemAdapterTv;

        TagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
