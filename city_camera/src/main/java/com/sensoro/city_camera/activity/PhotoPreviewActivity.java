package com.sensoro.city_camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ortiz.touchview.TouchImageView;
import com.sensoro.city_camera.IMainViews.IPhotoPreviewView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.city_camera.presenter.PhotoPreviewPresenter;
import com.sensoro.city_camera.widget.ExtendedViewPager;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.SensoroToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author bin.tian
 */
public class PhotoPreviewActivity extends BaseActivity<IPhotoPreviewView, PhotoPreviewPresenter> implements IPhotoPreviewView {

    @BindView(R2.id.image_viewpager)
    ExtendedViewPager mViewPager;
    @BindView(R2.id.security_warn_image_preview_type_tv)
    TextView mSecurityWarnTypeTv;
    @BindView(R2.id.security_warn_image_preview_title_tv)
    TextView mSecurityWarnTitleTv;
    @BindView(R2.id.security_warn_image_preview_subtitle_tv)
    TextView mSecurityWarnSubTitleTv;
    @BindView(R2.id.photo_info_rl)
    View mPhotoInfoView;

    private List<String> mUrlList = new ArrayList<>();

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_preview);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.c_383838));

        ButterKnife.bind(this);

        mPresenter.initData(this);
    }

    @Override
    protected PhotoPreviewPresenter createPresenter() {
        return new PhotoPreviewPresenter();
    }

    @Override
    public void updatePhotoList(List<String> urlList, int position) {
        mUrlList.clear();
        mUrlList.addAll(urlList);

        mViewPager.setAdapter(new TouchImageAdapter());

        mViewPager.setCurrentItem(position);
    }

    @Override
    public void updatePhotoInfo(int securityType, String title, String subTitle) {
        mPhotoInfoView.setVisibility(View.VISIBLE);
        mSecurityWarnTitleTv.setText(title);
        mSecurityWarnSubTitleTv.setText(subTitle);

        switch (securityType) {
            case SecurityConstants.SECURITY_TYPE_FOCUS:
                mSecurityWarnTypeTv.setText(R.string.focus_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_focus_bg);
                break;
            case SecurityConstants.SECURITY_TYPE_FOREIGN:
                mSecurityWarnTypeTv.setText(R.string.external_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_foreign_bg);
                break;
            case SecurityConstants.SECURITY_TYPE_INVADE:
                mSecurityWarnTypeTv.setText(R.string.invade_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_invade_bg);
                break;
            default:
        }
    }

    @OnClick({R2.id.back_iv, R2.id.download_iv})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.back_iv) {
            mPresenter.doBack();
        } else if (i == R.id.download_iv) {
            mPresenter.doDownload(mUrlList.get(mViewPager.getCurrentItem()));
        }
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    class TouchImageAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            Glide.with(container.getContext()).load(mUrlList.get(position)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    img.setImageDrawable(resource);
                }
            });
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
