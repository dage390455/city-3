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
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MenuDialogFragment extends DialogFragment {
    @BindView(R.id.dialog_main_home_menu_imv_close)
    ImageButton dialogMainHomeMenuImvClose;
    @BindView(R.id.dialog_main_home_menu_tv_quick_deploy)
    TextView dialogMainHomeMenuTvQuickDeploy;
    @BindView(R.id.dialog_main_home_menu_new_tv_construction)
    TextView dialogMainHomeMenuNewTvConstruction;
    @BindView(R.id.dialog_main_home_menu_tv_scan_login)
    TextView dialogMainHomeMenuTvScanLogin;
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

    @OnClick({R.id.dialog_main_home_menu_imv_close, R.id.dialog_main_home_menu_tv_quick_deploy, R.id.dialog_main_home_menu_new_tv_construction, R.id.dialog_main_home_menu_tv_scan_login, R.id.dialog_main_home_menu_rl_root})
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
}
