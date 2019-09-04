package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.common.utils.ScreenUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.CameraListPopAdapter;
import com.sensoro.common.model.CameraFilterModel;

import java.util.ArrayList;
import java.util.List;

public class CameraListFilterPopupWindow {
    private final Activity mActivity;
    private final PopupWindow mPopupWindow;
    private final View mFl;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private final RelativeLayout mLl;
    private TextView resetFilter, saveFilter;
    CameraListPopAdapter cameraListPopAdapter;

    private OnCameraListFilterPopupWindowListener onCameraListFilterPopupWindowListener;

    private final List<CameraFilterModel> mList = new ArrayList<>();


    public CameraListFilterPopupWindow(final Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_camera_list_filter, null);
        mFl = view.findViewById(R.id.item_pop_rl);
        final RecyclerView mRcStateSelect = view.findViewById(R.id.pop_rc_camera_list);
        mLl = view.findViewById(R.id.item_pop_select_state_ll);
        resetFilter = view.findViewById(R.id.camera_list_reset_filter);
        saveFilter = view.findViewById(R.id.camera_list_save_filter);
        view.findViewById(R.id.pop_type_view_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRcStateSelect.setLayoutManager(linearLayoutManager);
        cameraListPopAdapter = new CameraListPopAdapter(activity);
        mRcStateSelect.setAdapter(cameraListPopAdapter);
        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);

        int height = wm.getDefaultDisplay().getHeight();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.75));


        mRcStateSelect.setLayoutParams(layoutParams);


        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
//        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        mPopupWindow.setFocusable(true);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_B3000000)));
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentDropDownAnim);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onCameraListFilterPopupWindowListener != null) {
                    onCameraListFilterPopupWindowListener.onDismiss();
                }
            }
        });
        initAnimation();

        //重置
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCameraListFilterPopupWindowListener != null) {
                    onCameraListFilterPopupWindowListener.onReset();
                }
            }
        });

        //保存
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCameraListFilterPopupWindowListener != null) {
                    onCameraListFilterPopupWindowListener.onSave(mList);
                }
            }
        });

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

    public void updateSelectDeviceStatusList(List<CameraFilterModel> list) {
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
        cameraListPopAdapter.updateDeviceTypList(list);
    }


    /**
     * poup 展示在某个控件下
     */
    public void showAsDropDown(View view, List<CameraFilterModel> list) {
        updateSelectDeviceStatusList(list);
        cameraListPopAdapter.notifyDataSetChanged();
//        if (Build.VERSION.SDK_INT < 24) {
//            mPopupWindow.showAsDropDown(view);
//        } else {  // 适配 android 7.0
//            int[] location = new int[2];
//            view.getLocationOnScreen(location);
//            Point point = new Point();
//            mActivity.getWindowManager().getDefaultDisplay().getSize(point);
//            int tempHeight = mPopupWindow.getHeight();
//            if (tempHeight == WindowManager.LayoutParams.MATCH_PARENT || point.y <= tempHeight) {
//                mPopupWindow.setHeight(point.y - location[1] - view.getHeight());
//            }
//            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
//        }
//        mPopupWindow.showAsDropDown(view);
//        int i = cameraListPopAdapter.getItemCount() / 3;
//        i *= 100;
//        if (i < 300) {
//            i = 300;
//        }
//        showTranslateAnimation.setDuration(i);
//        dismissTranslateAnimation.setDuration(i);
//        mFl.startAnimation(showTranslateAnimation);


        if (Build.VERSION.SDK_INT < 24) {
            mPopupWindow.showAsDropDown(view);
        } else {  // 适配 android 7.0
            int[] location = new int[2];
            view.getLocationOnScreen(location);


            Point point = new Point();
            mActivity.getWindowManager().getDefaultDisplay().getSize(point);
            mPopupWindow.setHeight(point.y - location[1] - view.getHeight()+ ScreenUtils.getBottomStatusHeight(mActivity));
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
            ScreenUtils.isNavigationBarExist(mActivity, new ScreenUtils.OnNavigationStateListener() {
                @Override
                public void onNavigationState(boolean isShowing, int height) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isShowing){
                                mPopupWindow.setHeight(point.y - location[1] - view.getHeight());
                            }else{
                                mPopupWindow.setHeight(point.y - location[1] - view.getHeight()+ height);
                            }
                        }
                    }, 500);

                }
            });
        }
        int i = cameraListPopAdapter.getItemCount() / 3;
        i *= 100;
        if(i<300){
            i = 300;
        }
        showTranslateAnimation.setDuration(i);
        dismissTranslateAnimation.setDuration(i);
        mFl.startAnimation(showTranslateAnimation);




    }

    public void onDestroy() {
        mList.clear();
        if (dismissTranslateAnimation != null) {
            dismissTranslateAnimation.cancel();
        }
        mPopupWindow.dismiss();
    }

    public void setUpAnimation() {
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentUpAnim);
    }


    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void clearAnimation() {
        mPopupWindow.setAnimationStyle(-1);
    }

    public void setOnCameraListFilterPopupWindowListener(OnCameraListFilterPopupWindowListener listFilterPopupWindowListener) {
        this.onCameraListFilterPopupWindowListener = listFilterPopupWindowListener;
    }

    public void dismiss() {
        mFl.startAnimation(dismissTranslateAnimation);
    }

    public interface OnCameraListFilterPopupWindowListener {
        void onSave(List<CameraFilterModel> list);

        void onDismiss();

        void onReset();
    }
}
