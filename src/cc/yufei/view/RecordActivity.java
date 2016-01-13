package cc.yufei.view;


/**
 * 语音模块完成
 * 
 * 最大限度的模仿啪啪和微信的效果，部分代码还需优化
 * 
 * @author  Duxiaoqiang
 * @date 2013-8-15
 */
import java.io.File;
import java.io.IOException;

import cc.yufei.util.AudioRecorder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 *  录音的Activity
 */
public class RecordActivity extends Activity
{

	private Button record;
	private Dialog dialog;
	private AudioRecorder mr;
	private MediaPlayer mediaPlayer;
	//Button player;
	TextView luyin_path;
	private Thread recordThread;
	
	private static int MAX_TIME = 10;    //最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1;     //最短录制时间，单位秒，0为无时间限制，建议设为1
	
	private static int RECORD_NO = 0;  //不在录音
	private static int RECORD_ING = 1;   //正在录音
	private static int RECODE_ED = 2;   //完成录音
	
	private static int RECODE_STATE = 0;      //录音的状态
	
	private static float recodeTime=0.0f;    //录音的时间
	private static double voiceValue=0.0;    //麦克风获取的音量值
	
	private ImageView dialog_img;
	private static boolean playState = false;  //播放状态
	
	
	private String audioPath;//audio存放的路径
	
	private String videoName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yl_record_audio);
		//player = (Button) findViewById(R.id.player);
		luyin_path = (TextView)findViewById(R.id.luyin_path);
		videoName="voice"+System.currentTimeMillis();
		
		//播放
		/*player.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!playState) {
					mediaPlayer = new MediaPlayer();
					String url = "file:///sdcard/my/voice.amr";
					try
					{
						//模拟器里播放传url，真机播放传getAmrPath()
						mediaPlayer.setDataSource(url);
						//mediaPlayer.setDataSource(getAmrPath());
						mediaPlayer.prepare();
						mediaPlayer.start();
						player.setText(getString(R.string.playing));
						playState=true;
						//设置播放结束时监听
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								if (playState) {
									player.setText(getString(R.string.play_vidce));
									playState=false;
								}
							}
						});
					}
					catch (IllegalArgumentException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IllegalStateException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}else {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						playState=false;
					}else {
						playState=false;
					}
					player.setText(getString(R.string.paly_audio));
				}
				

			}
		});*/
		record = (Button) this.findViewById(R.id.record);

		//录音
		record.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					if (RECODE_STATE != RECORD_ING) {
						//scanOldFile();		
						mr = new AudioRecorder(videoName);
						RECODE_STATE=RECORD_ING;
						showVoiceDialog();
						try {
							mr.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mythread();
					}
					
					
					break;
				case MotionEvent.ACTION_UP:
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE=RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						try {
								mr.stop();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}
								
								if (recodeTime < MIX_TIME) {
									showWarnToast();
									record.setText(getString(R.string.press_start_recoding));
									RECODE_STATE=RECORD_NO;
								}else{
								 record.setText(getString(R.string.recoding_complete));
								 luyin_path.setText(getString(R.string.recoding_file_path)+"："+getAmrPath());
								 audioPath=getAmrPath();
								 Intent intent=new Intent();
								 intent.putExtra("filepath", audioPath);
								 setResult(21,intent);
								 RecordActivity.this.finish();
								}
					}

					break;
				}
				return false;
			}
		});
	}
	
	
	//录音时显示Dialog
	void showVoiceDialog(){
		dialog = new Dialog(RecordActivity.this,R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img=(ImageView)dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}
	
	//录音时间太短时Toast显示
	void showWarnToast(){
		Toast toast = new Toast(RecordActivity.this);
		 LinearLayout linearLayout = new LinearLayout(RecordActivity.this);
		 linearLayout.setOrientation(LinearLayout.VERTICAL); 
		 linearLayout.setPadding(20, 20, 20, 20);
		
		// 定义一个ImageView
		 ImageView imageView = new ImageView(RecordActivity.this);
		 imageView.setImageResource(R.drawable.voice_to_short); // 图标
		 
		 TextView mTv = new TextView(RecordActivity.this);
		 mTv.setText(getString(R.string.recoding_error));
		 mTv.setTextSize(14);
		 mTv.setTextColor(Color.WHITE);//字体颜色
		 //mTv.setPadding(0, 10, 0, 0);
		 
		// 将ImageView和ToastView合并到Layout中
		 linearLayout.addView(imageView);
		 linearLayout.addView(mTv);
		 linearLayout.setGravity(Gravity.CENTER);//内容居中
		 linearLayout.setBackgroundResource(R.drawable.record_bg);//设置自定义toast的背景
		 
		 toast.setView(linearLayout); 
		 toast.setGravity(Gravity.CENTER, 0,0);//起点位置为中间     100为向下移100dp
		 toast.show();				
	}
	
	//获取文件手机路径
	private String getAmrPath(){
		
		File file = new File(Environment  
                .getExternalStorageDirectory(), "YFWatermark/audio/"+videoName+".amr");
		return file.getAbsolutePath();
	}
	
	//录音计时线程
	void mythread(){
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}
	
	//录音Dialog图片随声音大小切换
	void setDialogImage(){
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		}else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		}else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		}else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		}else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		}else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		}else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		}else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		}else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		}else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		}else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		}else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		}else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		}else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}
	
	//录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE==RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				}else{
				try {
					Thread.sleep(200);
					recodeTime += 0.2;
					if (RECODE_STATE == RECORD_ING) {
						voiceValue = mr.getAmplitude();
						imgHandle.sendEmptyMessage(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
            
				switch (msg.what) {
				case 0:
					//录音超过60秒自动停止并且返回
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE=RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						try {
								mr.stop();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}
								
								if (recodeTime < 1.0) {
									showWarnToast();
									record.setText(getString(R.string.press_start_recoding));
									RECODE_STATE=RECORD_NO;
								}else{
								 record.setText(getString(R.string.recoding_complete));
								 luyin_path.setText(getString(R.string.recoding_file_path)+"："+getAmrPath());
								 audioPath=getAmrPath();
								 Intent intent=new Intent();
								 intent.putExtra("filepath", audioPath);
								 setResult(21,intent);
								 RecordActivity.this.finish();
								}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}
				
			}
		};
	};
}
