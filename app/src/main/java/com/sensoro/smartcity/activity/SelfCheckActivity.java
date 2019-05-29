package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SelfCheckAdapter;
import com.sensoro.smartcity.imainviews.ISelfCheckActivityView;
import com.sensoro.smartcity.presenter.SelfCheckActivityPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelfCheckActivity extends BaseActivity<ISelfCheckActivityView, SelfCheckActivityPresenter> {
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
    @BindView(R.id.ac_self_check_rc)
    RecyclerView acSelfCheckRc;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ic_no_content)
    LinearLayout ic_no_content;

    SelfCheckAdapter selfCheckAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_self_check);
        ButterKnife.bind(this);
        includeTextTitleTvTitle.setText(R.string.Self_check_state);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        selfCheckAdapter = new SelfCheckAdapter(this);


        acSelfCheckRc.setLayoutManager(new LinearLayoutManager(this));
        acSelfCheckRc.setAdapter(selfCheckAdapter);


        ArrayList<String> selftest = getIntent().getStringArrayListExtra("selftest");


        if (null != selftest && selftest.size() > 0) {

            selfCheckAdapter.updateAdapter(selftest);

        } else {
            ic_no_content.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);

        }
        refreshLayout.setEnabled(false);

    }

    @Override
    protected SelfCheckActivityPresenter createPresenter() {
        return new SelfCheckActivityPresenter();
    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {

        finish();
    }
}
