package com.sensoro.smartcity.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sensoro.smartcity.SensoroCityApplication;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;


	// IWXAPI 是第三方app和微信通信的openapi接口
//	private IWXAPI api;
	private final String TAG =getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		TextView textView = new TextView(this);
//		textView.setEditText("----------------------------------");
		setContentView(new View(this));
		this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
//		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//		api.registerApp(Constants.APP_ID);

		int wxSdkVersion = SensoroCityApplication.getInstance().api.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
			Log.d(TAG, "onCreate: "+"wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported");
		} else {
			Log.d(TAG, "onCreate: "+"wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported");
		}


		//注意：
		//第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
		try {
			SensoroCityApplication.getInstance().api.handleIntent(getIntent(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		SensoroCityApplication.getInstance().api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				goToGetMsg();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			default:
				break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		String result;
		Log.d(TAG, "onResp: "+"baseresp.getType = " + resp.getType());

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "errcode_success";
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "errcode_cancel";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "errcode_deny";
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
				result = "errcode_unsupported";
				break;
			default:

				result = "errcode_unknown";
				break;
		}
		Log.d(TAG, "onResp: "+result+", errorCode = "+resp.errCode+",str = "+resp.errStr);
		finish();
	}

	private void goToGetMsg() {
		Log.d(TAG, "goToGetMsg: goTOGetMsg");
	}

	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);

		Log.d(TAG, "goToShowMsg: goToShowMsg");
	}
}