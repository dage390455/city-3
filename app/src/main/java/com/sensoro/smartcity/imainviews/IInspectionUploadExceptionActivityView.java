package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.widgets.SelectDialog;

import java.util.ArrayList;
import java.util.List;

public interface IInspectionUploadExceptionActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateExceptionTagAdapter(List<String> exceptionTags);

    void updateWordCount(int count);

    void dismissExceptionDialog();

    List<Integer> getSelectTags();

    String getRemarkMessage();
    void updateImageList(ArrayList<ImageItem> imageList);

    void showDialog(SelectDialog.SelectDialogListener listener, List<String> names);
    void initUploadProgressDialog();
    void dismissUploadProgressDialog();
    void showUploadProgressDialog(String content, double percent);
}
