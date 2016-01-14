package cc.yufei.view;

/**
 * Copyright (c) 2013, �ɶ�������޹�˾ All rights reserved.
 * �ļ����ƣ�MainActivity.java
 * ��Ҫ˵����������ʾͼƬ������ͼƬ
 * ��ǰ�汾��V1.0
 * ���ߣ�Du
 * ���ڣ�2013-08-05
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
 * ��ʾͼƬ
 * 
 */
public class ShowPicActivity extends Activity implements OnClickListener{
	
    private ImageView  iv_showpic;
    private Bitmap bitmap,bmp;
    private LayoutInflater inflater;
    private View view;
    /**
     * ��ŵ�ǰ������X,Y����
     */
    private float x,y;
    
    private float imageX,imageY;
    
    private PopupWindow  mPopupWindow;
    
    private boolean flag=true;
    /**
     * ��ȡ����ͼƬ���ĸ���ť
     */
    private Button bt_insert_text,bt_insert_audio,bt_insert_video,bt_insert_image;
    
    private Animation animation,animation2;
    
    /**
     * ͼƬ�Ĵ��·��
     */
    private String picPath;
    
    /**
     * ��Ļ�Ŀ��  320*480     720*1280
     */
    private int dw,dh;
    /**
     * ����һ�����������ת
     */
    private Matrix matrix;
    /**
     * ��¼���ʵ�simpleSize��ֵ
     */
    private int size;
    /**
     * �ļ�ѡ��ģʽ
     */
    private static final int FILE_SELECT_CODE = 0;  
    /**
     * ��Ƶѡ��ģʽ
     */
    private static final int AUDIO_SELECT_CODE=1; 
    /**
     * ��Ƶѡ��ģʽ
     */
    private static final int VIDEO_SELECT_CODE=2;
    /**
     * ͼƬѡ��ģʽ
     */
    private static final int PIC_SELECT_CODE=3;
    /**
     * ��¼�������Activity
     */
	private static final int AUDIO_REQUEST = 20;
	private static final int CASE_CAMERA = 30;
	private static final int IMAGE_CAMERA = 40;
    private Intent intent;

    /**
     * dialog�Ի���Ĳ��ֿؼ�
     */
    private TextView tv_select_file,tv_select_oper;
    
   private   Dialog dialog;
   
   /**
    * ����һ����־�����ж�ѡ��Ƕ�����ַ�ʽ
    */
   private  int  embedway=0;
   
   private String wayName;//ѡ��ʽ
   private int requestCode;//������
   
   private Uri uri;//ѡȡ�ļ����ص�url 
   
   /**
    * ����һ����������϶�������½Ƕ���
    */
   private float startPointX,startPointY;
   private float endPointX,endPointY;
   private  List<Point> pointLists;
   private ImageView iv;
   
   /*
    * ��ǰ������ʾ��ͼƬ·��
    */
   private String curImage;
   /**
    * ���浱ǰ��ŵĵ��ͼƬ����Ϣ
    */
   private String fileInfo;
   /**
    * ���浱ǰǶ���ļ���·��
    */
   private String curFilePath;
   /**
    * ϵͳ��ǰ����
    */
   String currentLanuage;
   /**
    * ����һ�����ϴ�ŵ�ǰǶ���ͼƬ
    */
   List<String>  picPathLists;
   /**
    * ����Ƕ����ļ�Ŀ¼
    */
  private    String strFilesDir ;
  private AbsoluteLayout abslayout;
  
  /**������ļ����� 
   * */
  public static String strFilename;
  
  /**
   * ��ŵ��ͼƬ�ļ���
   */
  private List<String> fileInfoLists;
  
	//��Ƶ��ʽ
  private String[] audioFormatter={".amr",".mp3",".wav",".ac3",".flac",".eaac+"};
	//ͼƬ��ʽ
   private String[]  imageFormatter={".jpg",".png",".bmp",".gif"};
	//��Ƶ��ʽ
   private String[]  videoFormatter={".mp4",".wmv",".divx",".3gp",".xvid"};
   
   boolean type=false;
   
	private static final int REQUEST_EX = 1;
	private int wayType=-1;
	
	private String LOG_TAG = "samsung_info";
	
	// ����ͼ���ļ�����Ƕ����ļ���
	private File imageFile;
	private String captureMarkImagePath;
	
	// ��ǰ(ImageView)����Ļ�ϵ�����(left, right, top, bottom)
	private int ParentHeight = 0;
	private int ParentWidth = 0;
	
	// ʵ������ͼ��(��������֮��)�ĸ߿�
	private int ImageWidth = 0;
	private int ImageHeight = 0;
	
	// ����ͼ��ĸ�View�ĸ߿��
	private float ParentHWRatio = 0;
	// ����ͼ��ĸ߿��
	private float ImageHWRatio = 0;
	// ����ͼ������Ļ�е����귶Χ(�˴�������Point���ѱ�cc.yufei.util ����)
	private int xStart = 0;
    private int yStart = 0;
	private int xEnd = 0;
	private int yEnd = 0;
	
	// ��֮����С�ľ���
	private static double POINT_MIN_DISTANCE; 
	
	// �Ѿ����ص��ļ�����
	private int file_count = 0;
	// ���ص��ļ���������
	private static final int MAX_FILE_NUM = 4;
	
	// ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
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
		//��ʼ����ť
		initView();
		setImage();
		//�ж��Ƿ���jpgͼƬ
		if(BaseLib.CheckFileType(curImage)==1)//�������ļ���ʽ
		{
			showPrompt(R.string.file_no_support);	
		}
		
		if(BaseLib.CheckMarkedness(curImage)==1)
		{
			//�Ѿ�Ƕ��ˮӡ
			showPrompt(R.string.file_markness);
		}
		// ��ʾ�û��������в���
		else
		{
			Toast toast = Toast.makeText(getApplicationContext(),
				     getString(R.string.toast_text), Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
		}
		
		//�����ļ�Ŀ¼
	//	createFileDir();
		//��ʼ����
	    animation=AnimationUtils.loadAnimation(this, R.anim.scale_anim);
	}
   
	/**
	 * ��ʾ�û�ѡ���ͼƬ��ʽ
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
	 * ��ʼ����ť
	 */
	private void initView() {
		// TODO Auto-generated method stub
		bt_insert_text=(Button)view.findViewById(R.id.bt_insert_text);
		bt_insert_audio=(Button)view.findViewById(R.id.bt_insert_audio);
		bt_insert_video=(Button)view.findViewById(R.id.bt_insert_video);
		bt_insert_image=(Button)view.findViewById(R.id.bt_insert_image);
		abslayout=(AbsoluteLayout)findViewById(R.id.abl);
		//���ü���
		bt_insert_text.setOnClickListener(this); //���������ı�
		bt_insert_audio.setOnClickListener(this);//����������Ƶ
		bt_insert_video.setOnClickListener(this);//����������Ƶ
		bt_insert_image.setOnClickListener(this);//��������ͼƬ
		picPathLists=new  ArrayList<String>();
		fileInfoLists=new ArrayList<String>();
		pointLists=new ArrayList<Point>();
        
       //��ͼƬ���ô����¼�
       iv_showpic.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			// ������ȡ�õ�ǰ����ͼ���Parent View��ռ��Ļ������
			if( (ParentHeight == 0) || 
				(ParentWidth == 0) )
			{
				ParentHeight = iv_showpic.getHeight();
				ParentWidth = iv_showpic.getWidth();
			   
			    if(ParentWidth != 0)
			    {
			    	ParentHWRatio = (float)ParentHeight/(float)ParentWidth;
			    }
			    
			    // ��֮��ľ����趨Ϊ��Ļ��ȵ� 1/10;
			    POINT_MIN_DISTANCE = ParentWidth * 0.1;
			}
		       
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(!curImage.endsWith(".jpg"))
				{
					break;
				}
				// Ƕ���ļ������Ƿ��Ѵﵽ��ֵ 
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
			    // �жϵ�ǰ�ĵ�����Ƿ�����Ļ�ϵ�����ͼ��ImageView֮�� &&
			    int newXY[] = correctionPoint((int)x, (int)y);
			    if(newXY == null)
			    {
			    	break;
			    }
			    
				//��ȡ60*60����ĵ�
				startPointX=x-30;
				startPointY=y-30;
				endPointX=x+30;
				endPointY=y+30;
				//ͼƬ���ص����ʵλ�õ�
				imageX=x*size;
				imageY=y*size;
				//�ѵ���뵽Point��
			//	Log.i(LOG_TAG, "imageX: " + imageX + "   imageY: " + imageY );

				mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT-180, LayoutParams.WRAP_CONTENT);
			    //�����Ҫ��������ĵط�popupwindow�Զ���ʧ�� 1 popupwindow����ָ������   2 poupwindow�����ȡ����
			    mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_style));
			    mPopupWindow.setFocusable(true);	
			    //���ƴ�����ʾ��λ��
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
			// ��������ͼ������Ļ�е����귶Χ
			if( (ParentHWRatio != 0) && (ImageHWRatio != 0) )
			{
				// ����ͼ����Ļ���ռ�����߶�δռ��
				if(ParentHWRatio > ImageHWRatio)
				{
					this.xStart = 0;
					this.xEnd = this.ParentWidth;
					
					int realHeight = (int)(ImageHWRatio * ParentWidth);
					
					this.yStart = (ParentHeight - realHeight)/2;
					this.yEnd = this.yStart + realHeight;
				}
				// ����ͼ����Ļ�߶�ռ�������δռ�� (�Ƿ�ᷢ����)
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
		// �����ǰ������������ͼ��Χ֮�⣬���� null
		if( x <= this.xStart || 
			x >= this.xEnd || 
			y <= this.yStart ||
			y >= this.yEnd )
		{
			return null;
		}
		
		// ��ǰ����������еĵ㼯���е�����һ�����̫��������֮
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
	 * ��ȡ������ͼƬ���ֻ�
	 */
	private void setImage() {
		//��ȡ�ֻ���Ļ�Ĵ�С
		Display currentDisplay = getWindowManager().getDefaultDisplay();
	    dw = currentDisplay.getWidth();
	    dh = currentDisplay.getHeight();
	    
	    
		//��ȡ���ݹ�����uri
	//    Uri uri=SysApplication.uri;
	    
	    // ��ô��ݹ����������ļ���
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
	  
	        //ͨ�����㷨���Զ�̬�Ļ�ȡinsampleSize��ֵ
	        options.inSampleSize= SampleSizeUtils.computeSampleSize(options, -1, dw*dh);
	        options.inJustDecodeBounds=false;
	        size=options.inSampleSize;
	        bmp= BitmapFactory.decodeFile( curImage, options);
	        //������ŵĳߴ����1,����ת90��
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
	 * ��ȡ��ǰѡ��ͼƬ��·��
	 * @param uri
	 */
	public String getPicPath(Uri uri) {
		//��ȡͼƬ��·��
		  String[] proj = {MediaStore.Images.Media.DATA};
		//������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�
		 Cursor cursor = managedQuery(uri, proj, null, null, null); 
		 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		//�����������ͷ ���������Ҫ����С�ĺ���������Խ��
		 cursor.moveToFirst();
		//����������ֵ��ȡͼƬ·��
		 picPath = cursor.getString(column_index);
		 return picPath;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		
		case R.id.bt_insert_text://Ƕ���ı�
			mPopupWindow.dismiss();
		    embedway=0;
			//initDialog();
			View view0=inflater.inflate(R.layout.yl_dialog, null);
			//���Ի�����г�ʼ��
			initDialog(view0);
			tv_select_file.setText(getString(R.string.select_file_text));
			tv_select_oper.setText(getString(R.string.input_text));
			showDialog(view0);
			
			break;
		case R.id.bt_insert_audio://Ƕ����Ƶ	
			mPopupWindow.dismiss();
			embedway=1;
			View view1=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view1);
			tv_select_file.setText(getString(R.string.select_audio));
			tv_select_oper.setText(getString(R.string.select_recoding));
			showDialog(view1);
			break;
		case R.id.bt_insert_video://Ƕ����Ƶ
			mPopupWindow.dismiss();
			embedway=2;
			View view2=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view2);
			tv_select_file.setText(getString(R.string.select_video));
			tv_select_oper.setText(getString(R.string.select_shoot_video));
			showDialog(view2);
			break;
		case R.id.bt_insert_image://Ƕ��ͼ��
			mPopupWindow.dismiss();
			embedway=3;
			View view3=inflater.inflate(R.layout.yl_dialog, null);
			initDialog(view3);
			tv_select_file.setText(getString(R.string.select_pictures));
			tv_select_oper.setText(getString(R.string.select_shoot_pic));
			showDialog(view3);
			break;
		case R.id.tv_select_file://����ѡ���ļ�����
		    if(embedway==0)
		    {
		    	 requestCode=FILE_SELECT_CODE;
		    	 selectWay(requestCode);  
		    	
		    }else if(embedway==1)  //ѡ����Ƶ�ļ�
			{     
				 requestCode=AUDIO_SELECT_CODE;
				 selectWay(requestCode);  
			}else if(embedway==2)//ѡ����Ƶ�ļ�
			{   
				requestCode=VIDEO_SELECT_CODE;
				selectWay(requestCode);  
			}else if(embedway==3)//ѡ��ͼ���ļ�
			{   
				//��ͼ���л�ȡͼƬ
				requestCode=PIC_SELECT_CODE;;
				selectWay(requestCode);  
			}
			
			closeDialog(dialog);//�ر�dialog
			break;
		case R.id.tv_select_oper://�����������
			if(embedway==0)
			{
				inputText();
			}
		    else if(embedway==1)  //����¼������
			{     
				intent=new Intent(this,RecordActivity.class);
				startActivityForResult(intent, AUDIO_REQUEST);
			}else if(embedway==2)//������Ƶ�������
			{   
			     intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  
				startActivityForResult(intent, CASE_CAMERA);  
			}else if(embedway==3)//��������ͼƬ����
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
	 * �����ı�����
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
			      //��ȡ�ı�����	
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
		//���Ի�����г�ʼ��
		tv_select_file=(TextView)view.findViewById(R.id.tv_select_file);
		tv_select_oper=(TextView)view.findViewById(R.id.tv_select_oper);
		tv_select_file.setOnClickListener(this);
		tv_select_oper.setOnClickListener(this);
	}
	
	/**
	 * ��ʾ�Ի���
	 * @param view
	 */
	private void showDialog(View view2) {
		dialog=new Dialog(this);
		dialog.setTitle(getString(R.string.please_select_way));
		dialog.setContentView(view2);
		dialog.show();
	}
	
	/**
	 * �رնԻ���
	 */
	
	private void closeDialog(Dialog dialog)
	{
		 dialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("ִ��bitmap����");
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
        	 //ִ��Ƕ�����
        	 executeEmbed();
         }
    	return super.onOptionsItemSelected(item);
    }
     
     /**
      * ����Ƕ���ļ�������
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
				//�����ļ���Ϊ��
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
	        		  System.out.println("Ƕ����ļ���:"+files[i]);
	        	  }
	        	
	        	  strFilesDir=Environment.getExternalStorageDirectory()+"/YFWatermark/image/"+filename;
	        	  int x=  BaseLib.AddFilesToJpegFile(curImage,files, strFilesDir);
	        	  System.out.println("��ǰ��X="+x);
	        	  if(x>=1)
	        	  {
	                   Toast.makeText(ShowPicActivity.this, getString(R.string.embed_success), 1).show();
	                   //Ƕ���ʱ��ɾ��tempĿ¼�µ����������ı�
	                   deleteTempListFiles();
	           		   String strFile=Environment.getExternalStorageDirectory()+"/YFWatermark/temp/YFConfig.txt";
	           		   //ɾ��ʱ�Ƴ��ļ�
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
		 * Ƕ���ɾ��tempĿ¼�µ������ļ�
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
		 * ��������ʾͼƬ
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
 	        //ȷ������ؼ��Ĵ�С��λ��
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
		     * ѡ���ı�·��
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
			
		   
		   //ѡȡ�ı��ļ���Ĳ���
	        if (wayType == FILE_SELECT_CODE) {  
	        	    System.out.println("��ǰ�ı���ʽ:"+filePath);
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
		            
	        }  else if(wayType==AUDIO_SELECT_CODE)     //ѡȡ��Ƶ�ļ�
	        {       

	        	    System.out.println("��ǰ��Ƶ��ʽ:"+filePath);
	        	    //�ж��Ƿ�����Ƶ��ʽ
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
	        	 
	        }else if(wayType==VIDEO_SELECT_CODE)//ѡȡ��Ƶ�ļ�
	        {     
           
	        	  System.out.println("��ǰ��Ƶ��ʽ:"+filePath);
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
	       
	        }else if(wayType==PIC_SELECT_CODE)  //ѡȡͼ���ļ�
	        {       System.out.println("��ǰͼƬ��ʽ:"+filePath);
	        	 
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
	        
	        //ִ��¼����������Ƶ��ͼƬ��Ĳ���
	        
	        if(requestCode==AUDIO_REQUEST&&resultCode==21)//ִ��¼��������Ĳ���
	        {  
	        	if(data!=null)
	        	{	
	        	   filePath= data.getExtras().getString("filepath");
	        	   System.out.println("¼������ļ�"+filePath);
	        	   curFilePath=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
	        	   fileInfo=startPointX+","+startPointY+","+curFilePath;
	        	   picPathLists.add(filePath);
	        	   pointLists.add( new Point(startPointX, startPointY));
	        	   fileInfoLists.add(fileInfo);
	        	    showAnimation(); 
	        	    file_count++;
	        	}
	        }
	        
	        if(requestCode==CASE_CAMERA&&resultCode==this.RESULT_OK)//ִ��������Ƶ��Ĳ���
	        {
	        	
	        	  Uri uriVideo = data.getData();   
	        	  Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);    
                  if (cursor.moveToNext()) {    
                          /* _data���ļ��ľ���·�� ��_display_name���ļ��� */    
                          filePath = cursor.getString(cursor.getColumnIndex("_data"));   
                  }
                  curFilePath=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
                  System.out.println("��Ƶ������"+curFilePath);
                  fileInfo=startPointX+","+startPointY+","+curFilePath;
                  picPathLists.add(filePath);
                  pointLists.add( new Point(startPointX, startPointY));
                  fileInfoLists.add(fileInfo);
                  showAnimation();
                  file_count ++;
	        }
	        
	        if(requestCode == IMAGE_CAMERA && resultCode == this.RESULT_OK)//ִ��������ͼ���Ĳ��� 
	        {
	        	if( this.captureMarkImagePath != null )
	        	{
	        		filePath = this.captureMarkImagePath;
	        	}
	        	//filePath = getPicPath(data.getData());
	        	   System.out.println("ͼƬ������"+filePath);
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
	 * ������ͼƬ��Ϣ���ļ���
	 */
	 private void saveInfoToFile()
	 {   
		  Object[]  objs=fileInfoLists.toArray(); //��һ��object�������ת����һ���ַ�������
    	  String[] fileInfos=new String[objs.length];
    	  for(int i=0;i<objs.length;i++)
    	  {
    		  fileInfos[i]=objs[i].toString();
    		  System.out.println("д�ļ�:"+fileInfos[i]);
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
