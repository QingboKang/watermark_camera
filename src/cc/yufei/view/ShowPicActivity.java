package cc.yufei.view;

/**
 * Copyright (c) 2013, 成都宇飞有限公司 All rights reserved.
 * 文件名称：MainActivity.java
 * 简要说明：用于显示图片并操作图片
 * 当前版本：V1.0
 * 作者：Du
 * 日期：2013-08-05
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.yufei.watermark.BaseLib;
import cc.yufei.bean.Point;
import cc.yufei.util.DirectoryConfig;
import cc.yufei.util.SampleSizeUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 显示图片
 * 
 */
public class ShowPicActivity extends Activity implements OnClickListener{
	
    private ImageView  iv_showpic;
    private Bitmap bitmap,bmp;
    private LayoutInflater inflater;
    private View view;
    /**
     * 存放当前触摸的X,Y坐标
     */
    private float x,y;
    
    private float imageX,imageY;
    
    private PopupWindow  mPopupWindow;
    
    private boolean flag=true;
    /**
     * 获取操作图片的四个按钮
     */
    private Button bt_insert_text,bt_insert_audio,bt_insert_video,bt_insert_image;
    
    private Animation animation,animation2;
    
    /**
     * 图片的存放路径
     */
    private String picPath;
    
    /**
     * 屏幕的宽高  320*480     720*1280
     */
    private int dw,dh;
    /**
     * 创建一个矩阵进行旋转
     */
    private Matrix matrix;
    /**
     * 记录合适的simpleSize的值
     */
    private int size;
    /**
     * 文件选择模式
     */
    private static final int FILE_SELECT_CODE = 0;  
    /**
     * 音频选择模式
     */
    private static final int AUDIO_SELECT_CODE=1; 
    /**
     * 视频选择模式
     */
    private static final int VIDEO_SELECT_CODE=2;
    /**
     * 图片选择模式
     */
    private static final int PIC_SELECT_CODE=3;
    /**
     * 打开录音结果的Activity
     */
	private static final int AUDIO_REQUEST = 20;
	private static final int CASE_CAMERA = 30;
	private static final int IMAGE_CAMERA = 40;
    private Intent intent;

    /**
     * dialog对话框的布局控件
     */
    private TextView tv_select_file,tv_select_oper;
    
   private   Dialog dialog;
   
   /**
    * 定义一个标志符来判断选择嵌入哪种方式
    */
   private  int  embedway=0;
   
   private String wayName;//选择方式
   private int requestCode;//请求码
   
   private Uri uri;//选取文件返回的url 
   
   /**
    * 定义一个矩阵的左上顶点和右下角顶点
    */
   private float startPointX,startPointY;
   private float endPointX,endPointY;
   private  List<Point> pointLists;
   private ImageView iv;
   
   /*
    * 当前界面显示的图片路径
    */
   private String curImage;
   /**
    * 保存当前存放的点和图片的信息
    */
   private String fileInfo;
   /**
    * 保存当前嵌入文件的路径
    */
   private String curFilePath;
   /**
    * 系统当前语言
    */
   String currentLanuage;
   /**
    * 定义一个集合存放当前嵌入的图片
    */
   List<String>  picPathLists;
   /**
    * 保存嵌入的文件目录
    */
  private    String strFilesDir ;
  private AbsoluteLayout abslayout;
  
  /**保存的文件名字 
   * */
  public static String strFilename;
  
  /**
   * 存放点和图片的集合
   */
  private List<String> fileInfoLists;
  
	//音频格式
  private String[] audioFormatter={".amr",".mp3",".wav",".ac3",".flac",".eaac+"};
	//图片格式
   private String[]  imageFormatter={".jpg",".png",".bmp",".gif"};
	//视频格式
   private String[]  videoFormatter={".mp4",".wmv",".divx",".3gp",".xvid"};
   
   boolean type=false;
   
	private static final int REQUEST_EX = 1;
	private int wayType=-1;
	
	private String LOG_TAG = "samsung_info";
	
	// 拍照图像文件（被嵌入的文件）
	private File imageFile;
	private String captureMarkImagePath;
	
	// 当前(ImageView)在屏幕上的区域(left, right, top, bottom)
	private int ParentHeight = 0;
	private int ParentWidth = 0;
	
	// 实际载体图像(经过处理之后)的高宽
	private int ImageWidth = 0;
	private int ImageHeight = 0;
	
	// 载体图像的父View的高宽比
	private float ParentHWRatio = 0;
	// 载体图像的高宽比
	private float ImageHWRatio = 0;
	// 载体图像在屏幕中的坐标范围(此处不能用Point，已被cc.yufei.util 覆盖)
	private int xStart = 0;
    private int yStart = 0;
	private int xEnd = 0;
	private int yEnd = 0;
	
	// 点之间最小的距离
	private static double POINT_MIN_DISTANCE; 
	
	// 已经隐藏的文件数量
	private int file_count = 0;
	// 隐藏的文件数量上限
	private static final int MAX_FILE_NUM = 4;
	
	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'YufeiMark'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.yl_show_pic);
		iv_showpic=(ImageView)findViewById(R.id.iv_show_pic);
		inflater=getLayoutInflater().from(this);
	    currentLanuage=Locale.getDefault().getLanguage();
		if("en".equals(currentLanuage))
		{
			  view=inflater.inflate(R.layout.yl_show_popupwindow_en, null);
		}else
		{
		        view=inflater.inflate(R.layout.yl_show_popupwindow, null);
		}
		//初始化按钮
		initView();
		setImage();
		//判断是否是jpg图片
		if(BaseLib.CheckFileType(curImage)==1)//不满足文件格式
		{
			showPrompt(R.string.file_no_support);	
		}
		
		if(BaseLib.CheckMarkedness(curImage)==1)
		{
			//已经嵌入水印
			showPrompt(R.string.file_markness);
		}
		// 提示用户触屏进行操作
		else
		{
			Toast toast = Toast.makeText(getApplicationContext(),
				     getString(R.string.toast_text), Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
		}
		
		//创建文件目录
	//	createFileDir();
		//开始动画
	    animation=AnimationUtils.loadAnimation(this, R.anim.scale_anim);
	}
   
	/**
	 * 提示用户选择的图片格式
	 */
	private void showPrompt(int messageId) {
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle(getString(R.string.picformat_title));
		builder.setIcon(R.drawable.alert);
		builder.setMessage(getString(messageId));
		builder.setPositiveButton(getString(R.string.reset_select_img), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				intent=new Intent(ShowPicActivity.this,MainActivity.class);
				intent.putExtra("mode", "embed");
				startActivity(intent);
				ShowPicActivity.this.finish();
			}
		});
		builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			      ShowPicActivity.this.finish();
				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 初始化按钮
	 */
	private void initView() {
		// TODO Auto-generated method stub
		bt_insert_text=(Button)view.findViewById(R.id.bt_insert_text);
		bt_insert_audio=(Button)view.findViewById(R.id.bt_insert_audio);
		bt_insert_video=(Button)view.findViewById(R.id.bt_insert_video);
		bt_insert_image=(Button)view.findViewById(R.id.bt_insert_image);
		abslayout=(AbsoluteLayout)findViewById(R.id.abl);
		//设置监听
		bt_insert_text.setOnClickListener(this); //监听插入文本
		bt_insert_audio.setOnClickListener(this);//监听插入音频
		bt_insert_video.setOnClickListener(this);//监听插入视频
		bt_insert_image.setOnClickListener(this);//监听插入图片
		picPathLists=new  ArrayList<String>();
		fileInfoLists=new ArrayList<String>();
		pointLists=new ArrayList<Point>();
        
       //给图片设置触摸事件
       iv_showpic.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			// 在这里取得当前载体图像的Parent View所占屏幕的数据
			if( (ParentHeight == 0) || 
				(ParentWidth == 0) )
			{
				ParentHeight = iv_showpic.getHeight();
				ParentWidth = iv_showpic.getWidth();
			   
			    if(ParentWidth != 0)
			    {
			    	ParentHWRatio = (float)ParentHeight/(float)ParentWidth;
			    }
			    
			    // 点之间的距离设定为屏幕宽度的 1/10;
			    POINT_MIN_DISTANCE = ParentWidth * 0.1;
			}
		       
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(!curImage.endsWith(".jpg"))
				{
					break;
				}
				// 嵌入文件数量是否已达到阈值 
				if(file_count >= MAX_FILE_NUM)
				{
					Toast toast = Toast.makeText(getApplicationContext(),
						     getString(R.string.toast_toomanyfiles), Toast.LENGTH_SHORT);
						   toast.setGravity(Gravity.CENTER, 0, 0);
						   toast.show();
				    break;
				}
		        
				x=(int) event.getX();
			    y=(int)event.getY();
			    // 判断当前的点击点是否在屏幕上的载体图像ImageView之外 &&
			    int newXY[] = correctionPoint((int)x, (int)y);
			    if(newXY == null)
			    {
			    	break;
			    }
			    
				//获取60*60矩阵的点
				startPointX=x-30;
				startPointY=y-30;
				endPointX=x+30;
				endPointY=y+30;
				//图片像素点的真实位置点
				imageX=x*size;
				imageY=y*size;
				//把点加入到Point中
			//	Log.i(LOG_TAG, "imageX: " + imageX + "   imageY: " + imageY );

				mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT-180, LayoutParams.WRAP_CONTENT);
			    //如果需要点击其他的地方popupwindow自动消失： 1 popupwindow必须指定背景   2 poupwindow必须获取焦点
			    mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_style));
			    mPopupWindow.setFocusable(true);	
			    //控制窗口显示的位置
		    	if(y<dh/4)
		    	{   
		    		mPopupWindow.showAtLocation((View)v.getParent(), Gravity.TOP, 0, (int)y+5);
		    	}else if((y>dh/4&&y<dh/2)||(y>dh/2&&y<dh*3/4))
		    	{
		    		mPopupWindow.showAtLocation((View)v.getParent(), Gravity.TOP, 0, (int)y+5);
		    	}else
		    	{   
		    		mPopupWindow.showAtLocation((View)v.getParent(), Gravity.BOTTOM, 0, 10);
		    	}
		    	
				break; 
			default:
				break;
			}
			return true;
		}
	});
	     
	}
	
	public int[] correctionPoint(int x, int y)
	{
		if( (this.xStart == 0) && (this.yStart == 0) && 
			(this.xEnd == 0) && (this.yEnd == 0) )
		{
			// 计算载体图像在屏幕中的坐标范围
			if( (ParentHWRatio != 0) && (ImageHWRatio != 0) )
			{
				// 载体图像将屏幕宽度占满，高度未占满
				if(ParentHWRatio > ImageHWRatio)
				{
					this.xStart = 0;
					this.xEnd = this.ParentWidth;
					
					int realHeight = (int)(ImageHWRatio * ParentWidth);
					
					this.yStart = (ParentHeight - realHeight)/2;
					this.yEnd = this.yStart + realHeight;
				}
				// 载体图像将屏幕高度占满，宽度未占满 (是否会发生？)
				else
				{
					this.yStart = 0;
					this.yEnd = this.ParentHeight;
					
					int realWidth = (int) (this.ParentHeight / (ImageHWRatio));
					
					this.xStart = (ParentWidth - realWidth) / 2;
					this.xEnd = this.xStart + realWidth;
				}
			}
		}
		// 如果当前触摸点在载体图像范围之外，返回 null
		if( x <= this.xStart || 
			x >= this.xEnd || 
			y <= this.yStart ||
			y >= this.yEnd )
		{
			return null;
		}
		
		// 当前触摸点和已有的点集合中的任意一点距离太近，修正之
		for(int i = 0; i < pointLists.size(); i++)
		{
			Point pt = pointLists.get(i);
			float x1 = pt.getPointX();
			float y1 = pt.getPointY();
		
			double len = Math.sqrt( Math.pow((x1 - x), 2) + Math.pow((y1 - y), 2) );
			
			if( POINT_MIN_DISTANCE != 0 && len < POINT_MIN_DISTANCE )
			{
				return null;
			}
		}
		
		int [] pointcorr = new int[2];
		pointcorr[0] = x;
		pointcorr[1] = y;
	
		return pointcorr;
	}
	
	/**
	 * 获取并设置图片到手机
	 */
	private void setImage() {
		//获取手机屏幕的大小
		Display currentDisplay = getWindowManager().getDefaultDisplay();
	    dw = currentDisplay.getWidth();
	    dh = currentDisplay.getHeight();
	    
	    
		//获取传递过来的uri
	//    Uri uri=SysApplication.uri;
	    
	    // 获得传递过来的载体文件名
        Intent intent =getIntent();
        curImage = intent.getStringExtra("carrierImgPath");
	    
		try {
			// curImage=  getPicPath(uri);
		/*	 ContentResolver cr = this.getContentResolver();
			 InputStream is=cr.openInputStream(uri);*/
	         BitmapFactory.Options options=new BitmapFactory.Options();
	         options.inJustDecodeBounds=true;
	       //  bitmap=BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
	         bitmap = BitmapFactory.decodeFile( curImage, options);
	  
	        //通过该算法可以动态的获取insampleSize的值
	        options.inSampleSize= SampleSizeUtils.computeSampleSize(options, -1, dw*dh);
	        options.inJustDecodeBounds=false;
	        size=options.inSampleSize;
	        bmp= BitmapFactory.decodeFile( curImage, options);
	        //如果缩放的尺寸大于1,则旋转90度
	        if(size>1)
	        {
		        matrix=new Matrix();
		        matrix.setRotate(90);
		        bmp=Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	        }
	        if(bmp.getWidth() != 0)
	        {
	        	ImageWidth = bmp.getWidth();
	        	ImageHeight = bmp.getHeight();
	        	ImageHWRatio = (float)ImageHeight/(float)ImageWidth;
	        }
	        iv_showpic.setImageBitmap(bmp);
	        
	        this.ParentHeight = 0;
	        this.ParentWidth = 0;
	    } catch (Exception e) {
	    e.printStackTrace();
	}
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
		 picPath = cursor.getString(column_index);
		 return picPath;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		
		case R.id.bt_insert_text://嵌入文本
			mPopupWindow.dismiss();
		    embedway=0;
			//initDialog();
			View view0=inflater.inflate(R.layout.yl_dialog, null);
			//给对话框进行初始化
			initDialog(view0);
			tv_select_file.setText(getString(R.string.select_file_text));
			tv_select_oper.setText(getString(R.string.input_text));
			showDialog(view0);
			
			break;
		case R.id.bt_insert_audio://嵌入音频	
			mPopupWindow.dismiss();
			embedway=1;
			View view1=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view1);
			tv_select_file.setText(getString(R.string.select_audio));
			tv_select_oper.setText(getString(R.string.select_recoding));
			showDialog(view1);
			break;
		case R.id.bt_insert_video://嵌入视频
			mPopupWindow.dismiss();
			embedway=2;
			View view2=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view2);
			tv_select_file.setText(getString(R.string.select_video));
			tv_select_oper.setText(getString(R.string.select_shoot_video));
			showDialog(view2);
			break;
		case R.id.bt_insert_image://嵌入图像
			mPopupWindow.dismiss();
			embedway=3;
			View view3=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view3);
			tv_select_file.setText(getString(R.string.select_pictures));
			tv_select_oper.setText(getString(R.string.select_shoot_pic));
			showDialog(view3);
			break;
		case R.id.tv_select_file://进入选择文件操作
		    if(embedway==0)
		    {
		    	 requestCode=FILE_SELECT_CODE;
		    	 selectWay(requestCode);  
		    	
		    }else if(embedway==1)  //选择音频文件
			{     
				 requestCode=AUDIO_SELECT_CODE;
				 selectWay(requestCode);  
			}else if(embedway==2)//选择视频文件
			{   
				requestCode=VIDEO_SELECT_CODE;
				selectWay(requestCode);  
			}else if(embedway==3)//选择图像文件
			{   
				//从图库中获取图片
				requestCode=PIC_SELECT_CODE;;
				selectWay(requestCode);  
			}
			
			closeDialog(dialog);//关闭dialog
			break;
		case R.id.tv_select_oper://进入操作界面
			if(embedway==0)
			{
				inputText();
			}
		    else if(embedway==1)  //进入录音界面
			{     
				intent=new Intent(this,RecordActivity.class);
				startActivityForResult(intent, AUDIO_REQUEST);
			}else if(embedway==2)//进入视频拍摄界面
			{   
			     intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  
				startActivityForResult(intent, CASE_CAMERA);  
			}else if(embedway==3)//进入拍摄图片界面
			{
				 intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				
				 this.captureMarkImagePath = DirectoryConfig.TempFilesDir + "/" + getPhotoFileName();
				 imageFile = new File( this.captureMarkImagePath);
				 intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imageFile));
				 startActivityForResult(intent, IMAGE_CAMERA);  
			}
			closeDialog(dialog);
	
			break;
		default:
			break;
		}
		
		Log.i( LOG_TAG, "embedway: " + embedway);
	}
	/**
	 * 输入文本操作
	 */
	private void inputText() {
		AlertDialog.Builder builder=new Builder(ShowPicActivity.this);
		builder.setTitle(getString(R.string.please_input_text));
		final EditText et=new EditText(ShowPicActivity.this);
		
		builder.setView(et);
		builder.setPositiveButton(getString(R.string.isok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			      //获取文本内容	
				String strInputFile=Environment.getExternalStorageDirectory()+"/YFWatermark/temp/InputText"+System.currentTimeMillis()+".txt";
				String content=et.getText().toString();
			    
				if(TextUtils.isEmpty(content))
				{
					return;
				}
				try {
					FileOutputStream out=new FileOutputStream(new File(strInputFile));
					out.write(content.getBytes());
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				curFilePath=strInputFile.substring(strInputFile.lastIndexOf("/")+1,strInputFile.length());
			    fileInfo = startPointX+","+startPointY+","+curFilePath;
			    picPathLists.add(strInputFile);
			    pointLists.add( new Point(startPointX, startPointY));
				fileInfoLists.add(fileInfo);
				showAnimation();
				  file_count++;
				
			}
		});
		builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.create().show();
	}
	private void selectWay(int requestCode) {
		wayType=requestCode;
		Intent intent = new Intent();
		intent.putExtra("explorer_title",
				getString(R.string.dialog_read_from_dir));
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"")), "*/*");
		intent.setClass(ShowPicActivity.this, ExDialog.class);
		startActivityForResult(intent, REQUEST_EX);
	}
	private void initDialog(View view) {
		//给对话框进行初始化
		tv_select_file=(TextView)view.findViewById(R.id.tv_select_file);
		tv_select_oper=(TextView)view.findViewById(R.id.tv_select_oper);
		tv_select_file.setOnClickListener(this);
		tv_select_oper.setOnClickListener(this);
	}
	
	/**
	 * 显示对话框
	 * @param view
	 */
	private void showDialog(View view2) {
		dialog=new Dialog(this);
		dialog.setTitle(getString(R.string.please_select_way));
		dialog.setContentView(view2);
		dialog.show();
	}
	
	/**
	 * 关闭对话框
	 */
	
	private void closeDialog(Dialog dialog)
	{
		 dialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("执行bitmap回收");
		if(bitmap!=null)
		{
	    	bitmap.recycle();
		}
		if(bmp!=null)
		{
		   bmp.recycle();
		}
		picPathLists=null;
	}
	//08-23 20:40:20.780: I/System.out(3627): java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, getString(R.string.start_execute));
		return super.onCreateOptionsMenu(menu);
	}


	
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
         if(item.getItemId()==1)
         {      
        	 //执行嵌入操作
        	 executeEmbed();
         }
    	return super.onOptionsItemSelected(item);
    }
     
     /**
      * 输入嵌入文件的名字
      */
     private void executeEmbed() {
		// TODO Auto-generated method stub
    	 View view=View.inflate(this, R.layout.yl_inputname_dialog, null);
		 AlertDialog.Builder builder=new Builder(this);
		 final EditText et=(EditText) view.findViewById(R.id.et_filename);
		 final TextView tv_default_name = (TextView) view.findViewById(R.id.tv_defaultfilename);
		 
		 strFilename = "YufeiWatermark" + (System.currentTimeMillis()) + ".jpg";
		 tv_default_name.setText( getResources().getString( R.string.input_filename_propmt ) + strFilename);
		 
		 builder.setTitle(R.string.picformat_title);
		 builder.setIcon(R.drawable.alert);
		 builder.setView(view);
		 builder.setPositiveButton(getString(R.string.start_execute), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String newFileName=et.getText().toString();
			    String filename="";
				//输入文件名为空
				 if(TextUtils.isEmpty(newFileName))
				 {
					      filename = strFilename;
				 }else
				 {
					      filename = newFileName.trim()+".jpg";
				 }
				  saveInfoToFile();
	        	  Object[]  objs=picPathLists.toArray();
	        	  String[] files=new String[objs.length];
	        	  for(int i=0;i<objs.length;i++)
	        	  {
	        		  files[i]=objs[i].toString();
	        		  System.out.println("嵌入的文件名:"+files[i]);
	        	  }
	        	
	        	  strFilesDir=Environment.getExternalStorageDirectory()+"/YFWatermark/image/"+filename;
	        	  int x=  BaseLib.AddFilesToJpegFile(curImage,files, strFilesDir);
	        	  System.out.println("当前的X="+x);
	        	  if(x>=1)
	        	  {
	                   Toast.makeText(ShowPicActivity.this, getString(R.string.embed_success), 1).show();
	                   //嵌入的时候删除temp目录下的所有输入文本
	                   deleteTempListFiles();
	           		   String strFile=Environment.getExternalStorageDirectory()+"/YFWatermark/temp/YFConfig.txt";
	           		   //删除时移出文件
	           		   picPathLists.remove(strFile);
	        	  }else
	        	  {
	        		  Toast.makeText(ShowPicActivity.this, getString(R.string.pack_isweight), 0).show();
	        	  }
			}
   
		});
		 builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		 builder.create().show();
	}
     
     
		/**
		 * 嵌入后删除temp目录下的所有文件
		 */
		private void deleteTempListFiles() {
			// TODO Auto-generated method stub
          String strInputFile=DirectoryConfig.TempFilesDir;
          String strAudioFile=DirectoryConfig.AudioFilesDir;
           File[] files=new File(strInputFile).listFiles();
           File[] files2=new File(strAudioFile).listFiles(); 
           for(File file:files)
           {
        	   file.delete();
           }
           for(File file2:files2)
           {
        	   file2.delete();
           }
		}
   
		/**
		 * 动画的显示图片
		 */
	private void showAnimation() {
 		// TODO Auto-generated method stub
 		    AlphaAnimation alphaAnimation=new AlphaAnimation(0.0f, 1.0f);
 	        alphaAnimation.setDuration(2000);
 	        
 	       for(Point point:pointLists)
 	       { 
	    	  int x=(int) point.getPointX();
	    	  int y=(int) point.getPointY();      
 	          iv=new ImageView(this);
 	          iv.setImageDrawable(getResources().getDrawable(R.drawable.biying));
 	          iv.setOnClickListener(this);
 	        //确定这个控件的大小和位置
	        AbsoluteLayout.LayoutParams lp1 =new AbsoluteLayout.LayoutParams(
 	        ViewGroup.LayoutParams.WRAP_CONTENT,
 	        ViewGroup.LayoutParams.WRAP_CONTENT,
 	        x,y);
 	       abslayout.addView(iv, lp1 );
 	       iv.startAnimation(alphaAnimation);
 	       }
 	         
 	}
     
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        // TODO Auto-generated method stub 
		    /**
		     * 选择文本路径
		     */
		     String filePath="";
		
				if (requestCode == REQUEST_EX) {
					if(data!=null)
					{
					 filePath=data.getStringExtra("path");
					}else
					{
						return;
					}
				}
			
		   
		   //选取文本文件后的操作
	        if (wayType == FILE_SELECT_CODE) {  
	        	    System.out.println("当前文本格式:"+filePath);
	        	    if(filePath == null)
	        	    {
	        	    	return;
	        	    }
		            if( !filePath.endsWith(".txt"))
		            {
		            	Toast.makeText(getApplicationContext(), getString(R.string.please_select_text), 0).show();  	
		            	return ;
		            }
		            curFilePath=filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
		            fileInfo=startPointX+","+startPointY+","+curFilePath;
		    	     pointLists.add( new Point(startPointX, startPointY));
		            picPathLists.add(filePath);
		          	fileInfoLists.add(fileInfo);	 
		            showAnimation();
		            wayType=-1;
		            file_count++;
		            
	        }  else if(wayType==AUDIO_SELECT_CODE)     //选取音频文件
	        {       

	        	    System.out.println("当前音频格式:"+filePath);
	        	    //判断是否是音频格式
	        	    for(String audio:audioFormatter)
	        	    {
	        	    	if(filePath.endsWith(audio))
	        	    	{
	        	    		type=true;
	        	    	}
	        	    }
	        	    if(type)
	        	    {
		        	    curFilePath=filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
	   	        	    fileInfo=startPointX+","+startPointY+","+curFilePath;
	   	        	    pointLists.add( new Point(startPointX, startPointY));
	   	        	    picPathLists.add(filePath);
	   	        	    fileInfoLists.add(fileInfo);
	   	        	    showAnimation();
	   	        	    type=false;
	   	        	    wayType=-1;
	   	        	    file_count ++;
	        	    }else
	        	    {
	        	    	Toast.makeText(getApplicationContext(), getString(R.string.not_support_audio), 0).show();  	
		            	return ;
	        	    }
	        	 
	        }else if(wayType==VIDEO_SELECT_CODE)//选取视频文件
	        {     
           
	        	  System.out.println("当前视频格式:"+filePath);
	        	  for(String video:videoFormatter)
	        	  {
	        		  if(filePath.endsWith(video))
	        		  {
	        		 	 type=true;
	        		  }
	        	  }
	        	  if(type)
	        	  {
		        	  curFilePath=filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
		        	  fileInfo=startPointX+","+startPointY+","+curFilePath;
		        	  pointLists.add( new Point(startPointX, startPointY));
		        	  picPathLists.add(filePath);
		        	  fileInfoLists.add(fileInfo);
		        	  showAnimation();
		        	  type=false;
		        	  wayType=-1;
		        	  file_count++;
	        	  }else 
	        	  {
	        		  Toast.makeText(getApplicationContext(), getString(R.string.not_support_video), 0).show();  	
		              return ;
	        	  }
	       
	        }else if(wayType==PIC_SELECT_CODE)  //选取图像文件
	        {       System.out.println("当前图片格式:"+filePath);
	        	 
	        	  for(String image:imageFormatter)
	        	  {
	        		  if(filePath.endsWith(image))
	        		  {
	        			  type=true;
	        		  }
	        	  }
	        	
	        	  if(type)
	        	  {
	        	    picPathLists.add(filePath);
	        	    curFilePath=filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
	        	    fileInfo=startPointX+","+startPointY+","+curFilePath;
	        	    pointLists.add( new Point(startPointX, startPointY));
	        	    fileInfoLists.add(fileInfo);
	        	    showAnimation();
	        	    type=false;
	        	    wayType=-1;
	        	    file_count++;
	        	  }else
	        	  {
	        		  Toast.makeText(getApplicationContext(), getString(R.string.not_support_image), 0).show();  	
		              return ;
	        	  }
	        	  
	        }
	        
	        //执行录音，拍摄视频、图片后的操作
	        
	        if(requestCode==AUDIO_REQUEST&&resultCode==21)//执行录音结束后的操作
	        {  
	        	if(data!=null)
	        	{	
	        	   filePath= data.getExtras().getString("filepath");
	        	   System.out.println("录音后的文件"+filePath);
	        	   curFilePath=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
	        	   fileInfo=startPointX+","+startPointY+","+curFilePath;
	        	   picPathLists.add(filePath);
	        	   pointLists.add( new Point(startPointX, startPointY));
	        	   fileInfoLists.add(fileInfo);
	        	    showAnimation(); 
	        	    file_count++;
	        	}
	        }
	        
	        if(requestCode==CASE_CAMERA&&resultCode==this.RESULT_OK)//执行拍摄视频后的操作
	        {
	        	
	        	  Uri uriVideo = data.getData();   
	        	  Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);    
                  if (cursor.moveToNext()) {    
                          /* _data：文件的绝对路径 ，_display_name：文件名 */    
                          filePath = cursor.getString(cursor.getColumnIndex("_data"));   
                  }
                  curFilePath=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
                  System.out.println("视频拍摄后的"+curFilePath);
                  fileInfo=startPointX+","+startPointY+","+curFilePath;
                  picPathLists.add(filePath);
                  pointLists.add( new Point(startPointX, startPointY));
                  fileInfoLists.add(fileInfo);
                  showAnimation();
                  file_count ++;
	        }
	        
	        if(requestCode == IMAGE_CAMERA && resultCode == this.RESULT_OK)//执行拍摄完图像后的操作 
	        {
	        	if( this.captureMarkImagePath != null )
	        	{
	        		filePath = this.captureMarkImagePath;
	        	}
	        	//filePath = getPicPath(data.getData());
	        	   System.out.println("图片拍摄后的"+filePath);
	             curFilePath = filePath.substring( filePath.lastIndexOf("/") + 1, filePath.length() );
	            fileInfo = startPointX + "," + startPointY + "," + curFilePath;
	        	picPathLists.add(filePath);
	        	pointLists.add( new Point(startPointX, startPointY));
	        	fileInfoLists.add(fileInfo);
	            showAnimation();
	            file_count++;
	        }
	        super.onActivityResult(requestCode, resultCode, data);  
	    }
   
	/**
	 * 保存点和图片信息到文件中
	 */
	 private void saveInfoToFile()
	 {   
		  Object[]  objs=fileInfoLists.toArray(); //把一个object数组对象转换成一个字符串数组
    	  String[] fileInfos=new String[objs.length];
    	  for(int i=0;i<objs.length;i++)
    	  {
    		  fileInfos[i]=objs[i].toString();
    		  System.out.println("写文件:"+fileInfos[i]);
    	  }
		 String strFile=Environment.getExternalStorageDirectory()+"/YFWatermark/temp/YFConfig.txt";
		 File file=new File(strFile);
		
			 try {
				 FileOutputStream  fos=new FileOutputStream(file);
				 OutputStreamWriter osw=new OutputStreamWriter(fos);
				 BufferedWriter  bw=new BufferedWriter(osw);
				 for(String arr:fileInfos)
				 {
				        bw.write(arr+"\t\n");
				 }
				 bw.close();
				 osw.close();
				 fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 picPathLists.add(strFile);
	 }    
}
