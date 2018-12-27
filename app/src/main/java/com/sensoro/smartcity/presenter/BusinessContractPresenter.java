package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

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
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.RecognizeService;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractAddInfo;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BusinessContractPresenter extends BasePresenter<IBusinessContractView> {
    private Activity mActivity;
    private ContractListInfo mContractInfo = new ContractListInfo();;
    private int submitStatus = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        getContractTemplateInfos();
    }

    public void initData(Context context, Bundle bundle){
        this.initData(context);
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable(Constants.EXTRA_CONTRACT_INFO);
            if (serializable instanceof ContractListInfo) {
                submitStatus = 2;
                mContractInfo = (ContractListInfo) serializable;
                getView().setOwnerName(mContractInfo.getCustomer_name());
                getView().setEnterpriseName(mContractInfo.getCustomer_enterprise_name());
                getView().setContactNumber(mContractInfo.getCustomer_phone());
                getView().setSocialCreatedId(mContractInfo.getEnterprise_card_id());
                getView().setRegisterAddress(mContractInfo.getCustomer_address());
                getView().setSiteNature(mContractInfo.getPlace_type());
                ArrayList<ContractsTemplateInfo> data = getView().getContractTemplateList();
                if (data.size() > 0) {
                    refreshContractsTemplate(data,mContractInfo.getDevices());
                }
                getView().setServeAge(String.valueOf(mContractInfo.getServiceTime()));
                getView().setFirstAge(String.valueOf(mContractInfo.getFirstPayTimes()));
                getView().setPeriodAge(String.valueOf(mContractInfo.getPayTimes()));
                getView().setTvSubmitText(mActivity.getString(R.string.save));
            }
        }

    }

    private void refreshContractsTemplate(ArrayList<ContractsTemplateInfo> data, List<ContractsTemplateInfo> devices) {
        for (ContractsTemplateInfo datum : data) {
            for (ContractsTemplateInfo device : devices) {
                if (datum.getDeviceType().equals(device.getDeviceType())) {
                    datum.setQuantity(device.getQuantity());
                    break;
                }
            }
        }
        getView().updateContractTemplateAdapterInfo(data);
    }

    private void getContractTemplateInfos() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getContractstemplate().subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsTemplateRsp>(this) {

            @Override
            public void onCompleted(ContractsTemplateRsp contractsTemplateRsp) {
                ArrayList<ContractsTemplateInfo> data = contractsTemplateRsp.getData();
                if(mContractInfo.getDevices() != null && mContractInfo.getDevices().size()>0){
                    refreshContractsTemplate(data,mContractInfo.getDevices());
                }else{
                    getView().updateContractTemplateAdapterInfo(data);
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

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
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
        if (isAttachedView()) {
            getView().startACForResult(intent, Constants.REQUEST_CODE_BUSINESS_LICENSE);
        }
    }

    public void handActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
            if (isAttachedView()) {
                getView().showProgressDialog();
            }
            try {
                RecognizeService.recBusinessLicense(mActivity, FileUtil.getSaveFile(mActivity.getApplicationContext())
                                .getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(final String result) {
                                if (isAttachedView()) {
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
                                if (isAttachedView()) {
                                    getView().setBusinessMerchantName(enterpriseName);
                                    getView().setOwnerName(customerName);
                                    getView().setRegisterAddress(customerAddress);
                                    getView().setSocialCreatedId(enterpriseCardId);
                                }

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.read_error_try));
                }
            }

        }
    }

    public void doCreateContract() {
        getView().showProgressDialog();
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getNewContract(mContractInfo.getContract_type(), mContractInfo.getCard_id(), null,
                mContractInfo.getEnterprise_card_id(),null,mContractInfo.getCustomer_name(), mContractInfo.getCustomer_enterprise_name(),
                null, mContractInfo.getCustomer_address(), mContractInfo.getCustomer_phone(), mContractInfo.getPlace_type(),
                mContractInfo.getDevices(), mContractInfo.getPayTimes(), null,mContractInfo.getServiceTime(),
                mContractInfo.getFirstPayTimes()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractAddRsp>(this) {

            @Override
            public void onCompleted(ContractAddRsp contractAddRsp) {
                ContractAddInfo data = contractAddRsp.getData();
                int id = data.getId();
                LogUtils.loge(this, "id = " + id);
                Intent intent = new Intent(mActivity, ContractCreationSuccessActivity.class);
                intent.putExtra(Constants.EXTRA_CONTRACT_ID,id);
                String url = data.getFdd_viewpdf_url();
                if (!TextUtils.isEmpty(url)){
                    intent.putExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL,url);
                }
                getView().startAC(intent);
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
        mContractInfo.setContract_type(1);
        //
        if (TextUtils.isEmpty(enterpriseName)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_enterprise_name));
            return;
        } else {
            if (enterpriseName.length() > 100) {
                getView().toastShort(mActivity.getString(R.string.enterprise_name_not_more_100));
                return;
            }else{
                mContractInfo.setCustomer_enterprise_name(enterpriseName);
            }
        }
        if (TextUtils.isEmpty(customerName)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_customer_name));
            return;

        } else {
            if (customerName.length() > 48) {
                getView().toastShort(mActivity.getString(R.string.customer_name_not_more_48));
                return;
            }else{
                mContractInfo.setCustomer_name(customerName);
            }
        }
        if (RegexUtils.checkPhone(customerPhone)) {
            mContractInfo.setCustomer_phone(customerPhone);
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_a_valid_mobile_number));
            return;
        }

        if (TextUtils.isEmpty(enterpriseCardId)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_enterprise_card_id));
            return;
        } else {
            if (RegexUtils.checkEnterpriseCardID(enterpriseCardId)) {
                mContractInfo.setEnterprise_card_id(enterpriseCardId);
            }else{
                getView().toastShort(mActivity.getString(R.string.please_enter_correct_enterprise_card_id));
                return;
            }

        }

        if (TextUtils.isEmpty(customerAddress)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_register_address));
            return;
        }else{
            if (customerAddress.length() > 200) {
                getView().toastShort(mActivity.getString(R.string.customer_address_no_more_200));
                return;
            }else{
                mContractInfo.setCustomer_address(customerAddress);
            }
        }

        if (TextUtils.isEmpty(placeType)) {
            getView().toastShort(mActivity.getString(R.string.please_select_site_nature));
            return;
        }
        mContractInfo.setPlace_type(placeType);

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

        mContractInfo.setServiceTime(serverAge);
        mContractInfo.setFirstPayTimes(ageFirst);
        mContractInfo.setPayTimes(agePeriod);

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
                mContractInfo.setDevices(dataList);
            } else {
                getView().toastShort(mActivity.getString(R.string.please_select_devices_more_1));
                return;
            }

        } else {
            getView().toastShort(mActivity.getString(R.string.not_obtain_device_cout));
            return;
        }

        switch (submitStatus){
            case 1:
                //创建合同
                ContractEditorActivity contractEditorActivity = (ContractEditorActivity) mActivity;
                if (contractEditorActivity != null && !contractEditorActivity.isFinishing()) {
                    contractEditorActivity.showCreateDialog(2);
                }
                break;
            case 2:
                //编辑合同
                doModifyContract();
                break;
        }

    }

    private void doModifyContract() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.modifyContract(mContractInfo.getUid(),mContractInfo.getId(), mContractInfo.getContract_type(),  mContractInfo.getCard_id(), null,
                mContractInfo.getEnterprise_card_id(),null,
                mContractInfo.getCustomer_name(), mContractInfo.getCustomer_enterprise_name(), null, mContractInfo.getCustomer_address(),
                mContractInfo.getCustomer_phone(), mContractInfo.getPlace_type(), mContractInfo.getDevices(), mContractInfo.getPayTimes(), null, mContractInfo.getServiceTime(), mContractInfo.getFirstPayTimes()).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {

            @Override
            public void onCompleted(ResponseBase responseBase) {
                modifyContractSuccess();
                getView().dismissProgressDialog();
                getView().showSaveSuccessToast();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().cancelSuccessToast();
                            getView().finishAc();
                        }

                    }
                },1000);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void modifyContractSuccess() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA__CONTRACT_EDIT_REFRESH_CODE;
        eventData.data = mContractInfo.getId();
        EventBus.getDefault().post(eventData);
    }
}
