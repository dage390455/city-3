package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractResultActivityView;
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
        Constants {
    private Activity mContext;
    private String code;
    private Bitmap bitmapTemp;
    private boolean needFinish = true;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        code = mContext.getIntent().getStringExtra("code");
        needFinish = mContext.getIntent().getBooleanExtra(EXTRA_CONTRACT_RESULT_TYPE, false);
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
            }
        }.execute();
    }

    public void sharePic() {
        boolean wxAppInstalled = SensoroCityApplication.getInstance().api.isWXAppInstalled();
        if (wxAppInstalled) {
//            boolean wxAppSupportAPI = SensoroCityApplication.getInstance().api.isWXAppSupportAPI();
//            if (wxAppSupportAPI) {
                toShareWeChat();
//            } else {
//                getView().toastShort("当前版的微信不支持分享功能");
//            }
        } else {
            getView().toastShort("当前手机未安装微信，请安装后重试");
        }
    }

    private void toShareWeChat() {
        WXImageObject wxImageObject = new WXImageObject(bitmapTemp);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxImageObject;
        //
        byte[] ratio = ImageFactory.ratio(bitmapTemp, 31);
        wxMediaMessage.thumbData = ratio;
        //
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        boolean b = SensoroCityApplication.getInstance().api.sendReq(req);
        LogUtils.loge("toShareWeChat: isSuc = " + b + ",bitmap_ratio = " + ratio.length + ",bitmapLength = " +
                bitmapTemp.getByteCount());
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
