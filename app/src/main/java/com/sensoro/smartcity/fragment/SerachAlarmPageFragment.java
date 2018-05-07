package com.sensoro.smartcity.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.SearchAlarmActivity;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SerachAlarmPageFragment extends Fragment implements Constants, AdapterView.OnItemClickListener, View
        .OnClickListener {
    private LinearLayout mSearchHistoryLayout;
    private ImageView mClearBtn;
    private RecyclerView mSearchHistoryRv;
    private LinearLayout tipsLinearLayout;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private View rootView;
    public static final String KEY_INT ="key_int";
    public static final String KEY_STR ="key_str";
    public static SerachAlarmPageFragment newInstance(int type,String text) {
        SerachAlarmPageFragment merchantFragment = new SerachAlarmPageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_INT, type);
        args.putString(KEY_STR,text);
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
            rootView = inflater.inflate(R.layout.searche_device_pager_fragment_layout, container, false);
            init();
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void initSearchHistory() {
        String history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            list.add("test");
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x20);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(getActivity(), mHistoryKeywords, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                mKeywordEt.setText(mHistoryKeywords.get(position));
//                mClearKeywordIv.setVisibility(View.VISIBLE);
//                mProgressDialog.show();
//                mKeywordEt.clearFocus();
//                dismissInputMethodManager(view);
//                save();
//                String text = mKeywordEt.getText().toString();
//                requestData(text);
                        SearchAlarmActivity activity = (SearchAlarmActivity) getActivity();
                        activity.test();
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
//        mKeywordEt.requestFocus();
    }

    private void save(String text) {
//        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                List<String> list = new ArrayList<String>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                mHistoryKeywords = list;
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    if (i == (list.size() - 1)) {
                        stringBuffer.append(list.get(i));
                    } else {
                        stringBuffer.append(list.get(i) + ",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
                mEditor.commit();
            } else {
                if (TextUtils.isEmpty(oldText)) {
                    mEditor.putString(PREFERENCE_KEY_DEVICE, text);
                } else {
                    mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                }
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
        }
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
        super.onDestroyView();
    }

    private void init() {
        try {mPref = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_ALARM_HISTORY, Activity.MODE_PRIVATE);
            mEditor = mPref.edit();
            mSearchHistoryLayout = (LinearLayout) rootView.findViewById(R.id.search_alarm_history_ll);
            mClearBtn = (ImageView) rootView.findViewById(R.id.search_alarm_clear_btn);
            mClearBtn.setOnClickListener(this);
            mSearchHistoryRv = (RecyclerView) rootView.findViewById(R.id.search_alarm_history_rv);
            tipsLinearLayout = (LinearLayout) rootView.findViewById(R.id.search_alarm_tips);
            initSearchHistory();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        mSearchHistoryAdapter.notifyDataSetChanged();
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_alarm_clear_btn:
                cleanHistory();
                break;
//            case R.id.search_alarm_cancel_tv:
//                mKeywordEt.clearFocus();
//                Intent data = new Intent();
//                data.putExtra(EXTRA_ACTIVITY_CANCEL, true);
//                setResult(RESULT_CODE_SEARCH_ALARM, data);
//                finish();
//                break;
//            case R.id.search_alarm_clear_iv:
//                mKeywordEt.setText("");
//                mSearchHistoryAdapter.notifyDataSetChanged();
//                mClearKeywordIv.setVisibility(View.GONE);
//                tipsLinearLayout.setVisibility(View.GONE);
//                tagLinearLayout.setVisibility(View.VISIBLE);
//                break;
            default:
                break;
        }
    }
}
