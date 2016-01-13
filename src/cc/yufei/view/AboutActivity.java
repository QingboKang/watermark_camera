package cc.yufei.view;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity implements OnClickListener{

	private Button ButtonHelp;
	private Button ButtonCopyright;
	private Intent intent;
	private String LOG_TAG = "samsung_info";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		ButtonHelp = (Button)findViewById(R.id.btn_help);
		ButtonCopyright = (Button)findViewById(R.id.btn_copyright);
		
		ButtonHelp.setOnClickListener(this);
		ButtonCopyright.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_help:
			intent = new Intent(this, HelpActivity.class);
			Log.i(LOG_TAG, "enter case btn_help.");
			startActivity(intent);
			break;
			
		case R.id.btn_copyright:
			intent = new Intent(this, CopyrightActivity.class);
			startActivity(intent);
			break;
		}
	}
	


}
