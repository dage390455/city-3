package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.adapter.model.SecurityRisksTagModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISecurityRiskTagManagerView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

public class SecurityRiskTagManagerPresenter extends BasePresenter<ISecurityRiskTagManagerView> {
    private Activity mActivity;
    private ArrayList<SecurityRisksTagModel> mLocationTagList;
    private ArrayList<SecurityRisksTagModel> mBehaviorTagList;
    private ArrayList<String> mBehaviorData;
    private ArrayList<String> mLocationData;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mLocationTagList = PreferencesHelper.getInstance().getSecurityRiskLocationTags(mActivity);
        mBehaviorTagList = PreferencesHelper.getInstance().getSecurityRiskBehaviorTags(mActivity);

        mLocationData = new ArrayList<>();
        for (SecurityRisksTagModel model : mLocationTagList) {
            mLocationData.add(model.tag);
        }

        mBehaviorData = new ArrayList<>();
        for (SecurityRisksTagModel model : mBehaviorTagList) {
            mBehaviorData.add(model.tag);
        }


        getView().updateLocationAdapter(mLocationData);
        getView().updateBehaviorAdapter(mBehaviorData);

    }

    @Override
    public void onDestroy() {

    }

    public void doLocationTagDel(String tag) {
        mLocationData.remove(tag);
        getView().updateLocationAdapter(mLocationData);
    }

    public void doBehaviorTagDel(String tag) {
        mBehaviorData.remove(tag);
        getView().updateBehaviorAdapter(mBehaviorData);
    }

    public void doSave() {
        Iterator<SecurityRisksTagModel> iterator = mLocationTagList.iterator();
        while (iterator.hasNext()){
            SecurityRisksTagModel next = iterator.next();
            if (!mLocationData.contains(next.tag)) {
                iterator.remove();
            }
        }

        PreferencesHelper.getInstance().saveSecurityRiskLocationTag(mLocationTagList);
        Iterator<SecurityRisksTagModel> iterator1 = mBehaviorTagList.iterator();
        while (iterator.hasNext()) {
            SecurityRisksTagModel next = iterator.next();
            if (!mBehaviorTagList.contains(next.tag)) {
                iterator1.remove();
            }
        }
        PreferencesHelper.getInstance().saveSecurityRiskBehaviorTag(mBehaviorTagList);
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_SECURITY_RISK_TAG_MANAGER;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
