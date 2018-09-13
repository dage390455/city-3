package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.ContractResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractInfoActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractAddInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.util.AESUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContractInfoActivityPresenter extends BasePresenter<IContractInfoActivityView> implements IOnCreate,
        Constants {
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
    private ArrayList<ContractsTemplateInfo> deviceList;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        serviceType = mContext.getIntent().getIntExtra(EXTRA_CONTRACT_TYPE, -1);
        contract_service_life = mContext.getIntent().getStringExtra("contract_service_life");
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
                        line5, line6, placeType, contract_service_life);
                break;
            case 2:
                line1 = mContext.getIntent().getStringExtra("line1");
                line2 = mContext.getIntent().getStringExtra("line2");
                line3 = mContext.getIntent().getStringExtra("line3");
                line4 = mContext.getIntent().getStringExtra("line4");
                phone = mContext.getIntent().getStringExtra("phone");
                //
                getView().showContentText(serviceType, line1, phone, line2, line3, line4,
                        null, null, placeType, contract_service_life);
                break;
            case 3:
                line1 = mContext.getIntent().getStringExtra("line1");
                line2 = mContext.getIntent().getStringExtra("line2");
                line3 = mContext.getIntent().getStringExtra("line3");
                line4 = mContext.getIntent().getStringExtra("line4");
                //
                getView().showContentText(serviceType, line1, "", line2, line3, line4,
                        null, null, placeType, contract_service_life);
                break;
            default:
                break;
        }
        deviceList = (ArrayList<ContractsTemplateInfo>) mContext.getIntent().getSerializableExtra
                ("contract_template");
        getView().updateContractTemplateAdapterInfo(deviceList);
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
        int serviceTime = 2;
        try {
            serviceTime = Integer.parseInt(contract_service_life);
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
                RetrofitServiceHelper.INSTANCE.getNewContract(1, serviceType, null, null, line3, line4,
                        line1, line2, line6, line5, phone, placeType, deviceList, 2, null, serviceTime).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {

                    @Override
                    public void onCompleted(ContractAddRsp contractAddRsp) {
                        ContractAddInfo data = contractAddRsp.getData();
                        id = data.getId();
                        LogUtils.loge(this, "id = " + id);
                        handleCode(id + "", text);
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case 2:
                Integer sex = null;
                if ("男".equals(line2)) {
                    sex = 1;
                } else if ("女".equals(line2)) {
                    sex = 2;
                }
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getNewContract(2, serviceType, line3, sex, null, null,
                        line1, null, null, line4, phone, placeType, deviceList, 2, null, serviceTime).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {

                    @Override
                    public void onCompleted(ContractAddRsp contractAddRsp) {
                        ContractAddInfo data = contractAddRsp.getData();
                        int id = data.getId();
                        LogUtils.loge(this, "id = " + id);
                        handleCode(id + "", text);
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case 3:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getNewContract(2, serviceType, null, null, null, null,
                        line2, line1, null, line4, line3, placeType, deviceList, 2, null, serviceTime).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {


                    @Override
                    public void onCompleted(ContractAddRsp contractAddRsp) {
                        ContractAddInfo data = contractAddRsp.getData();
                        int id = data.getId();
                        LogUtils.loge(this, "id = " + id);
                        handleCode(id + "", text);
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void handleCode(String code, String text) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractResultActivity.class);
        switch (serviceType) {
            case 1:
            case 2:
                code = AESUtil.contractEncode(phone, code);
                break;
            case 3:
                code = AESUtil.contractEncode(line3, code);
                break;
        }
        intent.putExtra("code", code);
        if (text.startsWith("查看")) {
            intent.putExtra(EXTRA_CONTRACT_RESULT_TYPE, false);
        } else {
            intent.putExtra(EXTRA_CONTRACT_RESULT_TYPE, true);
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
        if (code == EVENT_DATA_FINISH_CODE) {
            Object data = eventData.data;
            if (data instanceof Boolean) {
                boolean needFinish = (boolean) data;
                if (needFinish) {
                    getView().finishAc();
                }
            }
        }
    }
}
