package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorDeployPicView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class DeployMonitorDeployPicPresenter extends BasePresenter<IDeployMonitorDeployPicView>
        implements SelectDialog.SelectDialogListener, Constants {
    private Activity mActivity;
    private final ImageItem[] selImages = new ImageItem[3];
    private volatile int mAddPicIndex = -1;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        ArrayList<ImageItem> imageList = (ArrayList<ImageItem>) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_TO_PHOTO);
        if (imageList != null && imageList.size() > 0 && imageList.size() < 4) {
            for (int i = 0; i < imageList.size(); i++) {
                selImages[i] = imageList.get(i);
                getView().displayPic(selImages, i);
            }
            checkCanSave();
        }


    }

    @Override
    public void onDestroy() {

    }

    public void doAddPic(int index) {
        //弹出对话框，选择照片还是相册，目前版本是直接调用相机
//        List<String> names = new ArrayList<>();
//        names.add(mActivity.getString(R.string.take_photo));
//        names.add(mActivity.getString(R.string.album));
//        getView().showSelectDialog(this, names);
        mAddPicIndex = index;
        Intent intent = new Intent(mActivity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 直接调起相机

                Intent intent = new Intent(mActivity, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            case 1:
                ImagePicker.getInstance().setSelectLimit(1);
                Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
//                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImages);
                mActivity.startActivityForResult(intent1, REQUEST_CODE_SELECT);
                break;
            case 2:
                Intent intent2 = new Intent(mActivity, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent2, REQUEST_CODE_RECORD);
                break;
            default:
                break;
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null && tempImages.size() > 0) {
                    if (mAddPicIndex == -1) {
                        return;
                    }
                    selImages[mAddPicIndex] = tempImages.get(0);
                    getView().displayPic(selImages, mAddPicIndex);
                }
                checkCanSave();
            }
//        else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
//            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
//                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
//                if (tempImages != null) {
////                    selImages.clear();
////                    selImages.addAll(tempImages);
////                    getView().updateImageList(selImages);
//                }
//            }
//        }
        }

    }

    private void checkCanSave() {
        if (selImages[0] != null && selImages[1] != null) {
            getView().setSaveBtnStatus(true);
        } else {
            getView().setSaveBtnStatus(false);
        }
    }

    public void deletePic(int index) {
        selImages[index] = null;
        checkCanSave();
    }

    public void doSave() {
        if (selImages[0] == null) {
            getView().toastShort(mActivity.getString(R.string.please_select_device_pic));
            return;
        }
        if (selImages[1] == null) {
            getView().toastShort(mActivity.getString(R.string.please_select_installation_site));
            return;
        }
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        imageItems.add(selImages[0]);
        imageItems.add(selImages[1]);
        if (selImages[2] != null) {
            imageItems.add(selImages[2]);
        }

        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_SETTING_PHOTO;
        eventData.data = imageItems;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void doPreviewPic(int index) {
        Intent intentPreview = new Intent(mActivity, ImagePreviewDelActivity.class);
        ArrayList<ImageItem> list = new ArrayList<>();
        for (int i = 0; i < selImages.length; i++) {
            if (selImages[i] == null) {
                if (i == 0) {
                    index = index - 1;
                } else if (i == 1 && index == 2) {
                    index = 1;
                }
            } else {
                list.add(selImages[i]);
            }
        }
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, list);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, index);
        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        intentPreview.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
        getView().startACForResult(intentPreview, REQUEST_CODE_PREVIEW);
    }
}
