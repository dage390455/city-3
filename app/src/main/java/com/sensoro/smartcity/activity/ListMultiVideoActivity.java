package com.sensoro.smartcity.activity;

import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ListMultiNormalAdapter;
import com.sensoro.smartcity.imainviews.IMutilCameraView;
import com.sensoro.smartcity.presenter.MutilCamerPresenter;
import com.shuyu.gsyvideoplayer.utils.CustomManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 多个同时播放的demo
 */
public class ListMultiVideoActivity extends BaseActivity<IMutilCameraView, MutilCamerPresenter> {

    @BindView(R.id.video_list)
    ListView videoList;
    @BindView(R.id.view_top_ac_alarm_camera_video_detail)
    View viewTopAcAlarmCameraVideoDetail;
    ListMultiNormalAdapter listMultiNormalAdapter;

    private boolean isPause;

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

                    listMultiNormalAdapter.setState(-1);

                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    listMultiNormalAdapter.setState(2);
                    CustomManager.onPauseAll();

                    break;

                default:

                    listMultiNormalAdapter.setState(1);
                    CustomManager.onPauseAll();
                    break;


            }
        }
    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {

        setContentView(R.layout.activity_list_video);
        ButterKnife.bind(this);
        initViewHeight();
        EventBus.getDefault().register(this);

        listMultiNormalAdapter = new ListMultiNormalAdapter(this);
        videoList.setAdapter(listMultiNormalAdapter);

        updataAdapter();

    }

    private void updataAdapter() {
        List<ForestFireCameraDetailInfo.MultiVideoInfoBean> list = new ArrayList<>();

        ForestFireCameraDetailInfo.MultiVideoInfoBean infoBean = new ForestFireCameraDetailInfo.MultiVideoInfoBean();
        infoBean.setHls("https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.m3u8?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd");
        infoBean.setFlv("https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.flv?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd&decryption=1");
        infoBean.setLastCover("https://imgsa.baidu.com/news/q%3D100/sign=1667cb956381800a68e58d0e813533d6/38dbb6fd5266d016ce1bac32982bd40735fa35ad.jpg");

        list.add(infoBean);

        ForestFireCameraDetailInfo.MultiVideoInfoBean videoInfoBean = new ForestFireCameraDetailInfo.MultiVideoInfoBean();
        videoInfoBean.setHls("https://scpub-api.antelopecloud.cn/cloud/v2/live/540672048.m3u8?client_token=540672048_3356491776_1600138489_db11210a990723fea8f00caf55c0e57c");
        videoInfoBean.setFlv("https://scpub-api.antelopecloud.cn/cloud/v2/live/540672048.flv?client_token=540672048_3356491776_1600138489_db11210a990723fea8f00caf55c0e57c&decryption=1");

        videoInfoBean.setLastCover("https://imgsa.baidu.com/news/q%3D100/sign=29cbd7bfde43ad4ba02e42c0b2035a89/e824b899a9014c089efe02bf057b02087af4f4e7.jpg");

        list.add(videoInfoBean);


        listMultiNormalAdapter.updataAdapter(list);
    }

    private void initViewHeight() {
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int result = this.getResources().getDimensionPixelSize(resourceId);
            ViewGroup.LayoutParams lp = viewTopAcAlarmCameraVideoDetail.getLayoutParams();
            lp.height = result;
            viewTopAcAlarmCameraVideoDetail.setLayoutParams(lp);
        }
    }


    @Override
    public boolean setMyCurrentStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.statusBarDarkFont(true).statusBarColor(R.color.white).init();
        return true;
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Translucent);
        return true;
    }

    //
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

//            listMultiNormalAdapter.setState(-1);

            CustomManager.clearAllVideo();

            videoList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listMultiNormalAdapter = new ListMultiNormalAdapter(ListMultiVideoActivity.this);
                    videoList.setAdapter(listMultiNormalAdapter);

                    updataAdapter();
                }
            }, 100);


        }
    }

    @Override
    public void onBackPressed() {
        if (CustomManager.backFromWindowFull(this, listMultiNormalAdapter.getFullKey())) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomManager.onPauseAll();
        isPause = true;
    }

    @Override
    protected MutilCamerPresenter createPresenter() {
        return new MutilCamerPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomManager.onResumeAll();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        CustomManager.clearAllVideo();
    }


}
