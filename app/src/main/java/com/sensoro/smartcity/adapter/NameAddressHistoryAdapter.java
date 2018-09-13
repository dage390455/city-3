package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NameAddressHistoryAdapter extends RecyclerView.Adapter<NameAddressHistoryAdapter.NameAddressHistoryHolder> {

    private final Context mContext;

    String[] tests = {"黎明","只要我还活着，就不会有人遭受苦难","大师，永远怀着一颗学徒的心","即使双眼失明，也丝毫不影响我追捕敌人，我能够闻到他们身上的臭味"
    ,"提莫队长，正在送命"};
    public NameAddressHistoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public NameAddressHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_name_address_view, parent, false);
        return new NameAddressHistoryHolder(view);
    }

    @Override
    public int getItemCount() {
        return tests.length;
    }

    @Override
    public void onBindViewHolder(NameAddressHistoryHolder holder, int position) {
        //一定要设置，因为是通用的，所以要设置这个
        holder.itemAdapterTv.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_soid_ee_full_corner));
        holder.itemAdapterTv.setText(tests[position]);
    }

    class NameAddressHistoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_tv)
        TextView itemAdapterTv;
        public NameAddressHistoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
