package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.ScanLoginResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IScanLoginFragmentView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.LogUtils;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanLoginFragmentPresenter extends BasePresenter<IScanLoginFragmentView> implements
        MediaPlayer.OnErrorListener {
    private Activity mContext;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mediaPlayer = buildMediaPlayer(mContext);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void scanFinish(final String qrcodeId) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getLoginScanResult(qrcodeId).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {
            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(ResponseBase responseBase) {
                if (responseBase.getErrcode() == 0) {
                    refresh(qrcodeId);
                } else {
                    getView().toastShort("请重新扫描后重试");
                    getView().startScan();
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().startScan();
            }
        });
    }


    private void refresh(String qrcodeId) {
        try {
            LogUtils.loge("qrcodeId = " + qrcodeId);
            Intent intent = new Intent();
            intent.setClass(mContext, ScanLoginResultActivity.class);
            intent.putExtra("qrcodeId", qrcodeId);
            getView().startAC(intent);
//            getView().startACForResult(intent, REQUEST_CODE_POINT_DEPLOY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private void playVoice() {
        vibrate();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void processResult(String result) {
        playVoice();
        int currentIndex = ((MainActivity) mContext).getSensoroPager().getCurrentItem();
        System.out.println("currentIndex==>" + currentIndex);
        if (currentIndex != 5) {
            return;
        }
        if (TextUtils.isEmpty(result)) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
            getView().startScan();
            return;
        }
        scanFinish(result);
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
            LogUtils.loge(this, ioe.getMessage());
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
