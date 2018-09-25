package com.smartcity.blelib.ble.exception;

import java.io.Serializable;

/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　         神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 * <p>
 * <p>
 * Created by Done on 2017/8/25.
 */

public abstract class BleException implements Serializable {
    private static final long serialVersionUID = 8004414918500865564L;

    public static final int ERROR_CODE_TIMEOUT = 100;
    public static final int ERROR_CODE_GATT = 101;
    public static final int ERROR_CODE_OTHER = 102;
    public static final int ERROR_CODE_NOT_FOUND_DEVICE = 103;
    public static final int ERROR_CODE_BLUETOOTH_NOT_ENABLE = 104;
    public static final int ERROR_CODE_SCAN_FAILED = 105;
    public static final int ERROR_CODE_DEVICE_NOT_SUPPORTED = 106;


    private int code;
    private String description;

    public BleException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public BleException setCode(int code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BleException setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "BleException { " +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
