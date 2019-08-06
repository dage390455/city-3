/**
 * 首页排序条件列表适配器
 */
package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortConditionSelectAdapter extends RecyclerView.Adapter<SortConditionSelectAdapter.SortConditionSelectHolder> {
    private final Context mContext;
    private RecycleViewItemClickListener mListener;
    private List<String> mSortConditionList = new ArrayList<>();
    private String  mSelectSortCondition;

    public SortConditionSelectAdapter(Context context) {
        mContext = context;

    }


    public void updateSortConditionList(List<String>  mSortConditionList){
        this.mSortConditionList.clear();
        this.mSortConditionList.addAll(mSortConditionList);
        if(this.mSortConditionList.size()>0){
            mSelectSortCondition=this.mSortConditionList.get(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public SortConditionSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_type_select, parent, false);
        SortConditionSelectHolder holder = new SortConditionSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SortConditionSelectHolder mHolder, final int position) {

        final  String  mSortConditionModel=mSortConditionList.get(position);
        if(mSortConditionModel.equalsIgnoreCase(this.mSelectSortCondition)){
            mHolder.itemPopSelectImvSortconditionIcon.setVisibility(View.VISIBLE);
            mHolder.itemPopSelectTvSortconditionName.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));
        }else{
            mHolder.itemPopSelectImvSortconditionIcon.setVisibility(View.INVISIBLE);
            mHolder.itemPopSelectTvSortconditionName.setTextColor(mContext.getResources().getColor(R.color.c_252525));
        }
        mHolder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectSortCondition=getItem(position);
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }

            }
        });


    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(SortConditionSelectHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    public  String getItem(int position){
            return    mSortConditionList.get(position);
    }

    @Override
    public int getItemCount() {
        return mSortConditionList.size() ;
    }

    public List<String> getDataList() {
        return mSortConditionList;
    }

    class SortConditionSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_pop_select_imv_sortcondition_icon)
        ImageView itemPopSelectImvSortconditionIcon;
        @BindView(R.id.item_pop_select_tv_sortcondition_name)
        TextView itemPopSelectTvSortconditionName;
        @BindView(R.id.item_pop_sortcondition_ll_root)
        LinearLayout itemPopSelectLlRoot;

        SortConditionSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
