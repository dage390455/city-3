package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.CameraListPopAdapter;
import com.sensoro.smartcity.model.CameraFilterModel;
import com.yixia.camera.util.StringUtils;

import java.util.HashMap;
import java.util.List;

public class CameraListFilterPopupWindow {
    private final Activity mActivity;
    private final PopupWindow mPopupWindow;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private final RelativeLayout mLl;
    private TextView resetFilter, saveFilter;
    CameraListPopAdapter cameraListPopAdapter;

    private SelectModleListener mSelectModleListener;
    private DismissListener dismissListener;

    public CameraListFilterPopupWindow(final Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_camera_list_filter, null);
        final RecyclerView mRcStateSelect = view.findViewById(R.id.pop_rc_camera_list);
        mLl = view.findViewById(R.id.item_pop_select_state_ll);
        resetFilter = view.findViewById(R.id.camera_list_reset_filter);
        saveFilter = view.findViewById(R.id.camera_list_save_filter);
        view.findViewById(R.id.item_pop_select_state_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLl.startAnimation(dismissTranslateAnimation);
            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRcStateSelect.setLayoutManager(linearLayoutManager);
        cameraListPopAdapter = new CameraListPopAdapter(activity);
        mRcStateSelect.setAdapter(cameraListPopAdapter);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1200);


        mRcStateSelect.setLayoutParams(layoutParams);


//        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
//        int height = wm.getDefaultDisplay().getHeight();
//
//
//        if (height - mRcStateSelect.getLayoutParams().height < 100) {
//
//        }


        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_aa000000)));
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentDropDownAnim);
        initAnimation();
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != cameraListPopAdapter.getmStateCountList()) {


                    List<CameraFilterModel> list = cameraListPopAdapter.getmStateCountList();

                    for (CameraFilterModel model : list) {

                        for (CameraFilterModel.ListBean countModel : model.getList()) {

                            countModel.setSelect(false);
                        }

                    }

                    cameraListPopAdapter.notifyDataSetChanged();
//                    cameraListPopAdapter = new CameraListPopAdapter(activity);
//                    mRcStateSelect.setAdapter(cameraListPopAdapter);
                }


            }
        });

        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cameraListPopAdapter.getmStateCountList()) {


                    List<CameraFilterModel> list = cameraListPopAdapter.getmStateCountList();

                    HashMap hashMap = new HashMap();
                    for (CameraFilterModel model : list) {

                        String key = model.getKey();
                        StringBuffer stringBuffer = new StringBuffer();


                        for (CameraFilterModel.ListBean listBean : model.getList()) {

                            if (listBean.isSelect()) {
                                stringBuffer.append(listBean.getCode());

                                stringBuffer.append(",");
                            }
                        }
                        if (!StringUtils.isEmpty(stringBuffer.toString())) {
//                            stringBuffer.substring(0, stringBuffer.length() - 1);
                            stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
                            hashMap.put(key, stringBuffer);
                        }

                    }

                    if (mSelectModleListener != null) {

                        mSelectModleListener.selectedListener(hashMap);
                    }
                    dismiss();

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

                if (null != dismissListener) {
                    dismissListener.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void updateSelectDeviceStatusList(List<CameraFilterModel> list) {
        cameraListPopAdapter.updateDeviceTypList(list);
    }


    public void dismiss() {
        mLl.startAnimation(dismissTranslateAnimation);
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
        int i = cameraListPopAdapter.getItemCount() / 3;
        i *= 100;
        if (i < 300) {
            i = 300;
        }
        showTranslateAnimation.setDuration(i);
        dismissTranslateAnimation.setDuration(i);
        mLl.startAnimation(showTranslateAnimation);


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


    public void setSelectModleListener(SelectModleListener listener) {

        mSelectModleListener = listener;
    }

    public interface SelectModleListener {

        void selectedListener(HashMap<String, String> hashMap);
    }

    public void setDismissListener(DismissListener listener) {

        dismissListener = listener;
    }

    public interface DismissListener {

        void dismiss();
    }


}
