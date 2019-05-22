package com.sensoro.smartcity.imainviews;

import android.content.Intent;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface ITakeRecordActivityView extends IToast, IProgressDialog, IActivityIntent {

    void setFinishResult(int resultCodeRecord, Intent intent);

}
