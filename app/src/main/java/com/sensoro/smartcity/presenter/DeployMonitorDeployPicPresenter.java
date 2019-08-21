package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorDeployPicView;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.smartcity.widget.dialog.DeployPicExampleDialogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.common.model.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.common.widgets.SelectDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeployMonitorDeployPicPresenter extends BasePresenter<IDeployMonitorDeployPicView>
        implements SelectDialog.SelectDialogListener, DeployPicExampleDialogUtils.DeployPicExampleClickListener {
    private Activity mActivity;
    //    private final ImageItem[] selImages = new ImageItem[3];
    private volatile int mAddPicIndex = -1;
    private String mergeType;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Bundle bundle = getBundle(mActivity);
        String deviceType = null;
        final List<DeployPicInfo> deployPicInfos = new ArrayList<>();
        if (bundle != null) {
            deviceType = bundle.getString(Constants.EXTRA_SETTING_DEPLOY_DEVICE_TYPE);
            mergeType = WidgetUtil.handleMergeType(deviceType);
            getView().setDeployPicTvInstallationSiteTipVisible(Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deviceType));
            Serializable serializable = bundle.getSerializable(Constants.EXTRA_DEPLOY_TO_PHOTO);
            if (serializable instanceof ArrayList) {
                ArrayList<DeployPicInfo> picInfos = (ArrayList<DeployPicInfo>) serializable;
                if (picInfos.size() > 0) {
                    deployPicInfos.addAll(picInfos);
                }
            }
        } else {
            getView().setDeployPicTvInstallationSiteTipVisible(false);
        }

        if (mergeType == null) {
            //主要用来存储今日是否提示，如果为空，就直接存储
            mergeType = "unknown";
        }


        if (deployPicInfos.isEmpty()) {
            List<DeployPicInfo> configDeviceDeployPic = PreferencesHelper.getInstance().getConfigDeviceDeployPic(deviceType);
            if (configDeviceDeployPic != null && configDeviceDeployPic.size() > 0) {
                //当存在旧数据时清除
                for (DeployPicInfo deployPicInfo : configDeviceDeployPic) {
                    deployPicInfo.photoItem = null;
                }
                deployPicInfos.addAll(configDeviceDeployPic);
            } else {
                //这里自定义了一个类型认为是部署摄像机

                if ("deploy_camera".equals(deviceType)) {
                    DeployPicInfo deployPicInfo1 = new DeployPicInfo();
                    deployPicInfo1.title = mActivity.getString(R.string.deploy_pic_device_pic);
                    deployPicInfo1.isRequired = true;
                    deployPicInfo1.description = mActivity.getString(R.string.deploy_pic_look_device_look_sn);
                    DeployPicInfo deployPicInfo2 = new DeployPicInfo();
                    deployPicInfo2.isRequired = true;
                    deployPicInfo2.title = mActivity.getString(R.string.deploy_pic_installation_site);
                    deployPicInfo2.description = mActivity.getString(R.string.deploy_pic_look_installation_environmental);
                    deployPicInfos.add(deployPicInfo1);
                    deployPicInfos.add(deployPicInfo2);
                } else if ("deploy_nameplate".equals(deviceType)) {
                    DeployPicInfo deployPicInfo2 = new DeployPicInfo();
                    deployPicInfo2.isRequired = null;
                    deployPicInfo2.title = mActivity.getString(R.string.deploy_pic_installation_sit);
                    deployPicInfo2.description = mActivity.getString(R.string.deploy_pic_nameplate_look_installation_environmental);

                    deployPicInfos.add(deployPicInfo2);
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
                }
            }
        }
        getView().updateData(deployPicInfos);
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
        mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_SELECT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 直接调起相机
                Intent intent = new Intent(mActivity, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_SELECT);
                break;
            case 1:
                ImagePicker.getInstance().setSelectLimit(1);
                Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
//                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImages);
                mActivity.startActivityForResult(intent1, Constants.REQUEST_CODE_SELECT);
                break;
            case 2:
                Intent intent2 = new Intent(mActivity, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent2, Constants.REQUEST_CODE_RECORD);
                break;
            default:
                break;
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == Constants.REQUEST_CODE_SELECT) {
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

    public void deletePic(int index) {
//        selImages[index] = null;

        if (isAttachedView()) {
            getView().updateIndexData(null, index);
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
//        if (!checkCanSave()) {
//            getView().toastShort(mActivity.getString(R.string.please_deploy_upload_all_pic));
//            return;
//        }
        ArrayList<ImageItem> imageItems = new ArrayList<>();
//        imageItems.add(selImages[0]);
//        imageItems.add(selImages[1]);
//        if (selImages[2] != null) {
//            imageItems.add(selImages[2]);
//        }
        List<DeployPicInfo> deployPicData = getView().getDeployPicData();
//        for (DeployPicInfo deployPicDatum : deployPicData) {
//            if (deployPicDatum.photoItem != null) {
//                imageItems.add(deployPicDatum.photoItem);
//            }
//
//        }

        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_PHOTO;
        eventData.data = deployPicData;
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
        intentPreview.putExtra(Constants.EXTRA_JUST_DISPLAY_PIC, true);
        getView().startACForResult(intentPreview, Constants.REQUEST_CODE_PREVIEW);
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
