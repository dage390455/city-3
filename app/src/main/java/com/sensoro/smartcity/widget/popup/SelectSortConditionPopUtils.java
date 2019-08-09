/**
 * 过滤条件选择弹出框工具类
 */
package com.sensoro.smartcity.widget.popup;


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
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SortConditionSelectAdapter;
import com.sensoro.smartcity.model.SortConditionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SelectSortConditionPopUtils {
    private final PopupWindow mPopupWindow;
    private final Activity mActivity;



    private SelectSortConditionItemClickListener mSelectFilterConditionItemClickListener;
    private SortConditionSelectAdapter mSortConditionSelectAdapter;
    private final RelativeLayout mRlTitle;
    private TranslateAnimation showTranslateAnimation;
    private final FrameLayout mFl;
    private TranslateAnimation dismissTranslateAnimation;



    public SelectSortConditionPopUtils(Activity activity) {
        mActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_pop_sortcondition_select, null);
        RecyclerView mRcSortConditionSelect = view.findViewById(R.id.pop_sortcondition_select_rc);
        mFl = view.findViewById(R.id.pop_sortcondition_fl);
        final TextView tvSelectType = view.findViewById(R.id.pop_sortcondition_tv_select_type);
        view.findViewById(R.id.pop_sortcondition_view_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPopupWindow.dismiss();
                mFl.startAnimation(dismissTranslateAnimation);
            }
        });

        mRlTitle = view.findViewById(R.id.pop_sortcondition_tv_select_rl_title);

        mSortConditionSelectAdapter = new SortConditionSelectAdapter(activity);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(mActivity.getResources().getDrawable(R.drawable.shape_soid_df));
        mRcSortConditionSelect.addItemDecoration(dividerItemDecoration);
        mRcSortConditionSelect.setLayoutManager(manager);
        mRcSortConditionSelect.setAdapter(mSortConditionSelectAdapter);
        mSortConditionSelectAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSelectFilterConditionItemClickListener.onSelectSortConditionItemClick(view, position, mSortConditionSelectAdapter.getItem(position));
            }
        });

        mPopupWindow = new PopupWindow(activity);
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
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


    public void updateSortConditionList(List mSortConditionList,SortConditionModel selectedCondition){
        mSortConditionSelectAdapter.setmSelectSortCondition(selectedCondition);
        mSortConditionSelectAdapter.updateSortConditionList(mSortConditionList);
    }

    public void dismiss() {
        mFl.startAnimation(dismissTranslateAnimation);
//        mPopupWindow.dismiss();
    }

    public void showAtLocation(View view, int gravity) {
        mPopupWindow.showAtLocation(view, gravity, 0, 0);
//        int i = mTypeSelectAdapter.getItemCount() / 4;
//        i *= 100;
        showTranslateAnimation.setDuration(300);
        dismissTranslateAnimation.setDuration(300);
        mFl.startAnimation(showTranslateAnimation);
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

        showAnimation();
    }


    public SelectSortConditionItemClickListener getmSelectFilterConditionItemClickListener() {
        return mSelectFilterConditionItemClickListener;
    }

    public void setmSelectFilterConditionItemClickListener(SelectSortConditionItemClickListener mSelectFilterConditionItemClickListener) {
        this.mSelectFilterConditionItemClickListener = mSelectFilterConditionItemClickListener;
    }


    private void showAnimation() {
        showTranslateAnimation.setDuration(300);
        dismissTranslateAnimation.setDuration(300);
        mFl.startAnimation(showTranslateAnimation);
    }

    public void setTitleVisible(boolean isVisible) {
        mRlTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setUpAnimation() {
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentUpAnim);
    }

    public SortConditionModel getItem(int position) {
        return mSortConditionSelectAdapter.getItem(position);
    }

    public List<SortConditionModel> getSelectSortConditionList() {
        return mSortConditionSelectAdapter.getDataList();
    }

    public void clearAnimation() {
        mPopupWindow.setAnimationStyle(-1);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public interface SelectSortConditionItemClickListener {
        void onSelectSortConditionItemClick(View view, int position, SortConditionModel sortCondition);
    }
}
