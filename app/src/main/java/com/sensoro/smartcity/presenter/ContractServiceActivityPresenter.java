package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractInfoActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractServiceActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.BusinessLicenseData;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.RecognizeService;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ContractServiceActivityPresenter extends BasePresenter<IContractServiceActivityView> implements
        IOnCreate, Constants {
    private Activity mContext;
    private int serviceType;
    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String line5;
    private String line6;
    private int contractID = -1;
    private int originType = -1;
    //

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        serviceType = mContext.getIntent().getIntExtra(EXTRA_CONTRACT_TYPE, -1);
        refreshContent();
        getContractTemplateInfos();
    }

    private void refreshContent() {
        Intent intent = mContext.getIntent();
        contractID = intent.getIntExtra(EXTRA_CONTRACT_ID, -1);
        originType = intent.getIntExtra(EXTRA_CONTRACT_ORIGIN_TYPE, -1);
        if (contractID != -1) {
            getView().setBtnNextText(mContext.getString(R.string.save));
        }
        String customer_phone = intent.getStringExtra("customer_phone");
        if (customer_phone == null) {
            customer_phone = "";
        }
        String place_type = intent.getStringExtra("place_type");
        if (place_type == null) {
            place_type = "";
        }
        int service_life = intent.getIntExtra("service_life", 1);
        int service_life_first = intent.getIntExtra("service_life_first", 1);
        int service_life_period = intent.getIntExtra("service_life_period", 1);


        switch (serviceType) {
            case 1:
                line1 = intent.getStringExtra("legal_person");
                line2 = intent.getStringExtra("company_name");
                line3 = intent.getStringExtra("credit_code");
                line4 = intent.getStringExtra("registration_number");
                line5 = intent.getStringExtra("address");
                line6 = intent.getStringExtra("validity_period");
                //
                getView().showContentText(originType,serviceType, line1, customer_phone, line2, line3, line4,
                        line5, line6, place_type, service_life, service_life_first, service_life_period);
                break;
            case 2:
                line1 = intent.getStringExtra("person_name");
                line2 = intent.getStringExtra("sex");
                line3 = intent.getStringExtra("id_number");
                line4 = intent.getStringExtra("address");

                getView().showContentText(originType,serviceType, line1, customer_phone, line2, line3, line4,
                        null, null, place_type, service_life, service_life_first, service_life_period);
                break;
            case 3:
                line1 = intent.getStringExtra("company_name");
                line2 = intent.getStringExtra("person_name");
                line3 = intent.getStringExtra("id_number");
                line4 = intent.getStringExtra("address");
                getView().showContentText(originType,serviceType, line1, customer_phone, line2, line3, line4, "", "", place_type, service_life, service_life_first, service_life_period);
                break;
            default:
                break;
        }

    }

    private void getContractTemplateInfos() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getContractstemplate().subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsTemplateRsp>(this) {

            @Override
            public void onCompleted(ContractsTemplateRsp contractsTemplateRsp) {
                ArrayList<ContractsTemplateInfo> data = contractsTemplateRsp.getData();
                Parcelable[] contract_devices = mContext.getIntent().getParcelableArrayExtra("contract_devices");
                if (contract_devices != null && contract_devices.length > 0) {
                    for (Parcelable contract : contract_devices) {
                        try {
                            ContractsTemplateInfo contract1 = (ContractsTemplateInfo) contract;
                            for (ContractsTemplateInfo datum : data) {
                                if (datum.getDeviceType().endsWith(contract1.getDeviceType())) {
                                    datum.setQuantity(contract1.getQuantity());
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
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

    public void retake() {
        switch (serviceType) {
            case 1:
                if (!SensoroCityApplication.getInstance().hasGotToken) {
                    return;
                }
                Intent intent = new Intent(mContext, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(mContext.getApplicationContext()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                getView().startACForResult(intent, REQUEST_CODE_LICENSE_SERVICE);
                break;
            case 2:
                if (!SensoroCityApplication.getInstance().hasGotToken) {
                    return;
                }
                Intent intent1 = new Intent(mContext, CameraActivity.class);
                intent1.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(mContext.getApplication()).getAbsolutePath());
                intent1.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                getView().startACForResult(intent1, REQUEST_CODE_PERSON_SERVICE);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    public void startToNext(String line1, String phone, String line2, String line3, String line4, String line5,
                            String line6,
                            String contractAge, String contractAgeFirst, String contractAgePeriod, String place, String sex, ArrayList<ContractsTemplateInfo> data) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractInfoActivity.class);
        switch (serviceType) {
            case 1:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 1);
                //
                if (RegexUtils.checkContractNotEmpty(line1)) {
                    if (line1.length() > 48) {
                        getView().toastShort("法定代表人不能超过48个字符");
                        return;
                    }
                    intent.putExtra("line1", line1);
                } else {
                    getView().toastShort("请输入法定代表人");
                    return;
                }
                if (RegexUtils.checkPhone(phone)) {
                    intent.putExtra("phone", phone);
                } else {
                    getView().toastShort("请输入有效手机号");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line2)) {
                    if (line2.length() > 100) {
                        getView().toastShort("企业名称不能超过100个字符");
                        return;
                    }
                    intent.putExtra("line2", line2);
                } else {
                    getView().toastShort("请输入企业名称");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line3) || RegexUtils.checkContractNotEmpty(line4)) {
                    boolean canGoOn = false;
                    boolean[] result = {false, false};
                    result[0] = RegexUtils.checkEnterpriseCardID(line3);
                    result[1] = RegexUtils.checkRegisterCode(line4);
                    for (boolean isSuc : result) {
                        if (isSuc) {
                            canGoOn = true;
                            break;
                        }
                    }
                    if (canGoOn) {
                        intent.putExtra("line3", line3);
                        intent.putExtra("line4", line4);
                    } else {
                        getView().toastShort("请输入正确的社会信用代码或注册号");
                        return;
                    }
                } else {
                    getView().toastShort("社会信用代码和注册号必须填写其中一个");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line5)) {
                    if (line5.length() > 200) {
                        getView().toastShort("住址信息不能超过200个字符");
                        return;
                    }
                    intent.putExtra("line5", line5);
                } else {
                    getView().toastShort("请填写住址信息");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line6)) {
                    if (line6.length() > 48) {
                        getView().toastShort("有效期不能超过48个字符");
                        return;
                    }
                    intent.putExtra("line6", line6);
                } else {
                    getView().toastShort("请输入有效期");
                    return;
                }
                break;
            case 2:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 2);
                if (RegexUtils.checkContractNotEmpty(line1)) {
                    if (line1.length() > 48) {
                        getView().toastShort("姓名不能超过48个字符");
                        return;
                    }
                    intent.putExtra("line1", line1);
                } else {
                    getView().toastShort("请输入姓名");
                    return;
                }
                if (RegexUtils.checkPhone(phone)) {
                    intent.putExtra("phone", phone);
                } else {
                    getView().toastShort("请输入有效手机号");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(sex)) {
                    intent.putExtra("line2", sex);
                } else {
                    getView().toastShort("请选择性别");
                    return;
                }
                if (RegexUtils.checkUserID(line3)) {
                    intent.putExtra("line3", line3);
                } else {
                    getView().toastShort("请输入有效身份证号码");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line4)) {
                    if (line4.length() > 200) {
                        getView().toastShort("住址信息不能超过200个字符");
                        return;
                    }
                    intent.putExtra("line4", line4);
                } else {
                    getView().toastShort("请填写住址信息");
                    return;
                }
                break;
            case 3:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 3);
                if (RegexUtils.checkContractNotEmpty(line1)) {
                    if (line1.length() > 100) {
                        getView().toastShort("甲方（客户名称）不能超过100个字符");
                        return;
                    }
                    intent.putExtra("line1", line1);
                } else {
                    getView().toastShort("请输入甲方（客户名称）");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line2)) {
                    if (line2.length() > 48) {
                        getView().toastShort("业主姓名不能超过48个字符");
                        return;
                    }
                    intent.putExtra("line2", line2);
                } else {
                    getView().toastShort("请输入业主姓名");
                    return;
                }
                if (RegexUtils.checkPhone(line3)) {
                    intent.putExtra("line3", line3);
                } else {
                    getView().toastShort("请输入有效手机号");
                    return;
                }
                //
                if (RegexUtils.checkUserID(line4)) {
                    intent.putExtra("line4", line4);
                } else {
                    getView().toastShort("请输入有效身份证号码");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line5)) {
                    if (line4.length() > 200) {
                        getView().toastShort("住址信息不能超过200个字符");
                        return;
                    }
                    intent.putExtra("line5", line5);
                } else {
                    getView().toastShort("请填写住址信息");
                    return;
                }
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(place)) {
            getView().toastShort("请选择一个场地性质");
            return;
        }
        intent.putExtra("place", place);
        int serverAgeTotal = 1;
        int serverAgeFirst = 1;
        int serverAgePeriod = 1;
//        总服务年限校验
        if (TextUtils.isEmpty(contractAge)) {
            getView().toastShort("合同服务年限不能少于1年");
            return;
        } else {
            try {
                serverAgeTotal = Integer.parseInt(contractAge);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (serverAgeTotal < 1) {
//                getView().toastShort("服务年限不能少于1年");
//                return;
//            }
        }
        // 首次服务年限校验
        if (TextUtils.isEmpty(contractAgeFirst)) {
            getView().toastShort("首次付款年限不能少于1年");
            return;
        } else {
            try {
                serverAgeFirst = Integer.parseInt(contractAgeFirst);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (serverAgeFirst < 1) {
//                getView().toastShort("首次服务年限不能少于1年");
//                return;
//            } else {
            if (serverAgeFirst > serverAgeTotal) {
                getView().toastShort("首次付款年限不能超过合同服务年限");
                return;
            }
//            }

        }
        if (TextUtils.isEmpty(contractAgePeriod)) {
            getView().toastShort("续费周期年限不能少于1年");
            return;
        } else {
            try {
                serverAgePeriod = Integer.parseInt(contractAgePeriod);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (serverAgePeriod < 1) {
//                getView().toastShort("付费周期年限不能少于1年");
//                return;
//            } else {
            if (serverAgePeriod > serverAgeTotal) {
                getView().toastShort("续费周期不能超过合同服务年限");
                return;
            }
//            }

        }
//        int temp = serverAgeTotal - serverAgeFirst;
//        if (temp > 0) {
//            if (TextUtils.isEmpty(contractAgePeriod)) {
//                getView().toastShort("付费周期年限不能少于1年");
//                return;
//            } else {
//                try {
//                    serverAgePeriod = Integer.parseInt(contractAgePeriod);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (serverAgePeriod < 1) {
//                    getView().toastShort("付费周期年限不能少于1年");
//                    return;
//                } else {
//                    if (temp % serverAgePeriod != 0) {
//                        getView().toastShort("剩余付款年限需为续费周期的整数倍");
//                        return;
//                    }
//                }
//
//            }
//        } else {
//            if (!TextUtils.isEmpty(contractAgePeriod)) {
//                try {
//                    serverAgePeriod = Integer.parseInt(contractAgePeriod);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (serverAgePeriod != 0) {
//                    getView().toastShort("首次付款与付费周期年限相加不能超过合同服务年限");
//                    return;
//                }
//            }
//        }
        intent.putExtra("contract_service_life", String.valueOf(serverAgeTotal));
        intent.putExtra("contract_service_life_first", String.valueOf(serverAgeFirst));
        intent.putExtra("contract_service_life_period", String.valueOf(serverAgePeriod));
//        intent.putExtra("contract_service_life", "6");
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
                intent.putExtra("contract_template", dataList);
            } else {
                getView().toastShort("至少选择添加一种设备");
                return;
            }

        }else{
            getView().toastShort("设备获取失败，请重试");
            return;
        }

        if (originType == Constants.CONTRACT_ORIGIN_TYPE_EDIT) {
            //重新编辑的
            modifyContractInfo(serverAgeTotal, serverAgeFirst, serverAgePeriod, line1, line2, line3, line4, line5, line6, phone
                    , place, sex, dataList);

        } else {
            getView().startAC(intent);
        }


    }

    private void modifyContractSuccess() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA__CONTRACT_EDIT_REFRESH_CODE;
        eventData.data = contractID;
        EventBus.getDefault().post(eventData);
        getView().toastShort(mContext.getString(R.string.contract_modified_success));
        getView().finishAc();
    }

    private void modifyContractInfo(int serverAgeTotal, int serverAgeFirst, int serverAgePeriod,
                                    String line1, String line2, String line3, String line4, String line5, String line6, String phone,
                                    String place, String sex, ArrayList<ContractsTemplateInfo> data) {

        switch (serviceType) {
            case 1:
                getView().showProgressDialog();
                if (!RegexUtils.checkContractNotEmpty(this.line3)) {
                    this.line3 = null;
                }
                if (!RegexUtils.checkContractNotEmpty(this.line4)) {
                    this.line4 = null;
                }
                RetrofitServiceHelper.INSTANCE.modifyContract(contractID,1, serviceType, null, null, line3, line4,
                        line1, line2, line6, line5, phone, place, data, serverAgePeriod, null, serverAgeTotal, serverAgeFirst).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {

                    @Override
                    public void onCompleted(ResponseBase responseBase) {
                        modifyContractSuccess();
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
                int sexInt = 1;
                if (sex.equals(mContext.getString(R.string.male))) {
                    sexInt = 1;
                }else if(sex.equals(mContext.getString(R.string.female))){
                    sexInt = 2;
                }
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.modifyContract(contractID,2, serviceType, line3, sexInt, null, null,
                        line1, null, null, line4, phone, place, data, serverAgePeriod, null, serverAgeTotal, serverAgeFirst).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {

                    @Override
                    public void onCompleted(ResponseBase responseBase) {
                        modifyContractSuccess();
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
                RetrofitServiceHelper.INSTANCE.modifyContract(contractID,2, serviceType, line4, null, null, null,
                        line2, line1, null, line5, line3, place, data, serverAgePeriod, null, serverAgeTotal, serverAgeFirst).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {


                    @Override
                    public void onCompleted(ResponseBase responseBase) {
                        modifyContractSuccess();
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

    public void handleResult(int requestCode, int resultCode, Intent data) {
        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_LICENSE_SERVICE && resultCode == Activity.RESULT_OK) {
            getView().showProgressDialog();
            //
            try {
                RecognizeService.recBusinessLicense(mContext, FileUtil.getSaveFile(mContext.getApplicationContext())
                                .getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(final String result) {
                                getView().dismissProgressDialog();
                                line1 = "无";
                                line2 = "无";
                                line3 = "无";
                                line4 = "无";
                                line5 = "无";
                                line6 = "无";
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
                                            line2 = words_result单位名称.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.地址Bean words_result地址 = words_result
                                                .get地址();
                                        if (words_result地址 != null) {
                                            line5 = words_result地址.getWords();
                                        }
//                                        BusinessLicenseData.WordsResultBean.成立日期Bean words_result成立日期 = words_result
//                                                .get成立日期();
//                                        if (words_result成立日期 != null) {
////                                            成立日期 = words_result成立日期.getWords();
//                                        }
                                        BusinessLicenseData.WordsResultBean.有效期Bean words_result有效期 = words_result
                                                .get有效期();
                                        if (words_result有效期 != null) {
                                            line6 = words_result有效期.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.法人Bean words_result法人 = words_result
                                                .get法人();
                                        if (words_result法人 != null) {
                                            line1 = words_result法人.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.社会信用代码Bean words_result社会信用代码 =
                                                words_result
                                                        .get社会信用代码();
                                        if (words_result社会信用代码 != null) {
                                            line3 = words_result社会信用代码.getWords();
                                        }
                                        BusinessLicenseData.WordsResultBean.证件编号Bean words_result证件编号 = words_result
                                                .get证件编号();
                                        if (words_result证件编号 != null) {
                                            line4 = words_result证件编号.getWords();
                                        }
                                    }
                                    LogUtils.loge(this, businessLicenseData.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                getView().showContentText(serviceType, line1, getView().getPhoneNumber(1), line2, line3, line4,
                                        line5, line6, "");
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                getView().dismissProgressDialog();
                getView().toastShort("读取失败请重试");
            }
        }
        if (requestCode == REQUEST_CODE_PERSON_SERVICE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                getView().showProgressDialog();
                //
                try {
                    String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                    String filePath = FileUtil.getSaveFile(mContext.getApplicationContext()).getAbsolutePath();
                    if (!TextUtils.isEmpty(contentType)) {
                        if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                        } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getView().dismissProgressDialog();
                    getView().toastShort("读取失败请重试");
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

        OCR.getInstance(mContext).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                getView().dismissProgressDialog();
                line1 = "无";
                line2 = "无";
                line3 = "无";
                line4 = "无";
                try {
                    if (result != null) {
                        LogUtils.loge(this, result.toString());
                        Word resultName = result.getName();
                        if (resultName != null) {
                            line1 = resultName.getWords();
                        }
                        Word resultGender = result.getGender();
                        if (resultGender != null) {
                            line2 = resultGender.getWords();
                        }
                        Word resultIdNumber = result.getIdNumber();
                        if (resultIdNumber != null) {
                            line3 = resultIdNumber.getWords();
                        }
                        Word resultAddress = result.getAddress();
                        if (resultAddress != null) {
                            line4 = resultAddress.getWords();
                        }
                        LogUtils.loge(this, result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getView().showContentText(serviceType, line1, getView().getPhoneNumber(2), line2, line3, line4,
                        null, null, "");
            }

            @Override
            public void onError(OCRError error) {
                getView().dismissProgressDialog();
                getView().toastShort(mContext.getString(R.string.identification_number_failed) + error.getMessage());
                LogUtils.loge(this, error.getMessage());
            }
        });
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case EVENT_DATA_FINISH_CODE:
                getView().finishAc();
                break;
        }
//        LogUtils.loge(this, eventData.toString());
    }
}
