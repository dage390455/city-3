package com.sensoro.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.common.R;
import com.sensoro.common.callback.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {

    private Context mContext;
    private final List<String> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;

    public SearchHistoryAdapter(Context context, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;

    }

    public List<String> getSearchHistoryList() {
        return mList;
    }

    @Override
    public SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_search_history, null);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchHistoryViewHolder holder, int position) {
        holder.nameTextView.setText(mList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;

        SearchHistoryViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.item_history_name);
        }

    }

    public void updateSearchHistoryAdapter(List<String> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }
}
