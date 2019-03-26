package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;
import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.adapter.model.SecurityRisksTagModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.util.PreferencesHelper;

import java.util.ArrayList;

public class SecurityRisksPresenter extends BasePresenter<ISecurityRisksActivityView> implements SecurityRisksContentAdapter.SecurityRisksContentClickListener{
    private Activity mActivity;
    private ArrayList<SecurityRisksAdapterModel> securityRisksList = new ArrayList<>(5);
    private ArrayList<SecurityRisksTagModel> locationTagList = new ArrayList<>();
    private ArrayList<SecurityRisksTagModel> behaviorTagList = new ArrayList<>();


    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
        securityRisksList.add(model);
        getView().updateSecurityRisksContent(securityRisksList);

        PreferencesHelper.getInstance().getSecurityRiskLocationTags();
    }

    @Override
    public void onDestroy() {

    }

    public void doAddSecurityRisk() {
        SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
        securityRisksList.add(model);
        getView().updateSecurityRisksContent(securityRisksList);
    }

    @Override
    public void onLocationClick(int position) {
        getView().setConstraintTagVisible(true);

    }

    @Override
    public void onBehaviorClick(int position) {
       getView().setConstraintTagVisible(true);
    }

    @Override
    public void onAddItemClick() {
        doAddSecurityRisk();
    }

    @Override
    public void onLocationDel() {

    }
}
