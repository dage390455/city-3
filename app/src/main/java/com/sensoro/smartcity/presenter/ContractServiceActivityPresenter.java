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
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private String placeType;
    //
    private static final int REQUEST_CODE_LICENSE_SERVICE = 0x111;
    private static final int REQUEST_CODE_PERSON_SERVICE = 0x112;

    private List<ContractsTemplateInfo> data;

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
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsTemplateRsp>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(ContractsTemplateRsp contractsTemplateRsp) {
                data = contractsTemplateRsp.getData();
                getView().updateContractTemplateAdapterInfo(data);
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

//    public void clickItem(View view, int position) {
//        if (data != null) {
//            ContractsTemplateInfo contractsTemplateInfo = data.get(position);
//            switch (view.getId()) {
//
//            }
//        }
//    }

    public void startToNext(String line1, String phone, String line2, String line3, String line4, String line5,
                            String line6,
                            String contractAge, String place) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractInfoActivity.class);
        switch (serviceType) {
            case 1:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 1);
                //
                intent.putExtra("line1", line1);
                intent.putExtra("line2", line2);
                if (RegexUtils.checkPhone(phone)) {
                    intent.putExtra("phone", phone);
                } else {
                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line3) || RegexUtils.checkContractNotEmpty(line4)) {
//                    if (RegexUtils.checkEnterpriseCardID(line3)) {
                    intent.putExtra("line3", line3);
//                    } else {
//                        getView().toastShort("请输入有效社会信用代码");
//                        return;
//                    }
//                    if (RegexUtils.checkRegisterCode(line4)) {
                    intent.putExtra("line4", line4);
//                    } else {
//                        getView().toastShort("请输入有效注册号");
//                        return;
//                    }
                } else {
                    getView().toastShort("社会信用代码和注册号必须填写其中一个");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line5)) {
                    intent.putExtra("line5", line5);
                } else {
                    getView().toastShort("请填写住址信息");
                    return;
                }
                intent.putExtra("line6", line6);
                break;
            case 2:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 2);
                if (RegexUtils.checkContractNotEmpty(line1)) {
                    intent.putExtra("line1", line1);
                } else {
                    getView().toastShort("请输入有效姓名");
                    return;
                }
                if (RegexUtils.checkPhone(phone)) {
                    intent.putExtra("phone", phone);
                } else {
                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line2)) {
                    intent.putExtra("line2", line2);
                } else {
                    getView().toastShort("请输入性别");
                    return;
                }
                if (RegexUtils.checkUserID(line3)) {
                    intent.putExtra("line3", line3);
                } else {
                    getView().toastShort("请输入有效身份证号码");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line4)) {
                    intent.putExtra("line4", line4);
                } else {
                    getView().toastShort("请填写住址信息");
                    return;
                }
                break;
            case 3:
                intent.putExtra(EXTRA_CONTRACT_TYPE, 3);
                if (RegexUtils.checkContractNotEmpty(line1)) {
                    intent.putExtra("line1", line1);
                } else {
                    getView().toastShort("请输入甲方（客户名称）");
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line2)) {
                    intent.putExtra("line2", line2);
                } else {
                    getView().toastShort("请输入业主姓名");
                    return;
                }
                if (RegexUtils.checkPhone(line3)) {
                    intent.putExtra("line3", line3);
                } else {
                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                    return;
                }
                if (RegexUtils.checkContractNotEmpty(line4)) {
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
            getView().toastShort("请选择一个场地类型");
            return;
        }
        intent.putExtra("place", place);
        intent.putExtra("contract_service_life", contractAge);
        if (data != null) {
            ArrayList<ContractsTemplateInfo> dataList = (ArrayList<ContractsTemplateInfo>) data;
            intent.putExtra("contract_template", dataList);
        }
        getView().startAC(intent);
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_LICENSE_SERVICE && resultCode == Activity.RESULT_OK) {
            getView().showProgressDialog();
            RecognizeService.recBusinessLicense(mContext, FileUtil.getSaveFile(mContext.getApplicationContext())
                            .getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(final String result) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getView().dismissProgressDialog();
                                    BusinessLicenseData businessLicenseData = RetrofitServiceHelper.INSTANCE.getGson()
                                            .fromJson(result, BusinessLicenseData.class);
                                    BusinessLicenseData.WordsResultBean words_result = businessLicenseData
                                            .getWords_result();
                                    line2 = words_result.get单位名称().getWords();
                                    line5 = words_result.get地址().getWords();
                                    String 成立日期 = words_result.get成立日期().getWords();
                                    line6 = words_result.get有效期().getWords();
                                    line1 = words_result.get法人().getWords();
                                    line3 = words_result.get社会信用代码().getWords();
                                    line4 = words_result.get证件编号().getWords();
//                            infoPopText(result);
                                    LogUtils.loge(this, businessLicenseData.toString());
                                    getView().showContentText(serviceType, line1, "", line2, line3, line4,
                                            line5, line6, 0);
                                }
                            });

                        }
                    });
        }
        if (requestCode == REQUEST_CODE_PERSON_SERVICE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                getView().showProgressDialog();
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(mContext.getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
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

        OCR.getInstance(mContext).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                getView().dismissProgressDialog();
                if (result != null) {
                    LogUtils.loge(this, result.toString());
                    line1 = result.getName().getWords();
                    line2 = result.getGender().getWords();
                    line3 = result.getIdNumber().getWords();
                    line4 = result.getAddress().getWords();
                    getView().showContentText(serviceType, line1, "", line2, line3, line4,
                            null, null, 0);
                }
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

        if (data != null) {
            data.clear();
            data = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == EVENT_DATA_FINISH_CODE) {
            getView().finishAc();
        }
        LogUtils.loge(this, eventData.toString());
    }
}
