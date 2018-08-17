package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class IndexFilterStatusAdapter extends RecyclerView.Adapter<IndexFilterStatusAdapter.IndexFilterTypeViewHolder> {

    private Context mContext;
    private final List<String> mList = new ArrayList<>();

    private RecycleViewItemClickListener itemClickListener;

    public IndexFilterStatusAdapter(Context context, List<String> list, RecycleViewItemClickListener
            itemClickListener) {
        this.mContext = context;
        this.mList.clear();
        this.mList.addAll(list);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public IndexFilterTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_index_status, null);
        return new IndexFilterTypeViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(IndexFilterTypeViewHolder holder, int position) {
        holder.nameTextView.setText(mList.get(position));
        Drawable drawable = null;
        if (position != 0) {
            switch (position) {
                case 1:
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_alarm);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;
                case 2:
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_normal);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;
                case 3:
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_lost);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;
                case 4:
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_inactive);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;

            }
        }
        holder.nameTextView.setCompoundDrawables(drawable, null, null, null);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class IndexFilterTypeViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final RecycleViewItemClickListener itemClickListener;

        IndexFilterTypeViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.item_index_status_name);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(onItemClickListener);
        }

        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        };
    }

}
