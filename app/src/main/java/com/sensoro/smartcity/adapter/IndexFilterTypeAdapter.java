package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class IndexFilterTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mList;

    RecycleViewItemClickListener itemClickListener;

    public IndexFilterTypeAdapter(Context context, List<String> list, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.mList = list;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_index_type, null);
        return new IndexFilterTypeViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mList == null) {
            return;
        }
        ((IndexFilterTypeViewHolder) holder).nameTextView.setText(mList.get(position));
        if (position != 0) {
            ((IndexFilterTypeViewHolder) holder).iconLayout.setVisibility(View.VISIBLE);
            ((IndexFilterTypeViewHolder) holder).iconImageView.setImageResource(Constants.TYPE_MENU_RESOURCE[position-1]);
        } else {
            ((IndexFilterTypeViewHolder) holder).iconLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class IndexFilterTypeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView iconImageView;
        LinearLayout iconLayout;
        RecycleViewItemClickListener itemClickListener;

        public IndexFilterTypeViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            iconImageView = (ImageView) itemView.findViewById(R.id.item_index_type_icon);
            iconLayout = (LinearLayout) itemView.findViewById(R.id.item_index_type_icon_layout);
            nameTextView = (TextView) itemView.findViewById(R.id.item_index_type_name);
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
