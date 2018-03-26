package com.sensoro.smartcity.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.MenuInfoAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.fragment.AlarmListFragment;
import com.sensoro.smartcity.fragment.IndexFragment;
import com.sensoro.smartcity.fragment.MerchantFragment;
import com.sensoro.smartcity.fragment.PointDeployFragment;
import com.sensoro.smartcity.server.SmartCityServerImpl;
import com.sensoro.smartcity.server.bean.Character;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.widget.SensoroPager;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, Constants, View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private MenuInfoAdapter mMenuInfoAdapter = null;
    private SensoroPager sensoroPager = null;
    private MenuDrawer mMenuDrawer = null;
    private ListView mListView = null;
    private IndexFragment indexFragment = null;
    private AlarmListFragment alarmListFragment = null;
    private MerchantFragment merchantFragment = null;
    private PointDeployFragment pointDeployFragment = null;
    private TextView mNameTextView = null;
    private TextView mPhoneTextView = null;
    private TextView mVersionTextView = null;
    private LinearLayout mExitLayout = null;
    private Socket mSocket = null;
    private DeviceInfoListener mInfoListener = null;
    private String mUserName = null;
    private String mPhone = null;
    private String mPhoneId = null;
    private Character mCharacter = null;
    private Handler mHandler = new Handler();
    private TaskRunnable mRunnable = new TaskRunnable();
    private long exitTime = 0;
    private ProgressDialog mProgressDialog = null;
    private String roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void init() {
        try {
            StatService.setDebugOn(true);
            StatService.start(this);
            mUserName = this.getIntent().getStringExtra(EXTRA_USER_NAME);
            mPhone = this.getIntent().getStringExtra(EXTRA_PHONE);
            mPhoneId = this.getIntent().getStringExtra(EXTRA_PHONE_ID);
            mCharacter = (Character) this.getIntent().getSerializableExtra(EXTRA_CHARACTER);
            roles = getIntent().getStringExtra(EXTRA_USER_ROLES);
            initWidget();
            mHandler.postDelayed(mRunnable, 3000L);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected boolean isNeedSlide() {
        return false;
    }


    private void initWidget() {
        try {
            requireCameraPermission();
            mInfoListener = new DeviceInfoListener();
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
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
            mNameTextView.setText(mUserName);
            mPhoneTextView.setText(mPhone == null ? mPhone : "");
            mMenuInfoAdapter = new MenuInfoAdapter(this);
            mListView.setAdapter(mMenuInfoAdapter);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setOnItemClickListener(this);
            getAPPVersionCode();
            indexFragment = IndexFragment.newInstance(mCharacter);
            alarmListFragment = AlarmListFragment.newInstance("");
            merchantFragment = MerchantFragment.newInstance("");
            pointDeployFragment = PointDeployFragment.newInstance("");
            List<Fragment> fragmentList = new ArrayList<>();
            fragmentList.add(indexFragment);
            fragmentList.add(alarmListFragment);
            fragmentList.add(merchantFragment);
            fragmentList.add(pointDeployFragment);

            sensoroPager = (SensoroPager) findViewById(R.id.main_container);
            sensoroPager.setOffscreenPageLimit(5);
            sensoroPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragmentList));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (checkDeviceHasNavigationBar()) {
                params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y200));
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
            } else {
                params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.y1));
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
            }
            mExitLayout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    public void getAPPVersionCode() {
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            int currentVersionCode = info.versionCode; // 版本号
            mVersionTextView.setText("City " + appVersionName);
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void requestUpdate() {
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getUpdateInfo(new Response.Listener<UpdateRsp>() {
            @Override
            public void onResponse(UpdateRsp response) {
                Log.d("ok===========", response.toString());
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (info.versionCode < response.getVersion()) {
                        showSimpleDialog(response.getChangelog(), response.getInstall_url());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("e===========", error.toString());
            }
        });
    }

    private void showSimpleDialog(String log, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(log);
        builder.setTitle("版本更新");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private boolean requireLocationPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean requireCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {


            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else {
            return true;
        }
    }

    private void createSocket() {
        try {
            String sessionId = SmartCityServerImpl.getInstance(this).getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(SmartCityServerImpl.SCOPE, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String getRoles() {
        return roles;
    }

    public void reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();

                mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            }

            String sessionId = SmartCityServerImpl.getInstance(this).getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(SmartCityServerImpl.SCOPE, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void reconnectSocketIO(String userName, String phone, String roles) {
        mUserName = userName;
        mPhone = phone;
        mNameTextView.setText(mUserName);
        this.roles = roles;
        if (indexFragment != null) {
            mMenuInfoAdapter.setSelectedIndex(0);
            sensoroPager.setCurrentItem(0);
            indexFragment.requestWithDirection(Constants.DIRECTION_DOWN);
            mMenuInfoAdapter.notifyDataSetChanged();
            reconnect();
        }
    }

    private void logout() {
        mProgressDialog.setMessage(getString(R.string.tips_logout));
        mProgressDialog.show();
        String phoneId = this.getIntent().getStringExtra(EXTRA_PHONE_ID);
        String uid = this.getIntent().getStringExtra(EXTRA_USER_ID);
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.logout(phoneId, uid, new Response.Listener<ResponseBase>() {
            @Override
            public void onResponse(ResponseBase response) {
                mProgressDialog.dismiss();
                if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    String reason = new String(error.networkResponse.data);
                    Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.tips_logout_failed, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.exit_main,
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public MenuDrawer getMenuDrawer() {
        return mMenuDrawer;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMenuInfoAdapter.setSelectedIndex(position);
        mMenuInfoAdapter.notifyDataSetChanged();
        mMenuDrawer.closeMenu();
        switch (position) {
            case 0:
                pointDeployFragment.hiddenRootView();
                sensoroPager.setCurrentItem(position);
                indexFragment.requestWithDirection(DIRECTION_DOWN);
                sensoroPager.setCurrentItem(0);
                break;
            case 1:
                pointDeployFragment.hiddenRootView();
                alarmListFragment.requestData(DIRECTION_DOWN, true);
                sensoroPager.setCurrentItem(1);
                break;
            case 2:
                pointDeployFragment.hiddenRootView();
                merchantFragment.requestData();
                merchantFragment.refreshData(mUserName, mPhone, mPhoneId);
                sensoroPager.setCurrentItem(2);
                break;
            case 3:
                if (requireCameraPermission()) {
                    boolean isRequire = requireLocationPermission();
                    if (isRequire) {
                        pointDeployFragment.showRootView();
                        sensoroPager.setCurrentItem(3);
                    } else {
                        mMenuInfoAdapter.setSelectedIndex(sensoroPager.getCurrentItem());
                        mMenuInfoAdapter.notifyDataSetChanged();
                        Toast.makeText(this, R.string.tips_location_permission, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, R.string.tips_camera_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                logout();
                break;
            default:
                pointDeployFragment.hiddenRootView();
                break;
        }

    }

    public SensoroPager getSensoroPager() {
        return sensoroPager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_ORIGIN) {
            sensoroPager.setCurrentItem(0);
            mMenuInfoAdapter.setSelectedIndex(0);
            mListView.setSelection(0);

        } else if (resultCode == RESULT_CODE_MAP) {
            sensoroPager.setCurrentItem(0);
            mMenuInfoAdapter.setSelectedIndex(0);
            mListView.setSelection(0);
            if (data.getSerializableExtra(EXTRA_DEVICE_INFO) != null) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                indexFragment.refreshDeviceInfo(deviceInfo);
            }
        } else if (resultCode == RESULT_CODE_SEARCH_DEVICE) {
            sensoroPager.setCurrentItem(0);
            mMenuInfoAdapter.setSelectedIndex(0);
            mListView.setSelection(0);
            DeviceInfoListRsp infoRspData = (DeviceInfoListRsp) data.getSerializableExtra(EXTRA_SENSOR_INFO);
            if (infoRspData != null) {
                indexFragment.refreshWithSearch(infoRspData);
            } else {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                indexFragment.refreshDeviceInfo(deviceInfo);
            }

        } else if (resultCode == RESULT_CODE_SEARCH_MERCHANT) {
            sensoroPager.setCurrentItem(2);
            mMenuInfoAdapter.setSelectedIndex(2);
            mListView.setSelection(2);
            UserAccountRsp infoRspData = (UserAccountRsp) data.getSerializableExtra(EXTRA_MERCHANT_INFO);
            if (infoRspData != null) {
                merchantFragment.refresh(infoRspData);
            }

        } else if (resultCode == RESULT_CODE_SEARCH_ALARM) {
            sensoroPager.setCurrentItem(1);
            mMenuInfoAdapter.setSelectedIndex(1);
            mListView.setSelection(1);
            String type = data.getStringExtra(EXTRA_SENSOR_TYPE);
            int searchIndex = data.getIntExtra(EXTRA_ALARM_SEARCH_INDEX, 0);
            String searchText = data.getStringExtra(EXTRA_ALARM_SEARCH_TEXT);
            boolean isFromCancel = data.getBooleanExtra(EXTRA_ACTIVITY_CANCEL, false);
            if (isFromCancel) {
                alarmListFragment.requestData(DIRECTION_DOWN, true);
            } else {
                if (searchIndex == 0) {
                    DeviceAlarmLogRsp alarmListRsp = (DeviceAlarmLogRsp) data.getSerializableExtra(EXTRA_ALARM_INFO);
                    alarmListFragment.refresh(DIRECTION_DOWN, alarmListRsp, searchText);
                } else {
                    alarmListFragment.refresh(type);
                }
            }
        } else if (resultCode == RESULT_CODE_ALARM) {
            sensoroPager.setCurrentItem(1);
            mMenuInfoAdapter.setSelectedIndex(1);
            mListView.setSelection(1);
            alarmListFragment.requestData(DIRECTION_DOWN, true);
        } else if (resultCode == RESULT_CODE_ALARM_DETAIL) {
            sensoroPager.setCurrentItem(1);
            mMenuInfoAdapter.setSelectedIndex(1);
            mListView.setSelection(1);
            DeviceAlarmLogInfo deviceAlarmLogInfo = (DeviceAlarmLogInfo) data.getSerializableExtra(EXTRA_ALARM_INFO);
            alarmListFragment.onPopupCallback(deviceAlarmLogInfo);
        } else if (resultCode == RESULT_CODE_CALENDAR) {
            sensoroPager.setCurrentItem(1);
            mMenuInfoAdapter.setSelectedIndex(1);
            mListView.setSelection(1);
            String startDate = data.getStringExtra(EXTRA_ALARM_START_DATE);
            String endDate = data.getStringExtra(EXTRA_ALARM_END_DATE);
            alarmListFragment.requestData(startDate, endDate);
        } else if (resultCode == RESULT_CODE_DEPLOY) {
            sensoroPager.setCurrentItem(3);
            mMenuInfoAdapter.setSelectedIndex(3);
            mListView.setSelection(3);
            pointDeployFragment.showRootView();
            boolean containsData = data.getBooleanExtra(EXTRA_CONTAINS_DATA, false);
            if (containsData) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                indexFragment.refreshDeviceInfo(deviceInfo);
            }
        }
        mMenuInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_left_exit:
                logout();
                break;
        }
    }

    private boolean isMainActivityTop(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(MainActivity.class.getName());
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }


    public class TaskRunnable implements Runnable {

        @Override
        public void run() {
            requestUpdate();
            createSocket();
        }
    }

    public class DeviceInfoListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) args[i];
                    } else {
                        JSONArray jsonArray = (JSONArray) args[i];
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (isMainActivityTop()) {
                            indexFragment.refreshWithJsonData(jsonObject.toString());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
