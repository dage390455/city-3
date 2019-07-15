package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final Activity mActivity;
    List<DeviceNotificationBean> mList = new ArrayList<>();

    public ContactAdapter(Activity activity) {
        mActivity = activity;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_adapter_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        DeviceNotificationBean notificationBean = mList.get(position);
//        if (notificationBean.getContent()!=null&&notificationBean.getContent()!=null) {
        holder.itemAdapterContactNamePhone.setText(notificationBean.getContact() + "：" + notificationBean.getContent());
//        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateContact(List<DeviceNotificationBean> notifications) {
        mList.clear();
        mList.addAll(notifications);
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_adapter_contact_name_phone)
        TextView itemAdapterContactNamePhone;

        ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
