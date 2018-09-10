package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;

public class TypeSelectAdapter extends RecyclerView.Adapter<TypeSelectAdapter.TypeSelectHolder>{
    private final Context mContext;

    public TypeSelectAdapter(Context context) {
        mContext = context;
    }

    @Override
    public TypeSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_type_select,parent,false);
        return null;
    }

    @Override
    public void onBindViewHolder(TypeSelectHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class TypeSelectHolder extends RecyclerView.ViewHolder{

        public TypeSelectHolder(View itemView) {
            super(itemView);
        }
    }

}
