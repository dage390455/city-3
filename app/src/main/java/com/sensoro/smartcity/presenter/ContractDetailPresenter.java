package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.activity.ContractPreviewActivity;
import com.sensoro.smartcity.activity.ContractResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractDetailView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.server.response.ContractInfoRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContractDetailPresenter extends BasePresenter<IContractDetailView> {
    private Activity mActivity;
    private ContractListInfo mContractInfo;
    private int contractId;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        contractId = mActivity.getIntent().getIntExtra(Constants.EXTRA_CONTRACT_ID, 0);
        EventBus.getDefault().register(this);
        if (contractId == 0) {
            getView().toastShort(mActivity.getString(R.string.not_obtain_contract_id));
        } else {
            requestContractInfo(contractId);
        }
    }

    private void requestContractInfo(int id) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getContractInfo(String.valueOf(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ContractInfoRsp>(this) {
                    @Override
                    public void onCompleted(ContractInfoRsp responseBase) {
                        mContractInfo = responseBase.getData();
                        if (mContractInfo != null) {
                            getView().setSignStatus(mContractInfo.isConfirmed());
                            getView().setCustomerEnterpriseName(mContractInfo.getCustomer_enterprise_name());
                            getView().setCustomerName(mContractInfo.getCustomer_name());
                            getView().setCustomerPhone(mContractInfo.getCustomer_phone());
                            getView().setCustomerAddress(mContractInfo.getCustomer_address());
                            getView().setPlaceType(mContractInfo.getPlace_type());
                            getView().setServerAge(String.format(Locale.CHINESE, "%d%s", mContractInfo.getServiceTime(), mActivity.getString(R.string.year)));
                            getView().setPeriodAge(String.format(Locale.CHINESE, "%d%s", mContractInfo.getPayTimes(), mActivity.getString(R.string.year)));
                            getView().setFirstAge(String.format(Locale.CHINESE, "%d%s", mContractInfo.getFirstPayTimes(), mActivity.getString(R.string.year)));
                            switch (mContractInfo.getContract_type()) {
                                case 1:
                                    getView().setCardIdOrEnterpriseId(mContractInfo.getEnterprise_card_id());
                                    break;
                                case 2:
                                    getView().setCardIdOrEnterpriseId(mContractInfo.getCard_id());
                                    break;
                            }
                            getView().setTipText(mContractInfo.getContract_type());
                            String createdAt = mContractInfo.getCreatedAt();
                            if (TextUtils.isEmpty(createdAt)) {
                                createdAt = "-";
                            } else {
                                try {
                                    String[] ts = createdAt.split("T");
                                    createdAt = ts[0].replaceAll("-", ".");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    createdAt = "-";
                                }
                            }
                            getView().setContractCreateTime(createdAt);

                            String confirmTime = mContractInfo.getConfirmTime();
                            if (TextUtils.isEmpty(confirmTime)) {
                                confirmTime = "-";
                            } else {
                                try {
                                    String[] ts = confirmTime.split("T");
                                    confirmTime = ts[0].replaceAll("-", ".");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    confirmTime = "-";
                                }
                            }
                            getView().setSignTime(confirmTime);

                            getView().updateContractTemplateAdapterInfo(mContractInfo.getDevices());
                        } else {
                            getView().toastShort(mActivity.getString(R.string.not_obtain_contract_info));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA__CONTRACT_EDIT_REFRESH_CODE:
                requestContractInfo((int) data);
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doEditContract() {
        Intent intent = new Intent(mActivity, ContractEditorActivity.class);
        intent.putExtra(Constants.EXTRA_CONTRACT_INFO, mContractInfo);
        intent.putExtra(Constants.EXTRA_CONTRACT_ORIGIN_TYPE, 2);
        getView().startAC(intent);
    }

    public void doViewContractQrCode() {
        int id = mContractInfo.getId();
        if (id == 0) {
            getView().toastShort(mActivity.getString(R.string.contract_id_failed));
            return;
        }
        Intent intent = new Intent();
        final String code = Constants.CONTRACT_WE_CHAT_BASE_URL + id;
        intent.putExtra(Constants.EXTRA_CONTRACT_ID_QRCODE, code);
        intent.setClass(mActivity, ContractResultActivity.class);
        getView().startAC(intent);
    }

    public void doPreviewActivity() {

        if (TextUtils.isEmpty(mContractInfo.getFdd_viewpdf_url())) {
            getView().toastShort(mActivity.getString(R.string.preview_contract_failed));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL, mContractInfo.getFdd_viewpdf_url());
        intent.setClass(mActivity, ContractPreviewActivity.class);
        getView().startAC(intent);
    }
}
