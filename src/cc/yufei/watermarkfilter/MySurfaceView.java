package cc.yufei.watermarkfilter;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
	
	private Thread th;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint p;
	private int screenWidth;
	private int screenHeight;
	private Camera camera; // 定义系统所用的照相机
	private boolean isPreview = false; // 是否在浏览中
	private Camera.AutoFocusCallback mAutoFocusCallBack;
	private String strCaptureFilePath = "/storage/emulated/0/";
	private static int count_img = 0;
	
	private static String TAG = MySurfaceView.class.getSimpleName();
	
	private Camera.PreviewCallback previewCallback;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private boolean isshoot=false;
	private boolean isFocus=false;

	private static final int MIN_PREVIEW_PIXELS = 470 * 320; // small screen
	// private static final int MAX_PREVIEW_PIXELS = 800 * 480; // large/HD
	// screen
	private static final int MAX_PREVIEW_PIXELS = 1280 * 720; // large/HD
	
	private Point screenResolution;
	private Point cameraResolution;
	
	private Bitmap bmpProcessed;
	private Rect rectROI = null;
	private Camera.Size szFrame = null;
	
	private static FilterImageView mFilterSurfaceView;
	public static double szFilterImageRatio[] = {(1./6), (1./6), (5./6), (5./6)};
	
	private boolean shouldContinue = true;
	 
	public MySurfaceView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		Log.i(TAG, "enter MySurfaceView");
		
		p = new Paint();
		p.setAntiAlias(true);
		sfh = this.getHolder();
		sfh.addCallback(this);
		th = new Thread(this);
		this.setKeepScreenOn(true);
		setFocusable(true);
		sfh.addCallback(this);
		sfh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		
		mFilterSurfaceView = FilterCameraActivity.getFilterSurfaceView();
		
		mAutoFocusCallBack = new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					isFocus=true;

				}
				else
				{
					
				}
			}
		};
		previewCallback = new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera arg1) {

		//		Log.i(TAG, "onPreviewFrame");
				FilterImageView.count_draw_time++;
				if (data != null) {
					Camera.Parameters parameters = camera.getParameters();

					//Camera.Size szFrame = parameters.getPreviewSize();

					// 感兴趣区域
				    if(rectROI == null || szFrame == null)
				    {
				        szFrame = parameters.getPreviewSize();
				    
				    	rectROI = new Rect( (int)(szFrame.width*szFilterImageRatio[0]), (int)(szFrame.height*szFilterImageRatio[1]), 
							(int)(szFrame.width * szFilterImageRatio[2]), (int)( szFrame.height * szFilterImageRatio[3]) );
				    }

					long start = System.currentTimeMillis();

					if(bmpProcessed != null && !bmpProcessed.isRecycled())
					{
					//	Log.i(TAG, "call bmpProcessed.recycle");
					//	bmpProcessed.recycle();
					//	bmpProcessed = null;
						System.gc();
					}

					try
					{
						YuvImage img = new YuvImage(data, ImageFormat.NV21, szFrame.width, szFrame.height, null);
				         
						if( img != null )
						{
							//Log.i(TAG, "img.width: " + img.getWidth() + "  height: " + img.getHeight());
							//  long ltime1 = System.currentTimeMillis();
				              ByteArrayOutputStream stream = new ByteArrayOutputStream();
				              boolean b = img.compressToJpeg(rectROI, 100, stream);
				          //    Log.i(TAG, "rectROI " + rectROI);
				           
				              
				              Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
				              
				              stream.close();
				              
				              long end = System.currentTimeMillis();
				              
				              int width = bmp.getWidth();
				              int height = bmp.getHeight();
				              
				          //    Log.i(TAG, "width: " + width + "  " + height);
				              int []pixels = new int[width * height]; //通过位图的大小创建像素点数组  
				              
				              bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
				              byte[] imgbytes = new byte[width * height];
				              byte[] filterbytes = new byte[ width * height * 4 ];
				              for(int i = 0; i < height; i++)
				              {  
				            	  for(int j = 0; j < width; j++)
				            	  {  
				                      int pixel = pixels[width * i + j];  
				                      
				                      byte blue = (byte)(pixel & 0x000000FF);   

				            		  imgbytes[width * i + j] = blue;  

				            	   }  
				               }  

				              count_img++;
				              LibWatermarkFilter.Filter( imgbytes, width * height, filterbytes, width, height,
				            		  strCaptureFilePath + "test_" + count_img + ".bmp" );
      
 				              bmpProcessed = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				              
 				              bmpProcessed.copyPixelsFromBuffer(ByteBuffer.wrap(filterbytes)); 
				           //  bmpProcessed = MyBitmapFactory.convertGreyImg(bmp);
				              System.gc();
				          }
					}
				    catch(Exception ex)
				    {
				    //	Log.i(TAG, "Error: " + ex.getMessage());
				    }
				    
					camera.autoFocus(mAutoFocusCallBack);

				}
			}
		};

		mTimer = new Timer();
		mTimerTask = new CameraTimerTask();
		mTimer.schedule(mTimerTask, 0, 5);
		//聚焦时间设置

		sfh.addCallback(new Callback() {

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.i(TAG, "surfaceChanged");
			}

			public void surfaceCreated(SurfaceHolder holder) {
				Log.i(TAG, "surfaceCreated");
				init_Camera(); // 打开初始化摄像头
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				Log.i(TAG, "surfaceDestroyed");
				if (camera != null) {
					Log.i(TAG, "surfaceDestroyed && camera != null");
					if (isPreview)
					{
						camera.setPreviewCallback(null);
						camera.stopPreview();
					}
					camera.release();
					camera = null;
				}
			//	System.exit(0);
			}
		});
	}
	
	
	public void saveMyBitmap(Bitmap bitmap, String strFilePath) throws IOException
	{
        File f = new File(strFilePath);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try 
        {
                fOut.flush();
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }
        try 
        {
                fOut.close();
        } 
        catch (IOException e) 
        {
                e.printStackTrace();
        }
	}
	
	private Bitmap Bytes2Bitmap(byte[] b, int w, int h)
	{            
		if(b.length!=0)
		{  
			return MyBitmapFactory.createMyBitmap(b, w, h);
		}
		else 
		{  
			return null;  
		}  	
	}

	@SuppressLint("NewApi")
	private static Point findBestPreviewSizeValue(Camera.Parameters parameters,
			Point screenResolution, boolean portrait) {
		List<Camera.Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG,
					"Device returned no supported preview sizes; using default");
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size a, Camera.Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		if (Log.isLoggable(TAG, Log.INFO)) {
			StringBuilder previewSizesString = new StringBuilder();
			for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
				previewSizesString.append(supportedPreviewSize.width)
						.append('x').append(supportedPreviewSize.height)
						.append(' ');
			}
		//	Log.i(TAG, "Supported preview sizes: " + previewSizesString);
		}

		Point bestSize = null;
		float screenAspectRatio = (float) screenResolution.x
				/ (float) screenResolution.y;

		float diff = Float.POSITIVE_INFINITY;
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
				continue;
			}
			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;
			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(TAG, "Found preview size exactly matching screen size: "
						+ exactPoint);
				return exactPoint;
			}
			float aspectRatio = (float) maybeFlippedWidth
					/ (float) maybeFlippedHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
			Log.i(TAG, "No suitable preview sizes, using default: " + bestSize);
		}

		Log.i(TAG, "Found best approximate preview size: " + bestSize);
		return bestSize;
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		th.start();
		init_Camera();
	}

	@SuppressLint("NewApi")
	private void init_Camera() {
		if (!isPreview) {
			camera = Camera.open();
		}
		if (camera != null && !isPreview) {
			try {
				
				Camera.Parameters parameters = camera.getParameters();
				
	
				screenResolution = new Point(screenWidth, screenHeight);
				//Log.i(TAG, "Screen resolution: " + screenResolution);
				cameraResolution = findBestPreviewSizeValue(parameters,
						screenResolution, false);
		//		Log.i(TAG, "Camera resolution: " + cameraResolution);
				
				parameters.setPreviewSize(cameraResolution.x, cameraResolution.y); // 设置预览照片的大小
				parameters.setPreviewFrameRate(15);
			//	parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
				
			//	parameters.setFlashMode(Parameters.FLASH_MODE_ON);
				try
				{
					camera.setParameters(parameters); // android2.3.3以后不需要此行代码
				}
				catch (RuntimeException ex)
				{
					Log.i(TAG, "camera.setParameters exception  " + ex.getMessage());
				}
				
				camera.setDisplayOrientation(0);
				camera.setPreviewDisplay(sfh); // 通过SurfaceView显示取景画面
		
				camera.autoFocus(mAutoFocusCallBack); // 自动对焦这个方法是在两者之间，startPreview与stop之间使用
				camera.setPreviewCallback(previewCallback);
				camera.startPreview(); // 开始预览
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

	public void takePoto()
	{
		if(isFocus)
		{
			isshoot=true;
			camera.autoFocus(mAutoFocusCallBack);
		}
		else
		{
			takePicture();
		}
	}
	
	public void takePicture() {
		if (camera != null) {
	
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return true;
	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Shutter has closed
		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
		
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle JPEG image data

		/*	Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
			Matrix matrix = new Matrix();

			matrix.setRotate(0);

			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);

			File myCaptureFile = new File(strCaptureFilePath + count_img + ".jpg");
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

				bos.flush();

				bos.close();

			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			} */
		}
	};


	private boolean checkSDCard() {
		/* 耞癘拘琌 */
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void surfaceChanged1(SurfaceHolder surfaceholder, int format, int w,
			int h) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Surface Changed");
	}

	public void draw()
	{
		if(bmpProcessed != null)
		{
			canvas = sfh.lockCanvas();
			if(canvas == null)
			{
				return;
			}
			Log.i(TAG, "after lockCanvas");
			canvas.drawBitmap(bmpProcessed, 0, 0, null);
			sfh.unlockCanvasAndPost(canvas); 
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		while (shouldContinue) {
			//draw();
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
		camera.setPreviewCallback(null);
		camera.stopPreview();  
		camera.release();
		camera = null;
		this.shouldContinue = false;
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	class CameraTimerTask extends TimerTask {
		@Override
		public void run() {
			if (camera != null) {
				
				if(mFilterSurfaceView == null)
				{
					mFilterSurfaceView = FilterCameraActivity.getFilterSurfaceView();
				
					mFilterSurfaceView.doDraw(bmpProcessed, rectROI);
				}
				else
				{
					mFilterSurfaceView.doDraw(bmpProcessed, rectROI);
				}
			//	Log.i(TAG, "run");
			//	camera.autoFocus(mAutoFocusCallBack);
			//	camera.setPreviewCallback(previewCallback);
				
			}
		}
	}
}
