package cc.yufei.view;
/**
 * Copyright (c) 2013, 成都宇飞有限公司 All rights reserved.
 * 文件名称：MainActivity.java
 * 简要说明：本文件的作用
 * 当前版本：V1.0
 * 作者：Du
 * 日期：2013-08-05
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.yufei.util.ImageUtil;
import cc.yufei.util.LogUtil;
import cc.yufei.util.DirectoryConfig;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 该应用的总界面
 * @author Administrator
 *
 */
public class MainActivity extends Activity implements OnClickListener {

    private int imageIds[];
	private String[] titles;
	private ArrayList<ImageView> images;
	private ArrayList<View> dots;
	private TextView title;
	
	private int oldPosition = 0;//记录上一次点的位置
	private int currentItem; //当前页面
	/**
	 * 选择图库和选择相机的按钮
	 */
    private Button btn_selector_picture,btn_selector_camera;
    /**
     * 图片浏览的布局控件
     */

	private Bitmap bitmap,bmp;
	
	private String mode;
	
	private    Intent intent;
	

	private static final int REQUEST_EX = 1;
	private static final int CARRIER_CAMERA = 2;
	private static final int CARRIER_FILE = 3;
	
	private static String LOG_TAG = "samsung_info";
	
	private File tempFile;
	
	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'YufeiCarrier'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	/**
	 * 获取当前选中图片的路径
	 * @param uri
	 */
	public String getPicPath(Uri uri) {
		//获取图片的路径
		  String[] proj = {MediaStore.Images.Media.DATA};
		//好像是android多媒体数据库的封装接口，具体的看Android文档
		 Cursor cursor = managedQuery(uri, proj, null, null, null); 
		 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		//将光标移至开头 ，这个很重要，不小心很容易引起越界
		 cursor.moveToFirst();
		//最后根据索引值获取图片路径
		 String picPath = cursor.getString(column_index);
		 return picPath;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.yl_main);
        mode=getIntent().getExtras().getString("mode");
        initView();      
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		btn_selector_picture=(Button)findViewById(R.id.btn_selector_picture);
		btn_selector_camera=(Button)findViewById(R.id.btn_selector_Camera);
		btn_selector_picture.setOnClickListener(this);
		btn_selector_camera.setOnClickListener(this);
		//如果是提取模式
		if("extract".equals(mode))
		{
			btn_selector_camera.setVisibility(View.GONE);
			btn_selector_picture.setText(getString(R.string.extract_pic));
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_selector_picture:

			// 提取模式
			if("extract".equals(mode))
			{
			   selectWay(Environment.getExternalStorageDirectory()+ "/YFWatermark/image" );
			   return;
			}
			// 隐藏模式
			//从图库中获取图片
			Intent picIntent=new Intent(Intent.ACTION_GET_CONTENT);
		//	Intent picIntent=new Intent(Intent.ACTION_PICK);
			picIntent.addCategory(Intent.CATEGORY_OPENABLE);
			picIntent.setType("image/*");
			picIntent.putExtra("return-data", true);
		    startActivityForResult(picIntent, CARRIER_FILE);
		   
			break;
        case R.id.btn_selector_Camera:
        	//从相机中获取图片
        	Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   

        	tempFile = new File( DirectoryConfig.CaptureFilesDir, getPhotoFileName());
        	camIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
        	Log.i(LOG_TAG, "absoluted path:" + tempFile.getAbsolutePath());
        	
            startActivityForResult(camIntent, CARRIER_CAMERA);  
			break;
		}
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	//进入文件夹选择图片
	private void selectWay(String strStartDir) {
		Intent intent = new Intent();
		intent.putExtra("explorer_title",
				getString(R.string.dialog_read_from_dir));
		//intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"")), "*/*");
		intent.setDataAndType(Uri.fromFile(new File( strStartDir )), "*/*");
		intent.setClass(MainActivity.this, ExDialog.class);
		startActivityForResult(intent, REQUEST_EX);
	}
	
	/*
	 * 华为P6测试，在用相机拍摄的时候，返回的data为null 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		 super.onActivityResult(requestCode, resultCode, data);

	     Log.i(LOG_TAG, "resultCode: " + resultCode );
	     Log.i(LOG_TAG, "requestCode: " + requestCode );

		 if (RESULT_OK == resultCode )
		 {
			 // 隐藏模式
			 if( requestCode == CARRIER_CAMERA || requestCode == CARRIER_FILE )
			 {
				 // 载体图像绝对路径
				 String carrierImgPath = null;
				 
				 // 隐藏模式 -- 拍照
				 if(requestCode == CARRIER_CAMERA)
				 {
					 if(tempFile.exists())
					 {
						 Log.i(LOG_TAG, "image file existed.");
						 carrierImgPath = tempFile.getAbsolutePath();
					 }
					 else
					 {
						 Log.i(LOG_TAG, "image file does not existed.");
						 return;
					 }
				 }
			 
				 // 隐藏模式 -- 选取图像文件
				 // Intent存放的是图像文件的URI
				 else if(requestCode == CARRIER_FILE)
				 {
					 Log.i( LOG_TAG, "embed - choose file");
					 Log.i(LOG_TAG, "requestCode: " + requestCode);
					 Log.i(LOG_TAG, "data: " + data);
				
					 if(data != null && data.getData() != null)
					 {
						 carrierImgPath = getPicPath(data.getData());
						 Log.i( LOG_TAG ,"pick file path: " + carrierImgPath);
					 }
				 }
				 
				 intent=new Intent(this,ShowPicActivity.class);
				 intent.putExtra("carrierImgPath", carrierImgPath);
				 startActivity(intent);
				 Log.i(LOG_TAG, "" + tempFile.getAbsolutePath());
				 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			 }
			 // 提取模式
			 else if (requestCode == REQUEST_EX && "extract".equals(mode))	
			 {
				 Log.i(LOG_TAG, "extract");
				 
				 String extractImgPath = data.getExtras().getString("path");  
			     intent=new Intent( this, ExtractPicActivity.class);
				 intent.putExtra("extractpath", extractImgPath);
				 Log.i(LOG_TAG, "path: " + extractImgPath);
			     startActivity(intent);
			 }
		/*	 intent=new Intent(this,ShowPicActivity.class);
			 intent.putExtra("carrierImgPath", carrierImgPath);
			 startActivity(intent);
			 Log.i(LOG_TAG, "" + tempFile.getAbsolutePath());
			 overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); */
	     } // end if 
	} // end onActivityResult
}
