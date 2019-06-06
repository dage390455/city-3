package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sensoro.smartcity.R;
import com.sensoro.common.model.DeployContactModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmContactRcContentAdapter extends RecyclerView.Adapter<AlarmContactRcContentAdapter.AlarmContactRcContentHolder> {

    private final Context mContext;
    public final List<DeployContactModel> mList = new ArrayList<>();

    private OnAlarmContactAdapterListener listener;

    public int mFocusPos = -1;//焦点位置


    public AlarmContactRcContentAdapter(Context context) {
        mContext = context;
    }


    public void setOnAlarmContactAdapterListener(OnAlarmContactAdapterListener onAlarmContactAdapterListener) {
        listener = onAlarmContactAdapterListener;
    }

    /**
     * 监听焦点
     */
    public interface OnAlarmContactAdapterListener {
        void onPhoneFocusChange(boolean hasFocus);

        void onNameFocusChange(boolean hasFocus);
    }

    @Override
    public AlarmContactRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_add_alarm_cantact, parent, false);

        return new AlarmContactRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmContactRcContentHolder itemHolder, final int position) {


        if (mList.size() == 1 && position == 0) {
            itemHolder.itemAdapterAlarmLlContactDelete.setVisibility(View.GONE);
        } else {
            itemHolder.itemAdapterAlarmLlContactDelete.setVisibility(View.VISIBLE);


        }
        itemHolder.itemAdapterAlarmLlContactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, mList.size() - position);

                notifyDataSetChanged();
            }
        });

        if (itemHolder.itemAdapterEtAlarmContactPhone.getTag() instanceof TextWatcher) {

            itemHolder.itemAdapterEtAlarmContactPhone.removeTextChangedListener((TextWatcher) itemHolder.itemAdapterEtAlarmContactPhone.getTag());
        }
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())) {
                    mList.get(position).phone = editable.toString();
                } else {
                    mList.get(position).phone = "";
                }


            }
        };

        itemHolder.itemAdapterEtAlarmContactPhone.addTextChangedListener(watcher);
        itemHolder.itemAdapterEtAlarmContactPhone.setTag(watcher);


        if (itemHolder.itemAdapterEtAlarmContactName.getTag() instanceof TextWatcher) {

            itemHolder.itemAdapterEtAlarmContactName.removeTextChangedListener((TextWatcher) itemHolder.itemAdapterEtAlarmContactName.getTag());
        }
        final TextWatcher watcherContactName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(editable.toString())) {
                    mList.get(position).name = editable.toString();
                } else {
                    mList.get(position).name = "";
                }

            }
        };


        itemHolder.itemAdapterEtAlarmContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (null != listener) {
                    listener.onNameFocusChange(hasFocus);
                }
                if (hasFocus) {
                    mFocusPos = position;
                    mList.get(position).clickType = 1;
                }
            }
        });


        itemHolder.itemAdapterEtAlarmContactPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != listener) {

                    listener.onPhoneFocusChange(hasFocus);
                }
                if (hasFocus) {
                    mFocusPos = position;
                    mList.get(position).clickType = 2;

                }
            }
        });


        itemHolder.itemAdapterEtAlarmContactName.addTextChangedListener(watcherContactName);
        itemHolder.itemAdapterEtAlarmContactName.setTag(watcherContactName);

        DeployContactModel deployContactModel = mList.get(position);
        itemHolder.itemAdapterEtAlarmContactName.setText(deployContactModel.name == null ? "" : deployContactModel.name);
        itemHolder.itemAdapterEtAlarmContactPhone.setText(deployContactModel.phone == null ? "" : deployContactModel.phone);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    class AlarmContactRcContentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_adapter_et_alarm_contact_name)
        EditText itemAdapterEtAlarmContactName;
        @BindView(R.id.item_adapter_et_alarm_contact_phone)
        EditText itemAdapterEtAlarmContactPhone;
        @BindView(R.id.item_adapter_alarm_ll_contact_delete)
        LinearLayout itemAdapterAlarmLlContactDelete;

        AlarmContactRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


    public void updateAdapter(List<DeployContactModel> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addNewDataAdapter() {
        DeployContactModel deployContactModel = new DeployContactModel();
        deployContactModel.name = "";
        deployContactModel.phone = "";
        this.mList.add(deployContactModel);
        notifyDataSetChanged();
//        notifyItemInserted(mList.size()-1);
    }
}
