package com.sensoro.smartcity.server.download;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadListener downloadListener;
    private BufferedSource bufferedSource;
    private Executor executor;
    private final double divisor;

    public DownloadResponseBody(ResponseBody responseBody, Executor executor, DownloadListener downloadListener) {
        this.responseBody = responseBody;
        this.downloadListener = downloadListener;
        this.executor = executor;
        divisor = 1024 * 1024;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    final long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    if (null != downloadListener) {
                        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                        final long fileSize = responseBody.contentLength();
                        final int progress = (int) (totalBytesRead * 100 / fileSize);

                        double l = fileSize / divisor;
                        double l1 = totalBytesRead /divisor;

                        final DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        final String downloadSize = decimalFormat.format(l1);
                        final String fileSizeStr = decimalFormat.format(l);
                        if (executor != null) {
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    downloadListener.onProgress(progress,downloadSize, fileSizeStr);
                                }
                            });
                        } else {
                            downloadListener.onProgress(progress, downloadSize, fileSizeStr);
                        }
                    }
                    return bytesRead;
                } catch (IOException e) {
                    e.printStackTrace();
                    //返回-2 表示异常
                    return -2;
                }
            }
        };
    }
}
