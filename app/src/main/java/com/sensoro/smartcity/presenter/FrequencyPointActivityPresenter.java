package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IFrequencyPointActivityView;

import java.util.ArrayList;

public class FrequencyPointActivityPresenter extends BasePresenter<IFrequencyPointActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {


        ArrayList<String> channels = mContext.getIntent().getStringArrayListExtra("channels");
        getView().updateData(channels);
    }

    @Override
    public void onDestroy() {

    }
}