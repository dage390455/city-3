package com.sensoro.smartcity.fragment;

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
import android.widget.RelativeLayout;
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
import com.sensoro.smartcity.widget.ProgressUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class MerchantFragment extends Fragment implements Constants, AdapterView.OnItemClickListener, View
        .OnClickListener {

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
    private ProgressUtils mProgressUtils = null;
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    private RelativeLayout rlTitleAccount;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroyView();
    }

    private void init() {
        try {
            mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this.getActivity()).build());
            mListView = (ListView) rootView.findViewById(R.id.merchant_list);
            mMenuListImageView = (ImageView) rootView.findViewById(R.id.merchant_iv_menu_list);
            mMenuListImageView.setOnClickListener(this);
            mSearchImageView = (ImageView) rootView.findViewById(R.id.merchant_iv_search);
            mSearchImageView.setOnClickListener(this);
            mCurrentNameTextView = (TextView) rootView.findViewById(R.id.merchant_current_name);
            mCurrentPhoneTextView = (TextView) rootView.findViewById(R.id.merchant_current_phone);
            mCurrentStatusImageView = (ImageView) rootView.findViewById(R.id.merchant_current_status);
            seperatorView = rootView.findViewById(R.id.merchant_list_sep);
            seperatorBottomView = rootView.findViewById(R.id.merchant_list_bottom_sep);
            rlTitleAccount = (RelativeLayout) rootView.findViewById(R.id.rl_title_account);

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
        mProgressUtils.showProgress();
        SensoroCityApplication.getInstance().smartCityServer.getUserAccountList(null, null, null, null, "100000", new
                Response
                        .Listener<UserAccountRsp>() {
                    @Override
                    public void onResponse(UserAccountRsp response) {
                        mProgressUtils.dismissProgress();
                        refresh(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressUtils.dismissProgress();
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(UserAccountRsp userAccountRsp) {
        List<UserInfo> list = userAccountRsp.getData();
        mUserInfoList.clear();
        mUserInfoList.addAll(list);
        mMerchantAdapter.setSelectedIndex(-1);
        mMerchantAdapter.notifyDataSetChanged();
        if (list.size() == 0) {
            seperatorView.setVisibility(View.GONE);
            seperatorBottomView.setVisibility(View.GONE);
        } else {
            seperatorView.setVisibility(View.VISIBLE);
        }
    }

    private void doAccountSwitch(String uid) {
        mProgressUtils.showProgress();
        SensoroCityApplication.getInstance().smartCityServer.doAccountControl(uid, phoneId, new Response
                .Listener<UserAccountControlRsp>() {
            @Override
            public void onResponse(UserAccountControlRsp response) {
                mProgressUtils.dismissProgress();
                if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    String sessionID = response.getData().getSessionID
                            ();
                    SensoroCityApplication.getInstance().smartCityServer.setSessionId(sessionID);
                    String nickname = response.getData().getNickname();
                    String phone = response.getData().getPhone();
                    String roles = response.getData().getRoles();
                    String isSpecific = response.getData().getIsSpecific();
                    ((MainActivity) getActivity()).reconnectSocketIO(nickname, phone, roles,
                            isSpecific);
                } else {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressUtils.dismissProgress();
                if (error.networkResponse != null) {
                    String reason = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getActivity(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getActivity(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }

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
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.merchant_iv_search:
                Intent searchIntent = new Intent(getContext(), SearchMerchantActivity.class);
                startActivityForResult(searchIntent, REQUEST_CODE_SEARCH_MERCHANT);
                break;
        }
    }
}
