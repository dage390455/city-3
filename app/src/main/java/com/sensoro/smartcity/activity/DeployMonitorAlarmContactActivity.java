package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmContactHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.presenter.AlarmContactActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.SoftHideKeyBoardUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorAlarmContactActivity extends BaseActivity<IAlarmContactActivityView, AlarmContactActivityPresenter>
        implements IAlarmContactActivityView, RecycleViewItemClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {


    @BindView(R.id.add_alarm_cantact_delete_tv)
    TextView addAlarmCantactDeleteTv;
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.rc_ac_deploy_alarm_contact_history)
    RecyclerView rcAcDeployAlarmContactHistory;
    //    @BindView(R.id.ac_name_address_et_alarm_contact_name)
//    EditText acNameAddressEtAlarmContactName;
//    @BindView(R.id.ac_name_address_et_alarm_contact_phone)
//    EditText acNameAddressEtAlarmContactPhone;
    @BindView(R.id.iv_ac_name_address_delete_tag)
    ImageView ivAcDeployAlarmContactDeleteHistory;
    //    @BindView(R.id.ac_name_address_ll_add_name_phone)
//    LinearLayout acNameAddressLlAddNamePhone;
    @BindView(R.id.ac_alarm_contact_rv)
    RecyclerView acAlarmContactRv;
    private AlarmContactHistoryAdapter mHistoryAdapter;
    private AlarmContactRcContentAdapter alarmContactRcContentAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_contact);
        ButterKnife.bind(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        //TODO 暂不支持多个联系人，所以先不做喽
        initRcContent();
        includeTextTitleTvTitle.setText(R.string.alert_contact);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        initTitle();
        initRcHistory();
        initClearHistoryDialog();
        addAlarmCantactDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmContactRcContentAdapter.addNewDataAdapter();
            }
        });

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_29c093));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }


    private void initRcHistory() {
        mHistoryAdapter = new AlarmContactHistoryAdapter(mActivity);
        mHistoryAdapter.setRecycleViewItemClickListener(this);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcAcDeployAlarmContactHistory.setLayoutManager(manager);
        rcAcDeployAlarmContactHistory.setAdapter(mHistoryAdapter);


        SensoroLinearLayoutManager contactManager = new SensoroLinearLayoutManager(mActivity);
        contactManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acAlarmContactRv.setLayoutManager(contactManager);
        acAlarmContactRv.setAdapter(alarmContactRcContentAdapter);
    }

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.alert_contact);
        includeTextTitleTvCancel.setVisibility(View.VISIBLE);
        includeTextTitleTvCancel.setTextColor(getResources().getColor(R.color.c_b6b6b6));
        includeTextTitleTvCancel.setText(R.string.cancel);
        includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
        includeTextTitleTvSubtitle.setText(getString(R.string.save));
        updateSaveStatus(true);
    }

    @Override
    public void updateSaveStatus(boolean isEnable) {
        includeTextTitleTvSubtitle.setEnabled(isEnable);
        includeTextTitleTvSubtitle.setTextColor(isEnable ? getResources().getColor(R.color.c_29c093) : getResources().getColor(R.color.c_dfdfdf));

    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }


    private void initRcContent() {
        alarmContactRcContentAdapter = new AlarmContactRcContentAdapter(this);
    }

    @Override
    protected AlarmContactActivityPresenter createPresenter() {
        return new AlarmContactActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle, R.id.iv_ac_name_address_delete_tag})
    public void onViewClicked(View view) {

        switch (view.getId()) {


            case R.id.include_text_title_tv_cancel:
                AppUtils.dismissInputMethodManager(mActivity);
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
//                AppUtils.dismissInputMethodManager(mActivity, acNameAddressEtAlarmContactName);
//                String name = acNameAddressEtAlarmContactName.getText().toString();
//                String phone = acNameAddressEtAlarmContactPhone.getText().toString();


                mPresenter.doFinish(alarmContactRcContentAdapter.mList);
                break;
            case R.id.iv_ac_name_address_delete_tag:
                AppUtils.dismissInputMethodManager(mActivity);
                showHistoryClearDialog();
                break;
//            case R.id.ac_name_address_et_alarm_contact_name:
//                acNameAddressEtAlarmContactName.requestFocus();
//                acNameAddressEtAlarmContactName.setCursorVisible(true);
//                break;
//            case R.id.ac_name_address_et_alarm_contact_phone:
//                acNameAddressEtAlarmContactPhone.requestFocus();
//                acNameAddressEtAlarmContactPhone.setCursorVisible(true);
//                break;
        }
    }


    @Override
    public void updateContactData(ArrayList<DeployContactModel> mdContactModelList) {
        alarmContactRcContentAdapter.updateAdapter(mdContactModelList);


    }

    @Override
    public void updateHistoryData(ArrayList<String> mHistoryKeywords) {
        mHistoryAdapter.updateSearchHistoryAdapter(mHistoryKeywords);
        ivAcDeployAlarmContactDeleteHistory.setVisibility(mHistoryKeywords.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        String s = mHistoryAdapter.getSearchHistoryList().get(position);

        if (alarmContactRcContentAdapter.mFoucusPos != -1) {
            DeployContactModel model = alarmContactRcContentAdapter.mList.get(alarmContactRcContentAdapter.mFoucusPos);

            if (model.clickType == 1) {

                model.name = s;
            } else if (model.clickType == 2) {
                model.phone = s;

            }
            alarmContactRcContentAdapter.notifyItemChanged(alarmContactRcContentAdapter.mFoucusPos);
        }


    }

    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }

    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearTag();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroy();
    }


    public class AlarmContactRcContentAdapter extends RecyclerView.Adapter<AlarmContactRcContentAdapter.AlarmContactRcContentHolder> {

        private final Context mContext;
        private final List<DeployContactModel> mList = new ArrayList<>();


        public int mFoucusPos = -1;//焦点位置


        public AlarmContactRcContentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public AlarmContactRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_add_alarm_cantact, parent, false);

            return new AlarmContactRcContentHolder(view);
        }

        @Override
        public void onBindViewHolder(final AlarmContactRcContentHolder itemHolder, final int position) {


            if (mList.size() == 1 && position == 0) {
                itemHolder.itemAdapterAlarmCantactDeletell.setVisibility(View.GONE);
            } else {
                itemHolder.itemAdapterAlarmCantactDeletell.setVisibility(View.VISIBLE);


            }
            itemHolder.itemAdapterAlarmCantactDeletell.setOnClickListener(new View.OnClickListener() {
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
                    if (hasFocus) {
                        mPresenter.updateStatus(0);

                        mFoucusPos = position;
                        mList.get(position).clickType = 1;
                    }
                }
            });


            itemHolder.itemAdapterEtAlarmContactPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mPresenter.updateStatus(1);
                        mFoucusPos = position;
                        mList.get(position).clickType = 2;

                    }
                }
            });


            itemHolder.itemAdapterEtAlarmContactName.addTextChangedListener(watcherContactName);
            itemHolder.itemAdapterEtAlarmContactName.setTag(watcherContactName);

            itemHolder.itemAdapterEtAlarmContactName.setText(mList.get(position).name);
            itemHolder.itemAdapterEtAlarmContactPhone.setText(mList.get(position).phone);
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
            @BindView(R.id.item_adapter_alarm_cantact_delete_tv)
            TextView itemAdapterAlarmCantactDeleteTv;
            @BindView(R.id.item_adapter_alarm_cantact_delete_ll)
            LinearLayout itemAdapterAlarmCantactDeletell;

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

}
