package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.server.bean.ContractsTemplateInfo;
import com.sensoro.common.server.response.ContractInfoRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractResultActivity;
import com.sensoro.smartcity.activity.ContractServiceActivity;
import com.sensoro.smartcity.imainviews.IContractInfoActivityView;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContractInfoActivityPresenter extends BasePresenter<IContractInfoActivityView> implements IOnCreate{

    private Activity mContext;
    private int serviceType;
    //
    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String line5;
    private String line6;
    private String phone;
    private int id = -1;
    //
    private String placeType;
    //
    private String contract_service_life;
    private String contract_service_life_first;
    private String contract_service_life_period;
    private ArrayList<ContractsTemplateInfo> deviceList;
    private ContractListInfo mContractInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        int contractId = mContext.getIntent().getIntExtra(Constants.EXTRA_CONTRACT_ID, -1);
        if (contractId == -1) {
            serviceType = mContext.getIntent().getIntExtra(Constants.EXTRA_CONTRACT_TYPE, -1);
            contract_service_life = mContext.getIntent().getStringExtra("contract_service_life");
            contract_service_life_first = mContext.getIntent().getStringExtra("contract_service_life_first");
            contract_service_life_period = mContext.getIntent().getStringExtra("contract_service_life_period");
            placeType = mContext.getIntent().getStringExtra("place");
            String signDate = mContext.getIntent().getStringExtra("signDate");
            id = mContext.getIntent().getIntExtra("id", -1);
            if (id == -1) {
                getView().setConfirmText("确认并生成二维码");
            } else {
                getView().setConfirmText("查看二维码");
            }
            if (!TextUtils.isEmpty(signDate)) {
                getView().setSignTime(signDate);
            }
            switch (serviceType) {
                case 1:
                    line1 = mContext.getIntent().getStringExtra("line1");
                    line2 = mContext.getIntent().getStringExtra("line2");
                    line3 = mContext.getIntent().getStringExtra("line3");
                    line4 = mContext.getIntent().getStringExtra("line4");
                    line5 = mContext.getIntent().getStringExtra("line5");
                    line6 = mContext.getIntent().getStringExtra("line6");
                    phone = mContext.getIntent().getStringExtra("phone");
                    //
                    getView().showContentText(serviceType, line1, phone, line2, line3, line4,
                            line5, line6, placeType, contract_service_life, contract_service_life_first, contract_service_life_period);
                    break;
                case 2:
                    line1 = mContext.getIntent().getStringExtra("line1");
                    line2 = mContext.getIntent().getStringExtra("line2");
                    line3 = mContext.getIntent().getStringExtra("line3");
                    line4 = mContext.getIntent().getStringExtra("line4");
                    phone = mContext.getIntent().getStringExtra("phone");
                    //
                    getView().showContentText(serviceType, line1, phone, line2, line3, line4,
                            null, null, placeType, contract_service_life, contract_service_life_first, contract_service_life_period);
                    break;
                case 3:
                    line1 = mContext.getIntent().getStringExtra("line1");
                    line2 = mContext.getIntent().getStringExtra("line2");
                    line3 = mContext.getIntent().getStringExtra("line3");
                    //身份证号
                    line4 = mContext.getIntent().getStringExtra("line4");
                    //住址
                    line5 = mContext.getIntent().getStringExtra("line5");
                    //
                    getView().showContentText(serviceType, line1, "", line2, line3, line4,
                            line5, null, placeType, contract_service_life, contract_service_life_first, contract_service_life_period);
                    break;
                default:
                    break;
            }
            deviceList = (ArrayList<ContractsTemplateInfo>) mContext.getIntent().getSerializableExtra
                    ("contract_template");
            getView().updateContractTemplateAdapterInfo(deviceList);
        } else {
            requestData(contractId);
        }

    }

    private void requestData(int contractId) {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getContractInfo(contractId + "").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ContractInfoRsp>(this) {
                    @Override
                    public void onCompleted(ContractInfoRsp responseBase) {
                        mContractInfo = responseBase.getData();
                        if (mContractInfo != null) {
                            getView().setConfirmVisible(mContractInfo.isConfirmed());
                            getView().setConfirmStatus(mContractInfo.isConfirmed());
                            id = mContractInfo.getId();
                            refreshContentText();

                            String createdAt = mContractInfo.getCreatedAt();
                            if (TextUtils.isEmpty(createdAt)) {
                                createdAt = "-";
                            } else {
                                try {
                                    String[] ts = createdAt.split("T");
                                    createdAt = ts[0].replaceAll("-",".");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    createdAt = "-";
                                }
                            }
                            getView().setContractCreateTime(createdAt);

                            String confirmTime = mContractInfo.getConfirmTime();
                            if (TextUtils.isEmpty(confirmTime)) {
                                confirmTime = "-";
                            } else {
                                try {
                                    String[] ts = confirmTime.split("T");
                                    confirmTime = ts[0].replaceAll("-",".");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    confirmTime = "-";
                                }
                            }
                            getView().setSignTime(confirmTime);

                            getView().updateContractTemplateAdapterInfo(mContractInfo.getDevices());
                        } else {
                            getView().toastShort("未获取到合同信息");
                        }
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
    }

    private void refreshContentText() {
        serviceType = mContractInfo.getCreated_type();
        placeType = mContractInfo.getPlace_type();
        if (TextUtils.isEmpty(placeType)) {
            placeType = "无";
        }
        contract_service_life = mContractInfo.getServiceTime() + "";
        contract_service_life_first = mContractInfo.getFirstPayTimes() + "";
        contract_service_life_period = mContractInfo.getPayTimes() + "";
        switch (serviceType) {
            case 1:
                line1 = mContractInfo.getCustomer_name();
                if (TextUtils.isEmpty(line1)) {
                    line1 = "无";
                }
                line2 = mContractInfo.getCustomer_enterprise_name();
                if (TextUtils.isEmpty(line2)) {
                    line2 = "无";
                }
                line3 = mContractInfo.getEnterprise_card_id();
                if (TextUtils.isEmpty(line3)) {
                    line3 = "无";
                }
                line4 = mContractInfo.getEnterprise_register_id();
                if (TextUtils.isEmpty(line4)) {
                    line4 = "无";
                }
                line5 = mContractInfo.getCustomer_address();
                if (TextUtils.isEmpty(line5)) {
                    line5 = "无";
                }
                line6 = mContractInfo.getCustomer_enterprise_validity();
                if (TextUtils.isEmpty(line6)) {
                    line6 = "无";
                }
                phone = mContractInfo.getCustomer_phone();
                if (TextUtils.isEmpty(phone)) {
                    phone = "无";
                }
                break;
            case 2:
                line1 = mContractInfo.getCustomer_name();
                if (TextUtils.isEmpty(line1)) {
                    line1 = "无";
                }
                int sex = mContractInfo.getSex();
                switch (sex) {
                    case 1:
                        line2 = "男";
                        break;
                    case 2:
                        line2 = "女";
                        break;
                    default:
                        line2 = "无";
                        break;
                }
                line3 = mContractInfo.getCard_id();
                if (TextUtils.isEmpty(line3)) {
                    line3 = "无";
                }
                line4 = mContractInfo.getCustomer_address();
                if (TextUtils.isEmpty(line4)) {
                    line4 = "无";
                }
                line5 = null;
                line6 = null;
                phone = mContractInfo.getCustomer_phone();
                if (TextUtils.isEmpty(phone)) {
                    phone = "无";
                }
                break;
            case 3:
                line1 = mContractInfo.getCustomer_enterprise_name();
                if (TextUtils.isEmpty(line1)) {
                    line1 = "无";
                }
                line2 = mContractInfo.getCustomer_name();
                if (TextUtils.isEmpty(line2)) {
                    line1 = "无";
                }
                line3 = mContractInfo.getCustomer_phone();
                if (TextUtils.isEmpty(line3)) {
                    line3 = "无";
                }
                line4 = mContractInfo.getCard_id();
                if (TextUtils.isEmpty(line4)) {
                    line4 = "无";
                }
                line5 = mContractInfo.getCustomer_address();
                if (TextUtils.isEmpty(line5)) {
                    line5 = "无";
                }
                line6 = null;
                phone = null;
                break;
        }
        getView().showContentText(serviceType, line1, phone, line2, line3, line4,
                line5, line6, placeType, contract_service_life, contract_service_life_first, contract_service_life_period);
    }

    public void startToConfirm(final String text) {
        if (id == -1) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("生成合同")
                    .setMessage("点击确认后将生成一份新的合同，不可修改")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addContract(text);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setCancelable(false)
                    .show();

        } else {
            handleCode(id + "", text);
        }

    }

    private void addContract(final String text) {
        int serviceTime = 1;
        int serviceTimeFirst = 1;
        int serviceTimePeriod = 1;
        try {
            serviceTime = Integer.parseInt(contract_service_life);
            serviceTimeFirst = Integer.parseInt(contract_service_life_first);
            serviceTimePeriod = Integer.parseInt(contract_service_life_period);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (serviceType) {
            case 1:
                getView().showProgressDialog();
                if (!RegexUtils.checkContractNotEmpty(line3)) {
                    line3 = null;
                }
                if (!RegexUtils.checkContractNotEmpty(line4)) {
                    line4 = null;
                }
//                RetrofitServiceHelper.INSTANCE.getNewContract(1, serviceType, null, null, line3, line4,
//                        line1, line2, line6, line5, phone, placeType, deviceList, serviceTimePeriod, null, serviceTime, serviceTimeFirst).subscribeOn
//                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {
//
//                    @Override
//                    public void onCompleted(ContractAddRsp contractAddRsp) {
//                        ContractAddInfo data = contractAddRsp.getData();
//                        id = data.getId();
//                        LogUtils.loge(this, "id = " + id);
//                        handleCode(id + "", text);
//                        getView().dismissProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
//                        getView().toastShort(errorMsg);
//                    }
//                });
                break;
            case 2:
                Integer sex = null;
                if ("男".equals(line2)) {
                    sex = 1;
                } else if ("女".equals(line2)) {
                    sex = 2;
                }
                getView().showProgressDialog();
//                RetrofitServiceHelper.INSTANCE.getNewContract(2, serviceType, line3, sex, null, null,
//                        line1, null, null, line4, phone, placeType, deviceList, serviceTimePeriod, null, serviceTime, serviceTimeFirst).subscribeOn
//                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {
//
//                    @Override
//                    public void onCompleted(ContractAddRsp contractAddRsp) {
//                        ContractAddInfo data = contractAddRsp.getData();
//                        int id = data.getId();
//                        LogUtils.loge(this, "id = " + id);
//                        handleCode(id + "", text);
//                        getView().dismissProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
//                        getView().toastShort(errorMsg);
//                    }
//                });
                break;
            case 3:
                getView().showProgressDialog();
//                RetrofitServiceHelper.INSTANCE.getNewContract(2, serviceType, line4, null, null, null,
//                        line2, line1, null, line5, line3, placeType, deviceList, serviceTimePeriod, null, serviceTime, serviceTimeFirst).subscribeOn
//                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {
//
//
//                    @Override
//                    public void onCompleted(ContractAddRsp contractAddRsp) {
//                        ContractAddInfo data = contractAddRsp.getData();
//                        int id = data.getId();
//                        LogUtils.loge(this, "id = " + id);
//                        handleCode(id + "", text);
//                        getView().dismissProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
//                        getView().toastShort(errorMsg);
//                    }
//                });
                break;
            default:
                break;
        }
    }

    private void handleCode(String code, String text) {
        Intent intent = new Intent();
        final String url = Constants.CONTRACT_WE_CHAT_BASE_URL + code;
        intent.setClass(mContext, ContractResultActivity.class);
//        switch (serviceType) {
//            case 1:
//            case 2:
//                code = AESUtil.contractEncode(phone, code);
//                break;
//            case 3:
//                code = AESUtil.contractEncode(line3, code);
//                break;
//        }
        intent.putExtra("code", url);
        if (text.startsWith("查看")) {
            intent.putExtra(Constants.EXTRA_CONTRACT_RESULT_TYPE, false);
        } else {
            intent.putExtra(Constants.EXTRA_CONTRACT_RESULT_TYPE, true);
        }
        getView().startAC(intent);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (deviceList != null) {
            deviceList.clear();
            deviceList = null;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_FINISH_CODE:
                if (data instanceof Boolean) {
                    boolean needFinish = (boolean) data;
                    if (needFinish) {
                        getView().finishAc();
                    }
                }
                break;
            case Constants.EVENT_DATA__CONTRACT_EDIT_REFRESH_CODE:
                if (data instanceof Integer) {
                    requestData((Integer) data);
                }

                break;
        }
    }

    public void startToEdit() {
        String no = mContext.getString(R.string.no);
        Intent intent = new Intent();
        intent.setClass(mContext, ContractServiceActivity.class);
        int createdType = mContractInfo.getCreated_type();
        intent.putExtra(Constants.EXTRA_CONTRACT_ORIGIN_TYPE, Constants.CONTRACT_ORIGIN_TYPE_EDIT);
        intent.putExtra(Constants.EXTRA_CONTRACT_TYPE, createdType);
        intent.putExtra(Constants.EXTRA_CONTRACT_ID, mContractInfo.getId());
        String placeType = mContractInfo.getPlace_type();
        if (TextUtils.isEmpty(placeType)) {
            placeType = no;
        }
        intent.putExtra("place_type", placeType);
        intent.putExtra("service_life", mContractInfo.getServiceTime());
        intent.putExtra("service_life_first", mContractInfo.getFirstPayTimes());
        intent.putExtra("service_life_period", mContractInfo.getPayTimes());
        List<ContractsTemplateInfo> devices = mContractInfo.getDevices();
        ContractsTemplateInfo[] objects = devices.toArray(new ContractsTemplateInfo[0]);
        intent.putExtra("contract_devices", objects);
        String uid = mContractInfo.getUid();
        if (!TextUtils.isEmpty(uid)) {
            intent.putExtra("contract_uid", uid);
        }
        switch (createdType) {
            case 1:
                addExtraCreateType1(intent, no);
                break;
            case 2:
                addExtraCreateType2(intent, no);
                break;
            case 3:
                addExtraCreateType3(intent, no);
                break;
        }
        getView().startAC(intent);


    }

    private void addExtraCreateType3(Intent intent, String no) {
        String customer_enterprise_name = mContractInfo.getCustomer_enterprise_name();
        if (TextUtils.isEmpty(customer_enterprise_name)) {
            customer_enterprise_name = no;
        }
        intent.putExtra("company_name", customer_enterprise_name);

        String customer_name = mContractInfo.getCustomer_name();
        if (TextUtils.isEmpty(customer_name)) {
            customer_name = no;
        }
        intent.putExtra("person_name", customer_name);

        String customer_phone = mContractInfo.getCustomer_phone();
        if (TextUtils.isEmpty(customer_phone)) {
            customer_phone = no;
        }
        intent.putExtra("customer_phone", customer_phone);

        String card_id = mContractInfo.getCard_id();
        if (TextUtils.isEmpty(card_id)) {
            card_id = no;
        }
        intent.putExtra("id_number", card_id);

        String customer_address = mContractInfo.getCustomer_address();
        if (TextUtils.isEmpty(customer_address)) {
            customer_address = no;
        }
        intent.putExtra("address", customer_address);

    }

    private void addExtraCreateType2(Intent intent, String no) {
        String customer_name = mContractInfo.getCustomer_name();
        if (TextUtils.isEmpty(customer_name)) {
            customer_name = no;
        }
        intent.putExtra("person_name", customer_name);

        int sex = mContractInfo.getSex();
        switch (sex) {
            case 1:
                intent.putExtra("sex", mContext.getString(R.string.male));
                break;
            case 2:
                intent.putExtra("sex", mContext.getString(R.string.female));
                break;
            default:
                intent.putExtra("sex", no);
                break;
        }

        String card_id = mContractInfo.getCard_id();
        if (TextUtils.isEmpty(card_id)) {
            card_id = no;
        }
        intent.putExtra("id_number", card_id);

        String customer_address = mContractInfo.getCustomer_address();
        if (TextUtils.isEmpty(customer_address)) {
            customer_address = no;
        }
        intent.putExtra("address", customer_address);

        String customer_phone = mContractInfo.getCustomer_phone();
        if (TextUtils.isEmpty(customer_phone)) {
            customer_phone = no;
        }
        intent.putExtra("customer_phone", customer_phone);

    }

    private void addExtraCreateType1(Intent intent, String no) {
        String customer_name = mContractInfo.getCustomer_name();
        if (TextUtils.isEmpty(customer_name)) {
            customer_name = no;
        }
        intent.putExtra("legal_person", customer_name);

        String customer_enterprise_name = mContractInfo.getCustomer_enterprise_name();
        if (TextUtils.isEmpty(customer_enterprise_name)) {
            customer_enterprise_name = no;
        }
        intent.putExtra("company_name", customer_enterprise_name);

        String enterprise_card_id = mContractInfo.getEnterprise_card_id();
        if (TextUtils.isEmpty(enterprise_card_id)) {
            enterprise_card_id = no;
        }
        intent.putExtra("credit_code", enterprise_card_id);

        String enterprise_register_id = mContractInfo.getEnterprise_register_id();
        if (TextUtils.isEmpty(enterprise_card_id)) {
            enterprise_register_id = no;
        }
        intent.putExtra("registration_number", enterprise_register_id);

        String customer_address = mContractInfo.getCustomer_address();
        if (TextUtils.isEmpty(customer_address)) {
            customer_address = no;
        }
        intent.putExtra("address", customer_address);

        String customer_enterprise_validity = mContractInfo.getCustomer_enterprise_validity();
        if (TextUtils.isEmpty(customer_enterprise_validity)) {
            customer_enterprise_validity = no;
        }
        intent.putExtra("validity_period", customer_enterprise_validity);

        String customer_phone = mContractInfo.getCustomer_phone();
        if (TextUtils.isEmpty(customer_phone)) {
            customer_phone = no;
        }
        intent.putExtra("customer_phone", customer_phone);
    }
}
