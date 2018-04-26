package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.SensoroTextView;

import java.util.ArrayList;
import java.util.List;

import static com.sensoro.smartcity.activity.MainActivity.BUSSISE_ACCOUNT;
import static com.sensoro.smartcity.activity.MainActivity.NORMOL_ACCOUNT;
import static com.sensoro.smartcity.activity.MainActivity.SUPPER_ACCOUNT;

/**
 * Created by fangping on 2016/7/7.
 */

public class MenuInfoAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;

    private int selectedIndex;
    private int tempAccountType = NORMOL_ACCOUNT;
    private List<String> mData;

    //    private int count;
    public MenuInfoAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        String mMenuInfoArray[] = context.getResources().getStringArray(R.array.drawer_title_array);
        for (String str : mMenuInfoArray) {
            mData.add(str);
        }
    }

    public void setDataList(List<String> data) {
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MenuInfoViewHolder holder = null;
        if (convertView == null) {
            holder = new MenuInfoViewHolder();
            convertView = mInflater.inflate(R.layout.item_menu, null);
            holder.item_name = (SensoroTextView) convertView.findViewById(R.id.item_menu_name);
            holder.item_icon = (ImageView) convertView.findViewById(R.id.item_menu_icon);
            holder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            convertView.setTag(holder);
        } else {
            holder = (MenuInfoViewHolder) convertView.getTag();
        }

        holder.item_name.setOriginalText(mData.get(position));
        holder.item_name.setText(mData.get(position));
        holder.item_name.setLetterSpacing(3);
        holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        if (tempAccountType == BUSSISE_ACCOUNT) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_BUSSIES[position]);
        } else if (tempAccountType == SUPPER_ACCOUNT) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_SUPPER);
        } else {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT[position]);
        }


        if (selectedIndex == position) {
            holder.item_icon.setColorFilter(mContext.getResources().getColor(R.color.popup_selected_text_color));
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.popup_selected_text_color));
        } else {
            holder.item_icon.setColorFilter(mContext.getResources().getColor(R.color.c_626262));
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        }
        return convertView;
    }

    /**
     * 切换账户
     *
     * @param accountType
     */
    public void showAccountSwitch(int accountType) {
        this.tempAccountType = accountType;

        notifyDataSetChanged();
    }

    class MenuInfoViewHolder {

        SensoroTextView item_name;
        ImageView item_icon;
        RelativeLayout rlItem;

        public MenuInfoViewHolder() {

        }
    }
}