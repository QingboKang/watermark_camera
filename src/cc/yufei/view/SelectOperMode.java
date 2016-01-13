package cc.yufei.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.yufei.util.DirectoryConfig;
import cc.yufei.view.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import cc.yufei.watermarkfilter.*;

/**
 * 该类实现嵌入和提取的选择
 * @author Administrator
 *
 */
public class SelectOperMode extends Activity implements OnClickListener {
    
	private Button bt_embed,bt_extract, bt_about, bt_filter;
	private Intent intent;
	private String LOG_TAG = "samsung_info";

	// 照片 文件
	private File tempFile;
	private static final int CAMERA_TAKE_PHOTO = 1;
	// for Intent name
	public static final String Intent_PhotoPath = "OriginalPhoto";
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.yl_opermode);

		bt_embed=(Button)findViewById(R.id.bt_embed);
		bt_extract=(Button)findViewById(R.id.bt_extract);
		bt_about = (Button)findViewById(R.id.bt_about);
		bt_filter = (Button)findViewById(R.id.bt_filter);
		
		bt_embed.setOnClickListener(this);
		bt_extract.setOnClickListener(this);
		bt_about.setOnClickListener(this);
		bt_filter.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_embed:
			intent=new Intent(this,MainActivity.class);
			intent.putExtra("mode", "embed");
			startActivity(intent);
			break;
		case R.id.bt_extract:
			intent=new Intent(this,MainActivity.class);
			intent.putExtra("mode", "extract");
			startActivity(intent);
			break;
			
			// 偏光水印提取 (图像滤镜)
		case R.id.bt_filter:
			Intent camIntent = new Intent(this, FilterCameraActivity.class);
			Log.i(LOG_TAG, "before startActivity");
			startActivity(camIntent);
			Log.i(LOG_TAG, "after startActivity");
			break;
		case R.id.bt_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i(LOG_TAG, "onActivityResult");
		if(RESULT_OK == resultCode)
		{
			if(CAMERA_TAKE_PHOTO == requestCode)
			{
				// 照片 文件存在
				if(tempFile.exists())
				{
					Log.i(LOG_TAG, "image file existed.");

				}
				// 照片 文件不存在
				else
				{
					Log.i(LOG_TAG, "image file does not existed.");
					return;
				}
			}
		}
	}
	private String getPhotoFileName()
	{
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'YufeiWatermark'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
}
