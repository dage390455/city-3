package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DiffUtils.TopListAdapterDiff;
import com.sensoro.smartcity.iwidget.IOnDestroy;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;

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
    private final List<HomeTopModel> mData = new ArrayList<>();
    private OnTopClickListener onTopClickListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public MainHomeFragRcTypeAdapter(Activity context) {
        mContext = context;
    }

    public void setOnTopClickListener(OnTopClickListener onTopClickListener) {
        this.onTopClickListener = onTopClickListener;
    }

    public void updateData(final RecyclerView recyclerView, final List<HomeTopModel> data) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                TopListAdapterDiff indexListAdapterDiff = new TopListAdapterDiff(mData, data);
                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(indexListAdapterDiff, true);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                            @Override
                            public void onInserted(final int position, final int count) {
                                LogUtils.loge("updateData-----onInserted-->>position = " + position + ", count = " + count);
                                notifyItemRangeInserted(position, count);
                                try {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(position);
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onRemoved(final int position, final int count) {
                                notifyItemRangeRemoved(position, count);
                                LogUtils.loge("updateData-----onRemoved-->>position = " + position + ", count = " + count);
                                try {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(position);
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onMoved(final int fromPosition, final int toPosition) {
                                notifyItemMoved(fromPosition, toPosition);
                                LogUtils.loge("updateData-----onMoved-->>fromPosition = " + fromPosition + ", toPosition = " + toPosition);
                                try {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(toPosition);
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onChanged(final int position, final int count, final Object payload) {
                                notifyItemRangeChanged(position, count, payload);
                                LogUtils.loge("updateData-----onChanged-->>position = " + position + ", count = " + count);
                                try {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(position);
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        mData.clear();
                        mData.addAll(data);
                    }
                });

            }
        });
    }

    public List<HomeTopModel> getData() {
        return mData;
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mData.clear();
    }

    public interface OnTopClickListener {
        void onStatusChange(int status);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_home_rc_type, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        HomeTopModel homeTopModel = mData.get(position);
        int type = homeTopModel.type;
        freshType(holder, type, position);
        int value = homeTopModel.value;
        freshValue(holder, value);
        setListener(holder, position);
    }

    private void freshValue(MyViewHolder holder, int value) {
        holder.mainRcTypeTvCount.setText(String.valueOf(value));
    }

    private void freshType(MyViewHolder holder, int type, int position) {
        int currentColor = R.color.c_29c093;
        switch (type) {
            case 0:
                currentColor = R.color.c_f34a4a;
                holder.mainRcTypeTvStateTxt.setText("预警监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_warning);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_alarm);
                break;
            case 1:
                currentColor = R.color.c_29c093;
                holder.mainRcTypeTvStateTxt.setText("正常监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_normal);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_normal);
                break;
            case 2:
                currentColor = R.color.c_5d5d5d;
                holder.mainRcTypeTvStateTxt.setText("失联监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_lose);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_lost);
                break;
            case 3:
                currentColor = R.color.c_b6b6b6;
                holder.mainRcTypeTvStateTxt.setText("未激活监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_inactivated);
                holder.ivHomeStatusCard.setImageResource(R.drawable.home_status_inactivated);
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
        } else {
            if (position == mData.size() - 1) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeStatusRoot.getLayoutParams());
                int pxR = AppUtils.dp2px(mContext, 14);
                lp.setMargins(0, 0, pxR, 0);
                holder.homeStatusRoot.setLayoutParams(lp);
            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            HashMap map = (HashMap) payloads.get(0);
            Integer type = (Integer) map.get("type");
            if (type != null) {
                freshType(holder, type, position);
            }
            Integer value = (Integer) map.get("value");
            if (value != null) {
                freshValue(holder, value);
            }
            setListener(holder, position);

        }
    }

    private void setListener(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTopClickListener != null) {
                    int type = mData.get(position).type;
                    onTopClickListener.onStatusChange(type);
                }
            }
        });
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
