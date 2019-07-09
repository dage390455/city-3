package com.sensoro.smartcity.presenter;

import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IUuidSettingActivityView;
import com.sensoro.smartcity.model.UuidSettingModel;

import java.util.ArrayList;
import java.util.List;

public class UuidSettingActivityPresenter extends BasePresenter<IUuidSettingActivityView> {
    private final List<UuidSettingModel> mData = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mData.add(new UuidSettingModel("Sensoro Alpha 系统默认", "70DC44C3-E2A8-4B22-A2C6-129B41A4BDBC"));
        mData.add(new UuidSettingModel("Sensoro iBeacon 默认", "23A01AF0-232A-4518-9C0E-323FB773F5EF"));
        mData.add(new UuidSettingModel("AirLocate", "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"));
        mData.add(new UuidSettingModel("Estimote", "B9407F30-F5F8-466E-AFF9-25556B57FE6D"));
        mData.add(new UuidSettingModel("微信", "FDA50693-A4E2-4FB1-AFCF-C6EB07647825"));
        mData.add(new UuidSettingModel("微信2", "AB8190D5-D11E-4941-ACC4-42F30510B408"));
        mData.add(new UuidSettingModel("Kontakt", "F7826DA6-4FA2-4E98-8024-BC5B71E0893E"));
    }

    @Override
    public void onDestroy() {

    }
}
