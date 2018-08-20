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
import com.sensoro.smartcity.model.MenuPageInfo;
import com.sensoro.smartcity.widget.SensoroTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangping on 2016/7/7.
 */

public class MenuInfoAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;

    private int selectedIndex;
    //
    private final List<MenuPageInfo> menuPageInfoList = new ArrayList<>();

    public MenuInfoAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuPageInfoList.size();
    }

    @Override
    public MenuPageInfo getItem(int i) {
        return menuPageInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return menuPageInfoList.get(i).menuPageId;
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

//        holder.item_name.setOriginalText(currentData[position]);
        holder.item_name.setOriginalText(mContext.getResources().getString(menuPageInfoList.get(position).pageTitleId));
//        holder.item_name.setText(currentData[position]);
        holder.item_name.setText(menuPageInfoList.get(position).pageTitleId);
        holder.item_name.setLetterSpacing(3);
        holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        holder.item_icon.setImageResource(menuPageInfoList.get(position).menuIconResId);

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
     * @param menuPageInfos
     */
    public void updateMenuPager(List<MenuPageInfo> menuPageInfos) {
        this.menuPageInfoList.clear();
        this.menuPageInfoList.addAll(menuPageInfos);
        notifyDataSetChanged();
    }

    static class MenuInfoViewHolder {

        SensoroTextView item_name;
        ImageView item_icon;
        RelativeLayout rlItem;

        MenuInfoViewHolder() {

        }
    }
}