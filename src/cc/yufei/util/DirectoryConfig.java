package cc.yufei.util;

import android.os.Environment;

/**
 * This class defines the configuration of the work directory.
 * @author kangqingbo
 * 
 */
public class DirectoryConfig {
	
	/* Directory for generated image files */
	public static String GenerateImageDir = Environment.getExternalStorageDirectory() + "/YFWatermark/image/";
	
	/* Directory for temporary files */
	public static String TempFilesDir = Environment.getExternalStorageDirectory() + "/YFWatermark/temp/";
	 
	/* Directory for audio files */
	public static String AudioFilesDir = Environment.getExternalStorageDirectory() + "/YFWatermark/audio/";
	
	/* Directory for camera captured image files */
	public static String CaptureFilesDir = Environment.getExternalStorageDirectory() + "/YFWatermark/capture/";
	
	/* Directory for save filter image files */
	public static String FilterFilesDir = Environment.getExternalStorageDirectory() + "/YFWatermark/filter/";

}
