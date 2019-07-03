package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.smartcity.widget.FixedAspectRationImageView;

import java.util.Locale;

public class DeployPicExampleDialogUtils {
    private final TextView tvTakePhoto;
    private final ImageView imvCheck;
    private final FixedAspectRationImageView imvExample;
    private final Activity mActivity;
    private final TextView tvDescription;
    //    private AlertDialog mDialog;
    private CustomCornerDialog mDialog;
    private final TextView tvTitle;
    private DeployPicExampleClickListener listener;
    private boolean isCheck = false;
    private int mPosition;
    private String mTitle;

    public DeployPicExampleDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_dialog_deploy_pic_example, null);
        tvTitle = view.findViewById(R.id.item_dialog_deploy_pic_tv_title);
        tvDescription = view.findViewById(R.id.item_dialog_deploy_pic_tv_description);
        tvTakePhoto = view.findViewById(R.id.item_dialog_deploy_pic_tv_take_photo);
        imvCheck = view.findViewById(R.id.item_dialog_deploy_pic_imv_check);
        imvExample = view.findViewById(R.id.item_dialog_deploy_pic_imv_example);
        mActivity = activity;
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (!isCheck) {
                        mTitle = null;
                    }
                    listener.onTakePhotoClick(mTitle, mPosition);
                }
            }
        });
        imvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck = !isCheck;
                imvCheck.setImageResource(isCheck ? R.drawable.deploy_pic_check : R.drawable.deploy_pic_no_check);
            }
        });

    }


    public void show(String exampleUrl, String title, String description, int position) {
        if (mDialog != null) {
            mPosition = position;
            mTitle = title;
            if (AppUtils.isChineseLanguage()) {
                tvTitle.setText(String.format(Locale.ROOT, "%s%s", title, mActivity.getString(R.string.deploy_pic_example_pic)));
            } else {
                tvTitle.setText(String.format(Locale.ROOT, "%s%s", mActivity.getString(R.string.deploy_pic_example_pic), title));
            }
            if (TextUtils.isEmpty(description)) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setText(description);
                tvDescription.setVisibility(View.VISIBLE);
            }
            isCheck = false;
            imvCheck.setImageResource(R.drawable.deploy_pic_no_check);

            Glide.with(mActivity)                             //配置上下文
                    .load(exampleUrl)
                    .apply(new RequestOptions().error(R.drawable.deploy_pic_placeholder).placeholder(R.drawable.ic_default_image).diskCacheStrategy(DiskCacheStrategy.ALL))
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    //设置错误图片
                    //设置占位图片
                    //缓存全尺寸
                    .into(imvExample);
            mDialog.show();
//            WindowManager m = mDialog.getWindow().getWindowManager();
//            Display d = m.getDefaultDisplay();
//            WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();
//            p.width = d.getWidth() - 100;
//            mDialog.getWindow().setAttributes(p);
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }


    public void setDeployPicExampleClickListener(DeployPicExampleClickListener listener) {
        this.listener = listener;
    }

    public interface DeployPicExampleClickListener {
        void onTakePhotoClick(String title, int index);
    }

}
