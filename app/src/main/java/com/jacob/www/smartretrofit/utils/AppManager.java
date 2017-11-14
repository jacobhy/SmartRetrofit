package com.jacob.www.smartretrofit.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * @className: AppManager
 * @classDescription: Activity管理类
 * @author: jacobHy
 * @createTime: 2017/11/9
 */
public class AppManager {
    // 单例
    private static AppManager      instance;
    // Activity堆栈
    private static Stack<Activity> activityStack;

    /**
     * 构造函数
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    private AppManager() {
    }

    /**
     * 单例
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public static AppManager getInstance() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null)
                    instance = new AppManager();
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity 页面实例
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void addActivity(Activity activity) {
        if (activity == null)
            return;
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 从堆栈移除Activity
     *
     * @param activity 页面实例
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void removeActivity(Activity activity) {
        if (activityStack != null && activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack == null)
            return activity;
        try {
            if (activityStack.size() > 0) {
                activity = activityStack.lastElement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishActivity() {
        if (activityStack == null)
            return;
        try {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity 页面实例
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishActivity(Activity activity) {
        if (activityStack == null || activity == null)
            return;
        try {
            if (activity != null) {
                activityStack.remove(activity);
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束所有Activity,除了当前Activity
     *
     * @param activityCls 页面类名
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishAllActivityExceptOne(Class activityCls) {
        if (activityStack == null || activityCls == null)
            return;
        for (int i = 0; i < activityCounts(); i++) {
            Activity activity = activityStack.get(i);
            if (!activity.getClass().equals(activityCls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * activity栈中是否存在该activity
     *
     * @param clsStr 页面类名
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public boolean hasActivity(String clsStr) {
        try {
            Class<?> cls = Class.forName(clsStr);
            return hasActivity(cls);
        }catch (Exception e) {
            return false;
        }
    }

    /**
     * activity栈中是否存在该activity
     *
     * @param activityCls
     * @return
     * @author jacobHy
     * @createTime 2017/6/19
     * @lastModify 2017/6/19
     */
    public boolean hasActivity(Class activityCls) {
        if (activityStack == null || activityCls == null)
            return false;
        for (int i = 0; i < activityCounts(); i++) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().equals(activityCls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关闭直到此activity
     *
     * @param activityCls
     * @return
     * @author jacobHy
     * @createTime 2017/6/19
     * @lastModify 2017/6/19
     */
    public void finishUtilThisActivity(Class activityCls) {
        if (activityStack == null || activityCls == null)
            return;
        for (int i = 0; i < activityCounts(); i++) {
            Activity activity = activityStack.lastElement();
            if (!activity.getClass().equals(activityCls)) {
                finishActivity(activity);
            } else {
                break;
            }
        }
    }

    /**
     * 结束所有Activity,除了当前Activity
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishAllActivityExceptCurrent() {
        Activity currentActivity = currentActivity();
        if (currentActivity != null) {
            finishAllActivityExceptOne(currentActivity.getClass());
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls 页面类名
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null || cls == null)
            return;
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断堆栈中是否存在该页面实例
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2017/6/23
     * @lastModify 2017/6/23
     */
    public boolean hasActivity(Activity activity) {
        if (activityStack == null || activity == null)
            return false;
        for (int i = 0; i < activityCounts(); i++) {
            Activity activitys = activityStack.get(i);
            if (activitys == activity)
                return true;
        }
        return false;
    }

    /**
     * 获取指定类名的Activity
     *
     * @param cls 页面类名
     * @return
     * @author jacobHy
     * @createTime 2017/5/27
     * @lastModify 2017/5/27
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack == null || cls == null)
            return null;
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 结束所有Activity
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void finishAllActivity() {
        if (activityStack == null)
            return;
        try {
            for (int i = 0; i < activityStack.size(); i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿到应用程序当前活跃Activity的个数
     *
     * @param
     * @return counts Activity的个数
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public int activityCounts() {
        int counts = 0;
        if (activityStack != null && activityStack.size() > 0) {
            counts = activityStack.size();
            for (int i = 0; i < counts; i++) {
                Activity activity = activityStack.get(i);
            }
        }
        return counts;
    }

    /**
     * 退出应用程序
     *
     * @param
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public void exit() {
        try {
            if (currentActivity() != null)
                finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某一个activity是否为当前activity
     *
     * @param activity 页面实例
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public boolean isCurrent(Activity activity) {
        if (activity == null || currentActivity() == null)
            return false;
        if (activity == currentActivity())
            return true;
        else
            return false;
    }

    /**
     * 判断某一个activity是否为当前activity
     *
     * @param cls 页面类名
     * @return
     * @author jacobHy
     * @createTime 2016/10/14
     * @lastModify 2016/10/14
     */
    public boolean isCurrent(Class<?> cls) {
        if (cls == null || currentActivity() == null)
            return false;
        if (currentActivity().getClass().equals(cls))
            return true;
        else
            return false;
    }
}
