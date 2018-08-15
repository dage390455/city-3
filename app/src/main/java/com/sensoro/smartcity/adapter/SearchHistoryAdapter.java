package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {

    private Context mContext;
    private List<String> mList;
    private RecycleViewItemClickListener itemClickListener;

    public SearchHistoryAdapter(Context context, List<String> list, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.mList = list;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_search_history, null);
        return new SearchHistoryViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(SearchHistoryViewHolder holder, int position) {
        if (mList == null) {
            return;
        }
        holder.nameTextView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final View itemView;
        final RecycleViewItemClickListener itemClickListener;

        SearchHistoryViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            this.itemView = itemView;
            nameTextView = (TextView) itemView.findViewById(R.id.item_history_name);
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

    public void setDataAndFresh(List<String> list) {
        this.mList = list;
        notifyDataSetChanged();
    }
}
