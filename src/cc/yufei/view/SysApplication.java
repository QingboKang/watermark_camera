package cc.yufei.view;
/**
 * Copyright (c) 2013, 成都宇飞有限公司 All rights reserved.
 * 文件名称：SysApplication.java
 * 简要说明：本文件的作用
 * 当前版本：V1.0
 * 作者：Du
 * 日期：2013-08-05
 */
import java.util.LinkedList;
import java.util.List;

import cc.yufei.crash.MyCrashHandler;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
/**
 * SysApplication实现整个应用的资源共享和应用的退出
 * @author Administrator
 *
 */
public class SysApplication extends Application {
	
	
	public static Uri uri;
	
	/**
	 * 图像缩放比例
	 */
	public static float scaleWidth;
	
	public static float scaleHeight;
	
	
	/**
	 * 定义一个集合用于存放Activity
	 */
	private List<Activity> mList = new LinkedList<Activity>();
	
	
  /*  private static SysApplication instance;

	private SysApplication() {
	}

	public synchronized static SysApplication getInstance() {
	if (null == instance) {
	instance = new SysApplication();
	}
	return instance;
	}*/
    public SysApplication(){};
    
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	super.onCreate();
    	//把自定义的异常处理类设置 给主线程 
		MyCrashHandler myCrashHandler =	MyCrashHandler.getInstance();
		myCrashHandler.init(getApplicationContext());
		Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);
    }

	//添加Activity到当前的集合当中
	public void addActivity(Activity activity) {
	    mList.add(activity);
	}
    //退出整个应用,销毁Activity
	public void exit() {
		try {
		for (Activity activity : mList) {
			if (activity != null)
			 activity.finish();
		}
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
		System.exit(0);
		}
	}
    
	//当系统内存低的时候系统回收资源
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
