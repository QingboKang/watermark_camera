package cc.yufei.util;

import java.io.BufferedOutputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  

import cc.yufei.view.SysApplication;
  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.Matrix;  
import android.graphics.YuvImage;
import android.util.Log;
  
public class ImageUtil {  
      
      
      
    /** 
     * @param ��ͼƬ���ݽ������ֽ����� 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception {  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        while ((len = inStream.read(buffer)) != -1) {  
            outStream.write(buffer, 0, len);  
        }  
        byte[] data = outStream.toByteArray();  
        outStream.close();  
        inStream.close();  
        return data;  
  
    }  
    /** 
     * @param ���ֽ�����ת��ΪImageView�ɵ��õ�Bitmap���� 
     * @param bytes 
     * @param opts 
     * @return Bitmap 
     */  
    public static Bitmap getPicFromBytes(byte[] bytes,  
            BitmapFactory.Options opts) {  
        if (bytes != null)  
            if (opts != null)  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,  
                        opts);  
            else  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
        return null;  
    }  
    /** 
     * @param ͼƬ���� 
     * @param bitmap ���� 
     * @param w Ҫ���ŵĿ�� 
     * @param h Ҫ���ŵĸ߶� 
     * @return newBmp �� Bitmap���� 
     */  
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  

        Matrix matrix = new Matrix();  
        float scaleWidth = ((float) w / width);  
        float scaleHeight = ((float) h / height);
        //������ű���
        SysApplication.scaleWidth=scaleWidth;
        SysApplication.scaleHeight=scaleHeight;
        matrix.postScale(scaleWidth, scaleHeight);  
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,  
                matrix, true);  
        return newBmp;  
    }  
      
    /** 
     * ��BitmapתByte 
     * @Author HEH 
     * @EditTime 2010-07-19 ����11:45:56 
     */  
    public static byte[] Bitmap2Bytes(Bitmap bm){  
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
       
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }  
    /** 
     * ���ֽ����鱣��Ϊһ���ļ� 
     * @Author HEH 
     * @EditTime 2010-07-19 ����11:45:56 
     */  
    public static File getFileFromBytes(byte[] b, String outputFile) {  
        BufferedOutputStream stream = null;  
        File file = null;  
        try {  
            file = new File(outputFile);  
            FileOutputStream fstream = new FileOutputStream(file);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
        return file;  
    }  
          
}  
