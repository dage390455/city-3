package com.sensoro.common.manger;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.Stack;


public final class ActivityTaskManager {
    private final Stack<Activity> activityStack = new Stack<>();

    private ActivityTaskManager() {
    }

    private final static class ActivityTaskManagerHolder {
        private final static ActivityTaskManager instance = new ActivityTaskManager();
    }

    public static ActivityTaskManager getInstance() {
        return ActivityTaskManagerHolder.instance;
    }

    /**
     * 出栈
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            if (activityStack.remove(activity)) {
                try {
                    Log.i("ActivityTaskManager", activity.getClass().getSimpleName() + "-->>出栈");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 入栈
     */
    public void pushActivity(Activity activity) {
        if (activity != null) {
            activityStack.push(activity);
            try {
                Log.i("ActivityTaskManager", activity.getClass().getSimpleName() + "-->>入栈");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否是最后一个
     *
     * @return
     */
    public boolean isEnd() {
        return activityStack.size() == 1;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return activityStack.isEmpty();
    }

    /**
     * 返回栈顶Activity
     *
     * @return
     */
    public Activity getTopActivity() {
        return activityStack.peek();
    }

    /**
     * 判断指定Activity是否存在
     *
     * @param cls
     * @return
     */
    public boolean contains(Class<? extends Activity> cls) {
        return activityStack.search(cls) > 0;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityMgr != null) {
                activityMgr.restartPackage(context.getPackageName());
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
