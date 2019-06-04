package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.ResourceUtils;
import com.sensoro.nameplate.IMainViews.IEditNameplateDetailActivityView;
import com.sensoro.nameplate.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class EditNameplateDetailActivityPresenter extends BasePresenter<IEditNameplateDetailActivityView> implements Constants {
    private final List<String> mTagList = new ArrayList<>();
    private Activity mContext;

    private String nameplateId, nameplateName;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;


        nameplateId = mContext.getIntent().getStringExtra("nameplateId");
        nameplateName = mContext.getIntent().getStringExtra("nameplateName");

        if (!TextUtils.isEmpty(nameplateName)) {
            getView().updateNameplateName(nameplateName);
        }
        ArrayList<String> stringArrayListExtra = mContext.getIntent().getStringArrayListExtra(EXTRA_SETTING_TAG_LIST);
        if (stringArrayListExtra != null) {
            mTagList.addAll(stringArrayListExtra);
        }
        if (mTagList.size() > 0) {
            getView().updateTags(mTagList);
        }
    }

    @Override
    public void onDestroy() {
        mTagList.clear();
    }

    public void clickDeleteTag(int position) {
        mTagList.remove(position);
        getView().updateTags(mTagList);
    }

    public void addTags(String tag) {
        if (TextUtils.isEmpty(tag)) {
            getView().toastShort(mContext.getString(R.string
                    .please_set_the_label));
            return;
        }
        if (mTagList.size() >= 8) {
            getView().toastShort(mContext.getString(R.string.can_only_add_up_to_limit_labels));
        } else {
            if (!TextUtils.isEmpty(tag)) {
                String trim = getTrim(tag);
                if (!TextUtils.isEmpty(trim)) {
                    if (ResourceUtils.getByteFromWords(trim) > 30) {
                        getView().toastShort(mContext.getString(R.string.the_maximum_length_of_the_label));
                        return;
                    }
                    if (mTagList.contains(trim)) {
                        getView().toastShort(mContext.getString(R.string.label_cannot_be_repeated));
                        return;
                    } else {
                        mTagList.add(trim);
                    }
                    getView().updateTags(mTagList);
                } else {
                    getView().toastShort(mContext.getString(R.string.cannot_be_all_spaces));
                    return;
                }
            }
        }
        getView().dismissDialog();
    }


    public void doFinish(String name) {


        if (TextUtils.isEmpty(name)) {

            getView().toastShort(mContext.getString(R.string.nameplate_name_empty));

            return;
        }
        if (mTagList.size() > 8) {
            getView().toastShort(mContext.getString(R.string.can_only_add_up_to_limit_labels));
        } else {
            for (String temp : mTagList) {
                String trim = getTrim(temp);
                if (ResourceUtils.getByteFromWords(trim) > 30) {
                    getView().toastShort(mContext.getString(R.string.the_maximum_length_of_the_label));
                    return;
                }
            }


            RetrofitServiceHelper.getInstance().updateNameplate(nameplateId, name, mTagList).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<Integer>>(this) {
                @Override
                public void onCompleted(ResponseResult<Integer> responseResult) {
                    if (responseResult.getData() == 1) {
                        getView().finishAc();
                        //更新
                        EventData eventData = new EventData();
                        eventData.code = EVENT_DATA_UPDATENAMEPALTELIST;
                        EventBus.getDefault().post(eventData);
                    }

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);

                }
            });
        }
    }


    public void doEditTag(int position) {
        if (position < mTagList.size()) {
            String tag = mTagList.get(position);
            getView().showDialogWithEdit(tag, position);
        }

    }

    public void updateEditTag(int position, String text) {

        if (TextUtils.isEmpty(text)) {
            getView().toastShort(mContext.getString(R.string.please_set_the_label));
            return;
        }
        String trim = getTrim(text);
        if (!TextUtils.isEmpty(trim)) {
            if (ResourceUtils.getByteFromWords(trim) > 30) {
                getView().toastShort(mContext.getString(R.string.the_maximum_length_of_the_label));
                return;
            }
            mTagList.set(position, text);
        } else {
            getView().toastShort(mContext.getString(R.string.cannot_be_all_spaces));
            return;
        }
        getView().updateTags(mTagList);
        getView().dismissDialog();
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
}
