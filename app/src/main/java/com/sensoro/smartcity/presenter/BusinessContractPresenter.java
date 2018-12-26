package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractCreationSuccessActivity;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IBusinessContractView;
import com.sensoro.smartcity.model.BusinessLicenseData;
import com.sensoro.smartcity.model.ContractInfoModel;
import com.sensoro.smartcity.push.RecognizeService;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractAddInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BusinessContractPresenter extends BasePresenter<IBusinessContractView> {
    private Activity mActivity;
    private ContractInfoModel mContractInfoModel;

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

    public void doCreateContract() {
        if(mContractInfoModel == null){
            getView().toastShort(mActivity.getString(R.string.info_check_faild));
            return;
        }
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getNewContract(mContractInfoModel.contractType, 1, mContractInfoModel.idCardNumber, null,
                mContractInfoModel.enterpriseCardId,null,mContractInfoModel.customerName, mContractInfoModel.customerEnterpriseName,
                null, mContractInfoModel.customerAddress, mContractInfoModel.customerPhone, mContractInfoModel.placeType,
                mContractInfoModel.devicesList, mContractInfoModel.periodAge, null, mContractInfoModel.serverAge, mContractInfoModel.firstAge).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {

            @Override
            public void onCompleted(ContractAddRsp contractAddRsp) {
                ContractAddInfo data = contractAddRsp.getData();
//                id = data.getId();
//                LogUtils.loge(this, "id = " + id);
//                handleCode(id + "", text);
                Intent intent = new Intent(mActivity,ContractCreationSuccessActivity.class);
//                intent.putExtra()
                getView().startACForResult(intent,Constants.EVENT_DATA_CONTRACT_CREATION_SUCCESS);
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }

    public void doSubmit(String enterpriseName, String customerName, String customerPhone, String enterpriseCardId, String customerAddress, String placeType, String contractAgeStr, String contractAgeFirstStr, String contractAgePeriodStr, ArrayList<ContractsTemplateInfo> data) {
//        intent.putExtra(EXTRA_CONTRACT_TYPE, 1);
        mContractInfoModel = new ContractInfoModel();
        mContractInfoModel.contractType = 1;
        //
        if (TextUtils.isEmpty(enterpriseName)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_enterprise_name));
            return;
        } else {
            if (enterpriseName.length() > 100) {
                getView().toastShort(mActivity.getString(R.string.enterprise_name_not_more_100));
                return;
            }else{
                mContractInfoModel.customerEnterpriseName = enterpriseName;
            }
        }
        if(TextUtils.isEmpty(customerName)){
            getView().toastShort(mActivity.getString(R.string.please_enter_customer_name));
            return;

        }else{
            if(customerName.length() > 48){
                getView().toastShort(mActivity.getString(R.string.customer_name_not_more_48));
                return;
            }else{
                mContractInfoModel.customerName = customerName;
            }
        }
        if (RegexUtils.checkPhone(customerPhone)) {
            mContractInfoModel.customerPhone = customerPhone;
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_a_valid_mobile_number));
            return;
        }

        if (TextUtils.isEmpty(enterpriseCardId)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_enterprise_card_id));
            return;
        }else{
            if (RegexUtils.checkEnterpriseCardID(enterpriseCardId)) {
                mContractInfoModel.enterpriseCardId = enterpriseCardId;
            }else{
                getView().toastShort(mActivity.getString(R.string.please_enter_correct_enterprise_card_id));
                return;
            }

        }

        if (TextUtils.isEmpty(customerAddress)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_register_address));
        }else{
            if (customerAddress.length() > 200) {
                getView().toastShort(mActivity.getString(R.string.customer_address_no_more_200));
                return;
            }else{
                mContractInfoModel.customerAddress = customerAddress;
            }
        }

        if (TextUtils.isEmpty(placeType)) {
            getView().toastShort(mActivity.getString(R.string.please_select_site_nature));
            return;
        }
        mContractInfoModel.placeType = placeType;

        int serverAge = 1;
        int ageFirst = 1;
        int agePeriod = 1;
//        总服务年限校验
        if (TextUtils.isEmpty(contractAgeStr)) {
            getView().toastShort(mActivity.getString(R.string.contract_service_year_more_1));
            return;
        } else {
            try {
                serverAge = Integer.parseInt(contractAgeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 首次服务年限校验
        if (TextUtils.isEmpty(contractAgeFirstStr)) {
            getView().toastShort(mActivity.getString(R.string.contract_first_year_more_1));
            return;
        } else {
            try {
                ageFirst = Integer.parseInt(contractAgeFirstStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ageFirst > serverAge) {
                getView().toastShort(mActivity.getString(R.string.contract_first_year_more_service_year));
                return;
            }

        }
        if (TextUtils.isEmpty(contractAgePeriodStr)) {
            getView().toastShort(mActivity.getString(R.string.contract_period_more_1));
            return;
        } else {
            try {
                agePeriod = Integer.parseInt(contractAgePeriodStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (agePeriod > serverAge) {
                getView().toastShort(mActivity.getString(R.string.contract_period_more_service_year));
                return;
            }

        }

        mContractInfoModel.serverAge = serverAge;
        mContractInfoModel.firstAge = ageFirst;
        mContractInfoModel.periodAge = agePeriod;

        final ArrayList<ContractsTemplateInfo> dataList = new ArrayList<>(data);
        if (data.size() > 0) {
            //去除未选择的设备
            Iterator<ContractsTemplateInfo> iterator = dataList.iterator();
            while (iterator.hasNext()) {
                ContractsTemplateInfo next = iterator.next();
                if (next.getQuantity() == 0) {
                    iterator.remove();
                }
            }
            if (dataList.size() > 0) {
                mContractInfoModel.devicesList = dataList;
            } else {
                getView().toastShort(mActivity.getString(R.string.please_select_devices_more_1));
                return;
            }

        } else {
            getView().toastShort(mActivity.getString(R.string.not_obtain_device_cout));
            return;
        }
        ContractEditorActivity contractEditorActivity = (ContractEditorActivity) mActivity;
        if (contractEditorActivity != null && !contractEditorActivity.isFinishing()) {
            contractEditorActivity.showCreateDialog(2);
        }
    }
}
