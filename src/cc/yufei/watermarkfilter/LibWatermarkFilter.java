package cc.yufei.watermarkfilter;

public class LibWatermarkFilter {

	static 
	{
		System.loadLibrary("WatermarkFilter");
	}
	
	public static native int Filter( byte[] buf, int buflen, byte[] filterbytes, int w, int h,
	         String strImgFilePath );
}
