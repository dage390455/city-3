package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskListActivityView;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.InspectionTaskModelRsp;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionTaskListActivityPresenter extends BasePresenter<IInspectionTaskListActivityView>
implements Constants{
    private Context mContext;
    private int cur_page;
    private List<InspectionIndexTaskInfo> tempTasks = new ArrayList<>();
    private Long tempStartTime;
    private Long tempFinishTime;
    private Integer tempFinish;

    @Override
    public void initData(Context context) {
        mContext = context;
        refreshData(DIRECTION_DOWN, 0,null,null);

    }

    private void refreshData(int direction, Integer finish,Long startTime,Long finishTime) {
        getView().showProgressDialog();
        tempStartTime = startTime;
        tempFinishTime = finishTime;
        tempFinish = finish;
        Log.e("hcs","::direction:"+direction+"  finish"+finish+"  startTime"+startTime+"  finishTime"+finishTime);
        if(direction == DIRECTION_DOWN){
            cur_page = 0;
            RetrofitServiceHelper.INSTANCE.getInspectTaskList(null, finish, 0, 20, tempStartTime, tempFinishTime).subscribeOn(Schedulers
                    .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskModelRsp>() {
                @Override
                public void onCompleted(InspectionTaskModelRsp inspectionTaskModel) {
                    tempTasks.clear();
                    List<InspectionIndexTaskInfo> tasks = inspectionTaskModel.getData().getTasks();
                    if(tasks.size()>0){
                        getView().updateRcContent(tasks);
                    }else{
                        getView().toastShort("没有更多数据了");
                    }
                    getView().onPullRefreshCompleted();
                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    tempTasks.clear();
                    getView().toastShort(errorMsg);
                    getView().onPullRefreshCompleted();
                    getView().dismissProgressDialog();
                }
            });
        }else{
            cur_page++;
            RetrofitServiceHelper.INSTANCE.getInspectTaskList(null, finish, cur_page*20, 20, tempFinishTime, tempFinishTime).subscribeOn(Schedulers
                    .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskModelRsp>() {
                @Override
                public void onCompleted(InspectionTaskModelRsp inspectionTaskModel) {
                    List<InspectionIndexTaskInfo> tasks = inspectionTaskModel.getData().getTasks();
                    if(tasks.size()>0){
                        tempTasks.addAll(tasks);
                        getView().updateRcContent(tempTasks);

                    }else{
                        getView().recycleViewRefreshCompleteNoMoreData();
                        getView().toastShort("没有更多数据了");
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

    @Override
    public void onDestroy() {

    }

    public void doItemClick(InspectionIndexTaskInfo task) {

        Intent intent = new Intent(mContext, InspectionTaskDetailActivity.class);
        intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO,task);
        getView().startAC(intent);
    }

    public void doUndone() {
        refreshData(DIRECTION_DOWN, 0,null,null);
    }

    public void doDone() {
        refreshData(DIRECTION_DOWN,1,null,null);
    }

    public void requestDataByDate(long startTime, long endTime) {
        refreshData(DIRECTION_DOWN,1,startTime,endTime);
    }

    public void LoadMore(int directionDown) {
        refreshData(directionDown,tempFinish,tempStartTime, tempFinishTime);
    }
}
