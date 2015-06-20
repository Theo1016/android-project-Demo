package com.theo.sdk.manager;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;

/**
 * Activity任务堆栈
 * @author Theo
 *
 */
public class ActivityTaskManager {
	private static ActivityTaskManager instance;
	/** Activity集合 */
	private static List<Activity> mList = new LinkedList<Activity>();

	/**
	 * 获取单实例 对象
	 * 
	 * @return ActivityTaskManager
	 */
	public static synchronized ActivityTaskManager getInstance() {
		if (null == instance) {
			instance = new ActivityTaskManager();
		}
		return instance;
	}
	
	/**
	 * 添加Activity
	 * 
	 * @param activity
	 *            待添加的Activity
	 */
	public static final void addActivity2Task(Activity mActivity) {
		if (!mList.contains(mActivity))
			mList.add(mActivity);
	}

	/**
	 * 移除Activity
	 * 
	 * @param activity
	 *            待移除的Activity
	 */
	public static final void destoryActivity4Task(Activity mActivity) {
		if (mList.contains(mActivity))
			mList.remove(mActivity);
	}

	/**
	 * 移除指定Activity以外的其他Activity
	 * 
	 * @param mActivity
	 *            指定的Activity
	 */
	public static final void finishOtherActity(Activity mActivity) {
		for (Activity activity : mList) {
			if (activity != null && !(mActivity.getClass().getName().equals(activity.getClass().getName())))
				activity.finish();
		}
	}

	/**
	 * 结束Activity队列中所有Activity
	 * 
	 * @introduce 一键退出多个Activity或者类似返回主界面功能是可使用
	 */
	public static final void removeAllActivity4Task() {
		for (Activity activity : mList) {
			if (activity != null) {
				activity.finish();
			}
		}
		mList.clear();
	}

}
