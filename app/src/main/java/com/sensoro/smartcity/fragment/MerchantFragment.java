package com.sensoro.smartcity.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchMerchantActivity;
import com.sensoro.smartcity.adapter.MerchantAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class MerchantFragment extends Fragment implements Constants, AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView mListView;
    private ImageView mMenuListImageView;
    private ImageView mSearchImageView;
    private View rootView;
    private View seperatorView;
    private View seperatorBottomView;
    private TextView mCurrentNameTextView;
    private TextView mCurrentPhoneTextView;
    private ImageView mCurrentStatusImageView;
    private MerchantAdapter mMerchantAdapter;
    private String phoneId = null;
    private ProgressDialog mProgressDialog = null;
    private List<UserInfo> mUserInfoList = new ArrayList<>();

    public static MerchantFragment newInstance(String input) {
        MerchantFragment merchantFragment = new MerchantFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        merchantFragment.setArguments(args);
        return merchantFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面埋点
        StatService.onPageStart(getActivity(), "MerchantFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面埋点
        StatService.onPageEnd(getActivity(), "MerchantFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_merchant, container, false);
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
            mListView = (ListView) rootView.findViewById(R.id.merchant_list);
            mMenuListImageView = (ImageView) rootView.findViewById(R.id.merchant_iv_menu_list);
            mMenuListImageView.setOnClickListener(this);
            mSearchImageView = (ImageView) rootView.findViewById(R.id.merchant_iv_search) ;
            mSearchImageView.setOnClickListener(this);
            mCurrentNameTextView = (TextView) rootView.findViewById(R.id.merchant_current_name);
            mCurrentPhoneTextView = (TextView) rootView.findViewById(R.id.merchant_current_phone);
            mCurrentStatusImageView = (ImageView) rootView.findViewById(R.id.merchant_current_status);
            seperatorView = rootView.findViewById(R.id.merchant_list_sep);
            seperatorBottomView =  rootView.findViewById(R.id.merchant_list_bottom_sep);
            mMerchantAdapter = new MerchantAdapter(getContext(), mUserInfoList);
            mListView.setAdapter(mMerchantAdapter);
            mListView.setOnItemClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    public void refreshData(String username, String phone, String phoneId) {
        this.phoneId = phoneId;
        mCurrentNameTextView.setText(username);
        mCurrentPhoneTextView.setText(phone);
        mCurrentStatusImageView.setVisibility(View.VISIBLE);
    }

    public void requestData() {
        mProgressDialog.show();
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getActivity().getApplication();
        sensoroCityApplication.smartCityServer.getUserAccountList(null, null, null, null, "100000", new Response.Listener<UserAccountRsp>() {
            @Override
            public void onResponse(UserAccountRsp response) {
                refresh(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressDialog.dismiss();
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(UserAccountRsp userAccountRsp) {
        mProgressDialog.dismiss();
        List<UserInfo> list = userAccountRsp.getData();
        mUserInfoList.clear();
        mUserInfoList.addAll(list);
        mMerchantAdapter.setSelectedIndex(-1);
        mMerchantAdapter.notifyDataSetChanged();
        mProgressDialog.dismiss();
        if (list.size() == 0) {
            seperatorView.setVisibility(View.GONE);
            seperatorBottomView.setVisibility(View.GONE);
        } else {
            seperatorView.setVisibility(View.VISIBLE);
        }
    }

    private void doAccountSwitch(String uid){
        mProgressDialog.show();
        final SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getActivity().getApplication();
        sensoroCityApplication.smartCityServer.doAccountControl(uid, phoneId, new Response.Listener<UserAccountControlRsp>() {
            @Override
            public void onResponse(UserAccountControlRsp response) {
                if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    sensoroCityApplication.smartCityServer.setSessionId(response.getData().getSessionID());
                    ((MainActivity)getActivity()).reconnectSocketIO(response.getData().getNickname(), response.getData().getPhone(), response.getData().getRoles());
                } else {

                }
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String reason = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getActivity(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mUserInfoList.get(position).isStop()) {
            mMerchantAdapter.setSelectedIndex(position);
            mMerchantAdapter.notifyDataSetChanged();
            mCurrentStatusImageView.setVisibility(View.GONE);
            String uid = mUserInfoList.get(position).get_id();
            doAccountSwitch(uid);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_iv_menu_list:
                ((MainActivity)getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.merchant_iv_search:
                Intent searchIntent = new Intent(getContext(), SearchMerchantActivity.class);
                startActivityForResult(searchIntent, REQUEST_CODE_SEARCH_MERCHANT);
                break;
        }
    }
}
