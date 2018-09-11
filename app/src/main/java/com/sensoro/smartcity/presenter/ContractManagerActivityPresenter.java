package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.ContractIndexActivity;
import com.sensoro.smartcity.activity.ContractInfoActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractManagerActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ContractListInfo;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractsListRsp;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContractManagerActivityPresenter extends BasePresenter<IContractManagerActivityView> implements Constants {
    private Activity mContext;
    private final List<ContractListInfo> dataList = new ArrayList<>();
    private Integer requestDataType = null;
    private volatile int cur_page = 0;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        requestDataByDirection(DIRECTION_DOWN, true);
    }

    public void startToAdd() {
        Intent intent = new Intent(mContext, ContractIndexActivity.class);
        getView().startAC(intent);
    }

    public void requestDataByDirection(int direction, boolean isFirst) {
        if (isFirst) {
            requestDataType = null;
        }
        refreshData(direction);
    }

    private void refreshData(int direction) {
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 0;
                dataList.clear();
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.searchContract(requestDataType, null, null, null, null).subscribeOn
                        (Schedulers
                                .io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsListRsp>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(ContractsListRsp contractsListRsp) {
                        List<ContractListInfo> data = contractsListRsp.getData();
                        dataList.addAll(data);
                        getView().updateContractList(dataList);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                int offset = cur_page * 20;
                RetrofitServiceHelper.INSTANCE.searchContract(requestDataType, null, null, null, offset).subscribeOn
                        (Schedulers
                                .io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ContractsListRsp>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(ContractsListRsp contractsListRsp) {
                        List<ContractListInfo> data = contractsListRsp.getData();
                        if (data.size() == 0) {
                            getView().toastShort("没有更多数据了");
                            cur_page--;
                        } else {
                            dataList.addAll(data);
                            getView().updateContractList(dataList);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            default:
                break;
        }

    }

    public void clickItem(int position) {
        ContractListInfo contractListInfo = dataList.get(position - 1);
        int created_type = contractListInfo.getCreated_type();
        Intent intent = new Intent();
        intent.setClass(mContext, ContractInfoActivity.class);
        //
        intent.putExtra(EXTRA_CONTRACT_TYPE, created_type);
        //
        String place_type = contractListInfo.getPlace_type();
        if (TextUtils.isEmpty(place_type)) {
            place_type = "无";
        }
        intent.putExtra("place", place_type);
        //
        int serviceTime = contractListInfo.getServiceTime();
        intent.putExtra("contract_service_life", serviceTime + "");
        //
        String createdAt = contractListInfo.getCreatedAt();
        if (TextUtils.isEmpty(createdAt)) {
            createdAt = "无";
        } else {
            try {
                String[] ts = createdAt.split("T");
                createdAt = ts[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        intent.putExtra("signDate", createdAt);
        //
        List<ContractsTemplateInfo> devices = contractListInfo.getDevices();
        if (devices != null) {
            ArrayList<ContractsTemplateInfo> dataList = (ArrayList<ContractsTemplateInfo>) devices;
            intent.putExtra("contract_template", dataList);
        }
        int id = contractListInfo.getId();
        intent.putExtra("id", id);
        switch (created_type) {
            case 1:
                String customer_name = contractListInfo.getCustomer_name();
                if (TextUtils.isEmpty(customer_name)) {
                    customer_name = "无";
                }
                intent.putExtra("line1", customer_name);
                //
                String phone = contractListInfo.getCustomer_phone();
                if (TextUtils.isEmpty(phone)) {
                    phone = "无";
                }
                intent.putExtra("phone", phone);
                //
                String customer_enterprise_name = contractListInfo.getCustomer_enterprise_name();
                if (TextUtils.isEmpty(customer_enterprise_name)) {
                    customer_enterprise_name = "无";
                }
                intent.putExtra("line2", customer_enterprise_name);
                //
                String enterprise_card_id = contractListInfo.getEnterprise_card_id();
                if (TextUtils.isEmpty(enterprise_card_id)) {
                    enterprise_card_id = "无";
                }
                intent.putExtra("line3", enterprise_card_id);
                //
                String enterprise_register_id = contractListInfo.getEnterprise_register_id();
                if (TextUtils.isEmpty(enterprise_register_id)) {
                    enterprise_register_id = "无";
                }
                intent.putExtra("line4", enterprise_register_id);
                //
                String customer_address = contractListInfo.getCustomer_address();
                if (TextUtils.isEmpty(customer_address)) {
                    customer_address = "无";
                }
                intent.putExtra("line5", customer_address);
                //
                String customer_enterprise_validity = contractListInfo.getCustomer_enterprise_validity();
                if (TextUtils.isEmpty(customer_enterprise_validity)) {
                    customer_enterprise_validity = "无";
                }
                intent.putExtra("line6", customer_enterprise_validity);
                //
                break;
            case 2:
                String customer_name1 = contractListInfo.getCustomer_name();
                if (TextUtils.isEmpty(customer_name1)) {
                    customer_name1 = "无";
                }
                intent.putExtra("line1", customer_name1);
                //
                String phone1 = contractListInfo.getCustomer_phone();
                if (TextUtils.isEmpty(phone1)) {
                    phone1 = "无";
                }
                intent.putExtra("phone", phone1);
                //
                int sex = contractListInfo.getSex();
                switch (sex) {
                    case 1:
                        intent.putExtra("line2", "男");
                        break;
                    case 2:
                        intent.putExtra("line2", "女");
                        break;
                    default:
                        intent.putExtra("line2", "无");
                        break;
                }
                //
                String card_id = contractListInfo.getCard_id();
                if (TextUtils.isEmpty(card_id)) {
                    card_id = "无";
                }
                intent.putExtra("line3", card_id);
                //
                String address = contractListInfo.getCustomer_address();
                if (TextUtils.isEmpty(address)) {
                    address = "无";
                }
                intent.putExtra("line4", address);
                break;
            case 3:
                String name = contractListInfo.getCustomer_enterprise_name();
                if (TextUtils.isEmpty(name)) {
                    name = "无";
                }
                intent.putExtra("line1", name);
                //
                String customer_name2 = contractListInfo.getCustomer_name();
                if (TextUtils.isEmpty(customer_name2)) {
                    customer_name2 = "无";
                }
                intent.putExtra("line2", customer_name2);
                //
                String phone2 = contractListInfo.getCustomer_phone();
                if (TextUtils.isEmpty(phone2)) {
                    phone2 = "无";
                }
                intent.putExtra("line3", phone2);
                //
                String customer_address1 = contractListInfo.getCustomer_address();
                if (TextUtils.isEmpty(customer_address1)) {
                    customer_address1 = "无";
                }
                intent.putExtra("line4", customer_address1);
                break;
            default:
                break;
        }
        getView().startAC(intent);
    }

    public void requestContractDataAll() {
        requestDataType = null;
        refreshData(DIRECTION_DOWN);
    }

    public void requestContractDataBusiness() {
        requestDataType = 1;
        refreshData(DIRECTION_DOWN);
    }

    public void requestContractDataPerson() {
        requestDataType = 2;
        refreshData(DIRECTION_DOWN);
    }

    @Override
    public void onDestroy() {
        dataList.clear();
    }
}
