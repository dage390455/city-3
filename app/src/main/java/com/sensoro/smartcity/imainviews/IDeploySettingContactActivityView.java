package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeploySettingContactActivityView extends IActivityIntent, IToast {
    void setName(String name);

    void setPhone(String phone);

    void setSearchHistoryLayoutVisible(boolean isVisible);

    void updateAdapter(List<String> nameHistoryKeywords, List<String> phoneHistoryKeywords);

    void setNameEditText(String text);

    void setPhoneEditText(String text);
}
