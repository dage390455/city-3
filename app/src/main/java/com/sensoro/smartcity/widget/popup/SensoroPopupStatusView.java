package com.sensoro.smartcity.widget.popup;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.IndexFilterStatusAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sensoro on 17/10/30.
 */

public class SensoroPopupStatusView extends LinearLayout implements RecycleViewItemClickListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private IndexFilterStatusAdapter mAdapter;
    private View mPopupView;
    private SensoroShadowView mShadowLayout;
    private Animation showAnimation;
    private Animation dismissAnimation;
    private OnStatusPopupItemClickListener mListener;

    public SensoroPopupStatusView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SensoroPopupStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SensoroPopupStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void init() {
        mPopupView = LayoutInflater.from(mContext).inflate(R.layout.layout_status_popup, this);
        List<String> tempList = Arrays.asList(Constants.INDEX_STATUS_ARRAY);
        mAdapter = new IndexFilterStatusAdapter(mContext, tempList, this);
        mRecyclerView = (RecyclerView) mPopupView.findViewById(R.id.index_status_popup_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(true, 0));
        mRecyclerView.setAdapter(mAdapter);
        showAnimation = AnimationUtils.loadAnimation(mContext, R.anim.push_menu_fadein);   //得到一个LayoutAnimationController对象；
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dismissAnimation = AnimationUtils.loadAnimation(mContext, R.anim.push_menu_fadeout);   //得到一个LayoutAnimationController对象；
        dismissAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mShadowLayout != null) {
                    mShadowLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void show(SensoroShadowView shadowLayout, OnStatusPopupItemClickListener listener) {
        this.mShadowLayout = shadowLayout;
        this.mShadowLayout.setVisibility(VISIBLE);
        this.mListener = listener;
        setVisibility(View.VISIBLE);
        this.startAnimation(showAnimation);
    }

    public void dismiss() {
        if (this.getVisibility() == VISIBLE) {
            this.startAnimation(dismissAnimation);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mListener.onItemClick(view, position);
        dismiss();
    }

    public interface OnStatusPopupItemClickListener {
        void onItemClick(View view, int position);
    }

}
