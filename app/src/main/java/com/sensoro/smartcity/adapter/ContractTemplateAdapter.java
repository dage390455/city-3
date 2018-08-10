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
import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class ContractTemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private final List<ContractsTemplateInfo> mList = new ArrayList<>();

    public ContractTemplateAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<ContractsTemplateInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<ContractsTemplateInfo> getData() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contracts_template, parent, false);

        return new ContractTemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String deviceType = mList.get(position).getDeviceType();
        ((ContractTemplateViewHolder) holder).nameTextView.setText(deviceType);
        final EditText etContractItemNum = ((ContractTemplateViewHolder) holder).etContractItemNum;
        if (etContractItemNum.getTag() instanceof TextWatcher) {
            etContractItemNum.removeTextChangedListener((TextWatcher) etContractItemNum.getTag());
        }
        TextWatcher watcher = new TextWatcher() {
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
        ((ContractTemplateViewHolder) holder).ivContractItemDel.setOnClickListener(new View.OnClickListener() {
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
        ((ContractTemplateViewHolder) holder).ivContractItemAdd.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ContractTemplateViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView ivContractItemDel;
        EditText etContractItemNum;
        ImageView ivContractItemAdd;

        public ContractTemplateViewHolder(View itemView) {
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
