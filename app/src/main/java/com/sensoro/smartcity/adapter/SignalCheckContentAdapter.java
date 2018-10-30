package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.SignalData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignalCheckContentAdapter extends RecyclerView.Adapter<SignalCheckContentAdapter.SignalCheckContentHolder> {

    private final Context mContext;
    private List<SignalData> datas = new ArrayList<>();

    public SignalCheckContentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SignalCheckContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_signal_chcek_content, parent, false);
        return new SignalCheckContentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignalCheckContentHolder holder, int position) {
        SignalData signalData = datas.get(position);
        int down_sf = 12 - signalData.getDownlinkDR();
        int up_sf = 12 - signalData.getUplinkDR();
        holder.itemAdapterSignalCheckTvTime.setText(signalData.getDate());
        float up_freq = (float)signalData.getUplinkFreq() / 1000000;
        StringBuilder upData = new StringBuilder();
        upData.append("RSSI：");
        upData.append(signalData.getUplinkRSSI() == 0 ? "-" : signalData.getUplinkRSSI());
        upData.append("SNR：");
        upData.append(signalData.getUplinkSNR() == 0 ? "-" : signalData.getUplinkSNR());
        upData.append(signalData.getUplinkTxPower() == 0 ? "-" : signalData.getUplinkTxPower());
        upData.append(up_sf);
        upData.append("@");
        upData.append(up_freq == 0 ? "-" : up_freq);
        upData.append("MHz");
        holder.itemAdapterSignalCheckTvUpData.setText(upData.toString());

        float down_freq = (float)signalData.getDownlinkFreq() / 1000000;
        StringBuilder loadData = new StringBuilder();
        loadData.append("RSSI：");
        loadData.append(signalData.getDownlinkRSSI() == 0 ? "-" : signalData.getDownlinkRSSI());
        loadData.append("SNR：");
        loadData.append(signalData.getDownlinkSNR() == 0 ? "-" : signalData.getDownlinkSNR());
        loadData.append(signalData.getDownlinkTxPower() == 0 ? "-" : signalData.getDownlinkTxPower());
        loadData.append(down_sf);
        loadData.append("@");
        loadData.append(down_freq == 0 ? "-" : down_freq);
        loadData.append("MHz");
        holder.itemAdapterSignalCheckTvLoadData.setText(loadData.toString());

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void updateData(SignalData signalData) {
        datas.add(signalData);
        notifyDataSetChanged();
    }

    class SignalCheckContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_signal_check_tv_time)
        TextView itemAdapterSignalCheckTvTime;
        @BindView(R.id.item_adapter_signal_check_tv_up_data)
        TextView itemAdapterSignalCheckTvUpData;
        @BindView(R.id.item_adapter_signal_check_tv_load_data)
        TextView itemAdapterSignalCheckTvLoadData;
        public SignalCheckContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
