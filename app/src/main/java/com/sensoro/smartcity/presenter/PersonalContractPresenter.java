package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractCreationSuccessActivity;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IPersonalContractView;
import com.sensoro.smartcity.model.ContractInfoModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractAddInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalContractPresenter extends BasePresenter<IPersonalContractView> {
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
        String absolutePath = FileUtil.getSaveFile(mActivity.getApplication()).getAbsolutePath();
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                absolutePath);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        if (isAttachedView()){
            getView().startACForResult(intent, Constants.REQUEST_CODE_CAMERA);
        }
    }

    public void handActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (isAttachedView()){
                    getView().showProgressDialog();
                }
                try {
                    String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                    String filePath = FileUtil.getSaveFile(mActivity.getApplicationContext()).getAbsolutePath();
                    if (!TextUtils.isEmpty(contentType)) {
                        if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                        } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                        }
                    }
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
                if (isAttachedView()){
                    getView().dismissProgressDialog();
                }
                String name = "";
                String idNumber = "";
                String address = "";
                try {
                    if (result != null) {
                        LogUtils.loge(this, result.toString());
                        Word resultName = result.getName();
                        if (resultName != null) {
                            name = resultName.getWords();
                        }
                        Word resultIdNumber = result.getIdNumber();
                        if (resultIdNumber != null) {
                            idNumber = resultIdNumber.getWords();
                        }
                        Word resultAddress = result.getAddress();
                        if (resultAddress != null) {
                            address = resultAddress.getWords();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getView().setOwnerName(name);
                getView().setIdCardNumber(idNumber);
                getView().setHomeAddress(address);
            }

            @Override
            public void onError(OCRError error) {
                if (isAttachedView()){
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.id_card_Identification_error) + error.getMessage());
                }
                LogUtils.loge(this, error.getMessage());
            }
        });
    }

    public void doSubmit(String partA, String ownerName, String contactInfo, String idCard, String homeAddress,
                         String siteNature, String contractAgeStr, String contractAgeFirstStr, String contractAgePeriodStr, ArrayList<ContractsTemplateInfo> data) {
        mContractInfoModel = new ContractInfoModel();
        mContractInfoModel.contractType = 2;
        if (TextUtils.isEmpty(partA)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_party_a_customer_name));
            return;
        } else {
            if (partA.length() > 100) {
                getView().toastShort(mActivity.getString(R.string.party_a_customer_name_not_more_100));
                return;
            }
            mContractInfoModel.customerEnterpriseName = partA;

        }
        if (TextUtils.isEmpty(ownerName)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_owner_name));
            return;
        } else {
            if (ownerName.length() > 48) {
                getView().toastShort(mActivity.getString(R.string.owner_name_more_48));
                return;
            }
            mContractInfoModel.customerName = ownerName;
        }
        if (RegexUtils.checkPhone(contactInfo)) {
            mContractInfoModel.customerPhone = contactInfo;
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_a_valid_mobile_number));
            return;
        }
        //
        if (RegexUtils.checkUserID(idCard)) {
            mContractInfoModel.idCardNumber = idCard;
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_valid_id_card_number));
            return;
        }
        if (TextUtils.isEmpty(homeAddress)) {
            getView().toastShort(mActivity.getString(R.string.please_enter_home_address));
            return;
        } else {
            if (homeAddress.length() > 200) {
                getView().toastShort(mActivity.getString(R.string.home_address_more_200));
                return;
            }
            mContractInfoModel.customerAddress = homeAddress;
        }

        if (TextUtils.isEmpty(siteNature)) {
            getView().toastShort(mActivity.getString(R.string.please_select_site_nature));
            return;
        }
        mContractInfoModel.placeType = siteNature;

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

    public void doCreateContract() {
        if(mContractInfoModel == null){
            getView().toastShort(mActivity.getString(R.string.info_check_faild));
            return;
        }
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getNewContract(mContractInfoModel.contractType, 2, mContractInfoModel.idCardNumber, null,
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
}
