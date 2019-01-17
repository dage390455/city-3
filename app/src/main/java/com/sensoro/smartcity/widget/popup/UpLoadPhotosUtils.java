package com.sensoro.smartcity.widget.popup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.QiNiuToken;
import com.sensoro.smartcity.util.AESUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.luban.CompressionPredicate;
import com.sensoro.smartcity.util.luban.Luban;
import com.sensoro.smartcity.util.luban.OnCompressListener;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class UpLoadPhotosUtils {
    private final Context mContext;
    private final List<ImageItem> imageItems = new ArrayList<>();
    private final List<ScenesData> mScenesDataList = new ArrayList<>();
    private volatile int pictureIndex = 0;
    private final UpLoadPhotoListener upLoadPhotoListener;
    private volatile String baseUrl = "";
    private final Handler mHandler;

    public UpLoadPhotosUtils(Context context, UpLoadPhotoListener upLoadPhotoListener) {
        mContext = context;
        this.upLoadPhotoListener = upLoadPhotoListener;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void doUploadPhoto(List<ImageItem> imageItems) {
        if (imageItems != null && imageItems.size() > 0) {
            this.mScenesDataList.clear();
            this.imageItems.clear();
            this.imageItems.addAll(imageItems);
            handleUpLoadPhotoListenerOnStart();
            getToken();
        }
    }

    private void getToken() {
        final long currentTimeMillis = System.currentTimeMillis();
        RetrofitServiceHelper.INSTANCE.getQiNiuToken().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<QiNiuToken>(null) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                handleUpLoadPhotoListenerOnError(errorMsg);
            }

            @Override
            public void onCompleted(QiNiuToken qiNiuToken) {
                try {
                    LogUtils.loge("接口速度--->>>getQiNiuToken: " + (System.currentTimeMillis() - currentTimeMillis));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                String upToken = qiNiuToken.getUptoken();
                baseUrl = qiNiuToken.getDomain();
                doUpLoadImages(upToken);
            }
        });
    }

    private void doUpLoadImages(final String token) {
        if (imageItems.size() == 0) {
            pictureIndex = 0;
            return;
        }
        if (pictureIndex == imageItems.size()) {
            //完成
            handleUpLoadPhotoListenerOnComplete(mScenesDataList);
            pictureIndex = 0;
        } else {
            //TODO 上传类型判断
            final ImageItem imageItem = imageItems.get(pictureIndex);
            //
            String thumbPath = imageItem.thumbPath;
            final String recordPath = imageItem.path;
            final int currentNum = pictureIndex + 1;
            if (imageItem.isRecord) {
                final String thumbKey = AESUtil.stringToMD5(thumbPath);
                final String recordKey = AESUtil.stringToMD5(recordPath);
                Luban.with(mContext).ignoreBy(200).load(thumbPath).filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                }).setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        String title = mContext.getString(R.string.upload_photo_dialog_append_title1) + currentNum + mContext.getString(R.string.upload_photo_dialog_append_title2) + imageItems.size() + mContext.getString(R.string.upload_photo_dialog_append_title3);
                        handleUpLoadPhotoListenerOnProgress(title, 0);
                    }

                    @Override
                    public void onSuccess(File file) {
                        final ScenesData scenesData = new ScenesData();
                        SensoroCityApplication.getInstance().uploadManager.put(file, thumbKey, token, new
                                UpCompletionHandler() {
                                    @Override
                                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                                        if (responseInfo.isOK()) {
                                            scenesData.type = "video";
                                            scenesData.thumbUrl = baseUrl + thumbKey;
                                            try {
                                                LogUtils.loge(this, "缩略图文件路径-->> " + baseUrl + thumbKey);
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                            SensoroCityApplication.getInstance().uploadManager.put(recordPath, recordKey, token, new
                                                    UpCompletionHandler() {
                                                        @Override
                                                        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                                                            if (responseInfo.isOK()) {
                                                                scenesData.url = baseUrl + recordKey;
                                                                try {
                                                                    LogUtils.loge(this, "视频文件路径-->> " + baseUrl + recordKey);
                                                                } catch (Throwable throwable) {
                                                                    throwable.printStackTrace();
                                                                }
                                                                mScenesDataList.add(scenesData);
                                                                pictureIndex++;
                                                                doUpLoadImages(token);
                                                            } else {
                                                                //TODO 可以添加重试获取token机制
                                                                try {
                                                                    LogUtils.loge(this, "上传七牛服务器失败：" + "第【" + currentNum + "】文件-" +
                                                                            imageItem.name + "-->" + responseInfo.error);
                                                                } catch (Throwable throwable) {
                                                                    throwable.printStackTrace();
                                                                }
                                                                handleUpLoadPhotoListenerOnError(mContext.getString(R.string.upload_photo_dialog_upload_video_failed));
                                                                pictureIndex = 0;
                                                            }

                                                        }
                                                    }, new UploadOptions(null, null, false, new UpProgressHandler() {

                                                @Override
                                                public void progress(String key, double percent) {
                                                    try {
                                                        LogUtils.loge(this, key + ": " + "progress ---->>" + percent);
                                                    } catch (Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
                                                    String title = mContext.getString(R.string.upload_photo_dialog_append_title1) + currentNum + mContext.getString(R.string.upload_photo_dialog_append_title2) + imageItems.size() + mContext.getString(R.string.upload_photo_dialog_append_title3);
                                                    handleUpLoadPhotoListenerOnProgress(title, percent);
                                                }
                                            }, null));
                                        } else {
                                            //TODO 可以添加重试获取token机制
                                            try {
                                                LogUtils.loge(this, "上传七牛服务器失败：" + "第【" + currentNum + "】张-" +
                                                        imageItem.name + "-->" + responseInfo.error);
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                            handleUpLoadPhotoListenerOnError(mContext.getString(R.string.upload_photo_dialog_upload_thumbnail_failed));
                                            pictureIndex = 0;
                                        }

                                    }
                                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                            @Override
                            public void progress(String key, double percent) {
                                try {
                                    LogUtils.loge(this, key + ": " + "progress ---->>" + percent);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                String title = mContext.getString(R.string.upload_photo_dialog_append_title1) + currentNum + mContext.getString(R.string.upload_photo_dialog_append_title2) + imageItems.size() + mContext.getString(R.string.upload_photo_dialog_append_title3);
                                handleUpLoadPhotoListenerOnProgress(title, percent);
                            }
                        }, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            LogUtils.loge(this, "压缩视频缩略图" + "第【" + currentNum + "】张-" + imageItem
                                    .name + "失败--->>" + e.getMessage());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        handleUpLoadPhotoListenerOnError(mContext.getString(R.string.upload_photo_dialog_upload_thumbnail_failed));
                    }
                }).launch();
            } else {
                String photoPath = imageItem.path;
                Luban.with(mContext).ignoreBy(200).load(photoPath).filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                }).setCompressListener(new OnCompressListener
                        () {
                    @Override
                    public void onStart() {
                        String title = mContext.getString(R.string.upload_photo_dialog_append_title1) + currentNum + mContext.getString(R.string.upload_photo_dialog_append_title2) + imageItems.size() + mContext.getString(R.string.upload_photo_dialog_append_title3);
                        handleUpLoadPhotoListenerOnProgress(title, 0);
                    }

                    @Override
                    public void onSuccess(File file) {
                        final String key = AESUtil.stringToMD5(file.getName());
                        SensoroCityApplication.getInstance().uploadManager.put(file, key, token, new
                                UpCompletionHandler() {
                                    @Override
                                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                                        if (responseInfo.isOK()) {
                                            ScenesData scenesData = new ScenesData();
                                            scenesData.type = "image";
                                            scenesData.url = baseUrl + key;
                                            mScenesDataList.add(scenesData);
                                            try {
                                                LogUtils.loge(this, "文件路径-->> " + baseUrl + key);
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                            pictureIndex++;
                                            doUpLoadImages(token);
                                        } else {
                                            //TODO 可以添加重试获取token机制
                                            try {
                                                LogUtils.loge(this, "上传七牛服务器失败：" + "第【" + currentNum + "】张-" +
                                                        imageItem.name + "-->" + responseInfo.error);
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                            handleUpLoadPhotoListenerOnError(mContext.getString(R.string.upload_photo_dialog_upload) + currentNum + mContext.getString(R.string.upload_photo_dialog_upload_count_failed));
                                            pictureIndex = 0;
                                        }

                                    }
                                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                            @Override
                            public void progress(String key, double percent) {
                                try {
                                    LogUtils.loge(this, key + ": " + "progress ---->>" + percent);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                String title = mContext.getString(R.string.upload_photo_dialog_append_title1) + currentNum
                                        + mContext.getString(R.string.upload_photo_dialog_append_title2) + imageItems.size() + mContext.getString(R.string.upload_photo_dialog_append_title3);
                                handleUpLoadPhotoListenerOnProgress(title, percent);
                            }
                        }, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            LogUtils.loge(this, "压缩 " + "第【" + currentNum + "】张-" + imageItem
                                    .name + "失败--->>" + e.getMessage());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        handleUpLoadPhotoListenerOnError(mContext.getString(R.string.upload_photo_dialog_upload) + currentNum + mContext.getString(R.string.upload_photo_dialog_upload_count_failed));
                        pictureIndex = 0;
                    }
                }).launch();
            }
        }

    }

    private void handleUpLoadPhotoListenerOnStart() {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (upLoadPhotoListener != null) {
                upLoadPhotoListener.onStart();
            }
        } else {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upLoadPhotoListener != null) {
                            upLoadPhotoListener.onStart();
                        }
                    }
                });
            }
        }

    }

    private void handleUpLoadPhotoListenerOnComplete(final List<ScenesData> scenesDataList) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (upLoadPhotoListener != null) {
                upLoadPhotoListener.onComplete(scenesDataList);
            }
        } else {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upLoadPhotoListener != null) {
                            upLoadPhotoListener.onComplete(scenesDataList);
                        }
                    }
                });
            }
        }
    }

    private void handleUpLoadPhotoListenerOnError(final String errMsg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (upLoadPhotoListener != null) {
                upLoadPhotoListener.onError(errMsg);
            }
        } else {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upLoadPhotoListener != null) {
                            upLoadPhotoListener.onError(errMsg);
                        }
                    }
                });
            }
        }
    }

    private void handleUpLoadPhotoListenerOnProgress(final String content, final double percent) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (upLoadPhotoListener != null) {
                upLoadPhotoListener.onProgress(content, percent);
            }
        } else {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upLoadPhotoListener != null) {
                            upLoadPhotoListener.onProgress(content, percent);
                        }
                    }
                });
            }
        }
    }

    public interface UpLoadPhotoListener {
        void onStart();

        void onComplete(List<ScenesData> scenesDataList);

        void onError(String errMsg);

        void onProgress(String content, double percent);
    }
}
