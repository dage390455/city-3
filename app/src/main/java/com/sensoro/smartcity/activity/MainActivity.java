package com.sensoro.smartcity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.sensoro.smartcity.model.MenuPageInfo;
import com.sensoro.smartcity.presenter.MainPresenter;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroPager;
import com.sensoro.smartcity.widget.SensoroToast;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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

    private ProgressUtils mProgressUtils;
    private MainPagerAdapter mainPagerAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        initWidget();
        mPresenter.checkPush();
        mPresenter.initData(mActivity);
        mPresenter.freshAccountType();
        mPresenter.onCreate();
        mPresenter.setAppVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLeftMenuExitButtonState();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        super.onDestroy();
    }

    public boolean isSupperAccount() {
        return mPresenter.isSupperAccount();
    }

    private void initWidget() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mMenuDrawer = MenuDrawer.attach(mActivity, MenuDrawer.Type.OVERLAY, Position.LEFT, MenuDrawer
                .MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.content_main);
        mMenuDrawer.setDropShadowEnabled(false);
        mMenuDrawer.setDrawOverlay(false);
        mMenuDrawer.setMenuView(R.layout.main_left_menu);
        int width = getWindowManager().getDefaultDisplay().getWidth() / 3 * 2;
        mMenuDrawer.setMenuSize(width);
        //
        mListView = (ListView) mMenuDrawer.findViewById(R.id.left_menu_list);
        mNameTextView = (TextView) findViewById(R.id.left_menu_name);
        mPhoneTextView = (TextView) findViewById(R.id.left_menu_phone);
        mVersionTextView = (TextView) findViewById(R.id.app_version);
        mExitLayout = (LinearLayout) findViewById(R.id.main_left_exit);
        mExitLayout.setOnClickListener(this);
        //
        mMenuInfoAdapter = new MenuInfoAdapter(mActivity);
        mListView.setAdapter(mMenuInfoAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);
        sensoroPager = (SensoroPager) findViewById(R.id.main_container);
        sensoroPager.setOffscreenPageLimit(8);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        sensoroPager.setAdapter(mainPagerAdapter);
    }

    public String getRoles() {
        return mPresenter.getRoles();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mPresenter.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        closeMenu();
        setMenuSelected(position);
        mPresenter.clickMenuItem((int) mMenuInfoAdapter.getItemId(position));
    }


//    public SensoroPager getSensoroPager() {
//        return sensoroPager;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_left_exit:
                mPresenter.logout();
                break;
        }
    }


    @Override
    public void setAPPVersionCode(String versionStr) {
        mVersionTextView.setText(versionStr);
    }

    @Override
    public void setMenuSelected(int position) {
        mMenuInfoAdapter.setSelectedIndex(position);
        mListView.setSelection(position);
    }


    @Override
    public void showUpdateAppDialog(String log, final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(log);
        builder.setTitle("版本更新");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.updateApp(url);
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
    public void setCurrentPagerItem(int position) {
        sensoroPager.setCurrentItem(position, false);
    }

    @Override
    public void updateMenuPager(List<MenuPageInfo> menuPageInfos) {
        mMenuInfoAdapter.updateMenuPager(menuPageInfos);
    }

    @Override
    public void changeAccount(String useName, String phone, String roles, boolean isSpecific, boolean isStation,
                              boolean hasContract, boolean hasScanLogin) {
        mPresenter.changeAccount(useName, phone, roles, isSpecific, isStation, hasContract, hasScanLogin);
    }

    @Override
    public void updateMainPageAdapterData(List<Fragment> fragments) {
        mainPagerAdapter.updateMainPagerAdapter(fragments);
    }

    @Override
    public void openMenu() {
        mMenuDrawer.openMenu();
    }

    /**
     * 检查是否全面屏
     */
    private void setLeftMenuExitButtonState() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        if (WidgetUtil.navigationBarExist(mActivity)) {
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y200));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
        } else {
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y1));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
        }
        mExitLayout.setLayoutParams(params);
    }

    @Override
    public void closeMenu() {
        mMenuDrawer.closeMenu();
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
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }
}
