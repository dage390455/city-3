package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IBusinessContractView;
import com.sensoro.smartcity.model.BusinessLicenseData;
import com.sensoro.smartcity.push.RecognizeService;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BusinessContractPresenter extends BasePresenter<IBusinessContractView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        getContractTemplateInfos();
    }

    private void getContractTemplateInfos() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getContractstemplate().subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsTemplateRsp>(this) {

            @Override
            public void onCompleted(ContractsTemplateRsp contractsTemplateRsp) {
                ArrayList<ContractsTemplateInfo> data = contractsTemplateRsp.getData();
//                Parcelable[] contract_devices = mActivity.getIntent().getParcelableArrayExtra("contract_devices");
//                if (contract_devices != null && contract_devices.length > 0) {
//                    for (Parcelable contract : contract_devices) {
//                        try {
//                            ContractsTemplateInfo contract1 = (ContractsTemplateInfo) contract;
//                            for (ContractsTemplateInfo datum : data) {
//                                if (datum.getDeviceType().endsWith(contract1.getDeviceType())) {
//                                    datum.setQuantity(contract1.getQuantity());
//                                    break;
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                getView().updateContractTemplateAdapterInfo(contractsTemplateRsp.getData());

                getView().dismissProgressDialog();
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }
    @Override
    public void onDestroy() {

    }

    public void doTakePhoto() {
        if (!SensoroCityApplication.getInstance().hasGotToken) {
            return;
        }
        Intent intent = new Intent(mActivity, CameraActivity.class);
        String absolutePath = FileUtil.getSaveFile(mActivity.getApplicationContext()).getAbsolutePath();
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                absolutePath);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        if (isAttachedView()){
            getView().startACForResult(intent, Constants.REQUEST_CODE_BUSINESS_LICENSE);
        }
    }

    public void handActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
            if (isAttachedView()){
                getView().showProgressDialog();
            }
            try {
                RecognizeService.recBusinessLicense(mActivity, FileUtil.getSaveFile(mActivity.getApplicationContext())
                                .getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(final String result) {
                                if (isAttachedView()){
                                    getView().dismissProgressDialog();
                                }
                                String enterpriseName = "";
                                String customerAddress = "";
//                                String 成立日期 = "";
//                                String 有效期 = "";
                                String customerName = "";
                                String enterpriseCardId = "";
//                                String 证件编号 = "";
                                try {
                                    BusinessLicenseData businessLicenseData = RetrofitServiceHelper.INSTANCE
                                            .getGson()
                                            .fromJson(result, BusinessLicenseData.class);
                                    BusinessLicenseData.WordsResultBean words_result = businessLicenseData
                                            .getWords_result();
                                    //
                                    if (words_result != null) {
                                        BusinessLicenseData.WordsResultBean.单位名称Bean words_result单位名称 = words_result
                                                .get单位名称();
                                        if (words_result单位名称 != null) {
                                            enterpriseName = words_result单位名称.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.地址Bean words_result地址 = words_result
                                                .get地址();
                                        if (words_result地址 != null) {
                                            customerAddress = words_result地址.getWords();
                                        }
//                                        BusinessLicenseData.WordsResultBean.成立日期Bean words_result成立日期 = words_result
//                                                .get成立日期();
//                                        if (words_result成立日期 != null) {
//                                            成立日期 = words_result成立日期.getWords();
//                                        }
//                                        BusinessLicenseData.WordsResultBean.有效期Bean words_result有效期 = words_result
//                                                .get有效期();
//                                        if (words_result有效期 != null) {
//                                            有效期 = words_result有效期.getWords();
//                                        }
                                        BusinessLicenseData.WordsResultBean.法人Bean words_result法人 = words_result
                                                .get法人();
                                        if (words_result法人 != null) {
                                            customerName = words_result法人.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.社会信用代码Bean words_result社会信用代码 =
                                                words_result
                                                        .get社会信用代码();
                                        if (words_result社会信用代码 != null) {
                                            enterpriseCardId = words_result社会信用代码.getWords();
                                        }
//                                        BusinessLicenseData.WordsResultBean.证件编号Bean words_result证件编号 = words_result
//                                                .get证件编号();
//                                        if (words_result证件编号 != null) {
//                                            证件编号 = words_result证件编号.getWords();
//                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                getView().setBusinessMerchantName(enterpriseName);
                                getView().setOwnerName(customerName);
                                getView().setRegisterAddress(customerAddress);
                                getView().setSocialCreatedId(enterpriseCardId);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                if (isAttachedView()){
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.read_error_try));
                }
            }

        }
    }
}
