package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IInspectionUploadExceptionActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateExceptionTagAdapter(List<String> exceptionTags);

    void updateWordCount(int count);

    void dismissExceptionDialog();

    List<Integer> getSelectTags();

    String getRemarkMessage();
}
