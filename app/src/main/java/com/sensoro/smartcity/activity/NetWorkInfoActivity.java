package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.INetWorkInfoActivityView;
import com.sensoro.smartcity.presenter.NetWorkInfoActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网络信息
 */
public class NetWorkInfoActivity extends BaseActivity<INetWorkInfoActivityView, NetWorkInfoActivityPresenter> {
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
    }

    @Override
    protected NetWorkInfoActivityPresenter createPresenter() {
        return new NetWorkInfoActivityPresenter();
    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {

        finish();
    }

}
