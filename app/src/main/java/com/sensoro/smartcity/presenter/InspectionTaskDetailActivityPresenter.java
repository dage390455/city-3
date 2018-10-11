package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.InspectionInstructionActivity;
import com.sensoro.smartcity.activity.InspectionTaskActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionTaskDetailActivityPresenter extends BasePresenter<IInspectionTaskDetailActivityView>
        implements Constants, IOnCreate {
    private Activity mContext;
    private InspectionIndexTaskInfo mTaskInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        getView().setTvTaskNumber(mTaskInfo.getIdentifier());
        getView().setTvTaskTime(DateUtil.getDateByOtherFormat(mTaskInfo.getBeginTime()) + " - " + DateUtil.getDateByOtherFormat(mTaskInfo.getEndTime()));

        initTvState();

        initDeviceTag();
    }

    private void initDeviceTag() {
        ArrayList<String> tags = new ArrayList<>();

        List<InspectionIndexTaskInfo.DeviceSummaryBean> deviceSummary = mTaskInfo.getDeviceSummary();
        for (InspectionIndexTaskInfo.DeviceSummaryBean deviceSummaryBean : deviceSummary) {
            String deviceType = deviceSummaryBean.getDeviceType();
            String inspectionDeviceName = WidgetUtil.getInspectionDeviceName(deviceType);
            tags.add(inspectionDeviceName + " （" + deviceSummaryBean.getNum() + "） ");
        }
        getView().updateTagsData(tags);
    }

    private void initTvState() {
        switch (mTaskInfo.getStatus()) {
            case 0:
                getView().setTvbtnStartState(R.drawable.shape_bg_corner_29c_shadow, R.color.white, "开始巡检");
                break;
            case 1:
                getView().setTvbtnStartState(R.drawable.shape_bg_corner_29c_shadow, R.color.white, "继续巡检");
                break;
            case 2:
                getView().setTvbtnStartState(R.drawable.shape_bg_corner_29c_shadow, R.color.white, "继续巡检");
                break;
            case 3:
                getView().setTvbtnStartState(R.drawable.shape_bg_solid_ff_corner, R.color.c_252525, "详情");
                break;
            case 4:
                getView().setTvbtnStartState(R.drawable.shape_bg_solid_ff_corner, R.color.c_252525, "详情");
                break;
        }
        getView().setTvState(INSPECTION_STATUS_COLORS[mTaskInfo.getStatus()], INSPECTION_STATUS_TEXTS[mTaskInfo.getStatus()]);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        }
    }

    public void doRlContent() {
        Intent intent = new Intent(mContext, InspectionInstructionActivity.class);
        List<InspectionIndexTaskInfo.DeviceSummaryBean> deviceSummary = mTaskInfo.getDeviceSummary();
        ArrayList<String> deviceTypes = new ArrayList<>();
        for (InspectionIndexTaskInfo.DeviceSummaryBean deviceSummaryBean : deviceSummary) {
            deviceTypes.add(deviceSummaryBean.getDeviceType());
        }
        intent.putExtra(Constants.EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE, deviceTypes);
        getView().startAC(intent);
    }

    public void doBtnStart() {
        //TODO 检查巡检
        if (mTaskInfo.getStatus() == 0) {
            changeTaskState();
        } else {
            Intent intent = new Intent(mContext, InspectionTaskActivity.class);
            intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO, mTaskInfo);
            getView().startAC(intent);
        }
    }

    private void changeTaskState() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doChangeInspectionTaskState(mTaskInfo.getId(), null, 1).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
            @Override
            public void onCompleted(ResponseBase responseBase) {
                getView().dismissProgressDialog();
                Intent intent = new Intent(mContext, InspectionTaskActivity.class);
                intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO, mTaskInfo);
                getView().startAC(intent);
                getView().setTvbtnStartState(R.drawable.shape_bg_corner_29c_shadow, R.color.white, "继续巡检");
                getView().setTvState(R.color.c_3aa7f0,"执行中");
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_INSPECTION_TASK_STATUS_CHANGE;
                EventBus.getDefault().post(eventData);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);

            }
        });
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
