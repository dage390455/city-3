package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.SecurityRisksAdapterModel;
import com.sensoro.common.model.SecurityRisksTagModel;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;
import com.sensoro.smartcity.adapter.SecurityRisksReferTagAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.widget.dialog.TagDialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;

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
        ArrayList<SecurityRisksAdapterModel> list = mActivity.getIntent().getParcelableArrayListExtra(Constants.EXTRA_SECURITY_RISK);
        if (list == null) {
            SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
            securityRisksList.add(model);
            getView().updateSecurityRisksContent(securityRisksList);
        } else {
            securityRisksList = list;
            getView().updateSecurityRisksContent(securityRisksList);
        }
        locationTagList = PreferencesHelper.getInstance().getSecurityRiskLocationTags(mActivity);
        behaviorTagList = PreferencesHelper.getInstance().getSecurityRiskBehaviorTags(mActivity);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        if (eventData.code == Constants.EVENT_DATA_SECURITY_RISK_TAG_MANAGER) {
            locationTagList = PreferencesHelper.getInstance().getSecurityRiskLocationTags(mActivity);
            behaviorTagList = PreferencesHelper.getInstance().getSecurityRiskBehaviorTags(mActivity);
            //
            if (getView().getIsLocation()) {
                SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
                for (SecurityRisksTagModel securityRisksTagModel : locationTagList) {
                    securityRisksTagModel.isCheck = securityRisksTagModel.tag != null && securityRisksTagModel.tag.equals(model.place);
                }
                getView().updateSecurityRisksTag(locationTagList, true);
                getView().setTvName(mActivity.getString(R.string.refer_loaction));
            } else {
                SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
                for (SecurityRisksTagModel securityRisksTagModel : behaviorTagList) {
                    securityRisksTagModel.isCheck = securityRisksTagModel.tag != null && model.action.contains(securityRisksTagModel.tag);
                }
                getView().updateSecurityRisksTag(behaviorTagList, false);
                getView().setTvName(mActivity.getString(R.string.refer_behavior));
            }

            for (SecurityRisksAdapterModel model : securityRisksList) {
                if (!containPlace(model.place)) {
                    model.place = "";
                }
                Iterator<String> iterator = model.action.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (!containAction(next)) {
                        iterator.remove();
                    }
                }
            }
            getView().updateSecurityRisksContent(securityRisksList);
        }
    }

    private boolean containAction(String action) {
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        for (SecurityRisksTagModel securityRisksTagModel : behaviorTagList) {

            if (securityRisksTagModel.tag != null && securityRisksTagModel.tag.equals(action)) {
                return true;
            }
        }
        return false;
    }

    private boolean containPlace(String place) {
        if (TextUtils.isEmpty(place)) {
            return false;
        }
        for (SecurityRisksTagModel securityRisksTagModel : locationTagList) {
            if (securityRisksTagModel.tag != null && securityRisksTagModel.tag.equals(place)) {
                return true;
            }
        }
        return false;
    }


    public void doAddSecurityRisk() {
        SecurityRisksAdapterModel model = new SecurityRisksAdapterModel();
        securityRisksList.add(model);
        getView().updateSecurityRisksContent(securityRisksList);
        getView().rvContentScrollBottom(securityRisksList.size());
    }

    @Override
    public void onLocationClick(int position) {
        mAdapterPosition = position;
        getView().setConstraintTagVisible(true);
//        getView().changLocationOrBehaviorColor(position, true);
        getView().rvContentScrollBottom(position);
        SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
        for (SecurityRisksTagModel securityRisksTagModel : locationTagList) {
            securityRisksTagModel.isCheck = securityRisksTagModel.tag.equals(model.place);
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
            securityRisksTagModel.isCheck = model.action.contains(securityRisksTagModel.tag);
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
        model.place = "";
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
        model.action.remove(tag);
        for (SecurityRisksTagModel securityRisksTagModel : behaviorTagList) {
            securityRisksTagModel.isCheck = model.action.contains(securityRisksTagModel.tag);
        }
        getView().updateSecurityRisksTag(behaviorTagList, false);
        getView().updateSecurityRisksContent(securityRisksList);
        getView().setTvName(mActivity.getString(R.string.refer_behavior));
    }

    @Override
    public void onItemDel(int position) {
        if (position != securityRisksList.size()) {
            securityRisksList.remove(position);
        }
        getView().updateSecurityRisksContent(securityRisksList);
    }


    @Override
    public void onTagClick(SecurityRisksTagModel tagModel, boolean isLocation, Integer position) {

        if (isLocation) {
            SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
            if (tagModel.isCheck) {
                model.place = tagModel.tag;
            } else {
                model.place = "";
            }
            getView().updateSecurityRisksContent(securityRisksList);
        } else {
            SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
            if (tagModel.isCheck) {
                model.action.add(tagModel.tag);
            } else {
                model.action.remove(tagModel.tag);
            }
            getView().updateSecurityRisksContent(securityRisksList);
        }
    }

    @Override
    public boolean isTagCountLarge() {
        SecurityRisksAdapterModel model = securityRisksList.get(mAdapterPosition);
        return model.action.size() > 9;
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
        text = getTrim(text);
        if (TextUtils.isEmpty(text)) {
            getView().toastShort(mActivity.getString(R.string.cannot_be_all_spaces));
            return;
        }
        byte[] bytes = text.getBytes();
        if (bytes.length > 30) {
            getView().toastShort(mActivity.getString(R.string.the_maximum_length_of_the_label));
            return;
        }
        if (mAddTagTypeIsLocation) {

            for (SecurityRisksTagModel tagModel : locationTagList) {
                if (tagModel.tag != null && tagModel.tag.equals(text)) {
                    getView().toastShort(mActivity.getString(R.string.label_cannot_be_repeated));
                    return;
                }
            }
            SecurityRisksTagModel model = new SecurityRisksTagModel();
            model.tag = text;
            locationTagList.add(model);
            PreferencesHelper.getInstance().saveSecurityRiskLocationTag(locationTagList);
            getView().updateSecurityRisksTag(locationTagList, true);
        } else {
            for (SecurityRisksTagModel tagModel : behaviorTagList) {
                if (tagModel.tag != null && tagModel.tag.equals(text)) {
                    getView().toastShort(mActivity.getString(R.string.label_cannot_be_repeated));
                    return;
                }
            }
            SecurityRisksTagModel model = new SecurityRisksTagModel();
            model.tag = text;
            behaviorTagList.add(model);
            PreferencesHelper.getInstance().saveSecurityRiskBehaviorTag(behaviorTagList);
            getView().updateSecurityRisksTag(behaviorTagList, false);
        }
        getView().tagScrollBottom();
        getView().dismissTagDialog();
    }

    private String getTrim(String text) {
        try {
            text = text.trim();
            while (text.startsWith("　")) {//这里判断是不是全角空格
                text = text.substring(1, text.length()).trim();
            }
            while (text.endsWith("　")) {
                text = text.substring(0, text.length() - 1).trim();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public void doSave() {
        for (SecurityRisksAdapterModel model : securityRisksList) {
            if (TextUtils.isEmpty(model.place) || model.action.size() < 1) {
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
