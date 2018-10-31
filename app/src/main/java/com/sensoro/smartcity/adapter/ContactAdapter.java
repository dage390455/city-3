package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final Activity mActivity;
    List<DeployRecordInfo.NotificationBean> mList = new ArrayList<>();
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
        DeployRecordInfo.NotificationBean notificationBean = mList.get(position);
//        if (notificationBean.getContent()!=null&&notificationBean.getContent()!=null) {
            holder.itemAdapterContactNamePhone.setText(notificationBean.getContact()+"："+notificationBean.getContent());
//        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateContact(List<DeployRecordInfo.NotificationBean> notifications) {
        mList.clear();
        mList.addAll(notifications);
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_adapter_contact_name_phone)
        TextView itemAdapterContactNamePhone;
        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
