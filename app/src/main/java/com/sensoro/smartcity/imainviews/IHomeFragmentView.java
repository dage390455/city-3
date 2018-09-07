package com.sensoro.smartcity.imainviews;

import android.support.v7.widget.LinearLayoutManager;

import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IHomeFragmentView extends IToast,IProgressDialog,IActivityIntent {
    void setRcTypeAdapter(MainHomeFragRcTypeAdapter adapter, LinearLayoutManager manager);

    void setRcContentAdapter(MainHomeFragRcContentAdapter adapter, LinearLayoutManager manager);

    void setImvAddVisible(boolean isVisible);

    void setImvSearchVisible(boolean isVisible);
}
