package com.sensoro.smartcity.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sensoro.smartcity.util.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetWorkUtils {
    /* @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuilder = new StringBuilder();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuilder.append(content);
            }
            LogUtils.logd("------ping-----", "result content : " + stringBuilder.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            LogUtils.loge("----result---", "result = " + result);
        }
        return false;
    }

    /**
     * 物理判断网络可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
