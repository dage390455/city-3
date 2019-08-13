package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.iwidget.IOnDestroy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置卡片背景色：holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
 * 改变阴影颜色，目前cardview 不支持，有其他解决方案，如第三库，再研究吧
 */
public class MainHomeFragRcTypeAdapter extends RecyclerView.Adapter<MainHomeFragRcTypeAdapter.MyViewHolder> implements IOnDestroy {

    private final Activity mContext;
    private List<HomeTopModel> mData = new ArrayList<>();


    public MainHomeFragRcTypeAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final RecyclerView recyclerView, final List<HomeTopModel> data) {
//        ArrayList<HomeTopModel> oldData = new ArrayList<>();
//        for (HomeTopModel homeTopModel : data) {
//            try {
//                oldData.add(homeTopModel.clone());
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//        }
//        TopListAdapterDiff indexListAdapterDiff = new TopListAdapterDiff(mData, oldData);
//        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(indexListAdapterDiff, true);
//        diffResult.dispatchUpdatesTo(this);
//        mData=oldData;
//        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
//            @Override
//            public void onInserted(final int position, final int count) {
//                LogUtils.loge("updateData-----onInserted-->>position = " + position + ", count = " + count);
//                MainHomeFragRcTypeAdapter.this.notifyItemRangeInserted(position, count);
//            }
//
//            @Override
//            public void onRemoved(final int position, final int count) {
//                MainHomeFragRcTypeAdapter.this.notifyItemRangeRemoved(position, count);
//                LogUtils.loge("updateData-----onRemoved-->>position = " + position + ", count = " + count);
//            }
//
//            @Override
//            public void onMoved(final int fromPosition, final int toPosition) {
//                notifyItemMoved(fromPosition, toPosition);
//                LogUtils.loge("updateData-----onMoved-->>fromPosition = " + fromPosition + ", toPosition = " + toPosition);
//            }
//
//            @Override
//            public void onChanged(final int position, final int count, final Object payload) {
//                MainHomeFragRcTypeAdapter.this.notifyItemRangeChanged(position, count, payload);
//                LogUtils.loge("updateData-----onChanged-->>position = " + position + ", count = " + count);
//            }
//        });
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                final ArrayList<HomeTopModel> homeTopModels = new ArrayList<>(data);
//                indexListAdapterDiff.updateTopListAdapterDiff(mData, homeTopModels);
//                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(indexListAdapterDiff, true);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        mData.clear();
////                        mData.addAll(data);
//                        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
//                            @Override
//                            public void onInserted(final int position, final int count) {
//                                LogUtils.loge("updateData-----onInserted-->>position = " + position + ", count = " + count);
//                                MainHomeFragRcTypeAdapter.this.notifyItemRangeInserted(position, count);
//                                mData = homeTopModels;
//                            }
//
//                            @Override
//                            public void onRemoved(final int position, final int count) {
//                                MainHomeFragRcTypeAdapter.this.notifyItemRangeRemoved(position, count);
//                                LogUtils.loge("updateData-----onRemoved-->>position = " + position + ", count = " + count);
//                                mData = homeTopModels;
//                            }
//
//                            @Override
//                            public void onMoved(final int fromPosition, final int toPosition) {
//                                notifyItemMoved(fromPosition, toPosition);
//                                LogUtils.loge("updateData-----onMoved-->>fromPosition = " + fromPosition + ", toPosition = " + toPosition);
//                                mData = homeTopModels;
//                            }
//
//                            @Override
//                            public void onChanged(final int position, final int count, final Object payload) {
//                                MainHomeFragRcTypeAdapter.this.notifyItemRangeChanged(position, count, payload);
//                                LogUtils.loge("updateData-----onChanged-->>position = " + position + ", count = " + count);
//                                mData = homeTopModels;
//                            }
//                        });
//                    }
//                });
//
//            }
//        });
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<HomeTopModel> getData() {
        return mData;
    }

    @Override
    public void onDestroy() {
        mData.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_home_rc_type, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        HomeTopModel homeTopModel = mData.get(position);
        int type = homeTopModel.status;
        freshType(holder, type, position);
        int value = homeTopModel.value;
        freshValue(holder, value);
    }

    private void freshValue(MyViewHolder holder, int value) {
        holder.mainRcTypeTvCount.setText(String.valueOf(value));
    }

    private void freshType(MyViewHolder holder, int type, int position) {
        int currentColor = R.color.c_1dbb99;
        switch (type) {
            case 0:
                currentColor = R.color.c_f34a4a;
                holder.mainRcTypeTvStateTxt.setText(R.string.warning_monitoring_point);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_warning);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_alarm);
                break;
            case 1:
                currentColor = R.color.c_1dbb99;
                holder.mainRcTypeTvStateTxt.setText(R.string.normal_monitoring_point);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_normal);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_normal);
                break;
            case 2:
                currentColor = R.color.c_5d5d5d;
                holder.mainRcTypeTvStateTxt.setText(R.string.lost_monitoring_point);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_lose);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_lost);
                break;
            case 3:
                currentColor = R.color.c_b6b6b6;
                holder.mainRcTypeTvStateTxt.setText(R.string.inactive_monitoring_point);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_inactivated);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_inactivated);
                break;
            case 4:
                currentColor = R.color.c_fdc83b;
                holder.mainRcTypeTvStateTxt.setText(R.string.malfunction_monitoring_point);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_trouble);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_wrong);
                break;
        }
        holder.mainRcTypeTvStateTxt.setTextColor(Color.WHITE);
        holder.mainRcTypeTvCount.setTextColor(Color.WHITE);
        holder.mainRcTypeImvState.setColorFilter(mContext.getResources().getColor(R.color.white));
        holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(currentColor));
        if (position == 0) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeStatusRoot.getLayoutParams());
            int pxL = AppUtils.dp2px(mContext, 14);
            lp.setMargins(pxL, 0, 0, 0);
            holder.homeStatusRoot.setLayoutParams(lp);
        } else  if (position == mData.size() - 1) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeStatusRoot.getLayoutParams());
                int pxR = AppUtils.dp2px(mContext, 14);
                lp.setMargins(0, 0, pxR, 0);
                holder.homeStatusRoot.setLayoutParams(lp);
         }else{
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeStatusRoot.getLayoutParams());
            int pxR = AppUtils.dp2px(mContext, 14);
            lp.setMargins(0, 0, 0, 0);
            holder.homeStatusRoot.setLayoutParams(lp);
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            try {
                LogUtils.loge("updateData-----onBindViewHolder-->>position = " + position + ", payloads = " + payloads);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            HashMap map = (HashMap) payloads.get(0);
            Integer type = (Integer) map.get("type");
            if (type != null) {
                freshType(holder, type, position);
            }
            Integer value = (Integer) map.get("value");
            if (value != null) {
                freshValue(holder, value);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.main_rc_type_imv_state)
        ImageView mainRcTypeImvState;
        @BindView(R.id.main_rc_type_tv_count)
        TextView mainRcTypeTvCount;
        @BindView(R.id.main_rc_type_cv)
        CardView mainRcTypeCv;
        @BindView(R.id.main_rc_type_tv_state_txt)
        TextView mainRcTypeTvStateTxt;
        @BindView(R.id.iv_home_status_card)
        ImageView ivHomeStatusCard;
        @BindView(R.id.home_status_root)
        LinearLayout homeStatusRoot;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
