package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.INetWorkInfoActivityView;
import com.sensoro.smartcity.presenter.NetWorkInfoActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网络信息
 */
public class NetWorkInfoActivity extends BaseActivity<INetWorkInfoActivityView, NetWorkInfoActivityPresenter> implements INetWorkInfoActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;

    @BindView(R.id.tv_networking_mode)
    TextView tvNetworkingMode;
    @BindView(R.id.tv_ip)
    TextView tvIp;
    @BindView(R.id.tv_gateway)
    TextView tvGateway;
    @BindView(R.id.tv_sub_merchant)
    TextView tvSubMerchant;
    @BindView(R.id.tv_primary_dns)
    TextView tvPrimaryDns;
    @BindView(R.id.tv_secondary_dns)
    TextView tvSecondaryDns;
    @BindView(R.id.tv_vpn)
    TextView tvVpn;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_network_info);
        ButterKnife.bind(this);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(R.string.network_info);
        mPresenter.initData(this);
    }

    @Override
    protected NetWorkInfoActivityPresenter createPresenter() {
        return new NetWorkInfoActivityPresenter();
    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {

        finish();
    }

    @Override
    public void updateNetWork(BaseStationDetailModel.NetWork netWork) {


        if (!TextUtils.isEmpty(netWork.getIp())) {
            tvIp.setText(netWork.getIp());


        }
        if (!TextUtils.isEmpty(netWork.getGw())) {

            tvGateway.setText(netWork.getGw());

        }
        if (!TextUtils.isEmpty(netWork.getNmask())) {

            tvSubMerchant.setText(netWork.getNmask());

        }
        if (!TextUtils.isEmpty(netWork.getPdns())) {

            tvPrimaryDns.setText(netWork.getPdns());

        }
        if (!TextUtils.isEmpty(netWork.getAdns())) {
            tvSecondaryDns.setText(netWork.getAdns());

        }
        if (!TextUtils.isEmpty(netWork.getAcm())) {

            tvNetworkingMode.setText(netWork.getAcm());

        }
        if (!TextUtils.isEmpty(netWork.getVpn())) {

            tvVpn.setText(netWork.getVpn());

        }

    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

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
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }
}
