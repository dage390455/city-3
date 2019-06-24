package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorSettingPhotoActivityView;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.common.model.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.common.widgets.SelectDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sensoro.smartcity.widget.imagepicker.ImagePicker.EXTRA_RESULT_BY_TAKE_PHOTO;


public class DeployMonitorSettingPhotoActivityPresenter extends BasePresenter<IDeployMonitorSettingPhotoActivityView> implements SelectDialog.SelectDialogListener {

    private final ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片

    private final int maxImgCount = 4;

    private ArrayList<ImageItem> tempImages = null;
    private Activity mContext;
    private boolean isJustDisplay = false;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        ArrayList<ImageItem> items = (ArrayList<ImageItem>) mContext.getIntent().getSerializableExtra
                (Constants.EXTRA_DEPLOY_TO_PHOTO);
        isJustDisplay = mContext.getIntent().getBooleanExtra(Constants.EXTRA_JUST_DISPLAY_PIC, false);
        getView().setJustDisplayPic(isJustDisplay);
        getView().setSubtitleVisible(!isJustDisplay);
        if (items != null && items.size() > 0) {
            selImageList.addAll(items);
            getView().updateImageList(selImageList);
        }
    }

    @Override
    public void onDestroy() {
        if (tempImages != null) {
            tempImages.clear();
            tempImages = null;
        }
        selImageList.clear();
    }

    public void doFinish() {
        if (selImageList.size() > 0) {
            EventData eventData = new EventData();
            eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_PHOTO;
            eventData.data = selImageList;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
        } else {
            getView().toastShort(mContext.getString(R.string.please_add_at_least_one_image));
        }

    }

    public ArrayList<ImageItem> getSelImageList() {
        return selImageList;
    }

    public void clickItem(int viewId, int position, List<ImageItem> images) {
        if (viewId == R.id.image_delete) {
            ImageItem imageItem = selImageList.get(position);
            Iterator<ImageItem> iterator = selImageList.iterator();
            while (iterator.hasNext()) {
                ImageItem next = iterator.next();
                if (next.equals(imageItem)) {
                    iterator.remove();
                    break;
                }
            }
            getView().updateImageList(selImageList);
//            updateButton();
        } else if (Constants.IMAGE_ITEM_ADD == position) {
            List<String> names = new ArrayList<>();
            names.add(mContext.getString(R.string.take_photo));
            names.add(mContext.getString(R.string.album));
            getView().showDialog(this, names, mContext.getResources().getString(R.string.camera_photo));
        } else {
            //打开预览

            Intent intentPreview = new Intent(mContext, ImagePreviewDelActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) images);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            intentPreview.putExtra(Constants.EXTRA_JUST_DISPLAY_PIC, isJustDisplay);
            getView().startACForResult(intentPreview, Constants.REQUEST_CODE_PREVIEW);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == Constants.REQUEST_CODE_SELECT) {
                tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null) {
                    boolean fromTakePhoto = data.getBooleanExtra(EXTRA_RESULT_BY_TAKE_PHOTO, false);
                    if (!fromTakePhoto) {
                        selImageList.clear();
                    }
                    selImageList.addAll(tempImages);
                    getView().updateImageList(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == Constants.REQUEST_CODE_PREVIEW) {
                tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (tempImages != null) {
                    selImageList.clear();
                    selImageList.addAll(tempImages);
                    getView().updateImageList(selImageList);
                }
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 直接调起相机
                /**
                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                 *
                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                 *
                 * 如果实在有所需要，请直接下载源码引用。
                 */
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(mContext, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mContext.startActivityForResult(intent, Constants.REQUEST_CODE_SELECT);
                break;
            case 1:
                //打开选择,本次允许选择的数量
                //修改选择逻辑
//                                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                ImagePicker.getInstance().setSelectLimit(maxImgCount);
                Intent intent1 = new Intent(mContext, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
                mContext.startActivityForResult(intent1, Constants.REQUEST_CODE_SELECT);
                break;
            case 2:
                Intent intent2 = new Intent(mContext, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mContext.startActivityForResult(intent2, Constants.REQUEST_CODE_RECORD);
                break;
            default:
                break;
        }

    }
}
