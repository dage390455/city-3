package com.sensoro.common.server.download;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 15;
    private final DownloadService api;
    private final Executor executor;
    private final DownloadListener listener;
    private Call<ResponseBody> responseBodyCall;


    public DownloadUtil(DownloadListener listener) {
        this.listener = listener;
        executor = new MainThreadExecutor();
        DownloadInterceptor interceptor = new DownloadInterceptor(executor,listener);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        api = new Retrofit.Builder()
                .client(client)
//                .baseUrl("https://city-video-cdn.sensoro.com/")
                .baseUrl(RetrofitServiceHelper.getInstance().BASE_URL)
                .build()
                .create(DownloadService.class);
    }

    public void cancelDownload() {
        if (responseBodyCall != null) {
            responseBodyCall.cancel();
        }
    }

    public void downloadFile(final String rUrl, final String filePath ) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    responseBodyCall = api.downloadWithDynamicUrl(rUrl);
                    Response<ResponseBody> result = responseBodyCall.execute();
                    ResponseBody body = result.body();
                    if (body == null) {
                        if (listener != null) {
                            try {
                                LogUtils.loge("response为空");
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            listener.onFailed("response为空");
                        }
                        return;
                    }
                    final File file = writeFile(filePath, body.byteStream());
                    if (listener != null){
                        final boolean isSuccess = file.length() == body.contentLength();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    listener.onFinish(file);
                                }else{
                                    try {
                                        LogUtils.loge("未下载完完成，失败");
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                    listener.onFailed("未下载完成");
                                }

                            }
                        });
                    }

                } catch (final Exception e) {
                    if (listener != null){
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailed(e.getMessage());
                            }
                        });

                    }
                    e.printStackTrace();
                }
            }
        });

           }

    private File writeFile(String filePath, InputStream ins) {
        if (ins == null)
            return null;
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = ins.read(b)) != -1) {
                if(len == -2){
                    //返回值在DownloadResponseBody.java的source()里面，表示出现异常
                    try {
                        LogUtils.loge("网络异常，返回-2");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    throw new DownloadException("网络异常",new SocketException());
                }
                fos.write(b, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException(e.getMessage(), e);
        } finally {
            try {
                ins.close();
                if (fos != null) {
                    fos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private class DownloadException extends RuntimeException {
         DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private class MainThreadExecutor implements Executor
    {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable r)
        {
            handler.post(r);
        }
    }
}
