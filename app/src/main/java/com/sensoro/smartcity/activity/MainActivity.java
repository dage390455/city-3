package com.sensoro.smartcity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainPagerAdapter;
import com.sensoro.smartcity.adapter.MenuInfoAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.presenter.MainPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroPager;
import com.sensoro.smartcity.widget.SensoroToast;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.sensoro.smartcity.presenter.MainPresenter.BUSSISE_ACCOUNT;
import static com.sensoro.smartcity.presenter.MainPresenter.NORMOL_ACCOUNT;
import static com.sensoro.smartcity.presenter.MainPresenter.SUPPER_ACCOUNT;

public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView, AdapterView
        .OnItemClickListener, View
        .OnClickListener {
    private MenuInfoAdapter mMenuInfoAdapter = null;
    private SensoroPager sensoroPager = null;
    private MenuDrawer mMenuDrawer = null;
    private ListView mListView = null;
    private TextView mNameTextView = null;
    private TextView mPhoneTextView = null;
    private TextView mVersionTextView = null;
    private LinearLayout mExitLayout = null;

    private long exitTime = 0;
    private ProgressUtils mProgressUtils;
    private MainPagerAdapter mainPagerAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        initWidget();
        mPrestener.checkPush();
        mPrestener.initData(mActivity);
        mPrestener.freshAccountType();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPrestener.setAppVersion();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onDestroy() {
        mPrestener.onDestroy();
        mProgressUtils.destroyProgress();
        super.onDestroy();
    }

    public boolean isSupperAccount() {
        return mPrestener.isSupperAccount();
    }

    private void initWidget() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mMenuDrawer = MenuDrawer.attach(mActivity, MenuDrawer.Type.OVERLAY, Position.LEFT, MenuDrawer
                .MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.content_main);
        mMenuDrawer.setDropShadowEnabled(false);
        mMenuDrawer.setDrawOverlay(false);
        mMenuDrawer.setMenuView(R.layout.main_left_menu);
        int width = getWindowManager().getDefaultDisplay().getWidth() - 200;
        mMenuDrawer.setMenuSize(width);
        mListView = (ListView) mMenuDrawer.findViewById(R.id.left_menu_list);
        mNameTextView = (TextView) findViewById(R.id.left_menu_name);
        mPhoneTextView = (TextView) findViewById(R.id.left_menu_phone);
        mVersionTextView = (TextView) findViewById(R.id.app_version);
        mExitLayout = (LinearLayout) findViewById(R.id.main_left_exit);
        mExitLayout.setOnClickListener(this);

        mMenuInfoAdapter = new MenuInfoAdapter(mActivity);
        mListView.setAdapter(mMenuInfoAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);

        sensoroPager = (SensoroPager) findViewById(R.id.main_container);
        sensoroPager.setOffscreenPageLimit(5);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mPrestener
                .getFragmentList());
        sensoroPager.setAdapter(mainPagerAdapter);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        if (checkDeviceHasNavigationBar()) {
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y200));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
        } else {
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y1));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
        }
        mExitLayout.setLayoutParams(params);
    }

    public String getRoles() {
        return mPrestener.getRoles();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            toastShort(mActivity.getResources().getString(R.string.exit_main));
            exitTime = System.currentTimeMillis();
        } else {
            finishAc();
        }
    }

    public MenuDrawer getMenuDrawer() {
        return mMenuDrawer;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMenuDrawer.closeMenu();
        mPrestener.switchAccountByType(position);
    }


    public SensoroPager getSensoroPager() {
        return sensoroPager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPrestener.handleActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_left_exit:
                mPrestener.logout();
                break;
        }
    }


    @Override
    public void setAPPVersionCode(String versionStr) {
        mVersionTextView.setText(versionStr);
    }


    @Override
    public void changeAccount(int accountType, int position) {
        mMenuInfoAdapter.setSelectedIndex(position);
        mMenuInfoAdapter.showAccountSwitch(accountType);
        //账户切换
        switch (accountType) {
            case SUPPER_ACCOUNT:
                sensoroPager.setCurrentItem(2);
                break;
            case NORMOL_ACCOUNT:
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        sensoroPager.setCurrentItem(position);
                        break;
                    case 4:
                        mPrestener.logout();
                        break;
                    default:
                        break;
                }
                break;
            case BUSSISE_ACCOUNT:
                switch (position) {
                    case 0:
                    case 1:
                        sensoroPager.setCurrentItem(position);
                        break;
                    case 2:
                        sensoroPager.setCurrentItem(3);
                        break;
                    case 3:
                        mPrestener.logout();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void showUpdateAppDialog(String log, final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(log);
        builder.setTitle("版本更新");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPrestener.updateApp(url);
            }
        });
        builder.setPositiveButton("取消", null);
        builder.create().show();
    }

    @Override
    public void showAccountInfo(String name, String phone) {
        mNameTextView.setText(TextUtils.isEmpty(name) ? "" : name);
        mPhoneTextView.setText(TextUtils.isEmpty(phone) ? "" : phone);
    }

    @Override
    public void setCurrentItem(int position) {
        sensoroPager.setCurrentItem(position);
        mMenuInfoAdapter.setSelectedIndex(position);
        mListView.setSelection(position);
    }

    @Override
    public void freshAccountSwitch(int accountType) {
        mMenuInfoAdapter.showAccountSwitch(accountType);
    }

    @Override
    public void setMenuInfoAdapterData(List<String> data) {
        mMenuInfoAdapter.setDataList(data);
    }

    @Override
    public void changeAccount(String useName, String phone, String roles, String isSpecific) {
        mPrestener.changeAccount(useName, phone, roles, isSpecific);
    }

    @Override
    public void updateMainPageAdapterData() {
        mainPagerAdapter.notifyDataSetChanged();
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

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

    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {

    }
}
