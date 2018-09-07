package com.sensoro.smartcity.imainviews;

import android.support.v7.widget.LinearLayoutManager;

import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IWarnFragmentView extends IToast, IActivityIntent, IProgressDialog {
    void setRcContentAdapter(MainWarnFragRcContentAdapter adapter, LinearLayoutManager manager);
}
