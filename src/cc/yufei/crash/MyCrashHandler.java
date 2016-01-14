package cc.yufei.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

public class MyCrashHandler implements UncaughtExceptionHandler {
	// ��֤MyCrashHandlerֻ��һ��ʵ��
	// 2.�ṩһ����̬�ĳ������
	private static MyCrashHandler myCrashHandler; 
	private Context context;

	// 1.˽�л����췽��
	private MyCrashHandler() {

	}

	// 3.��¶����һ����̬�ķ��� ��ȡmyCrashHandler

	public static synchronized MyCrashHandler getInstance() {
		if (myCrashHandler == null) {
			myCrashHandler = new MyCrashHandler();
		}
		return myCrashHandler;
	}

	public void init(Context context) {
		this.context = context;
	}

	// �������쳣��ʱ����õķ���
	// try catch

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		// 1.��ȡ��ǰӦ�ó���İ汾��.
		PackageManager pm = context.getPackageManager();
		try {
			System.out.println("111");
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(),
					0);
			sb.append("����İ汾��Ϊ" + packinfo.versionName);
			sb.append("\n");

			// 2.��ȡ�ֻ���Ӳ����Ϣ.
			Field[] fields = Build.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				// ��������,��ȡ˽�е��ֶ���Ϣ
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				sb.append(name + " = ");
				String value = fields[i].get(null).toString();
				sb.append(value);
				sb.append("\n");
			}
			System.out.println("sb is "+sb.toString());
			// 3.��ȡ�������Ķ�ջ��Ϣ .
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			
		    String result =	writer.toString();
		    sb.append(result);
		    System.out.println(result);
		    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    {   
		    	String path="/mnt/sdcard/error.txt";
		    	File file=new File(path);
		    	if(file.exists())
		    	{
		    		file.delete();
		    	}
		    	OutputStream out=new FileOutputStream(file);
		    	out.write(sb.toString().getBytes());
		    }

			// 4.�Ѵ�����Ϣ �ύ��������
		    
		} catch (Exception e) {
			e.printStackTrace();
		}

		// �����ɱ�Ĳ���
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
