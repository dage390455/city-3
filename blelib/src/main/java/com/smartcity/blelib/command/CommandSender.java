package com.smartcity.blelib.command;

import android.content.Context;
import android.util.Log;

/**
 * Created by lianxiang on 2017/8/25.
 * 命令执行者
 */
public class CommandSender {
    private static final String TAG = "CommandSender";
    private Context mContext;
    public static boolean  isShakeSuccess = false;              //握手指令是否执行成功

    private  static CommandSender instance ;

    private CommandSender(){}

    private CommandSender(Context mContext){
        this.mContext= mContext;
    }

    public static CommandSender getInstance(Context context){
        if(instance == null){
            synchronized (CommandSender.class){
                if(instance == null) {
                    instance = new CommandSender(context);
                }
            }
        }
        return instance;
    }

    /**
     * 发送指令
     * @param hexStr
     */
    public void sendCommand(final String hexStr){
        String name = Thread.currentThread().getName();
        Log.d(TAG, "sendCommand -thread: " + name);
//        byte[] bytes = DataTransfer.hexStr2ByteArr(hexStr);
//        BleManager.getInstance(mContext).writeDevice(SDKConfig.UUID_SERVICE,bytes, new BleCharacterCallback() {
//            @Override
//            public void onSuccess(BluetoothGattCharacteristic characteristic) {
//                BleLog.d(TAG,"write onSuccess");
//            }
//
//            @Override
//            public void onFailure(BleException exception) {
//                BleLog.d(TAG,"write onFailure");
//            }
//
//            @Override
//            public void onInitiatedResult(boolean result) {
//                BleLog.d(TAG,"write onInitiatedResult");
//            }
//        });
    }

    /**
     * 指令发送后状态观察者
     */
    public  interface  ISendResultObserver{
        /**
         *
         * @param response 返回的数据
         * @param which 区分是notify / indicate / write   ,0 1 2,防止indicate与write重复.
         */
        void onChange(String response, int which);
        void onWrite();
    }


    public interface IResponseHandleResult{
        void onFinish();    //由于微信取包是同一个command,无法直接在analyse之前pop,需要弄个回调.
    }

    public Context getmContext() {
        return mContext;
    }
}
