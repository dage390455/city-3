package com.sensoro.smartcity.temp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.antelope.sdk.ACMediaInfo;
import com.antelope.sdk.ACMessageListener;
import com.antelope.sdk.ACMessageType;
import com.antelope.sdk.ACResult;
import com.antelope.sdk.ACResultListener;
import com.antelope.sdk.capturer.ACFrame;
import com.antelope.sdk.capturer.ACSampleFormat;
import com.antelope.sdk.capturer.ACShape;
import com.antelope.sdk.codec.ACCodecID;
import com.antelope.sdk.codec.ACStreamPacket;
import com.antelope.sdk.player.ACPlayer;
import com.antelope.sdk.player.ACPlayerExtra;
import com.antelope.sdk.player.ACPlayerStatus;
import com.antelope.sdk.streamer.ACProtocolType;
import com.antelope.sdk.streamer.ACStreamer;
import com.antelope.sdk.streamer.ACStreamerFactory;
import com.antelope.sdk.utils.WorkThreadExecutor;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.DeviceCameraListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.temp.entity.DeviceDetailEntity;
import com.sensoro.smartcity.temp.entity.LmBaseResponseEntity;

import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class CameraTest extends AppCompatActivity {
    public static final String KEY_TOKEN = "app_token";

    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_PASSWORD = "password";
    private static final String TAG = "CameraTest";

    @BindView(R.id.video_play_progress)
    View mProgressView;
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    /**
     * 摄像机ID，目前为demo代码，暂时固定一个id，真实环境下需要动态去获取
     * <br>
     * 费家村村委会门口,"id":"72057600540409860","cid":"540409860"
     */
    private String mDeviceId = "72057600540409860";
    private DeviceDetailEntity mDeviceDetailEntity;
    private ACPlayer mPlayer;
    private ACStreamer mStreamer;
    private WorkThreadExecutor mWorkThreadExecutor;
    private boolean mIsPlay;
    private boolean isInit;
    private boolean isLive = true;
    private boolean mIsPlayComplete;
    private boolean mPaused;

    private int mShape;
    private int mACProtocol;
    private int mStartPlayBufferSize;//最小缓冲时长
    private int mStartDropBufferSize;//最大缓冲时长
    private int mVideoCodecId;//视频解码类型
    private int mAudioCodecId;//音频解码类型
    private int mSampleSize;//音频播放采样率
    private int mChannel;//声道

    private Handler mHandler;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("测试");
        }
        mWorkThreadExecutor = new WorkThreadExecutor("VideoPlay");
        mWorkThreadExecutor.start(null);
        mHandler = new Handler(Looper.getMainLooper());
        performGetDeviceInfo(mDeviceId);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause();
            mPlayer.enterBackground();
        }
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInit && mPaused) {
            startPlay();
            mPaused = false;
        } else {
            if (mPlayer != null && mPaused) {
                mPlayer.resume();
                mPlayer.enterForeground();
                mPaused = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        mIsPlay = false;
        isInit = false;
        if (mPlayer != null) {
            mPlayer.clearQueueBuffer();
            mPlayer.release();
        }
        if (mStreamer != null) {
            mStreamer.close();
            mStreamer.release();
        }
        if (mWorkThreadExecutor != null) {
            mWorkThreadExecutor.removeTasksAndMessages(null);
            mWorkThreadExecutor.release();
            mWorkThreadExecutor = null;
        }
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    /**
     * 播放器扩展接口，可获取解码前/后的数据,可进行自定义操作
     */
    private ACPlayerExtra mPlayerExtra = new ACPlayerExtra() {
        /**
         * 视频渲染之前和音频播放之前都会调用 （数据已解码） 自行判断区分音视频
         */
        @Override
        public ACResult onDecodePacket(ACStreamPacket packet, ACFrame frame) {
            return new ACResult(ACResult.ACS_UNIMPLEMENTED, "");
        }

        /**
         * 音视频解码前调用 packet 未解码的数据 frame 放解码后的数据
         */
        @Override
        public ACResult onProcessFrame(ACFrame frame) {
            return new ACResult(ACResult.ACS_UNIMPLEMENTED, "");
        }

        @Override
        public void onPlayerStatus(int status) {
            super.onPlayerStatus(status);
            if (isFinishing()) {
                return;
            }
            if (status == ACPlayerStatus.ACPlayerStatusStartPlay) {
                Log.i(TAG, "onPlayerStatus: ACPlayerStatusStartPlay");
            }
        }
    };

    private boolean initTheData() {
        mShape = ACShape.AC_SHAPE_NONE;
        if (isLive) {
            mACProtocol = ACProtocolType.AC_PROTOCOL_QSTP;
        } else {
            mACProtocol = ACProtocolType.AC_PROTOCOL_OSTP;
        }
        if (mACProtocol == ACProtocolType.AC_PROTOCOL_QSTP) {
            mStartPlayBufferSize = 1000;
            mStartDropBufferSize = 5000;
        } else {
            mStartPlayBufferSize = 0;
            mStartDropBufferSize = 0;
        }

        mVideoCodecId = ACCodecID.AC_CODEC_ID_H264;
        mAudioCodecId = ACCodecID.AC_CODEC_ID_AAC;
        mSampleSize = 16000;

        mChannel = 1;

        mPlayer = new ACPlayer();
        //初始化
        ACPlayer.Config config = new ACPlayer.Config.Builder()
                .setStartPlayBufferSize(mStartPlayBufferSize)
                .setStartDropBufferSize(mStartDropBufferSize)
                .setVideoCodecId(mVideoCodecId)
                .setAudioCodecId(mAudioCodecId)
                .setSampleRate(mSampleSize)
                .setChannelCount(mChannel)
                .setSampleFormat(ACSampleFormat.AC_SAMPLE_FMT_S16)
                .create();

        //解码模式在初始化后可随时动态设置切换
        ACResult result = mPlayer.initialize(config, mPlayerExtra);
        mPlayer.mute();
        if (!result.isResultOK()) {
            return false;
        }

        //设置surfaceview以及播放器形状
        mPlayer.setPlaySurfaceView(mSurfaceView, mShape);
        mStreamer = ACStreamerFactory.createStreamer(mACProtocol);
        if (mStreamer == null) {
            return false;
        }
        Log.i(TAG, "create streamer " + mStreamer);
        result = mStreamer.initialize(this, new ACMessageListener() {
            @Override
            public void onMessage(int type, Object message) {
                switch (type) {
                    case ACMessageType.AC_MESSAGE_DISCONNECTED:
                        Log.v(TAG, "qstp was disconnected!");
                        break;
                    case ACMessageType.AC_MESSSAGE_RECONNECTED:
                        Log.v(TAG, "qstp was reconnected successfully");
                        break;
                    case ACMessageType.AC_MESSAGE_START_PLAY_TIME:
                        if (message != null) {
                            mPlayer.setStartPlayingTimestamp(Long.parseLong(message.toString()));
                            Log.v(TAG, "play time " + message.toString());
                        }
                        break;
                    default:
                        if (message != null) {
                            Log.v(TAG, message.toString());
                        }
                        break;
                }
            }
        });
        return result.isResultOK();
    }

    private void startPlay() {
        //如果当前摄像机详情为空或正在播放 不做任何处理
        if (mIsPlay) return;

        //初始化播放器和流化器
        if (!isInit) {
            isInit = initTheData();
        }
        //开流并播放
        if (isInit) {
            openStreamer();
        } else {
            //初始化失败 隐藏progressbar  显示加载失败按钮
            playFailed();
        }
    }

    private void openStreamer() {
        String playUrl = mDeviceDetailEntity.getPlayUrl();
        mStreamer.open(playUrl, 5000, null, new ACResultListener() {

                    @Override
                    public void onResult(final ACResult status) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "openStreamer code:" + status.getCode());
                                if (status.isResultOK()) {
                                    play();
                                } else {
                                    isInit = false;
                                    playFailed();
                                }
                            }
                        });
                    }
                }
        );
    }

    private void play() {
        mIsPlay = true;
        mWorkThreadExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                int code = 0;
                while (mIsPlay) {
                    final ACStreamPacket packet = new ACStreamPacket();
                    packet.buffer = ByteBuffer.allocateDirect(1024 * 1024);
                    //读取数据
                    ACResult readResult = mStreamer.read(packet, 1000);
                    if (readResult.getCode() != code) {
                        code = readResult.getCode();
                    }

                    String videoCacheInfo = mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_VIDEO_CACHE);
                    String bufferTimeInfo = mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_BUFFER_TIME);
                    // LogUtils.w("cxm", "code = "+readResult.getCode()+"videoCacheInfo  = " + videoCacheInfo + ",bufferTimeInfo=" + bufferTimeInfo + ",delayTimeInfo=" + delayTimeInfo);
                    if (!mIsPlayComplete && readResult.getCode() == ACResult.ACC_OSTP_CHANNEL_CLOSE
                            && "0".equals(videoCacheInfo) && "0".equals(bufferTimeInfo)) {
                        mIsPlayComplete = true;
                    }
                    if (readResult.getCode() == ACResult.ACC_QSTP_READ_FAILED) {
                        mIsPlay = false;
                        //读取失败表示连接已断开
                        if (mHandler != null)
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    playFailed();
                                }
                            });
                    } else if (readResult.isResultOK()) {
                        mPlayer.playFrame(packet);
                    }

                }
            }
        });
    }

    private void playFailed() {
        Toast.makeText(this, "播放失败！", Toast.LENGTH_SHORT).show();
    }


    private void performGetDeviceInfo(String deviceId) {
        RetrofitServiceHelper.getInstance().getDeviceCameraList(null, null, null).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraListRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraListRsp deviceCameraListRsp) {

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

            }
        });
        RetrofitServiceHelper.getInstance().getDeviceCameraMapList().subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(null) {
            @Override
            public void onCompleted(ResponseBase responseBase) {

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

            }
        });

//        RetrofitManager.getInstance(this).getRetrofit()
//                .create(CommonService.class)
//                .getDeviceInfoById(deviceId)
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(disposable -> mProgressView.setVisibility(View.VISIBLE))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doFinally(() -> {
//                    if (isFinishing()) {
//                        return;
//                    }
//                    mProgressView.setVisibility(View.GONE);
//                })
//                .subscribe(new Observer<LmBaseResponseEntity<DeviceDetailEntity>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mCompositeDisposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(LmBaseResponseEntity<DeviceDetailEntity> entity) {
//                        if (entity.isSuccess() && entity.data != null) {
//                            mDeviceDetailEntity = entity.data;
//                            startPlay();
//                        } else {
//                            if (!TextUtils.isEmpty(entity.message)) {
//                                Snackbar.make(mSurfaceView, entity.message,
//                                        Snackbar.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        parseErrorInfo(e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    private void parseErrorInfo(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            Response<?> response = httpException.response();
            ResponseBody errorBody = response.errorBody();
            try {
                assert errorBody != null;
                String json = errorBody.string();
                Gson gson = new Gson();
                LmBaseResponseEntity entity = gson.fromJson(json, LmBaseResponseEntity.class);
                if (entity != null && !TextUtils.isEmpty(entity.message)) {
                    Snackbar.make(mSurfaceView, entity.message,
                            Snackbar.LENGTH_SHORT).show();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JsonParseException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
