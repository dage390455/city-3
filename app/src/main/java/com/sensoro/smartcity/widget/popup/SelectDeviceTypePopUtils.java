package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TypeSelectAdapter;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.List;

public class SelectDeviceTypePopUtils {
    private final PopupWindow mPopupWindow;
    private final Activity mActivity;
    private SelectDeviceTypeItemClickListener listener;
    private TypeSelectAdapter mTypeSelectAdapter;
    private final RelativeLayout mRlTitle;


    public SelectDeviceTypePopUtils(Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_pop_type_select, null);
        RecyclerView mRcTypeSelect = view.findViewById(R.id.pop_type_select_rc);
        final TextView tvSelectType = view.findViewById(R.id.pop_type_tv_select_type);
        mRlTitle = view.findViewById(R.id.pop_type_tv_select_rl_title);

        mTypeSelectAdapter = new TypeSelectAdapter(activity);
        GridLayoutManager manager = new GridLayoutManager(activity, 4);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        mRcTypeSelect.addItemDecoration(dividerItemDecoration);
        mRcTypeSelect.setLayoutManager(manager);
        mRcTypeSelect.setAdapter(mTypeSelectAdapter);
        mTypeSelectAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listener.onSelectDeviceTypeItemClick(view, position, mTypeSelectAdapter.getItem(position));

            }
        });
        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_aa000000)));
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentDropDownAnim);
        mPopupWindow.setFocusable(true);
    }

    public void updateSelectDeviceTypeList(List<String> list) {
        mTypeSelectAdapter.updateDeviceTypList(list);
    }

    public void setTypeStyle(int style) {
        mTypeSelectAdapter.setTypeStyle(style);
    }

    public void setSelectDeviceTypeItemClickListener(SelectDeviceTypeItemClickListener listener) {
        this.listener = listener;
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void showAtLocation(View view, int gravity) {
        mPopupWindow.showAtLocation(view, gravity, 0, 0);
    }

    /**
     * poup 展示在某个控件下
     */
    public void showAsDropDown(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            mPopupWindow.showAsDropDown(view);
        } else {  // 适配 android 7.0
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Point point = new Point();
            mActivity.getWindowManager().getDefaultDisplay().getSize(point);
            int tempHeight = mPopupWindow.getHeight();
            if (tempHeight == WindowManager.LayoutParams.MATCH_PARENT || point.y <= tempHeight) {
                mPopupWindow.setHeight(point.y - location[1] - view.getHeight());
            }
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
        }
        mPopupWindow.showAsDropDown(view);
    }

    public void setTitleVisible(boolean isVisible) {
        mRlTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setUpAnimation() {
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentUpAnim);
    }

    public DeviceTypeModel getItem(int position) {
        return mTypeSelectAdapter.getItem(position);
    }

    public List<String> getSelectDeviceTypeList() {
        return mTypeSelectAdapter.getDataList();
    }

    public void clearAnimation() {
        mPopupWindow.setAnimationStyle(-1);
    }

    public interface SelectDeviceTypeItemClickListener {
        void onSelectDeviceTypeItemClick(View view, int position, DeviceTypeModel deviceTypeModel);
    }
}
