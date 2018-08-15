package com.sensoro.smartcity.adapter;

import android.content.Context;
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

public class RelationAdapter extends RecyclerView.Adapter<RelationAdapter.RelationViewHolder> {

    private Context mContext;
    private List<String> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;

    public RelationAdapter(Context context, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<String> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<String> getData() {
        return mList;
    }

    @Override
    public RelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_relation, parent, false);

        return new RelationViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(RelationViewHolder holder, int position) {
        if (mList == null) {
            return;
        }
        holder.nameTextView.setText(mList.get(position));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class RelationViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final View itemView;
        final RecycleViewItemClickListener itemClickListener;

        RelationViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            this.itemView = itemView;
            nameTextView = (TextView) itemView.findViewById(R.id.item_relation_name);
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
