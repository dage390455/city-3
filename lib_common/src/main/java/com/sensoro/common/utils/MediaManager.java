package com.sensoro.common.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.sensoro.common.R;

import java.util.HashMap;


public class MediaManager {

    private Context mContext;
    /**
     * 声音开关 背景音乐
     */
    private boolean blnOpenBgSound;

    /**
     * 声音开关 特效音乐
     */
    private boolean blnOpenEffectSound;

    /**
     * media 背景音乐
     */
    public static final int STATIC_MEDIA_TYPE_BGSOUND = 0;

    public static final int STATIC_MEDIA_TYPE_COUNT = STATIC_MEDIA_TYPE_BGSOUND + 1;

    private int[] mediaListID = {
            R.raw.alarm
    };

    /**
     * sound
     */
    public static final int STATIC_SOUND_TYPE_DINGDONG = 0;

    public static final int STATIC_SOUND_TYPE_COUNT = STATIC_SOUND_TYPE_DINGDONG + 1;

    private int[] soundListID = {
            R.raw.alarm
    };


    private final int maxStreams = 10; //streamType音频池的最大音频流数目为10
    private final int srcQuality = 100;
    private final int soundPriority = 1;
    private final float soundSpeed = 1f;//播放速度 0.5 -2 之间

    /**
     * 游戏音效
     */
    private SoundPool soundPool;
    private HashMap <Integer, Integer> soundPoolMap;
    private HashMap <Integer, MediaPlayer> mediaMap;

    private static MediaManager mediaManager;

    private MediaManager(Context context){
        initMediaPlayer();
        initSoundPool();
    }

    /***
     * 实例MediaManager
     * @return
     */
    public static MediaManager getInstance(Context context){

        if(mediaManager == null){
            mediaManager = new MediaManager(context);
        }
        return mediaManager;
    }

    /***
     * 是否开启背景音乐
     */
    public void setOpenBgState(boolean bgSound){
        blnOpenBgSound = bgSound;
        if(!bgSound && mediaMap != null){
            for (int i = 0; i < mediaMap.size(); i++) {
                mediaMap.get(i).pause();
            }
        }
    }
    /***
     * 是否开启特效音乐
     */
    public void setOpenEffectState(boolean effectSound){
        blnOpenEffectSound = effectSound;
        if(!effectSound && soundPoolMap != null){
            for (int i = 0; i < soundPoolMap.size(); i++) {
                soundPool.pause(soundPoolMap.get(i));
            }
        }
    }


    private void initMediaPlayer(){
        mediaMap = new HashMap<Integer, MediaPlayer>();
        for (int i = 0; i < STATIC_SOUND_TYPE_COUNT; i++) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, mediaListID[i]);
            mediaMap.put(i, mediaPlayer);
        }
    }

    private void initSoundPool(){
        soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, srcQuality);
        soundPoolMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < STATIC_SOUND_TYPE_COUNT; i++) {
            soundPoolMap.put(i, soundPool.load(mContext, soundListID[i], soundPriority));
        }
    }

    /**
     * 播放MediaPlayer音乐
     */
    public void playMedia(int mediaType){
        if(!blnOpenBgSound){
            return;
        }
        MediaPlayer mediaPlayer = mediaMap.get(mediaType);
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    /**
     * 暂停MediaPlayer音乐
     */
    public void pauseMedia(int mediaType){
        MediaPlayer mediaPlayer = mediaMap.get(mediaType);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    /**
     * 播放soundPlayer音乐
     */
    public void playSound(int soundID, int loop){
        if(!blnOpenEffectSound){
            return;
        }
        AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundPool.play(soundPoolMap.get(soundID), volume, volume, soundPriority, loop, soundSpeed);
    }

    /**
     * 播放soundPlayer音乐
     */
    public void pauseSound(int soundID){
        soundPool.pause(soundPoolMap.get(soundID));
    }
}
