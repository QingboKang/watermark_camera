package cc.yufei.watermarkfilter;

import cc.yufei.view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FilterCameraActivity extends Activity {
	
	private MySurfaceView msurfaceView;  // 相机SurfaceView
	private static FilterImageView resultImageView;  // 滤镜之后的SurfaceView
	
	private String TAG = FilterCameraActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "before setContentView");
		setContentView(R.layout.layout_watermarkfilter);
		Log.i(TAG, "after setContentView");
	//	setContentView(new MyView(this));
		
		msurfaceView=(MySurfaceView)findViewById(R.id.mySurfaceView1);
		resultImageView = (FilterImageView)findViewById(R.id.resultImageView);
		
		resultImageView.setVisibility(View.VISIBLE);
		
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
	}
	
	public static FilterImageView getFilterSurfaceView()
	{
		return resultImageView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	

}
