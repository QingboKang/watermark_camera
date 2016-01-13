package cc.yufei.view;
/**
 * Copyright (c) 2013, 成都宇飞有限公司 All rights reserved.
 * 文件名称：SplashActivity.java
 * 简要说明：本文件的作用
 * 当前版本：V1.0
 * 作者：Du
 * 日期：2013-08-05
 */

import java.io.File;
import cc.yufei.util.DirectoryConfig;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 完成Splash欢迎界面
 *
 */
public class SplashActivity extends Activity {
	/**
	 * 版本号
	 */
	private TextView tv_version;
	private LinearLayout mLinearLayout01;
	
	   @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yl_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mLinearLayout01 = (LinearLayout) findViewById(R.id.LinearLayout01);

		String able= getResources().getConfiguration().locale.getCountry(); 
		Log.i("samsung_info", able);

		// 创建YFWatermark目录
		createFileDir();

		//进入动画
		AlphaAnimation animation=new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(2000);
		mLinearLayout01.setAnimation(animation);
		mLinearLayout01.startAnimation(animation); 
		//延迟两秒进入主界面
		new Handler().postDelayed(new LoadMain(), 1000);
	}
	   /**
		 * 该类延迟2秒后进入主界面
		 * @author Duxiaoqiang
		 *
		 */
		private class LoadMain implements Runnable
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//进入主界面
				Intent intent=new Intent(SplashActivity.this,Viewdoor.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
			
		}  

		private void createFileDir()
		{
			  File file=new File( DirectoryConfig.GenerateImageDir );
		 	  File file2=new File( DirectoryConfig.TempFilesDir );
		 	  File file3 = new File( DirectoryConfig.AudioFilesDir );
		 	  File fileFilter = new File( DirectoryConfig.FilterFilesDir);
		 	  File fileCapture = new File( DirectoryConfig.CaptureFilesDir );
	    	  if(!file.exists())
	    	  {
	    		  file.mkdirs();
	    	  }
	    	  if(!file2.exists())
	    	  {
	    		  file2.mkdirs();
	    	  }
	    	  if(!file3.exists())
	    	  {
	    		  file3.mkdirs();
	    	  }
	    	  if(!fileCapture.exists())
	    	  {
	    		  fileCapture.mkdir();
	    	  }
	    	  if(!fileFilter.exists())
	    	  {
	    		  fileFilter.mkdir();
	    	  }
		}

	/**
	 * 获取当前应用的版本号
	 */
	private String getAppVersion() {
		// TODO Auto-generated method stub
		try {
			PackageInfo info=getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			return info.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
