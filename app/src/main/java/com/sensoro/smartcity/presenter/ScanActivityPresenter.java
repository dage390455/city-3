package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.InspectionIndexTaskInfo;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.analyzer.DeployAnalyzerUtils;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.Serializable;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanActivityPresenter extends BasePresenter<IScanActivityView> implements IOnCreate,
        MediaPlayer.OnErrorListener {
    private Activity mContext;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;
    public int scanType = -1;
    private InspectionTaskDeviceDetail mDeviceDetail;
    private InspectionIndexTaskInfo mTaskInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra(Constants.EXTRA_SCAN_ORIGIN_TYPE, -1);
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(Constants.EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO);
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(Constants.EXTRA_INSPECTION_INDEX_TASK_INFO);
        mediaPlayer = buildMediaPlayer(mContext);
        updateTitle();
    }

    private void updateTitle() {
        switch (scanType) {
            case Constants.TYPE_SCAN_NAMEPLATE_ASSOCIATE_DEVICE:

            case Constants.TYPE_SCAN_DEPLOY_STATION:
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().updateTitleText(mContext.getString(R.string.device_deployment));
                getView().updateQrTipText(mContext.getString(R.string.device_deployment_tip));
                break;
            case Constants.TYPE_SCAN_LOGIN:
                getView().updateTitleText(mContext.getString(R.string.scan_login));
                getView().updateQrTipText(mContext.getString(R.string.scan_login_tip));
                getView().setBottomVisible(false);
                break;
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检/故障设备更换
                getView().updateTitleText(mContext.getString(R.string.device_change));
                getView().updateQrTipText(mContext.getString(R.string.device_change_tip));
                break;
            case Constants.TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                getView().updateTitleText(mContext.getString(R.string.device_inspetion));
                getView().updateQrTipText(mContext.getString(R.string.device_inspetion_tip));
                break;
            case Constants.TYPE_SCAN_SIGNAL_CHECK:
                //信号测试
                getView().updateTitleText(mContext.getString(R.string.signal_test));
                getView().updateQrTipText(mContext.getString(R.string.signal_test_tip));
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().finishAc();
                break;
            case Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                if (Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE == scanType || Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE == scanType) {
                    getView().finishAc();
                }
                break;
            case Constants.EVENT_DATA_SCAN_LOGIN_SUCCESS:
                getView().finishAc();
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void openSNTextAc() {
        Intent intent = new Intent(mContext, DeployManualActivity.class);
        intent.putExtra(Constants.EXTRA_INSPECTION_INDEX_TASK_INFO, mTaskInfo);
        intent.putExtra(Constants.EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, mDeviceDetail);
        intent.putExtra(Constants.EXTRA_SCAN_ORIGIN_TYPE, scanType);
        getView().startAC(intent);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    private void playVoice() {
        vibrate();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void processResult(String result) {
        playVoice();
        getView().showProgressDialog();


        if (scanType == Constants.TYPE_SCAN_NAMEPLATE_ASSOCIATE_DEVICE) {

            String nameplateId = mContext.getIntent().getStringExtra("nameplateId");

            DeployAnalyzerUtils.getInstance().handlerDeployAnalyzerResult(this, result, mContext, nameplateId, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
                @Override
                public void onSuccess(Intent intent) {
                    getView().dismissProgressDialog();
                    //更新列表，关闭界面

                    EventData data = new EventData(Constants.EVENT_DATA_ASSOCIATE_SENSOR_FROM_DETAIL);
                    EventBus.getDefault().post(data);
                    getView().finishAc();
                }

                @Override
                public void onError(int errType, Intent intent, String errMsg) {
                    getView().dismissProgressDialog();
                    if (intent != null) {
                        getView().startAC(intent);
                    } else {
                        getView().toastShort(errMsg);
                        getView().startScan();
                    }
                }
            });

        } else {


            DeployAnalyzerUtils.getInstance().handlerDeployAnalyzerResult(this, scanType, result, mContext, mTaskInfo, mDeviceDetail, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
                @Override
                public void onSuccess(Intent intent) {
                    getView().dismissProgressDialog();
                    if (intent != null && !TextUtils.isEmpty(intent.getStringExtra(ARouterConstants.AROUTER_PATH))) {
                        Serializable serializableExtra = intent.getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
                        if (serializableExtra instanceof DeployAnalyzerModel) {
                            DeployAnalyzerModel model = (DeployAnalyzerModel) serializableExtra;
                            if (model.deployNameplateFlag != null && model.deployNameplateFlag) {
                                startActivity(ARouter.getInstance().build(ARouterConstants.ACTIVITY_NAMEPLATE_DETAIL).withSerializable(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, serializableExtra), mContext);
                            } else {
                                startActivity(ARouter.getInstance().build(ARouterConstants.ACTIVITY_DEPLOY_NAMEPLATE).withSerializable(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, serializableExtra), mContext);
                            }
                        } else {
                            getView().toastShort("error");
                        }
                    } else if (scanType == Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY) {
                        Serializable serializableExtra = intent.getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);

                        if (null != serializableExtra && (serializableExtra instanceof DeployAnalyzerModel)) {
                            DeployAnalyzerModel model = (DeployAnalyzerModel) serializableExtra;

                            NamePlateInfo namePlateInfo = new NamePlateInfo();

                            namePlateInfo.setSn(model.sn);
                            namePlateInfo.setDeviceType(model.deviceType);
                            namePlateInfo.setName(model.nameAndAddress);


                            EventData data = new EventData(Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY);
                            data.data = namePlateInfo;
                            EventBus.getDefault().post(data);
                            getView().finishAc();

                        }


                    } else {
                        getView().startAC(intent);
                    }
//
                }

                @Override
                public void onError(int errType, Intent intent, String errMsg) {
                    getView().dismissProgressDialog();
                    if (intent != null) {
                        getView().startAC(intent);
                    } else {
                        getView().toastShort(errMsg);
                        getView().startScan();
                    }
                }
            });
        }
    }

    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try (AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep)) {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException ioe) {
            try {
                LogUtils.loge(this, ioe.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
