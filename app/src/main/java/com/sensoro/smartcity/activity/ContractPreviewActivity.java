package com.sensoro.smartcity.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IContractPreviewActivityView;
import com.sensoro.smartcity.presenter.ContractPreviewActivityPresenter;
import com.sensoro.smartcity.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractPreviewActivity extends BaseActivity<IContractPreviewActivityView, ContractPreviewActivityPresenter> implements IContractPreviewActivityView {
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.pb_preview)
    ProgressBar pbPreview;
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    private WebView wvPreview;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_preview);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleImvArrowsLeft.setImageResource(R.drawable.title_close);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_preview));
//        wvPreview.loadUrl("file:///android_asset/test.html");//加载asset文件夹下html
        //
//        webView.loadUrl("http://139.196.35.30:8080/OkHttpTest/apppackage/test.html");//加载url

        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);
        wvPreview = new WebView(mActivity);
        wvPreview.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        wvPreview.setWebChromeClient(webChromeClient);
        wvPreview.setWebViewClient(webViewClient);

        WebSettings webSettings = wvPreview.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        ClipDrawable clipDrawable = new ClipDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_1dbb99)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        pbPreview.setProgressDrawable(clipDrawable);
        //添加进来
        flContainer.addView(wvPreview);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            pbPreview.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            pbPreview.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                LogUtils.loge("ddong", "拦截url:" + url);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (url.equals("http://www.google.com/")) {
                SensoroToast.getInstance().makeText("国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private final WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            try {
                LogUtils.loge("ddong", "网页标题:" + title);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pbPreview.setProgress(newProgress);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            LogUtils.loge("ddong", "是否有上一个页面:" + wvPreview.canGoBack());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (wvPreview != null) {
            if (wvPreview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
                wvPreview.goBack(); // goBack()表示返回webView的上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        try {
            LogUtils.loge("ddong", "html调用客户端:" + str);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        //释放资源
        if (wvPreview != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destroy()
            ViewParent parent = wvPreview.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(wvPreview);
            }

            wvPreview.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvPreview.getSettings().setJavaScriptEnabled(false);
            wvPreview.clearHistory();
            wvPreview.clearView();
            wvPreview.removeAllViews();
            try {
                wvPreview.destroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    protected ContractPreviewActivityPresenter createPresenter() {
        return new ContractPreviewActivityPresenter();
    }

    @Override
    public void loadUrl(String url) {
        if (wvPreview != null) {
            wvPreview.loadUrl(url);
        }
    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }
}
