package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.model.DeployContactModel;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmContactRcContentAdapter extends RecyclerView.Adapter<AlarmContactRcContentAdapter.AlarmContactRcContentHolder> {

    private final Context mContext;
    public final List<DeployContactModel> mList = new ArrayList<>();
    public final List<Integer> mRepeatList = new ArrayList<>();

    private OnAlarmContactAdapterListener listener;

    public int mFocusPos = -1;//焦点位置


    public AlarmContactRcContentAdapter(Context context) {
        mContext = context;
    }


    public void setOnAlarmContactAdapterListener(OnAlarmContactAdapterListener onAlarmContactAdapterListener) {
        listener = onAlarmContactAdapterListener;
    }

    /**
     * 监听焦点
     */
    public interface OnAlarmContactAdapterListener {
        void onPhoneFocusChange(boolean hasFocus);

        void onNameFocusChange(boolean hasFocus);
    }

    @Override
    public AlarmContactRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_add_alarm_cantact, parent, false);

        return new AlarmContactRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmContactRcContentHolder itemHolder, final int position) {


        if (mList.size() == 1 && position == 0) {
            itemHolder.itemAdapterAlarmLlContactDelete.setVisibility(View.GONE);
        } else {
            itemHolder.itemAdapterAlarmLlContactDelete.setVisibility(View.VISIBLE);


        }
        itemHolder.itemAdapterAlarmLlContactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, mList.size() - position);

                notifyDataSetChanged();
            }
        });

        if (itemHolder.itemAdapterEtAlarmContactPhone.getTag() instanceof TextWatcher) {

            itemHolder.itemAdapterEtAlarmContactPhone.removeTextChangedListener((TextWatcher) itemHolder.itemAdapterEtAlarmContactPhone.getTag());
        }
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())) {
                    mList.get(position).phone = editable.toString();
                } else {
                    mList.get(position).phone = "";
                }


            }
        };

        itemHolder.itemAdapterEtAlarmContactPhone.addTextChangedListener(watcher);
        itemHolder.itemAdapterEtAlarmContactPhone.setTag(watcher);


        if (itemHolder.itemAdapterEtAlarmContactName.getTag() instanceof TextWatcher) {

            itemHolder.itemAdapterEtAlarmContactName.removeTextChangedListener((TextWatcher) itemHolder.itemAdapterEtAlarmContactName.getTag());
        }
        final TextWatcher watcherContactName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(editable.toString())) {
                    mList.get(position).name = editable.toString();
                } else {
                    mList.get(position).name = "";
                }

            }
        };


        itemHolder.itemAdapterEtAlarmContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (null != listener) {
                    listener.onNameFocusChange(hasFocus);
                }
                if (hasFocus) {
                    mFocusPos = position;
                    mList.get(position).clickType = 1;
                }
            }
        });


        itemHolder.itemAdapterEtAlarmContactPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != listener) {

                    listener.onPhoneFocusChange(hasFocus);
                }
                if (hasFocus) {
                    mFocusPos = position;
                    mList.get(position).clickType = 2;
                }


            }
        });

        itemHolder.itemAdapterEtAlarmContactPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                itemHolder.itemAdapterEtAlarmContactPhone.setFocusable(true);
                //有焦点且软键盘弹出
                if (mRepeatList.contains(position)) {
                    itemHolder.itemAdapterEtAlarmContactPhone.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                    mRepeatList.remove(mRepeatList.indexOf(position));
                }
                return false;
            }
        });


        if (mRepeatList.contains(position)) {
            itemHolder.itemAdapterEtAlarmContactPhone.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));

        } else {
            itemHolder.itemAdapterEtAlarmContactPhone.setTextColor(mContext.getResources().getColor(R.color.c_252525));

        }


        itemHolder.itemAdapterEtAlarmContactName.addTextChangedListener(watcherContactName);
        itemHolder.itemAdapterEtAlarmContactName.setTag(watcherContactName);

        DeployContactModel deployContactModel = mList.get(position);
        itemHolder.itemAdapterEtAlarmContactName.setText(deployContactModel.name == null ? "" : deployContactModel.name);
        itemHolder.itemAdapterEtAlarmContactPhone.setText(deployContactModel.phone == null ? "" : deployContactModel.phone);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    class AlarmContactRcContentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_adapter_et_alarm_contact_name)
        EditText itemAdapterEtAlarmContactName;
        @BindView(R.id.item_adapter_et_alarm_contact_phone)
        EditText itemAdapterEtAlarmContactPhone;
        @BindView(R.id.item_adapter_alarm_ll_contact_delete)
        LinearLayout itemAdapterAlarmLlContactDelete;

        AlarmContactRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


    public void updateAdapter(List<DeployContactModel> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateRepeatAdapter(List<Integer> list) {
        this.mRepeatList.clear();
        this.mRepeatList.addAll(list);

        notifyDataSetChanged();
    }

    public void addNewDataAdapter() {
        if (mList.size() >= 10) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.add_up_to_10_contacts), Toast.LENGTH_SHORT).show();
            return;
        }
        DeployContactModel deployContactModel = new DeployContactModel();
        deployContactModel.name = "";
        deployContactModel.phone = "";
        this.mList.add(deployContactModel);
        notifyDataSetChanged();
//        notifyItemInserted(mList.size()-1);
    }


    private boolean isSoftShowing() {
        if (mContext instanceof Activity) {

            //获取当前屏幕内容的高度
            int screenHeight = ((Activity) mContext).getWindow().getDecorView().getHeight();
            //获取View可见区域的bottom
            Rect rect = new Rect();
            ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            return screenHeight - rect.bottom != 0;
        }
        return false;
    }
}
