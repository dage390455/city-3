package com.smartcity.blelib.sdk_config;

/**
 * Created by lianxiang on 2017/9/14.
 * SDK全局变量
 * 打sdk包时，服务ID与协议视情况指定。
 */

public class SDKConfig {
    public static String UUID_SERVICE = null;
    public final static String TWO4_UUID_SERVICE = "0000fee9-0000-1000-8000-00805f9b34fb";
    //24协议service
    public final static String WX_UUID_SERVICE = "0000fee7-0000-1000-8000-00805f9b34fb";
    //微信Service
    public static String PROTOCAL = ProtocolConfig.PROTOCAL_24;
    public final static long COMMAND_RE_EXECUTE_TIME = 300;  //指令再次发起请求间隔的时间，毫秒作单位。
    public static WxWrapperProtocol PROTOCAL_WX_WRAPPER = WxWrapperProtocol.TWO4;
    //默认微信的包装协议为24协议
    public static String AREA = Area.WX33_QI_LU;     //地区
    public static final int DEVICE_PT = 0x1;
    public static final int DEVICE_WX_COMMON = 0x2;
    public static final int DEVICE_WX_QL_OLD = 0x3;
    public static final int DEVICE_WX_QL_NEW = 0x4;
    public static final int DEVICE_WX_GD = 0x5;

    public static final int DEVICE_DEFAULT = -0x01;
    /**
     * 设备类型
     */
    public static int deviceType = DEVICE_DEFAULT;
    /**
     * sdk的版本号
     */
    public static String SDK_VERSION = "版本号 V1.4.9";

    public static class ProtocolConfig {
        public final static String PROTOCAL_24 = "PROTOCAL_24";      //24协议
        public final static String PROTOCAL_WX = "PROTOCAL_WX";      //WX协议
    }

    public interface Area {
        String COMMON = "通用";
        String WX33_QI_LU = "齐鲁";
    }

    /**
     * 微信包装协议
     */
    public enum WxWrapperProtocol {
        TWO4,               //24协议
        DOUBLE3             //33协议
    }
}
