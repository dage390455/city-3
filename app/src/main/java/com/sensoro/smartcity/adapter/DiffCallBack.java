package com.sensoro.smartcity.adapter;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class DiffCallBack<T> extends DiffUtil.Callback {
    List<T> mOldList, mNewList;//看名字

    DiffCallBack(List<T> oldList, List<T> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    //老数据集size
    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    //新数据集size
    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     * 被DiffUtil调用，用来判断 两个对象是否是相同的Item。
     * For example, if your items have unique ids, this method should check their id equality.
     * 例如，如果你的Item有唯一的id字段，这个方法就 判断id是否相等。
     * 本例判断name字段是否一致
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//        return mOldList.get(oldItemPosition).getName().equals(mNewList.get(newItemPosition).getName());
        //用来判断 两个对象是否是相同的Item。
        return getItemsTheSame(oldItemPosition, newItemPosition);
    }

    abstract boolean getItemsTheSame(int oldItemPosition, int newItemPosition);

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * DiffUtil用返回的信息（true false）来检测当前item的内容是否发生了变化
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * DiffUtil 用这个方法替代equals方法去检查是否相等。
     * so that you can change its behavior depending on your UI.
     * 所以你可以根据你的UI去改变它的返回值
     * For example, if you are using DiffUtil with a
     * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * 例如，如果你用RecyclerView.Adapter 配合DiffUtil使用，你需要返回Item的视觉表现是否相同。
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldData = mOldList.get(oldItemPosition);
        T newData = mNewList.get(newItemPosition);
//        if (!beanOld.getDesc().equals(beanNew.getDesc())) {
//            return false;//如果有内容不同，就返回false
//        }
//        if (beanOld.getPic() != beanNew.getPic()) {
//            return false;//如果有内容不同，就返回false
//        }
        //默认两个data内容是相同的
        return getContentsTheSame(oldData, newData);
    }

    abstract boolean getContentsTheSame(T oldData, T newData);

    /**
     * When {@link #areItemsTheSame(int, int)} returns {@code true} for two items and
     * {@link #areContentsTheSame(int, int)} returns false for them, DiffUtil
     * calls this method to get a payload about the change.
     * <p>
     * 当{@link #areItemsTheSame(int, int)} 返回true，且{@link #areContentsTheSame(int, int)} 返回false时，DiffUtils会回调此方法，
     * 去得到这个Item（有哪些）改变的payload。
     * <p>
     * For example, if you are using DiffUtil with {@link RecyclerView}, you can return the
     * particular field that changed in the item and your
     * {@link android.support.v7.widget.RecyclerView.ItemAnimator ItemAnimator} can use that
     * information to run the correct animation.
     * <p>
     * 例如，如果你用RecyclerView配合DiffUtils，你可以返回  这个Item改变的那些字段，
     * {@link android.support.v7.widget.RecyclerView.ItemAnimator ItemAnimator} 可以用那些信息去执行正确的动画
     * <p>
     * Default implementation returns {@code null}.\
     * 默认的实现是返回null
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return A payload object that represents the change between the two items.
     * 返回 一个 代表着新老item的改变内容的 payload对象，
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //实现这个方法 就能成为文艺青年中的文艺青年
        // 定向刷新中的部分更新
        // 效率最高
        //只是没有了ItemChange的白光一闪动画，（反正我也觉得不太重要）
        T oldData = mOldList.get(oldItemPosition);
        T newData = mNewList.get(newItemPosition);

        //这里就不用比较核心字段了,一定相等
//        Bundle payload = new Bundle();
//        if (!oldBean.getDesc().equals(newBean.getDesc())) {
//            payload.putString("KEY_DESC", newBean.getDesc());
//        }
//        if (oldBean.getPic() != newBean.getPic()) {
//            payload.putInt("KEY_PIC", newBean.getPic());
//        }

//        if (payload.size() == 0)//如果没有变化 就传空
//            return null;
//        return payload;//
        return getChangePayload(oldData, newData);
    }

    abstract Object getChangePayload(T oldData, T newData);
//    @Override example
//    public void onBindViewHolder(DiffVH holder, int position, List<Object> payloads) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder, position);
//        } else {
//            //文艺青年中的文青
//            Bundle payload = (Bundle) payloads.get(0);//取出我们在getChangePayload（）方法返回的bundle
//            TestBean bean = mDatas.get(position);//取出新数据源，（可以不用）
//            for (String key : payload.keySet()) {
//                switch (key) {
//                    case "KEY_DESC":
//                        //这里可以用payload里的数据，不过data也是新的 也可以用
//                        holder.tv2.setText(bean.getDesc());
//                        break;
//                    case "KEY_PIC":
//                        holder.iv.setImageResource(payload.getInt(key));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    }
}
