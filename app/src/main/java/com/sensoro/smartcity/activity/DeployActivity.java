package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.TextureMapView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployActivityView;
import com.sensoro.smartcity.presenter.DeployActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by sensoro on 17/8/3.
 */

public class DeployActivity extends BaseActivity<IDeployActivityView, DeployActivityPresenter> implements
        IDeployActivityView {


    @BindView(R.id.deploy_name_address_et)
    TextView nameAddressEditText;
    @BindView(R.id.deploy_location_et)
    TextView locationEditText;
    @BindView(R.id.deploy_contact_et)
    TextView contactEditText;
    @BindView(R.id.deploy_upload_btn)
    Button uploadButton;
    @BindView(R.id.deploy_title)
    TextView titleTextView;
    @BindView(R.id.deploy_device_signal)
    TextView signalButton;
    @BindView(R.id.deploy_map)
    TextureMapView mMapView;
    @BindView(R.id.deploy_tag_layout)
    LinearLayout tagLayout;
    private ProgressUtils mProgressUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy);
        ButterKnife.bind(mActivity);
        mMapView.onCreate(savedInstanceState);
        mPrestener.initData(mActivity);
        init();

    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        mPrestener.onDestroy();
        setUploadButtonClickable(true);
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        setUploadButtonClickable(true);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected DeployActivityPresenter createPresenter() {
        return new DeployActivityPresenter();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void init() {
        try {
            mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
            mPrestener.initMap(mMapView.getMap());
            mActivity.getWindow().getDecorView().postInvalidate();
        } catch (Exception e) {
            e.printStackTrace();
            toastShort(mActivity.getResources().getString(R.string.tips_data_error));
        }

    }

    @Override
    public void refreshTagLayout(List<String> tagList) {
        tagLayout.removeAllViews();
        int textSize = getResources().getDimensionPixelSize(R.dimen.tag_default_size);
        for (int i = 0; i < tagList.size(); i++) {
            TextView textView = new TextView(mActivity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.setMargins(10, 0, 0, 0);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setText(tagList.get(i));
            textView.setPadding(5, 0, 0, 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawables(null, null, null, null);
            textView.setBackground(getResources().getDrawable(R.drawable.shape_textview));
            textView.setSingleLine();
            tagLayout.addView(textView, i, params);

        }
    }

    @Override
    public void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 300000)) {
            switch (signal) {
                case "good":
                    signal_text = "信号质量：优";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = "信号质量：良";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = "信号质量：差";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
            }
        } else {
            signal_text = "无信号";
            signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        signalButton.setText(signal_text);
        signalButton.setPadding(6, 10, 6, 10);
    }

    @Override
    public void addDefaultTextView() {
        int textSize = getResources().getDimensionPixelSize(R.dimen.tag_default_size);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 0, 20, 0);
        TextView textView = new TextView(mActivity);
        textView.setTextColor(getResources().getColor(R.color.c_888888));
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setText(R.string.tips_hint_tag);
        tagLayout.addView(textView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPrestener.handActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.deploy_name_relative_layout)
    public void doSettingByNameAndAddress() {
        String nameAddress = nameAddressEditText.getText().toString();
        mPrestener.doSettingByNameAndAddress(nameAddress);
    }

    @OnClick(R.id.deploy_tag_relative_layout)
    public void doSettingByTag() {
        mPrestener.doSettingByTag();
    }

    @OnClick(R.id.deploy_contact_relative_layout)
    public void doSettingContact() {
        mPrestener.doSettingContact();
    }

    @OnClick(R.id.deploy_device_signal)
    public void doSignal() {
        String sns = titleTextView.getText().toString();
        mPrestener.doSignal(sns);

    }

    @OnClick(R.id.deploy_upload_btn)
    public void deploy() {
        setUploadButtonClickable(false);
        String sn = titleTextView.getText().toString();
        final String name = nameAddressEditText.getText().toString();
        mPrestener.requestUpload(sn, name);
    }

    @OnClick(R.id.deploy_back)
    public void back() {
        mPrestener.back();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setTitleTextView(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setNameAddressEditText(String text) {
        nameAddressEditText.setText(text);
    }

    @Override
    public void setUploadButtonClickable(boolean isClickable) {
        if (uploadButton != null) {
            uploadButton.setEnabled(isClickable);
            uploadButton.setTextColor(mActivity.getResources().getColor(isClickable ? R.color.white : R.color
                    .sensoro_inactive));
        }
    }

    @Override
    public void setContactEditText(String contact) {
        contactEditText.setText(contact);
    }
}
