package com.sensoro.common.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


import com.sensoro.common.R;
import com.sensoro.common.adapter.StateSelectAdapter;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.model.StatusCountModel;
import com.sensoro.common.utils.ScreenUtils;

import java.lang.reflect.Method;
import java.util.List;



public class StatePopUtils {
    private final Activity mActivity;
    private final StateSelectAdapter mSelectStateAdapter;
    private final PopupWindow mPopupWindow;
    private SelectDeviceTypeItemClickListener listener;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private final LinearLayout mll;
    private final RelativeLayout mRl;

    public StatePopUtils(Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_pop_inspection_task_select_state, null);
        RecyclerView mRcStateSelect = view.findViewById(R.id.pop_inspection_task_rc_select_state);
        mll = view.findViewById(R.id.item_pop_select_state_ll);
        mRl = view.findViewById(R.id.item_pop_select_state_rl);
        view.findViewById(R.id.pop_inspection_task_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRl.startAnimation(dismissTranslateAnimation);
            }
        });

        mSelectStateAdapter = new StateSelectAdapter(activity);
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        mRcStateSelect.setLayoutManager(manager);
        mRcStateSelect.setAdapter(mSelectStateAdapter);
        mSelectStateAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listener.onSelectDeviceTypeItemClick(view,position);

            }
        });


        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        if (Build.VERSION.SDK_INT < 24) {
//        } else {  // 适配 android 7.0
//            int[] location = new int[2];
//            view.getLocationOnScreen(location);
//            Point point = new Point();
//            mActivity.getWindowManager().getDefaultDisplay().getSize(point);
//            mPopupWindow.setHeight(point.y - location[1] - view.getHeight());
//        }

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_B3000000)));
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

    public void updateSelectDeviceStatusList(List<StatusCountModel> list){
        mSelectStateAdapter.updateDeviceTypList(list);
    }
    public void setSelectDeviceTypeItemClickListener(SelectDeviceTypeItemClickListener listener){
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
            ScreenUtils.heightNavBarExisted=ScreenUtils.getRealScreenHeight(mActivity) - location[1] - view.getHeight()-ScreenUtils.getNavigationBarHeight(mActivity);
            ScreenUtils.heightNavBarNotExisted=ScreenUtils.getRealScreenHeight(mActivity) - location[1]- view.getHeight() ;
            if(ScreenUtils.isNavigationBarExist(mActivity)){
                mPopupWindow.setHeight(ScreenUtils.heightNavBarExisted);
            }else{
                mPopupWindow.setHeight(ScreenUtils.heightNavBarNotExisted);
            }
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScreenUtils.isNavigationBarExist(mActivity, new ScreenUtils.OnNavigationStateListener() {
                        @Override
                        public void onNavigationState(boolean isShowing,  int height) {
                            try {
                                if(isShowing){
                                    mPopupWindow.setHeight(ScreenUtils.heightNavBarExisted);
                                    mPopupWindow.update(WindowManager.LayoutParams.MATCH_PARENT,ScreenUtils.heightNavBarExisted);
                                }else{
                                    mPopupWindow.setHeight(ScreenUtils.heightNavBarNotExisted);
                                    mPopupWindow.update(WindowManager.LayoutParams.MATCH_PARENT,ScreenUtils.heightNavBarNotExisted);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            },500);

        }
        int i = mSelectStateAdapter.getItemCount() / 3;
        i *= 100;
        if(i<300){
            i = 300;
        }
        showTranslateAnimation.setDuration(i);
        dismissTranslateAnimation.setDuration(i);
        mRl.startAnimation(showTranslateAnimation);
    }
    public void setUpAnimation() {
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentUpAnim);
    }

    public StatusCountModel getItem(int position) {
        return mSelectStateAdapter.getItem(position);
    }

    public boolean isShowing(){
        return mPopupWindow.isShowing();
    }

    public void clearAnimation() {
        mPopupWindow.setAnimationStyle(-1);
    }

    public boolean isData() {
        return mSelectStateAdapter.getItemCount()>0;
    }

    public interface SelectDeviceTypeItemClickListener{
        void onSelectDeviceTypeItemClick(View view, int position);
    }





}
