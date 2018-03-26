package com.sensoro.smartcity.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.AlarmDetailActivity;
import com.sensoro.smartcity.activity.CalendarActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchAlarmActivity;
import com.sensoro.smartcity.adapter.AlarmListAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class AlarmListFragment extends Fragment implements View.OnClickListener, Constants, AdapterView.OnItemClickListener, AlarmListAdapter.AlarmItemClickListener, SensoroPopupAlarmView.OnPopupCallbackListener, AbsListView.OnScrollListener {
    private PullToRefreshListView mPtrListView;
    private ImageView mDateImageView;
    private ImageView mSearchImageView;
    private ImageView mAlarmMenuImageView;
    private ImageView mCloseImageView;
    private ImageView mReturnTopImageView;
    private TextView mSelectedDateTextView;
    private TextView mCancelTextView;
    private EditText mSearchEditText;
    private RelativeLayout mSearchLayout;
    private RelativeLayout mSelectedDateLayout;
    private RelativeLayout mTitleLayout;
    private View rootView;
    private SensoroShadowView mShadowView;
    private SensoroPopupAlarmView mAlarmPopupView;
    private AlarmListAdapter mAlarmListAdapter;
    private List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private int cur_page = 1;
    private long startTime;
    private long endTime;

    public static AlarmListFragment newInstance(String input) {
        AlarmListFragment alarmListFragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        alarmListFragment.setArguments(args);
        return alarmListFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面埋点
        StatService.onPageStart(getActivity(), "AlarmListFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面埋点
        StatService.onPageEnd(getActivity(), "AlarmListFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);
            init();
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void init() {
        try {
            mProgressDialog = new ProgressDialog(this.getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mPtrListView = (PullToRefreshListView) rootView.findViewById(R.id.alarm_ptr_list);
            mPtrListView.setRefreshing(false);
            mPtrListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(DIRECTION_DOWN, false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(DIRECTION_UP, false);
                }
            });
            mPtrListView.setMode(PullToRefreshBase.Mode.BOTH);
            mPtrListView.setOnScrollListener(this);
            mAlarmListAdapter = new AlarmListAdapter(getContext(), this);
            mPtrListView.setAdapter(mAlarmListAdapter);
            mPtrListView.setOnItemClickListener(this);
            mDateImageView = (ImageView) rootView.findViewById(R.id.alarm_iv_date);
            mDateImageView.setOnClickListener(this);
            mSearchImageView = (ImageView) rootView.findViewById(R.id.alarm_iv_search);
            mSearchImageView.setOnClickListener(this);
            mAlarmMenuImageView = (ImageView) rootView.findViewById(R.id.alarm_iv_menu_list);
            mAlarmMenuImageView.setOnClickListener(this);
            mReturnTopImageView = (ImageView) rootView.findViewById(R.id.alarm_return_top);
            mReturnTopImageView.setOnClickListener(this);
            mSearchLayout = (RelativeLayout) rootView.findViewById(R.id.alarm_search_layout);
            mSearchEditText = (EditText) rootView.findViewById(R.id.alarm_search_et);
            mCancelTextView = (TextView) rootView.findViewById(R.id.alarm_cancel_tv);
            mCancelTextView.setOnClickListener(this);
            mSearchEditText.setOnClickListener(this);
            mTitleLayout = (RelativeLayout) rootView.findViewById(R.id.alarm_title_layout);
            mSelectedDateLayout = (RelativeLayout) rootView.findViewById(R.id.alarm_log_date_edit);
            mSelectedDateTextView = (TextView) rootView.findViewById(R.id.alarm_log_selected_date);
            mCloseImageView = (ImageView) rootView.findViewById(R.id.alarm_log_selected_close);
            mCloseImageView.setOnClickListener(this);
            mAlarmPopupView = (SensoroPopupAlarmView) rootView.findViewById(R.id.alarm_popup_view);
            mShadowView = (SensoroShadowView) rootView.findViewById(R.id.alarm_popup_shadow);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    public void refresh(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp, String searchText) {
        try {
            if (direction == DIRECTION_DOWN) {
                mDeviceAlarmLogInfoList.clear();
            }
            if (searchText != null) {
                mTitleLayout.setVisibility(View.GONE);
                mSearchLayout.setVisibility(View.VISIBLE);
                mSearchEditText.setHint(searchText);
            }
            List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
            for (int i = 0; i < deviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogInfoList.get(i);
                AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                boolean isHaveRecovery = false;
                for (int j = 0; j < recordInfoArray.length; j++) {
                    AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
                    if (recordInfo.getType().equals("recovery")) {
                        deviceAlarmLogInfo.setSort(4);
                        isHaveRecovery = true;
                        break;
                    } else {
                        deviceAlarmLogInfo.setSort(1);
                    }
                }
                switch (deviceAlarmLogInfo.getDisplayStatus()) {
                    case DISPLAY_STATUS_CONFIRM:
                        if (isHaveRecovery) {
                            deviceAlarmLogInfo.setSort(2);
                        } else {
                            deviceAlarmLogInfo.setSort(1);
                        }
                        break;
                    case DISPLAY_STATUS_ALARM:
                        if (isHaveRecovery) {
                            deviceAlarmLogInfo.setSort(2);
                        } else {
                            deviceAlarmLogInfo.setSort(1);
                        }
                        break;
                    case DISPLAY_STATUS_MISDESCRIPTION:
                        if (isHaveRecovery) {
                            deviceAlarmLogInfo.setSort(3);
                        } else {
                            deviceAlarmLogInfo.setSort(1);
                        }
                        break;
                    case DISPLAY_STATUS_TEST:
                        if (isHaveRecovery) {
                            deviceAlarmLogInfo.setSort(4);
                        } else {
                            deviceAlarmLogInfo.setSort(1);
                        }
                        break;
                    default:
                        break;
                }
                mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
            }
            Collections.sort(mDeviceAlarmLogInfoList);
            mAlarmListAdapter.setData(mDeviceAlarmLogInfoList);
            mAlarmListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void refresh(String type) {
        try {
            List<DeviceAlarmLogInfo> tempList = new ArrayList<>();
            String typeArray[] = type.split(",");
            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo alarmLogInfo = mDeviceAlarmLogInfoList.get(i);
                String alarmType = alarmLogInfo.getSensorType();
                boolean isContains = Arrays.asList(typeArray).contains(alarmType);
                if (isContains) {
                    tempList.add(alarmLogInfo);
                }
            }
            mAlarmListAdapter.setData(tempList);
            mAlarmListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }


    public void requestData(int direction, boolean isForce) {
        if (mPtrListView.getState() == PullToRefreshBase.State.RESET && !isForce) {
            return;
        }
        mProgressDialog.show();
        Long temp_startTime = null;
        Long temp_endTime = null;
        if (mSelectedDateLayout.getVisibility() == View.VISIBLE) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getActivity().getApplication();
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                sensoroCityApplication.smartCityServer.getDeviceAlarmLogList(temp_startTime, temp_endTime, null, null, cur_page, new Response.Listener<DeviceAlarmLogRsp>() {
                    @Override
                    public void onResponse(DeviceAlarmLogRsp response) {
                        mProgressDialog.dismiss();
                        mPtrListView.onRefreshComplete();
                        refresh(DIRECTION_DOWN, response, null);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String reason = new String(error.networkResponse.data);
                            try {
                                JSONObject jsonObject = new JSONObject(reason);
                                Toast.makeText(getContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                sensoroCityApplication.smartCityServer.getDeviceAlarmLogList(temp_startTime, temp_endTime,  null, null, cur_page, new Response.Listener<DeviceAlarmLogRsp>() {
                    @Override
                    public void onResponse(DeviceAlarmLogRsp response) {
                        mProgressDialog.dismiss();
                        mPtrListView.onRefreshComplete();
                        if (response.getData().size() == 0) {
                            cur_page--;
                        } else {
                            refresh(DIRECTION_UP, response, null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cur_page--;
                        if (error.networkResponse != null) {
                            String reason = new String(error.networkResponse.data);
                            try {
                                JSONObject jsonObject = new JSONObject(reason);
                                Toast.makeText(getContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }


    public void requestData(String startDate, String endDate) {
        mProgressDialog.show();
        mSelectedDateLayout.setVisibility(View.VISIBLE);
        startTime = DateUtil.strToDate(startDate).getTime();
        endTime = DateUtil.strToDate(endDate).getTime();
        mSelectedDateTextView.setText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil.getMothDayFormatDate(endTime));
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getActivity().getApplication();
        sensoroCityApplication.smartCityServer.getDeviceAlarmLogList(startTime, endTime, null, null, 1, new Response.Listener<DeviceAlarmLogRsp>() {
            @Override
            public void onResponse(DeviceAlarmLogRsp response) {
                mProgressDialog.dismiss();
                refresh(DIRECTION_DOWN, response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
            }
        });
    }

    private void cancelSearch() {
        mTitleLayout.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        requestData(DIRECTION_DOWN, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_iv_date:
                Intent intent = new Intent(this.getActivity(), CalendarActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CALENDAR);
                break;
            case R.id.alarm_iv_search:
                Intent searchIntent1 = new Intent(this.getActivity(), SearchAlarmActivity.class);
                searchIntent1.putExtra(EXTRA_FRAGMENT_INDEX, 2);
                startActivityForResult(searchIntent1, REQUEST_CODE_SEARCH_ALARM);
                break;
            case R.id.alarm_iv_menu_list:
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.alarm_log_selected_close:
                mSelectedDateLayout.setVisibility(View.GONE);
                requestData(DIRECTION_DOWN, true);
                break;
            case R.id.alarm_cancel_tv:
                cancelSearch();
                break;
            case R.id.alarm_search_et:
                Intent searchIntent = new Intent(this.getActivity(), SearchAlarmActivity.class);
                searchIntent.putExtra(EXTRA_FRAGMENT_INDEX, 2);
                startActivityForResult(searchIntent, REQUEST_CODE_SEARCH_ALARM);
                break;
            case R.id.alarm_return_top:
                mPtrListView.getRefreshableView().smoothScrollToPosition(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), AlarmDetailActivity.class);
        intent.putExtra(EXTRA_ALARM_INFO, mDeviceAlarmLogInfoList.get(position - 1));
        startActivityForResult(intent, REQUEST_CODE_ALARM);

    }

    @Override
    public void onItemClick(View view, int position) {
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getActivity().getApplication();
        DeviceAlarmLogInfo deviceAlarmLogInfo = mDeviceAlarmLogInfoList.get(position);
        mAlarmPopupView.show(sensoroCityApplication, deviceAlarmLogInfo, mShadowView, this);
    }

    @Override
    public void onPopupCallback(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
            DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
            if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
                AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                deviceAlarmLogInfo.setSort(1);
                for (int j = 0; j < recordInfoArray.length; j++) {
                    AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
                    if (recordInfo.getType().equals("recovery")) {
                        deviceAlarmLogInfo.setSort(4);
                        break;
                    }
                }
                mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
//                Collections.sort(mDeviceAlarmLogInfoList);
                mAlarmListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int tempPos = mPtrListView.getRefreshableView().getFirstVisiblePosition();
        if (tempPos > 0) {
            mReturnTopImageView.setVisibility(View.VISIBLE);
        } else {
            mReturnTopImageView.setVisibility(View.GONE);
        }
    }
}
