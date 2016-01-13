package cc.yufei.watermarkfilter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;

public class MyBitmapFactory {
	
	/*
	 * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
	 */
	static public Bitmap createMyBitmap(byte[] data, int width, int height){	
		int []colors = convertByteToColor(data);
		if (colors == null){
			return null;
		}
			
		Bitmap bmp = null;

		try {
			bmp = Bitmap.createBitmap(colors, 0, width, width, height, 
					Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			// TODO: handle exception
	
			return null;
		}
						
		return bmp;
	}

	
	/*
	 * 将RGB数组转化为像素数组
	 */
	private static int[] convertByteToColor(byte[] data){
		int size = data.length;
		if (size == 0){
			return null;
		}
		
		
		// 理论上data的长度应该是3的倍数，这里做个兼容
		int arg = 0;
		if (size % 3 != 0){
			arg = 1;
		}
		
		int []color = new int[size / 3 + arg];
		int red, green, blue;
		
		
		if (arg == 0){									//  正好是3的倍数
			for(int i = 0; i < color.length; ++i){
		
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
						   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
						   (data[i * 3 + 2] & 0x000000FF ) | 
						    0xFF000000;
			}
		}else{										// 不是3的倍数
			for(int i = 0; i < color.length - 1; ++i){
				color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
				   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
				   (data[i * 3 + 2] & 0x000000FF ) | 
				    0xFF000000;
			}
			
			color[color.length - 1] = 0xFF000000;					// 最后一个像素用黑色填充
		}
	
		return color;
	}
	
	/** 
     * 将彩色图转换为灰度图 
     * @param img 位图 
     * @return  返回转换好的位图 
     */  
    public static Bitmap convertGreyImg(Bitmap img) {  
        int width = img.getWidth();         //获取位图的宽  
        int height = img.getHeight();       //获取位图的高  
          
        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组  
          
        img.getPixels(pixels, 0, width, 0, 0, width, height);  
        int alpha = 0xFF << 24;   
        for(int i = 0; i < height; i++)  
        {  
            for(int j = 0; j < width; j++) 
            {  
                int grey = pixels[width * i + j];  
                  
                int red = ((grey  & 0x00FF0000 ) >> 16);  
                int green = ((grey & 0x0000FF00) >> 8);  
                int blue = (grey & 0x000000FF);  
                
                	
            //    grey = diff; 
                grey = alpha | (blue << 16) | (blue << 8) | blue;  
             //   grey = blue;
                pixels[width * i + j] = grey;  
            }  
        }  
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);  
        result.setPixels(pixels, 0, width, 0, 0, width, height);  
        return result;  
    }  
    
    /**
     * 解码
     * @param rgb
     * @param yuv420sp
     * @param width
     * @param height
     */
    public static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height)
    {
    	final int frameSize = width * height;
    	for (int j = 0, yp = 0; j < height; j++)
    	{
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++)
    		{
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0)
    				y = 0;
    			if ((i & 1) == 0) 
    			{
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			if (r < 0)
    				r = 0;
    			else if (r > 262143)
    				r = 262143;
    			if (g < 0)
    				g = 0;
    			else if (g > 262143)
    				g = 262143;
    			if (b < 0)
    				b = 0;
    			else if (b > 262143)
    				b = 262143;
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}

    }
    

}
