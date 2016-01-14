package cc.yufei.view;


/**
 * ����ģ�����
 * 
 * ����޶ȵ�ģ��žž��΢�ŵ�Ч�������ִ��뻹���Ż�
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
 *  ¼����Activity
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
	
	private static int MAX_TIME = 10;    //�¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static int MIX_TIME = 1;     //���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1
	
	private static int RECORD_NO = 0;  //����¼��
	private static int RECORD_ING = 1;   //����¼��
	private static int RECODE_ED = 2;   //���¼��
	
	private static int RECODE_STATE = 0;      //¼����״̬
	
	private static float recodeTime=0.0f;    //¼����ʱ��
	private static double voiceValue=0.0;    //��˷��ȡ������ֵ
	
	private ImageView dialog_img;
	private static boolean playState = false;  //����״̬
	
	
	private String audioPath;//audio��ŵ�·��
	
	private String videoName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yl_record_audio);
		//player = (Button) findViewById(R.id.player);
		luyin_path = (TextView)findViewById(R.id.luyin_path);
		videoName="voice"+System.currentTimeMillis();
		
		//����
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
						//ģ�����ﲥ�Ŵ�url��������Ŵ�getAmrPath()
						mediaPlayer.setDataSource(url);
						//mediaPlayer.setDataSource(getAmrPath());
						mediaPlayer.prepare();
						mediaPlayer.start();
						player.setText(getString(R.string.playing));
						playState=true;
						//���ò��Ž���ʱ����
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

		//¼��
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
								 luyin_path.setText(getString(R.string.recoding_file_path)+"��"+getAmrPath());
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
	
	
	//¼��ʱ��ʾDialog
	void showVoiceDialog(){
		dialog = new Dialog(RecordActivity.this,R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img=(ImageView)dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}
	
	//¼��ʱ��̫��ʱToast��ʾ
	void showWarnToast(){
		Toast toast = new Toast(RecordActivity.this);
		 LinearLayout linearLayout = new LinearLayout(RecordActivity.this);
		 linearLayout.setOrientation(LinearLayout.VERTICAL); 
		 linearLayout.setPadding(20, 20, 20, 20);
		
		// ����һ��ImageView
		 ImageView imageView = new ImageView(RecordActivity.this);
		 imageView.setImageResource(R.drawable.voice_to_short); // ͼ��
		 
		 TextView mTv = new TextView(RecordActivity.this);
		 mTv.setText(getString(R.string.recoding_error));
		 mTv.setTextSize(14);
		 mTv.setTextColor(Color.WHITE);//������ɫ
		 //mTv.setPadding(0, 10, 0, 0);
		 
		// ��ImageView��ToastView�ϲ���Layout��
		 linearLayout.addView(imageView);
		 linearLayout.addView(mTv);
		 linearLayout.setGravity(Gravity.CENTER);//���ݾ���
		 linearLayout.setBackgroundResource(R.drawable.record_bg);//�����Զ���toast�ı���
		 
		 toast.setView(linearLayout); 
		 toast.setGravity(Gravity.CENTER, 0,0);//���λ��Ϊ�м�     100Ϊ������100dp
		 toast.show();				
	}
	
	//��ȡ�ļ��ֻ�·��
	private String getAmrPath(){
		
		File file = new File(Environment  
                .getExternalStorageDirectory(), "YFWatermark/audio/"+videoName+".amr");
		return file.getAbsolutePath();
	}
	
	//¼����ʱ�߳�
	void mythread(){
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}
	
	//¼��DialogͼƬ��������С�л�
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
	
	//¼���߳�
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
					//¼������60���Զ�ֹͣ���ҷ���
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
								 luyin_path.setText(getString(R.string.recoding_file_path)+"��"+getAmrPath());
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
