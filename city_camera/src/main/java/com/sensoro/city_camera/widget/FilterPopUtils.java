package com.sensoro.city_camera.widget;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.adapter.FilterAdapter;
import com.sensoro.city_camera.model.FilterModel;
import com.sensoro.common.callback.RecycleViewItemClickListener;

import java.util.List;


/**
 * 筛选下拉弹窗
 */

public class FilterPopUtils {
    private final Activity mActivity;
    private final FilterAdapter mSelectStateAdapter;
    private final PopupWindow mPopupWindow;
    private SelectFilterTypeItemClickListener listener;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private LinearLayout mll;
    private LinearLayout mRl;

    public FilterPopUtils(Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.security_filter_popuwindow_layout, null);
        RecyclerView mRcStateSelect = view.findViewById(R.id.pop_inspection_task_rc_select_state);
        mll = view.findViewById(R.id.item_pop_select_state_ll);
        mRl = view.findViewById(R.id.item_pop_select_state_rl);
        view.findViewById(R.id.pop_inspection_task_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRl.startAnimation(dismissTranslateAnimation);
            }
        });

        mSelectStateAdapter = new FilterAdapter(activity);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        mRcStateSelect.setLayoutManager(manager);
        mRcStateSelect.setAdapter(mSelectStateAdapter);
        mSelectStateAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listener.onSelectFilterTypeItemClick(view, position);

            }
        });
        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_B2000000)));
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentDropDownAnim);
//        mPopupWindow.setFocusable(true);
        initAnimation();

    }

    private void initAnimation() {
        showTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0);
        dismissTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1);
        dismissTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPopupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void updateSelectDeviceStatusList(List<FilterModel> list) {
        mSelectStateAdapter.updateDeviceTypList(list);
    }

    public void setSelectDeviceTypeItemClickListener(SelectFilterTypeItemClickListener listener) {
        this.listener = listener;
    }

    public void dismiss() {
        mRl.startAnimation(dismissTranslateAnimation);
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
        int i = mSelectStateAdapter.getItemCount() / 3;
        i *= 100;
        if (i < 300) {
            i = 300;
        }
        showTranslateAnimation.setDuration(i);
        dismissTranslateAnimation.setDuration(i);
        mRl.startAnimation(showTranslateAnimation);
    }

    public void setUpAnimation() {
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentUpAnim);
    }

    public FilterModel getItem(int position) {
        return mSelectStateAdapter.getItem(position);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void clearAnimation() {
        mPopupWindow.setAnimationStyle(-1);
    }

    public boolean isData() {
        return mSelectStateAdapter.getItemCount() > 0;
    }

    public interface SelectFilterTypeItemClickListener {
        void onSelectFilterTypeItemClick(View view, int position);
    }
}
