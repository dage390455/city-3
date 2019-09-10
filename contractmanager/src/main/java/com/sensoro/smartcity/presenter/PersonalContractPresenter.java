package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.sensoro.common.base.BaseApplication;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.ContractAddInfo;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.server.bean.ContractsTemplateInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.FileUtil;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.RegexUtils;
import com.sensoro.contractmanager.R;
import com.sensoro.smartcity.activity.ContractCreationSuccessActivity;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.imainviews.IPersonalContractView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PersonalContractPresenter extends BasePresenter<IPersonalContractView> {
    private Activity mActivity;
    private ContractListInfo mContractInfo = new ContractListInfo();
    private int submitStatus = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        getContractTemplateInfos();
    }

    public void initData(Context context, Bundle bundle) {
        this.initData(context);
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable(Constants.EXTRA_CONTRACT_INFO);
            if (serializable instanceof ContractListInfo) {
                submitStatus = 2;
                mContractInfo = (ContractListInfo) serializable;
                getView().setOwnerName(mContractInfo.getCustomer_name());
                getView().setPartAName(mContractInfo.getCustomer_enterprise_name());
                getView().setContactNumber(mContractInfo.getCustomer_phone());
                getView().setIdCardNumber(mContractInfo.getCard_id());
                getView().setHomeAddress(mContractInfo.getCustomer_address());
                getView().setSiteNature(mContractInfo.getPlace_type());
                ArrayList<ContractsTemplateInfo> data = getView().getContractTemplateList();
                if (data.size() > 0) {
                    refreshContractsTemplate(data, mContractInfo.getDevices());
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
        RetrofitServiceHelper.getInstance().getContractstemplate().subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ArrayList<ContractsTemplateInfo>>>(this) {

            @Override
            public void onCompleted(ResponseResult<ArrayList<ContractsTemplateInfo>> contractsTemplateRsp) {
                ArrayList<ContractsTemplateInfo> data = contractsTemplateRsp.getData();
                List<ContractsTemplateInfo> devices = mContractInfo.getDevices();
                if (devices != null && devices.size() > 0) {
                    refreshContractsTemplate(data, devices);
                } else {
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
        EventBus.getDefault().unregister(this);
    }

    public void doTakePhoto() {
        if (!BaseApplication.getInstance().hasGotToken) {
            return;
        }
        Intent intent = new Intent(mActivity, CameraActivity.class);
        String absolutePath = FileUtil.getSaveFile(mActivity.getApplication()).getAbsolutePath();
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                absolutePath);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        if (isAttachedView()) {
            getView().startACForResult(intent, Constants.REQUEST_CODE_CAMERA);
        }
    }

    public void handActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
            }
        }
    }

    private void recPersonalLicense() {
        try {
//            String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
            String filePath = FileUtil.getSaveFile(mActivity.getApplicationContext()).getAbsolutePath();
//            if (!TextUtils.isEmpty(contentType)) {
//                if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
//                } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
//                    recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isAttachedView()) {
                getView().dismissProgressDialog();
                getView().toastShort(mActivity.getString(R.string.read_error_try));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String msg) {
        if ("ocr_ui__file_success".equals(msg)) {
            recPersonalLicense();
        } else if ("ocr_ui__file_failed".equals(msg)) {
            if (isAttachedView()) {
                getView().dismissProgressDialog();
                getView().toastShort(mActivity.getString(R.string.identification_failed_try_again));
            }
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(mActivity).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                }
                String name = "无";
                String idNumber = "无";
                String address = "无";
                try {
                    if (result != null) {
                        try {
                            LogUtils.loge(this, result.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        Word resultName = result.getName();
                        if (resultName != null) {
                            String words = resultName.getWords();
                            if (!TextUtils.isEmpty(words)) {
                                name = words;
                            }
                        }
                        Word resultIdNumber = result.getIdNumber();
                        if (resultIdNumber != null) {
                            String words = resultIdNumber.getWords();
                            if (!TextUtils.isEmpty(words)) {
                                idNumber = words;
                            }

                        }
                        Word resultAddress = result.getAddress();
                        if (resultAddress != null) {
                            String words = resultAddress.getWords();
                            if (!TextUtils.isEmpty(words)) {
                                address = words;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isAttachedView()) {
                    getView().setOwnerName(name);
                    getView().setIdCardNumber(idNumber);
                    getView().setHomeAddress(address);
                }

            }

            @Override
            public void onError(OCRError error) {

                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.id_card_Identification_error) + error.getMessage());
                }
                try {
                    LogUtils.loge(this, error.getMessage());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    public void doSubmit(String partA, String ownerName, String contactInfo, String idCard, String homeAddress,
                         String siteNature, String contractAgeStr, String contractAgeFirstStr, String contractAgePeriodStr, ArrayList<ContractsTemplateInfo> data) {
        mContractInfo.setContract_type(2);
        if (RegexUtils.checkContractIsEmpty(partA)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_party_a_customer_name));
            return;
        } else {
            if (partA.length() > 30) {
                getView().toastShort(mActivity.getString(R.string.party_a_customer_name_not_more_100));
                return;
            }
            if (RegexUtils.checkContractName(partA)) {
                mContractInfo.setCustomer_enterprise_name(partA);
            } else {
                getView().toastShort(mActivity.getString(R.string.party_a_customer_name) + mActivity.getString(R.string.do_not_enter_illegal_characters_such_as_english_and_numbers));
                return;
            }
        }
        if (RegexUtils.checkContractIsEmpty(ownerName)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_owner_name));
            return;
        } else {
            if (ownerName.length() > 8) {
                getView().toastShort(mActivity.getString(R.string.owner_name_more_48));
                return;
            }
            if (RegexUtils.checkContractName(ownerName)) {
                mContractInfo.setCustomer_name(ownerName);
            } else {
                getView().toastShort(mActivity.getString(R.string.owners_name) + mActivity.getString(R.string.do_not_enter_illegal_characters_such_as_english_and_numbers));
                return;
            }

        }
        if (RegexUtils.checkPhone(contactInfo)) {
            mContractInfo.setCustomer_phone(contactInfo);
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_a_valid_mobile_number));
            return;
        }
        //
        if (RegexUtils.checkUserID(idCard)) {
            mContractInfo.setCard_id(idCard);
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_valid_id_card_number));
            return;
        }
        if (RegexUtils.checkContractIsEmpty(homeAddress)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_home_address));
            return;
        } else {
            if (homeAddress.length() > 30) {
                getView().toastShort(mActivity.getString(R.string.home_address_more_200));
                return;
            }
            mContractInfo.setCustomer_address(homeAddress);
        }

        if (RegexUtils.checkContractIsEmpty(siteNature)) {
            getView().toastShort(mActivity.getString(R.string.please_select_site_nature));
            return;
        }
        mContractInfo.setPlace_type(siteNature);

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
        switch (submitStatus) {
            case 1:
                //创建合同
                ContractEditorActivity contractEditorActivity = (ContractEditorActivity) mActivity;
                if (contractEditorActivity != null && !contractEditorActivity.isFinishing()) {
                    contractEditorActivity.showCreateDialog(1);
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
        RetrofitServiceHelper.getInstance().modifyContract(mContractInfo.getUid(), mContractInfo.getId(), mContractInfo.getContract_type(), mContractInfo.getCard_id(), null,
                mContractInfo.getEnterprise_card_id(), null,
                mContractInfo.getCustomer_name(), mContractInfo.getCustomer_enterprise_name(), null, mContractInfo.getCustomer_address(),
                mContractInfo.getCustomer_phone(), mContractInfo.getPlace_type(), mContractInfo.getDevices(), mContractInfo.getPayTimes(), null, mContractInfo.getServiceTime(), mContractInfo.getFirstPayTimes()).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult>(this) {

            @Override
            public void onCompleted(ResponseResult responseBase) {
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
                }, 1000);
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

    public void doCreateContract() {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getNewContract(mContractInfo.getContract_type(), mContractInfo.getCard_id(), null,
                mContractInfo.getEnterprise_card_id(), null, mContractInfo.getCustomer_name(), mContractInfo.getCustomer_enterprise_name(),
                null, mContractInfo.getCustomer_address(), mContractInfo.getCustomer_phone(), mContractInfo.getPlace_type(),
                mContractInfo.getDevices(), mContractInfo.getPayTimes(), null, mContractInfo.getServiceTime(),
                mContractInfo.getFirstPayTimes()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ContractAddInfo>>(this) {

            @Override
            public void onCompleted(ResponseResult<ContractAddInfo> contractAddRsp) {
                ContractAddInfo data = contractAddRsp.getData();
                int id = data.getId();
                try {
                    LogUtils.loge(this, "id = " + id);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                String url = data.getFdd_viewpdf_url();
                Intent intent = new Intent(mActivity, ContractCreationSuccessActivity.class);
                intent.putExtra(Constants.EXTRA_CONTRACT_ID, id);
                if (!TextUtils.isEmpty(url)) {
                    intent.putExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL, url);
                }
                getView().startACForResult(intent, Constants.EVENT_DATA_CONTRACT_CREATION_SUCCESS);
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }
}
