package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.ContractResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IContractInfoActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.LogUtils;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractAddInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.EXTRA_CONTRACT_TYPE;

public class ContractInfoActivityPresenter extends BasePresenter<IContractInfoActivityView> {
    private Activity mContext;
    private int serviceType;
    //
    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String line5;
    private String line6;
    //
    private String contract_service_life;
    private List<ContractsTemplateInfo> data;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        serviceType = mContext.getIntent().getIntExtra(EXTRA_CONTRACT_TYPE, -1);
        contract_service_life = mContext.getIntent().getStringExtra("contract_service_life");
        switch (serviceType) {
            case 1:
                line1 = mContext.getIntent().getStringExtra("line1");
                line2 = mContext.getIntent().getStringExtra("line2");
                line3 = mContext.getIntent().getStringExtra("line3");
                line4 = mContext.getIntent().getStringExtra("line4");
                line5 = mContext.getIntent().getStringExtra("line5");
                line6 = mContext.getIntent().getStringExtra("line6");
                //
                getView().showContentText(serviceType, line1, line2, line3, line4,
                        line5, line6, 0, contract_service_life);
                break;
            case 2:
            case 3:
                line1 = mContext.getIntent().getStringExtra("line1");
                line2 = mContext.getIntent().getStringExtra("line2");
                line3 = mContext.getIntent().getStringExtra("line3");
                line4 = mContext.getIntent().getStringExtra("line4");
                //
                getView().showContentText(serviceType, line1, line2, line3, line4,
                        null, null, 0, contract_service_life);
                break;
            default:
                break;
        }
        data = (ArrayList<ContractsTemplateInfo>) mContext.getIntent().getSerializableExtra
                ("contract_template");
        getView().updateContractTemplateAdapterInfo(data);
    }

    public void startToConfirm() {
        switch (serviceType) {
            case 1:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getNewContract(null, null, null, line3, line4,
                        line1, line2, null, line5, "13111111111", "企业", data, 2, true, 2).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>() {

                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onNext(ContractAddRsp contractAddRsp) {
                        ContractAddInfo data = contractAddRsp.getData();
                        int id = data.getId();
                        LogUtils.loge(this, "id = " + id);
                        handleCode(id + "");
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }

    }

    private void handleCode(String code) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractResultActivity.class);
        intent.putExtra("code", code);
        getView().startAC(intent);

    }
}
