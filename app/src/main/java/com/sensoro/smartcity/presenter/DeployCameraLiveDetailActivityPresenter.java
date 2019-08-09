package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployCameraLiveDetailActivityView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;


public class DeployCameraLiveDetailActivityPresenter extends BasePresenter<IDeployCameraLiveDetailActivityView> {
    private Activity mActivity;
    private DeployAnalyzerModel deployAnalyzerModel;
    private ArrayList<String> urlList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        getView().setTitle("观看直播");
        Serializable extra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        if (extra instanceof DeployAnalyzerModel) {
//            getView().initVideoOption(extra.get);
            deployAnalyzerModel = (DeployAnalyzerModel) extra;


            urlList.add(deployAnalyzerModel.flv);
            urlList.add(deployAnalyzerModel.hls);


            getView().doPlayLive(urlList, "");
        } else {
            getView().toastShort(mActivity.getString(R.string.tips_data_error));
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 网络改变状态
     *
     * @param eventData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == Constants.NetworkInfo) {
            int data = (int) eventData.data;

            switch (data) {

                case ConnectivityManager.TYPE_WIFI:
                    getView().getPlayView().setCityPlayState(-1);
                    getView().doPlayLive(urlList, "");


                    break;

                case ConnectivityManager.TYPE_MOBILE:

                    if (isAttachedView()) {
                        getView().getPlayView().setCityPlayState(2);
                        getView().setVerOrientationUtilEnable(false);
                        getView().getPlayView().getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getView().getPlayView().setCityPlayState(-1);
                                getView().setVerOrientationUtilEnable(true);
                                getView().doPlayLive(urlList, "");

                            }
                        });


                        getView().backFromWindowFull();
                    }


                    break;

                case -1:
                    if (isAttachedView()) {
                        getView().getPlayView().setCityPlayState(1);
                    }
                    break;


                default:
                    break;

            }
        } else if (code == Constants.VIDEO_START) {

            getView().doPlayLive(urlList, "");

        } else if (code == Constants.VIDEO_STOP) {
            getView().setVerOrientationUtilEnable(false);
            GSYVideoManager.onPause();

            getView().backFromWindowFull();


        }
    }

    public void doRetry() {
        getView().doPlayLive(urlList, "");
    }
}
