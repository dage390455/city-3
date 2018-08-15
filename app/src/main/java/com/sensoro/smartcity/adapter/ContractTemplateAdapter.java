package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;
import java.util.Collections;


public class ContractTemplateAdapter extends RecyclerView.Adapter<ContractTemplateAdapter.ContractTemplateViewHolder> {

    private Context mContext;
    private final ArrayList<ContractsTemplateInfo> mList = new ArrayList<>();

    public ContractTemplateAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(ArrayList<ContractsTemplateInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
        Collections.sort(mList);
        notifyDataSetChanged();
    }

    public ArrayList<ContractsTemplateInfo> getData() {
        return mList;
    }

    @Override
    public ContractTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contracts_template, parent, false);
        return new ContractTemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractTemplateViewHolder holder, final int position) {
        final String name = mList.get(position).getName();
        holder.nameTextView.setText(name);
        final EditText etContractItemNum = holder.etContractItemNum;
        holder.ivContractItemDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContractItemNum.clearFocus();
                dismissInputMethodManager(etContractItemNum);
                String text = etContractItemNum.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    int i = Integer.parseInt(text);
                    if (i > 0) {
                        i--;
                        etContractItemNum.setText(i + "");
                    }
                } else {
                    etContractItemNum.setText(0 + "");
                }
            }
        });
        holder.ivContractItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContractItemNum.clearFocus();
                dismissInputMethodManager(etContractItemNum);
                String text = etContractItemNum.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    int i = Integer.parseInt(text);
                    if (i >= 0) {
                        i++;
                        etContractItemNum.setText(i + "");
                    }
                } else {
                    etContractItemNum.setText(0 + "");
                }
            }
        });
        if (etContractItemNum.getTag() instanceof TextWatcher) {
            etContractItemNum.removeTextChangedListener((TextWatcher) etContractItemNum.getTag());
        }
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    try {
                        int i = Integer.parseInt(text);
                        if (i >= 0) {
                            mList.get(position).setQuantity(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mList.get(position).setQuantity(0);
                }
            }
        };
        etContractItemNum.addTextChangedListener(watcher);
        etContractItemNum.setTag(watcher);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ContractTemplateViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final ImageView ivContractItemDel;
        final EditText etContractItemNum;
        final ImageView ivContractItemAdd;

        ContractTemplateViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_contacts_template_name);
            ivContractItemDel = (ImageView) itemView.findViewById(R.id.iv_contract_item_del);
            etContractItemNum = (EditText) itemView.findViewById(R.id.et_contract_item_num);
            ivContractItemAdd = (ImageView) itemView.findViewById(R.id.iv_contract_item_add);
            //
        }


    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }
}
