package cc.yufei.view;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.widget.TextView;

public class CopyrightActivity extends Activity {

	private TextView tvAboutAppName;
	private TextView tvAboutAppContent;
	private TextView tvAboutAppCopyright;
	private TextView tvAboutAppCopyrightEng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copyright);
		
		tvAboutAppName = (TextView)findViewById(R.id.tvAboutAppName);
		tvAboutAppContent = (TextView)findViewById(R.id.tvAboutAppContent);
		tvAboutAppCopyright = (TextView)findViewById(R.id.tvAboutAppCopyright);
		tvAboutAppCopyrightEng = (TextView)findViewById(R.id.tvAboutAppCopyrightEng);
		
		String strAppNameVer = getApplicationName() + "(Android)" + " " + getAppVersion();
		tvAboutAppName.setText(strAppNameVer);
		
		tvAboutAppContent.setText(getApplicationName() + " " + this.getString(R.string.aboutapp_content));
		tvAboutAppCopyright.setText("宇飞公司  版权所有");
		tvAboutAppCopyrightEng.setText("Copyright © 2000 - 2014 Yufei. All Rights Reserved.");
	}
	
	public String getApplicationName() 
	{ 
		PackageManager packageManager = null; 
		ApplicationInfo applicationInfo = null; 
		try
		{ 
			packageManager = getApplicationContext().getPackageManager(); 
			applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0); 
		}
		catch (PackageManager.NameNotFoundException e)
		{ 
			applicationInfo = null; 
		} 
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo); 
		return applicationName; 
	} 
	
	public static String ToDBC(String input) 
	{  
		   char[] c = input.toCharArray();  
		   for (int i = 0; i< c.length; i++) {  
		       if (c[i] == 12288) {  
		         c[i] = (char) 32;  
		         continue;  
		       }if (c[i]> 65280&& c[i]< 65375)  
		          c[i] = (char) (c[i] - 65248);  
		       }  
		   return new String(c);  
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
