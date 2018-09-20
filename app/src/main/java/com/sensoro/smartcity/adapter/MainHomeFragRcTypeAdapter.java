package com.sensoro.smartcity.adapter;

import android.content.Context;
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
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.iwidget.IOnDestroy;
import com.sensoro.smartcity.model.HomeTopModel;
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

    private final Context mContext;
    private final List<HomeTopModel> mData = new ArrayList<>();
    private OnTopClickListener onTopClickListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public MainHomeFragRcTypeAdapter(Context context) {
        mContext = context;
    }

    public void setOnTopClickListener(OnTopClickListener onTopClickListener) {
        this.onTopClickListener = onTopClickListener;
    }

    public void updateData(final RecyclerView recyclerView, final List<HomeTopModel> data) {
        TopListAdapterDiff indexListAdapterDiff = new TopListAdapterDiff(mData, data);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(indexListAdapterDiff, true);
//        diffResult.dispatchUpdatesTo(this);
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
        freshType(holder, type);
        int value = homeTopModel.value;
        freshValue(holder, value);
        if (position == 0) {
//            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
        } else {
//            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTopClickListener != null) {
                    onTopClickListener.onStatusChange(mData.get(position).type);
                }
            }
        });
    }

    private void freshValue(MyViewHolder holder, int value) {
        holder.mainRcTypeTvCount.setText(String.valueOf(value));
    }

    private void freshType(MyViewHolder holder, int type) {
        switch (type) {
            case 0:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_f34a4a));
                holder.mainRcTypeTvStateTxt.setTextColor(Color.WHITE);
                holder.mainRcTypeTvStateTxt.setText("预警");
                holder.mainRcTypeTvCount.setTextColor(Color.WHITE);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_warning);
                holder.mainRcTypeImvState.setColorFilter(mContext.getResources().getColor(R.color.white));
                break;
            case 1:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_29c093));
                holder.mainRcTypeTvStateTxt.setTextColor(Color.WHITE);
                holder.mainRcTypeTvCount.setTextColor(Color.WHITE);
                holder.mainRcTypeTvStateTxt.setText("正常监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_normal);
                holder.mainRcTypeImvState.setColorFilter(mContext.getResources().getColor(R.color.white));
                break;
            case 2:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_5d5d5d));
                holder.mainRcTypeTvStateTxt.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mainRcTypeTvCount.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_lose);
                holder.mainRcTypeImvState.setColorFilter(mContext.getResources().getColor(R.color.white));
                holder.mainRcTypeTvStateTxt.setText("失联");
                break;
            case 3:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_b6b6b6));
                holder.mainRcTypeTvStateTxt.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mainRcTypeTvCount.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mainRcTypeTvStateTxt.setText("未激活");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_inactivated);
                holder.mainRcTypeImvState.setColorFilter(mContext.getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            HashMap map = (HashMap) payloads.get(0);
            Integer type = (Integer) map.get("type");
            if (type != null) {
                freshType(holder, type);
            }
            Integer value = (Integer) map.get("value");
            if (value != null) {
                freshValue(holder, value);
            }
        }
//        super.onBindViewHolder(holder, position, payloads);
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

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
