package com.sensoro.nameplate.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.sensoro.common.R;
import com.sensoro.common.widgets.CustomCornerDialog;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class QrCodeDialogUtils {

    private CustomCornerDialog mDialog;

    private ImageView qrcodeIv, closeIv;
    private Activity activity;

    public QrCodeDialogUtils(Activity activity) {
        this.activity = activity;
        View view = View.inflate(activity, R.layout.qrcode_dialog_tip, null);
        qrcodeIv = view.findViewById(R.id.qrcode_iv);
        closeIv = view.findViewById(R.id.close_iv);

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

    }


    public void setImageUrl(String imageUrl) {


        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(imageUrl, BGAQRCodeUtil.dp2px(activity, 250));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    qrcodeIv.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destory() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }


}
