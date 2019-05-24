package com.sensoro.common.server.download;

import java.io.File;

/**
 * Created by liuyang on 2016/12/20.
 */

public interface DownloadListener {
    void onFinish(File file);
    void onProgress(int progress, String totalBytesRead, String fileSize);
    void onFailed(String errMsg);
}