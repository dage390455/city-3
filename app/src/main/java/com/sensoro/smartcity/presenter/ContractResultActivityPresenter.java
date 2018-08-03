package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractResultActivityView;
import com.sensoro.smartcity.iwidget.IOnDestroy;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.ImageFactory;
import com.sensoro.smartcity.util.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import org.greenrobot.eventbus.EventBus;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ContractResultActivityPresenter extends BasePresenter<IContractResultActivityView> implements
        IOnDestroy, Constants {
    private Activity mContext;
    private String code;
    private Bitmap bitmapTemp;
    private boolean needFinish = true;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        code = mContext.getIntent().getStringExtra("code");
        needFinish = mContext.getIntent().getBooleanExtra(EXTRA_CONTRACT_RESULT_TYPE, true);
        if (needFinish) {
            getView().setTextResultInfo("业主扫描此二维码，生成合同预览");
        } else {
            getView().setTextResultInfo("业主扫描此二维码，查看合同详情");
        }
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
                bitmapTemp = bitmap;
                if (bitmapTemp != null) {
                    getView().setImageBitmap(bitmapTemp);
                } else {
                    getView().toastShort("生成二维码失败");
                }
                bitmap = null;
            }
        }.execute();
    }

    public void sharePic() {
        WXImageObject wxImageObject = new WXImageObject(bitmapTemp);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxImageObject;
        //
        wxMediaMessage.thumbData = ImageFactory.ratio(bitmapTemp);
        //
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        boolean b = SensoroCityApplication.getInstance().api.sendReq(req);
        LogUtils.loge("toShareWeChat: isSuc = " + b + ",bitmapLength = " + bitmapTemp.getByteCount());
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (bitmapTemp != null) {
            bitmapTemp.recycle();
            bitmapTemp = null;
        }
    }

    public void finish() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_FINISH_CODE;
        eventData.data = needFinish;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
