package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;
import com.sensoro.smartcity.adapter.SecurityRisksReferTagAdapter;
import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.adapter.model.SecurityRisksTagModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.dialog.TagDialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SecurityRisksPresenter extends BasePresenter<ISecurityRisksActivityView> implements SecurityRisksContentAdapter.SecurityRisksContentClickListener
        , SecurityRisksReferTagAdapter.OnTagClickListener, TagDialogUtils.OnTagDialogListener {
    private Activity mActivity;
    private ArrayList<SecurityRisksAdapterModel> securityRisksList = new ArrayList<>(5);
    private ArrayList<SecurityRisksTagModel> locationTagList = new ArrayList<>();
    private ArrayList<SecurityRisksTagModel> behaviorTagList = new ArrayList<>();
    private int mAdapterPosition;
    private boolean mAddTagTypeIsLocation;


    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
        securityRisksList.add(model);
        getView().updateSecurityRisksContent(securityRisksList);

        locationTagList = PreferencesHelper.getInstance().getSecurityRiskLocationTags(mActivity);
        behaviorTagList = PreferencesHelper.getInstance().getSecurityRiskBehaviorTags(mActivity);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData){
        if (eventData.code == Constants.EVENT_DATA_SECURITY_RISK_TAG_MANAGER) {
            locationTagList = PreferencesHelper.getInstance().getSecurityRiskLocationTags(mActivity);
            behaviorTagList = PreferencesHelper.getInstance().getSecurityRiskBehaviorTags(mActivity);

            if (getView().getIsLocation()) {
                getView().updateSecurityRisksTag(locationTagList,true);
            }else{
                getView().updateSecurityRisksTag(behaviorTagList,false);
            }

            for (SecurityRisksAdapterModel model : securityRisksList) {
                if (!locationTagList.contains(model.location)) {
                    model.location = "";
                }

                for (String behavior : model.behaviors) {
                    if (!behaviorTagList.contains(behavior)) {
                        model.behaviors.remove(behavior);
                    }
                }
            }

            getView().updateSecurityRisksContent(securityRisksList);
        }
    }

    public void doAddSecurityRisk() {
        SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
        securityRisksList.add(model);
        getView().updateSecurityRisksContent(securityRisksList);
    }

    @Override
    public void onLocationClick(int position) {
        mAdapterPosition = position;
        getView().setConstraintTagVisible(true);
//        getView().changLocationOrBehaviorColor(position, true);
        getView().rvContentScrollBottom(position);
        SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
        for (SecurityRisksTagModel securityRisksTagModel : locationTagList) {
            securityRisksTagModel.isCheck = securityRisksTagModel.tag.equals(model.location);
        }
        getView().updateSecurityRisksTag(locationTagList, true);
        getView().setTvName(mActivity.getString(R.string.refer_loaction));
    }

    @Override
    public void onBehaviorClick(int position) {
        mAdapterPosition = position;
        getView().setConstraintTagVisible(true);
        getView().rvContentScrollBottom(position);
        SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
        for (SecurityRisksTagModel securityRisksTagModel : behaviorTagList) {
            securityRisksTagModel.isCheck = model.behaviors.contains(securityRisksTagModel.tag);
        }
        getView().updateSecurityRisksTag(behaviorTagList, false);
        getView().setTvName(mActivity.getString(R.string.refer_behavior));
    }

    @Override
    public void onAddItemClick() {
        doAddSecurityRisk();
    }

    @Override
    public void onLocationDel(String tag, Integer position) {
        mAdapterPosition = position;
        getView().changLocationOrBehaviorColor(position, true);
        SecurityRisksAdapterModel model = securityRisksList.get(position);
        model.location = "";
        for (SecurityRisksTagModel securityRisksTagModel : locationTagList) {
            securityRisksTagModel.isCheck = false;
        }
        getView().updateSecurityRisksTag(locationTagList, true);
        getView().updateSecurityRisksContent(securityRisksList);
        getView().setTvName(mActivity.getString(R.string.refer_loaction));

    }

    @Override
    public void onBehaviorDel(String tag, int position) {
        mAdapterPosition = position;
        getView().changLocationOrBehaviorColor(position, false);
        SecurityRisksAdapterModel model = securityRisksList.get(position);
        model.behaviors.remove(tag);
        for (SecurityRisksTagModel securityRisksTagModel : behaviorTagList) {
            securityRisksTagModel.isCheck = model.behaviors.contains(securityRisksTagModel.tag);
        }
        getView().updateSecurityRisksTag(behaviorTagList, false);
        getView().updateSecurityRisksContent(securityRisksList);
        getView().setTvName(mActivity.getString(R.string.refer_behavior));
    }

    @Override
    public void onItemDel(int position) {
        securityRisksList.remove(position);
        getView().updateSecurityRisksContent(securityRisksList);
    }


    @Override
    public void onTagClick(SecurityRisksTagModel tagModel, boolean isLocation, Integer position) {

        if (isLocation) {
            SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
            if (tagModel.isCheck) {
                model.location = tagModel.tag;
            } else {
                model.location = "";
            }
            getView().updateSecurityRisksContent(securityRisksList);
        } else {
            SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
            if (tagModel.isCheck) {
                model.behaviors.add(tagModel.tag);
            } else {
                model.behaviors.remove(tagModel.tag);
            }
            getView().updateSecurityRisksContent(securityRisksList);

        }

    }

    @Override
    public void onAddTag(boolean mIsLocation) {
        mAddTagTypeIsLocation = mIsLocation;
        getView().showAddTagDialog(mIsLocation);
    }

    @Override
    public void onConfirm(int type, String text, int position) {
        if (TextUtils.isEmpty(text)) {
            getView().toastShort(mActivity.getString(R.string.input_not_null));
            return;
        }
        byte[] bytes = text.getBytes();
        if (bytes.length > 30) {
            getView().toastShort(mActivity.getString(R.string.the_maximum_length_of_the_label));
            return;
        }
        if (mAddTagTypeIsLocation) {
            SecurityRisksTagModel model = new SecurityRisksTagModel();
            model.tag = text;
            locationTagList.add(model);
            PreferencesHelper.getInstance().saveSecurityRiskLocationTag(locationTagList);
            getView().updateSecurityRisksTag(locationTagList,true);
        }else{
            SecurityRisksTagModel model = new SecurityRisksTagModel();
            model.tag = text;
            behaviorTagList.add(model);
            PreferencesHelper.getInstance().saveSecurityRiskBehaviorTag(behaviorTagList);
            getView().updateSecurityRisksTag(behaviorTagList,false);
        }
        getView().dismissTagDialog();
    }

    public void doSave() {
        for (SecurityRisksAdapterModel model : securityRisksList) {
            if (TextUtils.isEmpty(model.location) || model.behaviors.size() < 1) {
                getView().toastShort(mActivity.getString(R.string.please_input_all_save));
                return;
            }
        }

        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_SECURITY_RISK_TAG;
        eventData.data = securityRisksList;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
