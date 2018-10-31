package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

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
    void showBleTips();

    void hideBleTips();
}
