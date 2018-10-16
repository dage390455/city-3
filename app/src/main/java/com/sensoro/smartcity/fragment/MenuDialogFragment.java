package com.sensoro.smartcity.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.util.PreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MenuDialogFragment extends DialogFragment {
    @BindView(R.id.dialog_main_home_menu_imv_close)
    ImageButton dialogMainHomeMenuImvClose;
    @BindView(R.id.rl_fast_deploy)
    RelativeLayout rlFastDeploy;
    @BindView(R.id.rl_fast_contract)
    RelativeLayout rlFastContract;
    @BindView(R.id.rl_fast_scan_login)
    RelativeLayout rlFastScanLogin;
    @BindView(R.id.dialog_main_home_menu_rl_root)
    RelativeLayout dialogMainHomeMenuRlRoot;
    Unbinder unbinder;
    private OnDismissListener onDismissListener;
    private int currentResId;

    public interface OnDismissListener {
        void onMenuDialogFragmentDismiss(int resId);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.c_aa000000);
        }
        View view = inflater.inflate(R.layout.dialog_frag_main_menu, container);
        unbinder = ButterKnife.bind(this, view);
        checkPermission();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dialog_main_home_menu_imv_close, R.id.rl_fast_deploy, R.id.rl_fast_contract, R.id.rl_fast_scan_login, R.id.dialog_main_home_menu_rl_root})
    public void onViewClicked(View view) {
        currentResId = view.getId();
        getDialog().dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onMenuDialogFragmentDismiss(currentResId);
        }
    }

    private void checkPermission() {
        try {
            EventLoginData userData = PreferencesHelper.getInstance().getUserData();
            if (userData != null) {
                if (userData.hasContract) {
                    rlFastContract.setVisibility(View.VISIBLE);
                } else {
                    rlFastContract.setVisibility(View.GONE);
                }
                if (userData.hasScanLogin) {
                    rlFastScanLogin.setVisibility(View.VISIBLE);
                } else {
                    rlFastScanLogin.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
