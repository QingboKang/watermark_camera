package cc.yufei.watermarkfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FilterImageView extends SurfaceView implements SurfaceHolder.Callback {

	protected SurfaceHolder sh; // 专门用于控制surfaceView的
	private int width;
	private int height;
	
	public static int count_draw_time = 0;
	
	// draw rectangle 
	private Rect rectDraw;
	
	private String TAG = FilterImageView.class.getSimpleName();

	private Canvas canvas;
	
	// XML文件解析需要调用View的构造函数View(Context , AttributeSet)
	// 因此自定义SurfaceView中也需要该构造函数
	public FilterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.i(TAG, "enter constru");
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT); // 设置为透明
		setZOrderOnTop(true);// 设置为顶端
	}

	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
		// TODO Auto-generated method stub
		width = w;
		height = h;
		
		Log.i( TAG, "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "surfaceDestroyed");
	}

	void clearDraw() {

		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.BLUE);// 清除画布
		sh.unlockCanvasAndPost(canvas);
	}

	/**
	 * 绘制
	 */
	public void doDraw(Bitmap bmp, Rect dstRect) {
		
		if (bmp != null) {

			if(sh == null)
			{
				Log.i(TAG, "sh is null");
			}
			else
			{
			//	Log.i(TAG, "sh is not null");
			}
			if(canvas == null)
			{
			//	Log.i(TAG, "canvas is null");
				return;
			}
			
			canvas.drawColor(Color.TRANSPARENT);// 这里是绘制背景
			Paint p = new Paint(); // 笔触
			// clear before drawing
			p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(p);
			p.setXfermode(new PorterDuffXfermode(Mode.SRC));

			p.setColor(Color.RED);
			p.setTextSize((float) 50.0);
		//	p.setStyle(Style.STROKE);

			if(rectDraw == null)
			{
				width = this.getWidth();
				height = this.getHeight();
				rectDraw = new Rect( (int)(width * MySurfaceView.szFilterImageRatio[0] ), (int)(height * MySurfaceView.szFilterImageRatio[1] ) , 
									 (int)(width * MySurfaceView.szFilterImageRatio[2] ), (int)(height * MySurfaceView.szFilterImageRatio[3] ) );
			}
			canvas.drawBitmap(bmp, null, rectDraw,  p);

			canvas.drawText(count_draw_time + " ", 100, 100, p);
		//	Log.i(TAG, "draw" + bmp);

			// 提交绘制
			sh.unlockCanvasAndPost(canvas); 

		//	Log.i(TAG, "draw time: " + (end - start) + " ms");
		}

	}

}
