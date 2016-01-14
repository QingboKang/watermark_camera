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
 * ��ȡͼƬ�Ľ���
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
	     * ��ŵ�ǰ������X,Y����
	     */
	    
	    private float imageX,imageY;
	    
	    private PopupWindow  mPopupWindow;
	    
	    private boolean flag=true;
	    
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
		private String curImage;
		private ImageView iv;
		private List<Point> pointLists;
		private List<String> fileInfos;
		//���浱ǰ�ĵ�����Ӧ��·��
		private Map<Point,String>   fileInfoMaps;
		private  int x,y;
		private String strFilesDir;
		
		//��Ƶ��ʽ
		private String[] audioFormatter={".amr",".mp3",".wav",".ac3",".flac",".eaac+"};
		//ͼƬ��ʽ
		private String[]  imageFormatter={".jpg",".png",".bmp",".gif"};
		//��Ƶ��ʽ
		private String[]  videoFormatter={".mp4",".wmv",".divx",".3gp",".xvid"};
		/**
		 * �ļ�����
		 */
		private  int type;
		
		// �Ƿ��������ȡ����ֹ�����ȡ
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
	 * ��ʼ���ؼ�
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
			 * ����map���������
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
		
		// ��ʾ�û������Ļ��ʼ��ȡ
		Toast toast = Toast.makeText(getApplicationContext(),
			     getString(R.string.toast_extract_text), Toast.LENGTH_SHORT);
			   toast.setGravity(Gravity.CENTER, 0, 0);
			   toast.show();
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
	 
		try {
			 // 08-27 21:00:23.230: I/System.out(32292): ���ݹ�����uri/storage/sdcard0/YFWatermark/image/%E5%93%A6%E5%93%A6.jpg
		 	

			 System.out.println("���ݹ�����uri"+curImage);
			 
	         BitmapFactory.Options options=new BitmapFactory.Options();
	         options.inJustDecodeBounds=true;
	         
	         bitmap= BitmapFactory.decodeFile(curImage, options);
	  
	        //ͨ�����㷨���Զ�̬�Ļ�ȡinsampleSize��ֵ
	        options.inSampleSize= SampleSizeUtils.computeSampleSize(options, -1, dw*dh);
	        options.inJustDecodeBounds=false;
	        size=options.inSampleSize;
	        bmp= BitmapFactory.decodeFile(curImage, options);
	        //������ŵĳߴ����1,����ת90��
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
	 * ��ȡ�ļ�
	 */
	private void ExtractFiles()
	{
		// ����Ѿ���ȡ���ˣ�ֱ�ӷ���
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
     	  //��ȡtemp�ļ�
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
            	  //��ȡtemp�ļ�
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
	 * ��ȡ��ǰѡ��ͼƬ��·��
	 * @param uri
	 */
	private String getPicPath(Uri uri) {
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
			        //�������ı�
			        if(".txt".equals(curEndName))
			        {
			        	tv.setText(R.string.text);
			        	type=0;
			        }
			        //��������Ƶ
			        for(String audio:audioFormatter)
			        {
			        	if(audio.equals(curEndName))
			        	{
			        		tv.setText(R.string.audio);
			        		type=1;
			        	}
			        }
			        //��������Ƶ
			        for(String video:videoFormatter)
			        {
			        	if(video.equals(curEndName))
			        	{
			        		tv.setText(R.string.video);
			        		type=2;
			        	}
			        }
			        //������ ͼƬ
			        
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
						    //����� �ı�
							dialog.dismiss();
							if(type==0)
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==1)//�������Ƶ
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==2)//�������Ƶ
							{
								openFile(new File(curFilePath.trim()));
							}else if(type==3)//�����ͼƬ
							{
								openFile(new File(curFilePath.trim()));
							}
						}
					});
				}
			});	          
	        //ȷ������ؼ��Ĵ�С��λ��
	        AbsoluteLayout.LayoutParams lp1 =new AbsoluteLayout.LayoutParams(
	        ViewGroup.LayoutParams.WRAP_CONTENT,
	        ViewGroup.LayoutParams.WRAP_CONTENT,
	        x,y);
	        abslayout.addView(iv, lp1 );
	        iv.startAnimation(alphaAnimation);
	       }
	         
	}
	
	/**
	 * ���ļ�
	 * @param file
	 */
	private void openFile(File file){    
        
	    Intent intent = new Intent();    
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	    //����intent��Action����     
	    intent.setAction(Intent.ACTION_VIEW);    
	    //��ȡ�ļ�file��MIME����     
	    String type = getMIMEType(file);    
	    //����intent��data��Type���ԡ�     
	    intent.setDataAndType(/*uri*/Uri.fromFile(file), type);    
	    //��ת     
	    startActivity(intent);      
	        
	}    

	/**  
	 * �����ļ���׺����ö�Ӧ��MIME���͡�  
	 * @param file  
	 */    
	private String getMIMEType(File file) {    
	        
	    String type="*/*";    
	    String fName = file.getName();    
	    //��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�     
	    int dotIndex = fName.lastIndexOf(".");    
	    if(dotIndex < 0){    
	        return type;    
	    }    
	    /* ��ȡ�ļ��ĺ�׺�� */    
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase();    
	    if(end=="")return type;    
	    //��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�     
	    for(int i=0;i<MimeTypeUtil.MIME_MapTable.length;i++){ //MIME_MapTable??��������һ�������ʣ����MIME_MapTable��ʲô��     
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
	//ɾ��tempĿ¼, captureĿ¼�µ������ļ�
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
