package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.bean.MalfunctionTypeStyles;
import com.sensoro.smartcity.R;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MalfunctionDetailRcContentAdapter extends RecyclerView.Adapter<MalfunctionDetailRcContentAdapter.MalfunctionDetailRcContentViewHolder> {
    private final Context mContext;
    private String mMalfunctionText;
    private List<MalfunctionListInfo.RecordsBean> mRecordInfoList = new ArrayList<>();
    private List<MalfunctionListInfo.RecordsBean.Event> receiveStautus0 = new ArrayList<>();
    private List<MalfunctionListInfo.RecordsBean.Event> receiveStautus1 = new ArrayList<>();
    private List<MalfunctionListInfo.RecordsBean.Event> receiveStautus2 = new ArrayList<>();
    private List<MalfunctionListInfo.RecordsBean.Event> receiveStautus3 = new ArrayList<>();


    public MalfunctionDetailRcContentAdapter(Context context) {
        mContext = context;

    }

    @NonNull
    @Override
    public MalfunctionDetailRcContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_malfunction_detail_content, parent, false);

        return new MalfunctionDetailRcContentViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MalfunctionDetailRcContentViewHolder holder, int position) {
        MalfunctionListInfo.RecordsBean recordsBean = mRecordInfoList.get(position);
        String type = recordsBean.getType();
        switch (type) {
            case "malfunction":
                holder.itemMalfunctionDetailContentTvContent.setText(mContext.getString(R.string.system_upload_malfunction_task));
                holder.itemMalfunctionDetailContentTvContent.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.itemMalfunctionDetailContentImvIcon.setImageResource(R.drawable.smoke_icon);
                holder.itemMalfunctionDetailContentTvTime.setText(DateUtil.getStrTimeToday(mContext, recordsBean.getUpdatedTime(), 0));
                holder.llConfirm.setVisibility(View.VISIBLE);
                String subFunctionType = recordsBean.getMalfunctionSubType();
                if (!TextUtils.isEmpty(subFunctionType)) {
                    MalfunctionTypeStyles malfunctionTypeStyles = PreferencesHelper.getInstance().getConfigMalfunctionSubTypes(subFunctionType);
                    if (malfunctionTypeStyles != null) {
                        String name = malfunctionTypeStyles.getName();
                        if (!TextUtils.isEmpty(name)) {
                            try {
                                LogUtils.loge("subFunctionType = " + subFunctionType);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            holder.itemMalfunctionDetailChildMalfunctionCause.setText(name);
                            break;
                        }
                    }
                }
                holder.itemMalfunctionDetailChildMalfunctionCause.setText(mContext.getString(R.string.unknown));
                break;
            case "recovery":
                String content = mMalfunctionText + mContext.getString(R.string.recover_normal);
                holder.itemMalfunctionDetailContentTvContent.setText(content);
                holder.itemMalfunctionDetailContentTvContent.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
                holder.itemMalfunctionDetailContentImvIcon.setImageResource(R.drawable.no_smoke_icon);
                holder.itemMalfunctionDetailContentTvTime.setText(DateUtil.getStrTimeToday(mContext, recordsBean.getUpdatedTime(), 0));
                holder.llConfirm.setVisibility(View.GONE);
                break;
            case "sendSMS":
                StringBuilder smsContent = new StringBuilder();
                smsContent.append(mContext.getString(R.string.system_sms_send));
                holder.itemMalfunctionDetailContentTvContent.setText(appendResult(smsContent, recordsBean.getPhoneList()));
                holder.itemMalfunctionDetailContentImvIcon.setImageResource(R.drawable.msg_icon);
                holder.itemMalfunctionDetailContentTvTime.setText(DateUtil.getStrTimeToday(mContext, recordsBean.getUpdatedTime(), 0));
                holder.llConfirm.setVisibility(View.GONE);
                break;
        }

    }

    private SpannableString appendResult(StringBuilder stringBuffer, List<MalfunctionListInfo.RecordsBean.Event> phoneList) {
        //情况集合
        receiveStatusListClear();
        for (int i = 0; i < phoneList.size(); i++) {
            MalfunctionListInfo.RecordsBean.Event event = phoneList.get(i);
            switch (event.getReciveStatus()) {
                case 0:
                    receiveStautus0.add(event);
                    break;
                case 1:
                    receiveStautus1.add(event);
                    break;
                case 2:
                    receiveStautus2.add(event);
                    break;
                default:
                    receiveStautus3.add(event);
                    break;
            }

        }
        List[] receiveStautus = {receiveStautus0, receiveStautus1, receiveStautus2, receiveStautus3};
        StringBuilder temp = null;
        ArrayList<StringBuilder> tempList = new ArrayList<>();
        for (List stautus : receiveStautus) {
            if (stautus.size() > 0) {
                temp = new StringBuilder();
                for (int i = 0; i < stautus.size(); i++) {
                    String number = ((MalfunctionListInfo.RecordsBean.Event) stautus.get(i)).getNumber();
                    if (i != (stautus.size() - 1)) {
                        temp.append(" " + number + " ;");
                    } else {
                        temp.append(" " + number + " ");
                    }
                }

                switch (((MalfunctionListInfo.RecordsBean.Event) stautus.get(0)).getReciveStatus()) {
                    case 0:
                        stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_sending));
                        break;
                    case 1:
                        stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_successfully));
                        break;
                    case 2:
                        stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_failed));
                        break;
                    default:
                        stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_unknow));
                        break;
                }
                tempList.add(temp);
            }
        }
        SpannableString spannableString = new SpannableString(stringBuffer);

        for (StringBuilder sb : tempList) {
            try {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.c_252525));
                int i = stringBuffer.indexOf(sb.toString());
                if (i == -1) {
                    i = 0;
                }
                spannableString.setSpan(foregroundColorSpan, i, i + sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return spannableString;
    }

    private void receiveStatusListClear() {
        receiveStautus0.clear();
        receiveStautus1.clear();
        receiveStautus2.clear();
        receiveStautus3.clear();
    }


    public void setData(List<MalfunctionListInfo.RecordsBean> recordInfoList, String malfunctionText) {
        this.mRecordInfoList.clear();
        this.mRecordInfoList.addAll(recordInfoList);
        mMalfunctionText = malfunctionText;
        if (TextUtils.isEmpty(mMalfunctionText)) {
            mMalfunctionText = mContext.getString(R.string.unknown_malfunction);
        }
    }

    @Override
    public int getItemCount() {
        return mRecordInfoList.size();
    }

    class MalfunctionDetailRcContentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_malfunction_detail_content_imv_icon)
        ImageView itemMalfunctionDetailContentImvIcon;
        @BindView(R.id.item_malfunction_detail_content_tv_content)
        TextView itemMalfunctionDetailContentTvContent;
        @BindView(R.id.item_malfunction_detail_content_tv_time)
        TextView itemMalfunctionDetailContentTvTime;
        @BindView(R.id.item_malfunction_detail_child_malfunction_cause)
        TextView itemMalfunctionDetailChildMalfunctionCause;
        @BindView(R.id.ll_confirm)
        LinearLayout llConfirm;

        MalfunctionDetailRcContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
