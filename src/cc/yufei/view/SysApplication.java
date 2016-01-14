package cc.yufei.view;
/**
 * Copyright (c) 2013, �ɶ�������޹�˾ All rights reserved.
 * �ļ����ƣ�SysApplication.java
 * ��Ҫ˵�������ļ�������
 * ��ǰ�汾��V1.0
 * ���ߣ�Du
 * ���ڣ�2013-08-05
 */
import java.util.LinkedList;
import java.util.List;

import cc.yufei.crash.MyCrashHandler;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
/**
 * SysApplicationʵ������Ӧ�õ���Դ�����Ӧ�õ��˳�
 * @author Administrator
 *
 */
public class SysApplication extends Application {
	
	
	public static Uri uri;
	
	/**
	 * ͼ�����ű���
	 */
	public static float scaleWidth;
	
	public static float scaleHeight;
	
	
	/**
	 * ����һ���������ڴ��Activity
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
    	//���Զ�����쳣���������� �����߳� 
		MyCrashHandler myCrashHandler =	MyCrashHandler.getInstance();
		myCrashHandler.init(getApplicationContext());
		Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);
    }

	//���Activity����ǰ�ļ��ϵ���
	public void addActivity(Activity activity) {
	    mList.add(activity);
	}
    //�˳�����Ӧ��,����Activity
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
    
	//��ϵͳ�ڴ�͵�ʱ��ϵͳ������Դ
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
