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
        switch (serviceType) {
            case 1:
                line1 = mContext.getIntent().getStringExtra("legal_person");
                line2 = mContext.getIntent().getStringExtra("company_name");
                line3 = mContext.getIntent().getStringExtra("credit_code");
                line4 = mContext.getIntent().getStringExtra("registration_number");
                line5 = mContext.getIntent().getStringExtra("address");
                line6 = mContext.getIntent().getStringExtra("validity_period");
                //
                getView().showContentText(serviceType, line1, "", line2, line3, line4,
                        line5, line6, 0);
                break;
            case 2:
                line1 = mContext.getIntent().getStringExtra("person_name");
                line2 = mContext.getIntent().getStringExtra("sex");
                line3 = mContext.getIntent().getStringExtra("id_number");
                line4 = mContext.getIntent().getStringExtra("address");
                getView().showContentText(serviceType, line1, "", line2, line3, line4,
                        null, null, 0);
                break;
            case 3:
                getView().showContentText(serviceType, "", "", "", "", "", "", "", 0);
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
                            String contractAge, String place, String sex, ArrayList<ContractsTemplateInfo> data) {
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
            default:
                break;
        }
        if (TextUtils.isEmpty(place)) {
            getView().toastShort("请选择一个场地性质");
            return;
        }
        intent.putExtra("place", place);
//        if (TextUtils.isEmpty(contractAge)) {
//            getView().toastShort("服务年限不能少于1年");
//            return;
//        } else {
//            int serverAge = 0;
//            try {
//                serverAge = Integer.parseInt(contractAge);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (serverAge == 0) {
//                getView().toastShort("服务年限不能少于1年");
//                return;
//            }
//            intent.putExtra("contract_service_life", String.valueOf(serverAge));
//        }
        intent.putExtra("contract_service_life", "6");
        if (data != null && data.size() > 0) {
            final ArrayList<ContractsTemplateInfo> dataList = new ArrayList<>(data);
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

        }
        getView().startAC(intent);
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
                                getView().showContentText(serviceType, line1, "", line2, line3, line4,
                                        line5, line6, 0);
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
                getView().showContentText(serviceType, line1, "", line2, line3, line4,
                        null, null, 0);
            }

            @Override
            public void onError(OCRError error) {
                getView().dismissProgressDialog();
                getView().toastShort("身份证识别失败：" + error.getMessage());
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
        switch (code){
            case EVENT_DATA_FINISH_CODE:
                getView().finishAc();
                break;
        }
//        LogUtils.loge(this, eventData.toString());
    }
}
