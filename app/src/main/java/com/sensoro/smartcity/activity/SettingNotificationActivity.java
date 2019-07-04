package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingNotificationActivity extends BaseActivity {


    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.devicein_sw)
    Switch deviceinSw;
    @BindView(R.id.devicein_edit)
    EditText deviceinEdit;
    @BindView(R.id.deviceout_sw)
    Switch deviceoutSw;
    @BindView(R.id.deviceout_edit)
    EditText deviceoutEdit;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settingnotification);

        ButterKnife.bind(this);

        includeTextTitleTvTitle.setText("设备通知提示");
        includeTextTitleTvSubtitle.setText("保存");
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SETTINGNOTIFICATION_NAME, Context
                .MODE_PRIVATE);
        String oldText = sp.getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");


        if (!TextUtils.isEmpty(oldText)) {


            String[] split = oldText.split(",");

            if (!TextUtils.isEmpty(split[0])) {
                deviceinSw.setChecked(Boolean.parseBoolean(split[0]));
            }
            if (!TextUtils.isEmpty(split[1])) {
                deviceoutSw.setChecked(Boolean.parseBoolean(split[1]));
            }
            if (!TextUtils.isEmpty(split[2])) {
                deviceinEdit.setText(split[2]);
            }
            if (!TextUtils.isEmpty(split[3])) {
                deviceoutEdit.setText(split[3]);
            }


        }
//        deviceinSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
//        deviceoutSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
    }

    @Override
    protected BasePresenter createPresenter() {
        return new BasePresenter() {


            @Override
            public void onDestroy() {

            }

            @Override
            public void initData(Context context) {

            }
        };
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finish();
                break;
            case R.id.include_text_title_tv_subtitle:

                //保存

                boolean deviceoutSwstate = deviceoutSw.isChecked();
                boolean deviceinSwstate = deviceinSw.isChecked();
                String deviceout = deviceoutEdit.getText().toString().trim();
                String devicein = deviceinEdit.getText().toString().trim();

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(deviceinSwstate).append(",").append(deviceoutSwstate).append(",").append(devicein).append(",").append(deviceout);

                PreferencesSaveAnalyzer.savePreferences(SearchHistoryTypeConstants.TYPE_SETTINGNOTIFICATION, stringBuffer.toString());

                finish();

                break;

        }
    }
}
