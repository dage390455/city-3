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
import com.sensoro.smartcity.adapter.IndexFilterTypeAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sensoro on 17/10/30.
 */

public class SensoroPopupTypeView extends LinearLayout implements RecycleViewItemClickListener, Constants {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private IndexFilterTypeAdapter mAdapter;
    private SensoroShadowView mShadowLayout;
    private Animation showAnimation;
    private Animation dismissAnimation;
    private OnTypePopupItemClickListener mListener;

    public SensoroPopupTypeView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SensoroPopupTypeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SensoroPopupTypeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void init() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_type_popup, this);
        List<String> tempList = Arrays.asList(Constants.SELECT_TYPE_VALUES);
        mAdapter = new IndexFilterTypeAdapter(mContext, tempList, this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.index_type_popup_rv);
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

    public void show(OnTypePopupItemClickListener listener) {
        setVisibility(View.VISIBLE);
        this.mListener = listener;
        this.startAnimation(showAnimation);
    }

    public void show(SensoroShadowView shadowLayout, OnTypePopupItemClickListener listener) {
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

    public interface OnTypePopupItemClickListener {
        void onItemClick(View view, int position);
    }
}
