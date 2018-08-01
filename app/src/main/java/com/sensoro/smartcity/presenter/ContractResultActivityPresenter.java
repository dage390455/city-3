package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IContractResultActivityView;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ContractResultActivityPresenter extends BasePresenter<IContractResultActivityView> {
    private Activity mContext;
    private String code;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        code = mContext.getIntent().getStringExtra("code");
        processCode();
    }

    private void processCode() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(code, BGAQRCodeUtil.dp2px(mContext, 150));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    getView().setImageBitmap(bitmap);
                } else {
                    getView().toastShort("生成中文二维码失败");
                }
            }
        }.execute();
    }
}
