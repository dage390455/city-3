package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.smartcity.server.bean.DeployPicInfo;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorDeployPicView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.dialog.DeployPicExampleDialogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeployMonitorDeployPicPresenter extends BasePresenter<IDeployMonitorDeployPicView>
        implements SelectDialog.SelectDialogListener, Constants, DeployPicExampleDialogUtils.DeployPicExampleClickListener {
    private Activity mActivity;
    //    private final ImageItem[] selImages = new ImageItem[3];
    private volatile int mAddPicIndex = -1;
    private String deviceType;
    private String mergeType;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deviceType = mActivity.getIntent().getStringExtra(EXTRA_SETTING_DEPLOY_DEVICE_TYPE);
        mergeType = WidgetUtil.handleMergeType(deviceType);
        if (mergeType == null) {
            //主要用来存储今日是否提示，如果为空，就直接存储
            mergeType = "unknown";
        }
        getView().setDeployPicTvInstallationSiteTipVisible(DEVICE_CONTROL_DEVICE_TYPES.contains(deviceType));
        ArrayList<ImageItem> imageList = (ArrayList<ImageItem>) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_TO_PHOTO);

        List<DeployPicInfo> deployPicInfos = new ArrayList<>();
        List<DeployPicInfo> configDeviceDeployPic = PreferencesHelper.getInstance().getConfigDeviceDeployPic(deviceType);

        if (configDeviceDeployPic != null && configDeviceDeployPic.size() > 0) {
            for (int i = 0; i < configDeviceDeployPic.size(); i++) {
                DeployPicInfo copy = configDeviceDeployPic.get(i).copy();
                try {
                    copy.photoItem = imageList.get(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                deployPicInfos.add(copy);
            }

        } else {
            DeployPicInfo deployPicInfo1 = new DeployPicInfo();
            deployPicInfo1.title = mActivity.getString(R.string.deploy_pic_device_pic);
            deployPicInfo1.isRequired = true;
            deployPicInfo1.description = mActivity.getString(R.string.deploy_pic_device_pic_tip);
            DeployPicInfo deployPicInfo2 = new DeployPicInfo();
            deployPicInfo2.isRequired = true;
            deployPicInfo2.title = mActivity.getString(R.string.deploy_pic_installation_site);

            DeployPicInfo deployPicInfo3 = new DeployPicInfo();
            deployPicInfo3.title = mActivity.getString(R.string.deploy_pic_shop_pic);
            deployPicInfo3.description = mActivity.getString(R.string.if_it_is_a_store_please_upload);
            deployPicInfo3.isRequired = false;
            deployPicInfos.add(deployPicInfo1);
            deployPicInfos.add(deployPicInfo2);
            deployPicInfos.add(deployPicInfo3);
            if (imageList != null && imageList.size() > 0 && imageList.size() < 4) {
                for (int i = 0; i < imageList.size(); i++) {
                    try {
                        deployPicInfos.get(i).photoItem = imageList.get(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        getView().updateData(deployPicInfos);
        checkCanSave();
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
//                    selImages[mAddPicIndex] = tempImages.get(0);
//                    getView().displayPic(selImages, mAddPicIndex);
//                    DeployPicInfo model = deployPicModels.get(mAddPicIndex);
//                    model.photoItem = tempImages.get(0);
                    if (isAttachedView()) {
                        getView().updateIndexData(tempImages.get(0), mAddPicIndex);
                    }

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
        if (isAttachedView()) {
            List<DeployPicInfo> deployPicData = getView().getDeployPicData();
            boolean isCanSave = true;
            for (DeployPicInfo deployPicDatum : deployPicData) {
                if (deployPicDatum.isRequired && deployPicDatum.photoItem == null) {
                    isCanSave = false;
                    break;
                }
            }
            getView().setSaveBtnStatus(isCanSave);
        }

//        if (selImages[0] != null && selImages[1] != null) {
//            getView().setSaveBtnStatus(true);
//        } else {
//            getView().setSaveBtnStatus(false);
//        }
    }

    public void deletePic(int index) {
//        selImages[index] = null;

        if (isAttachedView()) {
            getView().updateIndexData(null, index);
            checkCanSave();
        }

    }

    public void doSave() {
//        if (selImages[0] == null) {
//            getView().toastShort(mActivity.getString(R.string.please_select_device_pic));
//            return;
//        }
//        if (selImages[1] == null) {
//            getView().toastShort(mActivity.getString(R.string.please_select_installation_site));
//            return;
//        }
        ArrayList<ImageItem> imageItems = new ArrayList<>();
//        imageItems.add(selImages[0]);
//        imageItems.add(selImages[1]);
//        if (selImages[2] != null) {
//            imageItems.add(selImages[2]);
//        }
        List<DeployPicInfo> deployPicData = getView().getDeployPicData();
        for (DeployPicInfo deployPicDatum : deployPicData) {
            imageItems.add(deployPicDatum.photoItem);
            if (deployPicDatum.photoItem != null) {
            }

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
        List<DeployPicInfo> deployPicData = getView().getDeployPicData();
        for (int i = 0; i < deployPicData.size(); i++) {
            ImageItem photoItem = deployPicData.get(i).photoItem;
            if (photoItem == null) {
                if (i != deployPicData.size() - 1) {
                    index -= 1;
                }

            } else {
                list.add(photoItem);
            }
        }
//        for (int i = 0; i < selImages.length; i++) {
//            if (selImages[i] == null) {
//                if (i == 0) {
//                    index = index - 1;
//                } else if (i == 1 && index == 2) {
//                    index = 1;
//                }
//            } else {
//                list.add(selImages[i]);
//            }
//        }
        if (index < 0) {
            index = 0;
        }
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, list);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, index);
        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        intentPreview.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
        getView().startACForResult(intentPreview, REQUEST_CODE_PREVIEW);
    }

    /**
     * 示例照片对话框中拍照按钮
     */
    @Override
    public void onTakePhotoClick(String title, int position) {
        if (!TextUtils.isEmpty(title)) {
            PreferencesHelper.getInstance().saveDeployExamplePicTimestamp(String.format(Locale.ROOT, "%s%s", mergeType, title));
        }
        getView().dismissDeployPicExampleDialog();
        doAddPic(position);
    }

    public void doTakePhoto(int position) {
        DeployPicInfo item = getView().getDeployPicItem(position);
        if (TextUtils.isEmpty(item.imgUrl) || DateUtil.getStrTime_yymmdd(System.currentTimeMillis()).
                equals(PreferencesHelper.getInstance().getDeployExamplePicTimestamp(String.format(Locale.ROOT, "%s%s", mergeType, item.title)))) {
            doAddPic(position);
        } else {
            getView().showDeployPicExampleDialog(item, position);
        }

    }
}
