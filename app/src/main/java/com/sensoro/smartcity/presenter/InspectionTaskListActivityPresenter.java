package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskListActivityView;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.response.InspectionTaskModelRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InspectionTaskListActivityPresenter extends BasePresenter<IInspectionTaskListActivityView>
        implements Constants, IOnCreate {
    private Context mContext;
    private int cur_page;
    private List<InspectionIndexTaskInfo> tempTasks = new ArrayList<>();
    private Long tempStartTime;
    private Long tempFinishTime;
    private Integer tempFinish = 0;

    @Override
    public void initData(Context context) {
        mContext = context;
        onCreate();
        refreshData(DIRECTION_DOWN);
    }

    public void refreshData(int direction) {
        getView().showProgressDialog();
        if (direction == DIRECTION_DOWN) {
            cur_page = 0;
            RetrofitServiceHelper.getInstance().getInspectTaskList(null, tempFinish, 0, 20, tempStartTime, tempFinishTime).subscribeOn(Schedulers
                    .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskModelRsp>(this) {
                @Override
                public void onCompleted(InspectionTaskModelRsp inspectionTaskModel) {
                    tempTasks.clear();
                    List<InspectionIndexTaskInfo> tasks = inspectionTaskModel.getData().getTasks();
                    tempTasks.addAll(tasks);
                    getView().dismissProgressDialog();
                    getView().updateRcContent(tempTasks);
                    getView().onPullRefreshCompleted();
                    if (tasks.size() > 0) {
                        getView().rcSmoothScrollToTop();
                        getView().closeRefreshHeaderOrFooter();
                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    getView().onPullRefreshCompleted();
                    getView().dismissProgressDialog();
                }
            });
        } else {
            cur_page++;
            RetrofitServiceHelper.getInstance().getInspectTaskList(null, tempFinish, cur_page * 20, 20, tempStartTime, tempFinishTime).subscribeOn(Schedulers
                    .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskModelRsp>(this) {
                @Override
                public void onCompleted(InspectionTaskModelRsp inspectionTaskModel) {
                    List<InspectionIndexTaskInfo> tasks = inspectionTaskModel.getData().getTasks();
                    if (tasks.size() > 0) {
                        tempTasks.addAll(tasks);
                        getView().updateRcContent(tempTasks);
                    } else {
                        getView().recycleViewRefreshCompleteNoMoreData();
                        getView().toastShort(mContext.getString(R.string.no_more_data));
                    }
                    getView().onPullRefreshCompleted();
                    getView().dismissProgressDialog();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    cur_page--;
                    getView().toastShort(errorMsg);
                    getView().onPullRefreshCompleted();
                    getView().dismissProgressDialog();
                }
            });
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        switch (code) {
                //刷新上报异常结果
            case EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE:
                //正常上报结果
            case EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE:
                //设备更换结果
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                //巡检任务状态改变
            case EVENT_DATA_INSPECTION_TASK_STATUS_CHANGE:
                refreshData(DIRECTION_DOWN);
                break;
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().finishAc();
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doItemClick(InspectionIndexTaskInfo task) {

        Intent intent = new Intent(mContext, InspectionTaskDetailActivity.class);
        intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO, task);
        getView().startAC(intent);
    }

    public void doUndone() {
        tempFinish = 0;
        tempStartTime = null;
        tempFinishTime = null;
        refreshData(DIRECTION_DOWN);
    }

    public void doDone() {
        tempFinish = 1;
        tempStartTime = null;
        tempFinishTime = null;
        refreshData(DIRECTION_DOWN);
    }

    public void requestDataByDate(long startTime, long endTime) {
        tempStartTime = startTime;
        tempFinishTime = endTime;
        tempFinish = 1;
        refreshData(DIRECTION_DOWN);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
