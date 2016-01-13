package cc.yufei.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yufei.watermark.BaseLib;
import cc.yufei.util.DirectoryConfig;

import cc.yufei.bean.Point;
import cc.yufei.util.MimeTypeUtil;
import cc.yufei.util.SampleSizeUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 提取图片的界面
 * @author Administrator
 *
 */
public class ExtractPicActivity extends Activity {
	   private AbsoluteLayout abslayout;
	   private ImageView iv_show_pic;
	   private Bitmap bitmap,bmp;
	    private LayoutInflater inflater;
	    private View view;
	    /**
	     * 存放当前触摸的X,Y坐标
	     */
	    
	    private float imageX,imageY;
	    
	    private PopupWindow  mPopupWindow;
	    
	    private boolean flag=true;
	    
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
		private String curImage;
		private ImageView iv;
		private List<Point> pointLists;
		private List<String> fileInfos;
		//保存当前的点和其对应的路径
		private Map<Point,String>   fileInfoMaps;
		private  int x,y;
		private String strFilesDir;
		
		//音频格式
		private String[] audioFormatter={".amr",".mp3",".wav",".ac3",".flac",".eaac+"};
		//图片格式
		private String[]  imageFormatter={".jpg",".png",".bmp",".gif"};
		//视频格式
		private String[]  videoFormatter={".mp4",".wmv",".divx",".3gp",".xvid"};
		/**
		 * 文件类型
		 */
		private  int type;
		
		// 是否进行了提取，防止多次提取
		private boolean isExtract = false;
		
		private static String LOG_TAG = "samsung_info";
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yl_show_pic);
	   curImage=getIntent().getStringExtra("extractpath");
	   Log.i( LOG_TAG, "curImage: " + curImage);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		abslayout=(AbsoluteLayout)findViewById(R.id.abl);
		iv_show_pic=(ImageView)findViewById(R.id.iv_show_pic);
		fileInfos=new ArrayList<String>();
		fileInfoMaps=new HashMap<Point, String>();
		pointLists=new ArrayList<Point>();
		setImage();
		iv_show_pic.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					ExtractFiles();
					setMapValue();
					showAnimation();
					break;

				default:
					break;
				}
				return true;
			}
			/**
			 * 设置map里面的数据
			 */
			private void setMapValue() {
				// TODO Auto-generated method stub
				String[] arrs;
				Point point;
				for(String fileInfo:fileInfos)
				{
					  arrs=fileInfo.split(",");
					  point=new Point();
					  point.setPointX(Float.parseFloat(arrs[0]));
					  point.setPointY(Float.parseFloat(arrs[1]));
					  pointLists.add(point);
					  fileInfoMaps.put(point, arrs[2]);
					  point=null;
				}
			}
		});
		
		// 提示用户点击屏幕开始提取
		Toast toast = Toast.makeText(getApplicationContext(),
			     getString(R.string.toast_extract_text), Toast.LENGTH_SHORT);
			   toast.setGravity(Gravity.CENTER, 0, 0);
			   toast.show();
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
	 
		try {
			 // 08-27 21:00:23.230: I/System.out(32292): 传递过来的uri/storage/sdcard0/YFWatermark/image/%E5%93%A6%E5%93%A6.jpg
		 	

			 System.out.println("传递过来的uri"+curImage);
			 
	         BitmapFactory.Options options=new BitmapFactory.Options();
	         options.inJustDecodeBounds=true;
	         
	         bitmap= BitmapFactory.decodeFile(curImage, options);
	  
	        //通过该算法可以动态的获取insampleSize的值
	        options.inSampleSize= SampleSizeUtils.computeSampleSize(options, -1, dw*dh);
	        options.inJustDecodeBounds=false;
	        size=options.inSampleSize;
	        bmp= BitmapFactory.decodeFile(curImage, options);
	        //如果缩放的尺寸大于1,则旋转90度
            if(size>1)
            {    
        	   matrix=new Matrix();
		        matrix.setRotate(90);
		        bmp=Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	        }
	    	 iv_show_pic.setImageBitmap(bmp);
	    	
	    } catch (Exception e) {
	    e.printStackTrace();
	}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
	//	menu.add(0, 1, 1, getString(R.string.start_extract));
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 提取文件
	 */
	private void ExtractFiles()
	{
		// 如果已经提取过了，直接返回
		if(isExtract) 
			return;
 	   //08-23 21:57:00.550: I/System.out(30053): ------->/storage/sdcard0/log.txt
 	   //strFilesDir = Environment.getExternalStorageDirectory()+"/YFWatermark/temp/";
	   strFilesDir = DirectoryConfig.TempFilesDir;
 	   int x=  BaseLib.GetFilesFromJpegFile(curImage, strFilesDir);
       if(x>=1)
       {
			Toast toast = Toast.makeText(getApplicationContext(),
				     getString(R.string.extract_success), Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
     	  //读取temp文件
     	  String config = strFilesDir + "YFConfig.txt";
     	  try {
				  FileInputStream fin=new FileInputStream(new File(config));
				  InputStreamReader isr=new InputStreamReader(fin);
				  BufferedReader buf=new BufferedReader(isr);
				  String line="";
				  while((line=buf.readLine())!=null)
				  {   
					    fileInfos.add(line);
				  }
				  buf.close();
				  isr.close();
				  fin.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	 isExtract = true;
       }
       
       else
       {
			Toast toast = Toast.makeText(getApplicationContext(),
				     getString(R.string.toast_extract_failed), Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
       }
	}
	
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
         if(item.getItemId()==1)
         {
        	   //08-23 21:57:00.550: I/System.out(30053): ------->/storage/sdcard0/log.txt
        	  strFilesDir = DirectoryConfig.TempFilesDir;
        	  int x=  BaseLib.GetFilesFromJpegFile(curImage, strFilesDir);
              if(x>=1)
              {
            	  Toast.makeText(this, getString(R.string.extract_success), 0).show();
            	  //读取temp文件
            	  String config=strFilesDir+"YFConfig.txt";
            	  try {
					  FileInputStream fin=new FileInputStream(new File(config));
					  InputStreamReader isr=new InputStreamReader(fin);
					  BufferedReader buf=new BufferedReader(isr);
					  String line="";
					  while((line=buf.readLine())!=null)
					  {   
						    fileInfos.add(line);
					  }
					  buf.close();
					  isr.close();
					  fin.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	  
              }
         }
    	return super.onOptionsItemSelected(item);
    }
	/**
	 * 获取当前选中图片的路径
	 * @param uri
	 */
	private String getPicPath(Uri uri) {
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
	
	private void showAnimation() {
		// TODO Auto-generated method stub
		    AlphaAnimation alphaAnimation=new AlphaAnimation(0.0f, 1.0f);
	        alphaAnimation.setDuration(2000);
	 
	       for(final Point point:pointLists)
	       { 
	    	  x=(int) point.getPointX();
	    	  y=(int) point.getPointY();      
	          iv=new ImageView(this);
	          iv.setImageDrawable(getResources().getDrawable(R.drawable.biying));
	          iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String curFileName=fileInfoMaps.get(point);
					final String curFilePath=strFilesDir+curFileName;
			        String curEndName=curFileName.substring(curFileName.lastIndexOf("."), curFileName.length()).trim();
			        final Dialog dialog=new Dialog(ExtractPicActivity.this);
			        dialog.setTitle(R.string.please_click_openfile);
			        View view=View.inflate(ExtractPicActivity.this, R.layout.yl_dialog2, null);
			        TextView tv=(TextView) view.findViewById(R.id.tv_select_file);
			        tv.setTextColor(Color.BLACK);
			        //这里是文本
			        if(".txt".equals(curEndName))
			        {
			        	tv.setText(R.string.text);
			        	type=0;
			        }
			        //这里是音频
			        for(String audio:audioFormatter)
			        {
			        	if(audio.equals(curEndName))
			        	{
			        		tv.setText(R.string.audio);
			        		type=1;
			        	}
			        }
			        //这里是视频
			        for(String video:videoFormatter)
			        {
			        	if(video.equals(curEndName))
			        	{
			        		tv.setText(R.string.video);
			        		type=2;
			        	}
			        }
			        //这里是 图片
			        
			        for(String image:imageFormatter)
			        {
			        	if(image.equals(curEndName))
			        	{
			        		tv.setText(R.string.image);
			        		type=3;
			        	}
			        }
			        dialog.setContentView(view);
			        dialog.show();
			       
			        tv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						 	// TODO Auto-generated method stub
						    //如果是 文本
							dialog.dismiss();
							if(type==0)
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==1)//如果是音频
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==2)//如果是视频
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==3)//如果是图片
							{
								openFile(new File(curFilePath.trim()));
							}
						}
					});
				}
			});	          
	        //确定这个控件的大小和位置
	        AbsoluteLayout.LayoutParams lp1 =new AbsoluteLayout.LayoutParams(
	        ViewGroup.LayoutParams.WRAP_CONTENT,
	        ViewGroup.LayoutParams.WRAP_CONTENT,
	        x,y);
	        abslayout.addView(iv, lp1 );
	        iv.startAnimation(alphaAnimation);
	       }
	         
	}
	
	/**
	 * 打开文件
	 * @param file
	 */
	private void openFile(File file){    
        
	    Intent intent = new Intent();    
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	    //设置intent的Action属性     
	    intent.setAction(Intent.ACTION_VIEW);    
	    //获取文件file的MIME类型     
	    String type = getMIMEType(file);    
	    //设置intent的data和Type属性。     
	    intent.setDataAndType(/*uri*/Uri.fromFile(file), type);    
	    //跳转     
	    startActivity(intent);      
	        
	}    

	/**  
	 * 根据文件后缀名获得对应的MIME类型。  
	 * @param file  
	 */    
	private String getMIMEType(File file) {    
	        
	    String type="*/*";    
	    String fName = file.getName();    
	    //获取后缀名前的分隔符"."在fName中的位置。     
	    int dotIndex = fName.lastIndexOf(".");    
	    if(dotIndex < 0){    
	        return type;    
	    }    
	    /* 获取文件的后缀名 */    
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase();    
	    if(end=="")return type;    
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。     
	    for(int i=0;i<MimeTypeUtil.MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？     
	        if(end.equals(MimeTypeUtil.MIME_MapTable[i][0]))    
	            type = MimeTypeUtil.MIME_MapTable[i][1];    
	    }           
	    return type;    
	}    

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	if(bmp!=null)
	{
		bmp.recycle();
	}
	//删除temp目录, capture目录下的所有文件
	File file = new File(DirectoryConfig.TempFilesDir);
	File file1 = new File( DirectoryConfig.CaptureFilesDir );
	File[]  listfiles=file.listFiles();
	for(File listfile:listfiles)
	{
		listfile.delete();
	}
	listfiles= file1.listFiles();
	for(File listfile:listfiles)
	{
		listfile.delete();
	}
	
	isExtract = false;
	super.onDestroy();
}
	
}
