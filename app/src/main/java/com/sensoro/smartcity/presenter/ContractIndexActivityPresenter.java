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
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractServiceActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractIndexActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.BusinessLicenseData;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.RecognizeService;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.util.FileUtil;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class ContractIndexActivityPresenter extends BasePresenter<IContractIndexActivityView> implements Constants,
        IOnCreate {
    private Activity mContext;
    private static final int REQUEST_CODE_BUSINESS_LICENSE = 123;
    private static final int REQUEST_CODE_CAMERA = 102;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
    }

    public void startLicense() {
        if (!SensoroCityApplication.getInstance().hasGotToken) {
            return;
        }
        Intent intent = new Intent(mContext, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(mContext.getApplicationContext()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        getView().startACForResult(intent, REQUEST_CODE_BUSINESS_LICENSE);
    }

    public void startPerson() {
        if (!SensoroCityApplication.getInstance().hasGotToken) {
            return;
        }
        Intent intent = new Intent(mContext, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(mContext.getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        getView().startACForResult(intent, REQUEST_CODE_CAMERA);
    }

    public void startManual() {
        Intent intent = new Intent(mContext, ContractServiceActivity.class);
        intent.putExtra(EXTRA_CONTRACT_TYPE, 3);
        getView().startAC(intent);

    }

    public void handActivityResult(int requestCode, int resultCode, Intent data) {
        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
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
                                    String 单位名称 = words_result.get单位名称().getWords();
                                    String 地址 = words_result.get地址().getWords();
                                    String 成立日期 = words_result.get成立日期().getWords();
                                    String 有效期 = words_result.get有效期().getWords();
                                    String 法人 = words_result.get法人().getWords();
                                    String 社会信用代码 = words_result.get社会信用代码().getWords();
                                    String 证件编号 = words_result.get证件编号().getWords();
                                    LogUtils.loge(this, businessLicenseData.toString());
                                    startServiceByLience(单位名称, 地址, 成立日期, 有效期, 法人, 社会信用代码, 证件编号);
                                }
                            });

                        }
                    });
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
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
                    String name = result.getName().getWords();
                    String sex = result.getGender().getWords();
                    String idNumber = result.getIdNumber().getWords();
                    String address = result.getAddress().getWords();
                    startServiceByNumber(name, sex, idNumber, address);
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

    private void startServiceByLience(String 单位名称, String 地址, String 成立日期, String 有效期, String 法人, String 社会信用代码,
                                      String 证件编号) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractServiceActivity.class);
        intent.putExtra(EXTRA_CONTRACT_TYPE, 1);
        intent.putExtra("legal_person", 法人);
        intent.putExtra("company_name", 单位名称);
        intent.putExtra("credit_code", 社会信用代码);
        intent.putExtra("registration_number", 证件编号);
        intent.putExtra("address", 地址);
        intent.putExtra("validity_period", 有效期);
        getView().startAC(intent);
    }

    private void startServiceByNumber(String personName, String sex, String idNumber, String address) {
        Intent intent = new Intent();
        intent.setClass(mContext, ContractServiceActivity.class);
        intent.putExtra(EXTRA_CONTRACT_TYPE, 2);
        if (TextUtils.isEmpty(personName)) {
            intent.putExtra("person_name", "无");
        } else {
            intent.putExtra("person_name", personName);
        }
        if (TextUtils.isEmpty(sex)) {
            intent.putExtra("sex", "无");
        } else {
            intent.putExtra("sex", sex);
        }
        if (TextUtils.isEmpty(idNumber)) {
            intent.putExtra("id_number", "无");
        } else {
            intent.putExtra("id_number", idNumber);
        }
        if (TextUtils.isEmpty(address)) {
            intent.putExtra("address", "无");
        } else {
            intent.putExtra("address", address);
        }
        getView().startAC(intent);
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
        if (code == EVENT_DATA_FINISH_CODE) {
            getView().finishAc();
        }
        LogUtils.loge(this, eventData.toString());
    }
}
