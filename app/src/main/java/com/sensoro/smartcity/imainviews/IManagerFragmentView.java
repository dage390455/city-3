package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IManagerFragmentView extends IToast, IActivityIntent, IProgressDialog {
    void setMerchantName(String name);

    void setAppUpdateVisible(boolean isVisible);

    void showVersionDialog();

    void setContractVisible(boolean isVisible);

    void setInspectionVisible(boolean isVisible);

    void setScanLoginVisible(boolean isVisible);

    void setMerchantVisible(boolean isVisible);

    void changeMerchantTitle(boolean hasSubMerchant);

    void setSignalCheckVisible(boolean hasSignalCheck);

    void setDeviceCameraVisible(boolean hasDeviceCamera);

    void setStationManagerVisible(boolean hasStationList);

    void setNameplateVisible(boolean hasNameplate);

    void setIBeaconVisible(boolean hasIBeacon);

    void showBleTips();

    void hideBleTips();
}
