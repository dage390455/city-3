package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.SensoroTextView;

/**
 * Created by fangping on 2016/7/7.
 */

public class MenuInfoAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private String mMenuInfoArray[] = null;
    private int selectedIndex;

    public MenuInfoAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mMenuInfoArray = context.getResources().getStringArray(R.array.drawer_title_array);
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }
    @Override
    public int getCount() {
        return mMenuInfoArray.length;
    }

    @Override
    public Object getItem(int i) {
        return mMenuInfoArray[i];
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
            convertView.setTag(holder);
        } else {
            holder = (MenuInfoViewHolder) convertView.getTag();
        }

        holder.item_name.setOriginalText(mMenuInfoArray[position]);
        holder.item_name.setText(mMenuInfoArray[position]);
        holder.item_name.setLetterSpacing(3);
        holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT[position]);

        if (selectedIndex == position) {
            holder.item_icon.setColorFilter(mContext.getResources().getColor(R.color.popup_selected_text_color));
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.popup_selected_text_color));
        } else {
            holder.item_icon.setColorFilter(mContext.getResources().getColor(R.color.c_626262));
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        }
        return convertView;
    }


    class MenuInfoViewHolder {

        SensoroTextView item_name;
        ImageView item_icon;

        public MenuInfoViewHolder() {

        }
    }
}