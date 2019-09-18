package com.sensoro.common.widgets;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.common.R;
import com.sensoro.common.adapter.CameraListPopAdapter;
import com.sensoro.common.utils.ScreenUtils;

import com.sensoro.common.model.CameraFilterModel;

import java.util.ArrayList;
import java.util.List;

public class CameraListFilterPopupWindow {
    private final Activity mActivity;
    private  PopupWindow mPopupWindow;
    private  View mFl;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private  RelativeLayout mLl;
    private TextView resetFilter, saveFilter;
    CameraListPopAdapter cameraListPopAdapter;

    private OnCameraListFilterPopupWindowListener onCameraListFilterPopupWindowListener;

    private final List<CameraFilterModel> mList = new ArrayList<>();


    public static  final  int FILL_MODE_RATE=0;
    public static  final  int FILL_MODE_WRAPCONTENT=0;
    public static  final  int FILL_MODE_MATHPARENT=0;

    private int fillMode=FILL_MODE_RATE;//0按照0.75高度展示，1，包裹内容展示，2，铺满展示
    private float  rate=0.75f;


    public CameraListFilterPopupWindow(final Activity activity,int fillMode,float rate) {
        mActivity = activity;
        this.fillMode=fillMode;
        this.rate=rate;
        initPopWindows(activity,initView());
    }

   private View  initView(){
       View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_camera_list_filter, null);
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
       cameraListPopAdapter = new CameraListPopAdapter(mActivity);
       mRcStateSelect.setAdapter(cameraListPopAdapter);
       WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
       int height = wm.getDefaultDisplay().getHeight();
       if(fillMode==FILL_MODE_RATE){
           LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (height * rate));
           mRcStateSelect.setLayoutParams(layoutParams);
       }else if(fillMode==FILL_MODE_WRAPCONTENT){
           LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           mRcStateSelect.setLayoutParams(layoutParams);
       }else if(fillMode==FILL_MODE_MATHPARENT){
           LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           layoutParams.weight=1;
           mRcStateSelect.setLayoutParams(layoutParams);
       }
       return view;
    }

    private void initPopWindows(final Activity activity,View view){
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

        if (Build.VERSION.SDK_INT < 24) {
            mPopupWindow.showAsDropDown(view);
        } else {  // 适配 android 7.0
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            ScreenUtils.heightNavBarExisted = ScreenUtils.getRealScreenHeight(mActivity) - location[1] - view.getHeight() - ScreenUtils.getNavigationBarHeight(mActivity);
            ScreenUtils.heightNavBarNotExisted = ScreenUtils.getRealScreenHeight(mActivity) - location[1] - view.getHeight();
            if (ScreenUtils.isNavigationBarExist(mActivity)) {
                mPopupWindow.setHeight(ScreenUtils.heightNavBarExisted);
            } else {
                mPopupWindow.setHeight(ScreenUtils.heightNavBarNotExisted);
            }
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScreenUtils.isNavigationBarExist(mActivity, new ScreenUtils.OnNavigationStateListener() {
                        @Override
                        public void onNavigationState(boolean isShowing, int height) {
                            try {
                                if (isShowing) {
                                    mPopupWindow.setHeight(ScreenUtils.heightNavBarExisted);
                                    mPopupWindow.update(WindowManager.LayoutParams.MATCH_PARENT, ScreenUtils.heightNavBarExisted);
                                } else {
                                    mPopupWindow.setHeight(ScreenUtils.heightNavBarNotExisted);
                                    mPopupWindow.update(WindowManager.LayoutParams.MATCH_PARENT, ScreenUtils.heightNavBarNotExisted);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 500);
        }
        int i = cameraListPopAdapter.getItemCount() / 3;
        i *= 100;
        if (i < 300) {
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
