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
import java.util.Locale;

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
        String upData;
        String loadData;
        if (signalData.getDownlinkRSSI() == 0) {
            upData = String.format(Locale.CHINA,"RSSI: %d SNR: %.2f %ddBm SF%d@%.1fMHz",signalData.getUplinkRSSI(),
                    signalData.getUplinkSNR(),signalData.getUplinkTxPower(),12-signalData.getUplinkDR(),
                    (float) (signalData.getUplinkFreq() / 1000000));
            loadData = String.format(Locale.CHINA,"RSSI: %d SNR: %.2f %ddBm SF%d@%.1fMHz",signalData.getDownlinkRSSI(),
                    signalData.getDownlinkSNR(),signalData.getDownlinkTxPower(),12-signalData.getDownlinkDR(),
                    (float) (signalData.getDownlinkFreq() / 1000000));
        }else{
            upData = String.format(Locale.CHINA,"RSSI: - SNR: - %ddBm SF%d@%.1fMHz",
                    signalData.getUplinkTxPower(),12-signalData.getUplinkDR(),
                    (float) (signalData.getUplinkFreq() / 1000000));
            loadData = String.format(Locale.CHINA,"\"RSSI: - SNR: - -dBm SF-@-MHz");
        }

        holder.itemAdapterSignalCheckTvUpData.setText(upData);
        holder.itemAdapterSignalCheckTvLoadData.setText(loadData);

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void updateData(SignalData signalData) {
        datas.add(signalData);
        notifyDataSetChanged();
    }

    public int getLastPosition() {
        if(datas.size()>0){
            return datas.size()-1;
        }else{
            return 0;
        }

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
