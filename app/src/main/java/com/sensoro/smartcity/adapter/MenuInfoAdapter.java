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

import static com.sensoro.smartcity.presenter.MainPresenter.BUSINESS_ACCOUNT_HAS_STATION;
import static com.sensoro.smartcity.presenter.MainPresenter.BUSINESS_ACCOUNT_NO_STATION;
import static com.sensoro.smartcity.presenter.MainPresenter.NORMAL_ACCOUNT_HAS_STATION;
import static com.sensoro.smartcity.presenter.MainPresenter.NORMAL_ACCOUNT_NO_STATION;
import static com.sensoro.smartcity.presenter.MainPresenter.SUPPER_ACCOUNT;

/**
 * Created by fangping on 2016/7/7.
 */

public class MenuInfoAdapter extends BaseAdapter implements Constants {

    private final String[] titleNormalArrayNoStation;
    private final String[] titleBusinessArrayNoStation;
    private Context mContext;
    private LayoutInflater mInflater;

    private int selectedIndex;
    private int tempAccountType = NORMAL_ACCOUNT_HAS_STATION;
    private String[] currentData;
    private final String[] titleNormalArray;
    private final String[] titleBusinesArray;
    private final String[] titleSupperArray;

    public MenuInfoAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        //
        titleNormalArray = context.getResources().getStringArray(R.array.drawer_title_array);

        titleBusinesArray = context.getResources().getStringArray(R.array.drawer_title_array_nobussise);

        titleNormalArrayNoStation = context.getResources().getStringArray(R.array.drawer_title_array_nostation);
        titleBusinessArrayNoStation = context.getResources().getStringArray(R.array
                .drawer_title_array_nobussise_nostation);
        titleSupperArray = context.getResources().getStringArray(R.array.drawer_title_array_supper);
        currentData = titleNormalArray;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentData.length;
    }

    @Override
    public Object getItem(int i) {
        return currentData[i];
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

        holder.item_name.setOriginalText(currentData[position]);
        holder.item_name.setText(currentData[position]);
        holder.item_name.setLetterSpacing(3);
        holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        if (tempAccountType == BUSINESS_ACCOUNT_HAS_STATION) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_BUSSIES[position]);
        } else if (tempAccountType == SUPPER_ACCOUNT) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_SUPPER);
        } else if (tempAccountType == BUSINESS_ACCOUNT_NO_STATION) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_BUSSIES_NO_STATION[position]);
        } else if (tempAccountType == NORMAL_ACCOUNT_NO_STATION) {
            holder.item_icon.setImageResource(LEFT_MENU_ICON_UNSELECT_NO_STATION[position]);
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
        switch (accountType) {
            case SUPPER_ACCOUNT:
                currentData = titleSupperArray;
                break;
            case NORMAL_ACCOUNT_HAS_STATION:
                currentData = titleNormalArray;
                break;
            case BUSINESS_ACCOUNT_HAS_STATION:
                currentData = titleBusinesArray;
                break;
            case NORMAL_ACCOUNT_NO_STATION:
                currentData = titleNormalArrayNoStation;
                break;
            case BUSINESS_ACCOUNT_NO_STATION:
                currentData = titleBusinessArrayNoStation;
                break;
            default:
                currentData = titleNormalArray;
                break;
        }
        int length = currentData.length;
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