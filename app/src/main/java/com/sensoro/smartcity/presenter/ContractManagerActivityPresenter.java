package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.activity.ContractDetailActivity;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.imainviews.IContractManagerActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContractManagerActivityPresenter extends BasePresenter<IContractManagerActivityView> implements IOnCreate
        , CalendarPopUtils.OnCalendarPopupCallbackListener {
    private Activity mContext;
    private final List<ContractListInfo> dataList = new ArrayList<>();
    private Integer requestDataType = null;
    private volatile int cur_page = 0;
    List<InspectionStatusCountModel> mSelectStatuslist = new ArrayList<>();
    List<InspectionStatusCountModel> mSelectTypelist = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private Integer requestDataConfirmed = null;
    private String tempSearch;
    private long startTime;
    private long endTime;
    private CalendarPopUtils mCalendarPopUtils;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        initSelectData();
        initSearchHistoryData();
        initCalendarPop();
        getView().setContractMangerAddVisible(PreferencesHelper.getInstance().getUserData().hasContractCreate);
        requestDataByDirection(Constants.DIRECTION_DOWN, true);

    }

    private void initCalendarPop() {
        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils.setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);
    }

    private void initSearchHistoryData() {
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().UpdateSearchHistoryList(mSearchHistoryList);
        }
    }

    private void initSelectData() {
        InspectionStatusCountModel im1 = new InspectionStatusCountModel();
        im1.statusTitle = "全部状态";
        im1.count = -1;
        im1.status = 0;
        mSelectStatuslist.add(im1);
        InspectionStatusCountModel im2 = new InspectionStatusCountModel();
        im2.statusTitle = "未签订";
        im2.count = -1;
        im2.status = 1;
        mSelectStatuslist.add(im2);
        InspectionStatusCountModel im3 = new InspectionStatusCountModel();
        im3.statusTitle = "已签订";
        im3.count = -1;
        im3.status = 2;
        mSelectStatuslist.add(im3);

        InspectionStatusCountModel im4 = new InspectionStatusCountModel();
        im4.statusTitle = "全部合同";
        im4.count = -1;
        im4.status = 0;
        mSelectTypelist.add(im4);
        InspectionStatusCountModel im5 = new InspectionStatusCountModel();
        im5.statusTitle = "企业合同";
        im5.count = -1;
        im5.status = 1;
        mSelectTypelist.add(im5);
        InspectionStatusCountModel im6 = new InspectionStatusCountModel();
        im6.statusTitle = "个人合同";
        im6.count = -1;
        im6.status = 2;
        mSelectTypelist.add(im6);
    }

    public void startToAdd() {
        Intent intent = new Intent(mContext, ContractEditorActivity.class);
        intent.putExtra(Constants.EXTRA_CONTRACT_ORIGIN_TYPE, 1);
        getView().startAC(intent);
    }

    public void requestDataByDirection(int direction, boolean isFirst) {
        if (isFirst) {
            requestDataType = null;
            requestDataConfirmed = null;
            tempSearch = null;
        }
        refreshData(direction);
    }

    private void refreshData(int direction) {
        Long temp_startTime = null;
        Long temp_endTime = null;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 0;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().searchContract(requestDataType, tempSearch, requestDataConfirmed, temp_startTime, temp_endTime, null, null).subscribeOn
                        (Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<ContractListInfo>>>(this) {

                    @Override
                    public void onCompleted(ResponseResult<List<ContractListInfo>> contractsListRsp) {
                        getView().dismissProgressDialog();
                        dataList.clear();
                        List<ContractListInfo> data = contractsListRsp.getData();
                        dataList.addAll(data);
                        if (dataList.size() > 0) {
                            getView().smoothScrollToPosition(0);
                            getView().closeRefreshHeaderOrFooter();
                        }
                        if (!TextUtils.isEmpty(tempSearch)) {
                            getView().setSearchButtonTextVisible(true);
                        } else {
                            getView().setSearchButtonTextVisible(false);
                        }
                        getView().updateContractList(dataList);
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                int offset = cur_page * 20;
                RetrofitServiceHelper.getInstance().searchContract(requestDataType, tempSearch, requestDataConfirmed, temp_startTime, temp_endTime, null, offset).subscribeOn
                        (Schedulers
                                .io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<ContractListInfo>>>(this) {

                    @Override
                    public void onCompleted(ResponseResult<List<ContractListInfo>> contractsListRsp) {
                        getView().dismissProgressDialog();
                        List<ContractListInfo> data = contractsListRsp.getData();
                        if (data.size() == 0) {
                            getView().toastShort("没有更多数据了");
//                            getView().showSmartRefreshNoMoreData();
                            cur_page--;
                        } else {
                            dataList.addAll(data);
                            getView().updateContractList(dataList);
                            if (!TextUtils.isEmpty(tempSearch)) {
                                getView().setSearchButtonTextVisible(true);
                            } else {
                                getView().setSearchButtonTextVisible(false);
                            }
                        }

                        getView().onPullRefreshComplete();
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
        ContractListInfo contractListInfo = dataList.get(position);
        Intent intent = new Intent();
        intent.setClass(mContext, ContractDetailActivity.class);
        intent.putExtra(Constants.EXTRA_CONTRACT_ID, contractListInfo.getId());
        getView().startAC(intent);
//
//        int created_type = contractListInfo.getCreated_type();
//        Intent intent = new Intent();
//        intent.setClass(mContext, ContractInfoActivity.class);
//        intent.putExtra(EXTRA_CONTRACT_ID, contractListInfo.getId());
//        //
//        intent.putExtra(EXTRA_CONTRACT_TYPE, created_type);
//        //
//        String place_type = contractListInfo.getPlace_type();
//        if (TextUtils.isEmpty(place_type)) {
//            place_type = "无";
//        }
//        intent.putExtra("place", place_type);
//        //
//        int serviceTime = contractListInfo.getServiceTime();
//        intent.putExtra("contract_service_life", serviceTime + "");
//
//        int serviceTimeFirst = contractListInfo.getFirstPayTimes();
//        intent.putExtra("contract_service_life_first", serviceTimeFirst + "");
//
//        int serviceTimePeriod = contractListInfo.getPayTimes();
//        intent.putExtra("contract_service_life_period", serviceTimePeriod + "");
//        //
//        String createdAt = contractListInfo.getCreatedAt();
//        if (TextUtils.isEmpty(createdAt)) {
//            createdAt = "无";
//        } else {
//            try {
//                String[] ts = createdAt.split("T");
//                createdAt = ts[0].replaceAll("-",".");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        intent.putExtra("signDate", createdAt);
//        //
//        List<ContractsTemplateInfo> devices = contractListInfo.getDevices();
//        if (devices != null) {
//            ArrayList<ContractsTemplateInfo> dataList = (ArrayList<ContractsTemplateInfo>) devices;
//            intent.putExtra("contract_template", dataList);
//        }
//        int id = contractListInfo.getId();
//        intent.putExtra("id", id);
//        switch (created_type) {
//            case 1:
//                String customer_name = contractListInfo.getCustomer_name();
//                if (TextUtils.isEmpty(customer_name)) {
//                    customer_name = "无";
//                }
//                intent.putExtra("line1", customer_name);
//                //
//                String phone = contractListInfo.getCustomer_phone();
//                if (TextUtils.isEmpty(phone)) {
//                    phone = "无";
//                }
//                intent.putExtra("phone", phone);
//                //
//                String customer_enterprise_name = contractListInfo.getCustomer_enterprise_name();
//                if (TextUtils.isEmpty(customer_enterprise_name)) {
//                    customer_enterprise_name = "无";
//                }
//                intent.putExtra("line2", customer_enterprise_name);
//                //
//                String enterprise_card_id = contractListInfo.getEnterprise_card_id();
//                if (TextUtils.isEmpty(enterprise_card_id)) {
//                    enterprise_card_id = "无";
//                }
//                intent.putExtra("line3", enterprise_card_id);
//                //
//                String enterprise_register_id = contractListInfo.getEnterprise_register_id();
//                if (TextUtils.isEmpty(enterprise_register_id)) {
//                    enterprise_register_id = "无";
//                }
//                intent.putExtra("line4", enterprise_register_id);
//                //
//                String customer_address = contractListInfo.getCustomer_address();
//                if (TextUtils.isEmpty(customer_address)) {
//                    customer_address = "无";
//                }
//                intent.putExtra("line5", customer_address);
//                //
//                String customer_enterprise_validity = contractListInfo.getCustomer_enterprise_validity();
//                if (TextUtils.isEmpty(customer_enterprise_validity)) {
//                    customer_enterprise_validity = "无";
//                }
//                intent.putExtra("line6", customer_enterprise_validity);
//                //
//                break;
//            case 2:
//                String customer_name1 = contractListInfo.getCustomer_name();
//                if (TextUtils.isEmpty(customer_name1)) {
//                    customer_name1 = "无";
//                }
//                intent.putExtra("line1", customer_name1);
//                //
//                String phone1 = contractListInfo.getCustomer_phone();
//                if (TextUtils.isEmpty(phone1)) {
//                    phone1 = "无";
//                }
//                intent.putExtra("phone", phone1);
//                //
//                int sex = contractListInfo.getSex();
//                switch (sex) {
//                    case 1:
//                        intent.putExtra("line2", "男");
//                        break;
//                    case 2:
//                        intent.putExtra("line2", "女");
//                        break;
//                    default:
//                        intent.putExtra("line2", "无");
//                        break;
//                }
//                //
//                String card_id = contractListInfo.getCard_id();
//                if (TextUtils.isEmpty(card_id)) {
//                    card_id = "无";
//                }
//                intent.putExtra("line3", card_id);
//                //
//                String address = contractListInfo.getCustomer_address();
//                if (TextUtils.isEmpty(address)) {
//                    address = "无";
//                }
//                intent.putExtra("line4", address);
//                break;
//            case 3:
//                String name = contractListInfo.getCustomer_enterprise_name();
//                if (TextUtils.isEmpty(name)) {
//                    name = "无";
//                }
//                intent.putExtra("line1", name);
//                //
//                String customer_name2 = contractListInfo.getCustomer_name();
//                if (TextUtils.isEmpty(customer_name2)) {
//                    customer_name2 = "无";
//                }
//                intent.putExtra("line2", customer_name2);
//                //
//                String phone2 = contractListInfo.getCustomer_phone();
//                if (TextUtils.isEmpty(phone2)) {
//                    phone2 = "无";
//                }
//                intent.putExtra("line3", phone2);
//                //
//                String cardId = contractListInfo.getCard_id();
//                if (TextUtils.isEmpty(cardId)) {
//                    cardId = "无";
//                }
//                intent.putExtra("line4", cardId);
//                String customer_address1 = contractListInfo.getCustomer_address();
//                if (TextUtils.isEmpty(customer_address1)) {
//                    customer_address1 = "无";
//                }
//                intent.putExtra("line5", customer_address1);
//                break;
//            default:
//                break;
//        }
//        getView().startAC(intent);
    }

    public void requestContractDataAll() {
        requestDataType = null;
        refreshData(Constants.DIRECTION_DOWN);
    }

    public void requestContractDataBusiness() {
        requestDataType = 1;
        refreshData(Constants.DIRECTION_DOWN);
    }

    public void requestContractDataPerson() {
        requestDataType = 2;
        refreshData(Constants.DIRECTION_DOWN);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        dataList.clear();
    }

    public void doSelectTypePop() {
        getView().UpdateSelectTypePopList(mSelectTypelist);
        getView().showSelectStTypePop();
    }

    public void doSelectStatusPop() {
        getView().UpdateSelectStatusPopList(mSelectStatuslist);
        getView().showSelectStatusPop();
    }

    public void doSelectTypeDevice(InspectionStatusCountModel item) {
        switch (item.status) {
            case 0:
                requestDataType = null;
                break;
            case 1:
            case 2:
                requestDataType = item.status;
                break;
        }
        refreshData(Constants.DIRECTION_DOWN);
    }

    public void doSelectStatusDevice(InspectionStatusCountModel item) {
        switch (item.status) {
            case 0:
                requestDataConfirmed = null;
                break;
            case 1:
            case 2:
                requestDataConfirmed = item.status;
                break;
        }
        refreshData(Constants.DIRECTION_DOWN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_FINISH_CODE:
                if (data instanceof Boolean) {
                    boolean needFinish = (boolean) data;
                    if (needFinish) {
                        requestDataByDirection(Constants.DIRECTION_DOWN, false);
                    }
                }
                break;

            case Constants.EVENT_DATA__CONTRACT_EDIT_REFRESH_CODE:
            case Constants.EVENT_DATA_CONTRACT_CREATION_SUCCESS:
                refreshData(Constants.DIRECTION_DOWN);
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void requestSearchData(int direction, String text) {
        tempSearch = text;
        refreshData(direction);
    }

    public void doCancelSearch() {
        tempSearch = null;
        refreshData(Constants.DIRECTION_DOWN);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT);
//        mSearchHistoryList.remove(text);
//        mSearchHistoryList.add(0, text);
        List<String> contractList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(contractList);
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }

    public void doCalendar(LinearLayout fgMainWarnTitleRoot) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }

        mCalendarPopUtils.show(fgMainWarnTitleRoot, temp_startTime, temp_endTime);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        requestDataByDate(calendarDateModel.startDate, calendarDateModel.endDate);
        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearch)) {
//            PreferencesHelper.getInstance().saveSearchHistoryText(tempSearch, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT);
//            mSearchHistoryList.remove(tempSearch);
//            mSearchHistoryList.add(0, tempSearch);
//            getView().UpdateSearchHistoryList(mSearchHistoryList);
            save(tempSearch);

        }
    }

    private void requestDataByDate(String startDate, String endDate) {
        getView().setSelectedDateLayoutVisible(true);
        startTime = DateUtil.strToDate(startDate).getTime();
        endTime = DateUtil.strToDate(endDate).getTime();
        getView().setSelectedDateSearchText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        refreshData(Constants.DIRECTION_DOWN);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT);
        mSearchHistoryList.clear();
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }
}
