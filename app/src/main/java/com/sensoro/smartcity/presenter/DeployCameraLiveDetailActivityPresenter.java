package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployCameraLiveDetailActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import static com.sensoro.smartcity.constant.Constants.NetworkInfo;

public class DeployCameraLiveDetailActivityPresenter extends BasePresenter<IDeployCameraLiveDetailActivityView> {
    private Activity mActivity;
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        getView().setTitle("观看直播");
        Serializable extra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        if (extra instanceof DeployAnalyzerModel) {
//            getView().initVideoOption(extra.get);
            deployAnalyzerModel = (DeployAnalyzerModel) extra;
            getView().doPlayLive(deployAnalyzerModel.hls, "");
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
        if (code == NetworkInfo) {
            int data = (int) eventData.data;

            switch (data) {

                case ConnectivityManager.TYPE_WIFI:
//                    if (gsyPlayerAcCameraDetail..getVisibility() == VISIBLE) {
//                        rMobileData.setVisibility(GONE);
//                        GSYVideoManager.onResume();
//                    }

                    break;

                case ConnectivityManager.TYPE_MOBILE:

                    GSYVideoManager.onPause();
                    if (isAttachedView()) {
                        getView().getPlayView().setCityPlayState(2);
                        getView().getPlayView().getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GSYVideoManager.onResume();
//                            gsyPlayerAcCameraDetail.startPlayLogic();

                            }
                        });
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
        }
    }

    public void doRetry() {
        getView().doPlayLive(deployAnalyzerModel.hls, "");
    }
}
