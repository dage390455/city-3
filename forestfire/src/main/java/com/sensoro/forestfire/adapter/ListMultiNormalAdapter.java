package com.sensoro.forestfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.forestfire.R;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CustomManager;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.MultiSampleVideo;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个播放的listview adapter
 */

public class ListMultiNormalAdapter extends BaseAdapter {

    public static final String TAG = "ListMultiNormalAdapter";

    private LayoutInflater inflater = null;
    private Context context;

    private String fullKey = "null";


    private final boolean available;
    private final boolean wifiConnected;
    private ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> list = new ArrayList<>();

    public void setState(int state) {
        for (ForestFireCameraDetailInfo.MultiVideoInfoBean videoModel : list) {
            videoModel.state = state;
        }
        notifyDataSetChanged();

    }

    public ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> getData() {

        return list;
    }


    public void updataAdapter(List<ForestFireCameraDetailInfo.MultiVideoInfoBean> multiVideoInfo) {
        this.list.clear();
        this.list.addAll(multiVideoInfo);
        for (ForestFireCameraDetailInfo.MultiVideoInfoBean videoModel : list) {
            if (!available) {
                videoModel.state = 1;
            }
            if (!wifiConnected) {
                videoModel.state = 2;
            }

        }
        notifyDataSetChanged();
    }

    public ListMultiNormalAdapter(Context context) {
        super();
        this.context = context;
        available = NetworkUtils.isAvailable(context);
        wifiConnected = NetworkUtils.isWifiConnected(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_video_item_mutli, null);
            holder.gsyVideoPlayer = (MultiSampleVideo) convertView.findViewById(R.id.video_item_player);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //多个播放时必须在setUpLazy、setUp和getGSYVideoManager()等前面设置
        holder.gsyVideoPlayer.setPlayTag(TAG);
        holder.gsyVideoPlayer.setPlayPosition(position);

        boolean isPlaying = holder.gsyVideoPlayer.getCurrentPlayer().isInPlayingState();


        holder.gsyVideoPlayer.setIsLive(View.INVISIBLE);
        ForestFireCameraDetailInfo.MultiVideoInfoBean videoModel = list.get(position);
        if (!isPlaying) {

            holder.gsyVideoPlayer.setCityURl(list, "");
            holder.gsyVideoPlayer.startPlayLogic();
        }

        //增加title
        holder.gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

        //设置返回键
        holder.gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

        //设置全屏按键功能
        holder.gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(holder.gsyVideoPlayer);
            }
        });
        String gsyVideoPlayerKey = holder.gsyVideoPlayer.getKey();


        holder.gsyVideoPlayer.setRotateViewAuto(false);
        holder.gsyVideoPlayer.setLockLand(true);
        holder.gsyVideoPlayer.setReleaseWhenLossAudio(false);
        holder.gsyVideoPlayer.setShowFullAnimation(false);
        holder.gsyVideoPlayer.setIsTouchWiget(false);

        holder.gsyVideoPlayer.setNeedLockFull(true);


        holder.gsyVideoPlayer.setIsShowMaskTopBack(false);
        holder.gsyVideoPlayer.setCityPlayState(-1);
        holder.gsyVideoPlayer.mCoverImage.setVisibility(View.VISIBLE);


//        holder.gsyVideoPlayer.loadCoverImage(videoModel.getLastCover(), R.drawable.camera_detail_mask);


        if (videoModel.state == -1) {
            holder.gsyVideoPlayer.setCityPlayState(-1);

            holder.gsyVideoPlayer.setCityURl(list, "");
            holder.gsyVideoPlayer.startPlayLogic();
            videoModel.state = -10;
        } else if (videoModel.state == 2) {
            holder.gsyVideoPlayer.setCityPlayState(2);
            CustomManager.backFromWindowFull(context, gsyVideoPlayerKey);
            holder.gsyVideoPlayer.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.gsyVideoPlayer.getMaskLayoutTop().setVisibility(View.GONE);
                    holder.gsyVideoPlayer.getrMobileData().setVisibility(View.GONE);
                    holder.gsyVideoPlayer.setCityURl(list, "");
                    holder.gsyVideoPlayer.startPlayLogic();
                    videoModel.state = -10;


                }
            });

        } else if (videoModel.state == 1) {
            holder.gsyVideoPlayer.setCityPlayState(1);
            CustomManager.backFromWindowFull(context, gsyVideoPlayerKey);


        }

        holder.gsyVideoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {

            @Override
            public void onPlayError(final String url, Object... objects) {

                holder.gsyVideoPlayer.setCityPlayState(3);
                CustomManager.backFromWindowFull(context, gsyVideoPlayerKey);
                holder.gsyVideoPlayer.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.gsyVideoPlayer.setCityPlayState(-1);
                        holder.gsyVideoPlayer.setCityURl(list, "");
                        holder.gsyVideoPlayer.startPlayLogic();
                        videoModel.state = -10;
                    }
                });
            }

            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);

                holder.gsyVideoPlayer.mCoverImage.setVisibility(View.GONE);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                fullKey = "null";
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                holder.gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                fullKey = holder.gsyVideoPlayer.getKey();
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                CustomManager.backFromWindowFull(context, gsyVideoPlayerKey);

            }
        });

        return convertView;
    }

    /**
     * 全屏幕按键处理
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(context, false, true);
    }

    class ViewHolder {
        MultiSampleVideo gsyVideoPlayer;
    }


    public String getFullKey() {
        return fullKey;
    }

}
