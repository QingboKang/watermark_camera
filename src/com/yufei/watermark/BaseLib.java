package com.yufei.watermark;

import java.io.File;
import java.security.acl.LastOwnerException;

import android.util.Log;

public class BaseLib {
	
	static 
	{
		System.loadLibrary("WatermarkBaseLib");
	}
	
	private static String GetDirPath(String strFullPath)
	{
		int end1 = strFullPath.lastIndexOf('/');
		int end2 = strFullPath.lastIndexOf('\\');
		
		int end;
		if(end1 != -1)
		{
			end = end1;
		}
		else if(end2 != -1)
		{
			end = end2;
		}
		else 
		{
			return null;
		}
		
		return strFullPath.substring(0, end + 1);
	}
	
	/**
	 * Add mark files to carrier JPEG file.
	 * 
	 * @param strCarrierFile  
	 *             The absolute path of the carrier JPEG file(should be end with .jpg)
	 * @param strMarkFiles
	 *             The absolute paths of all the marked files. 
	 * @param strDstFile
	 *             The absolute path of the destination JPEG file.(should be end with .jpg)
	 * @return
	 *             < 0 means failed. ( -5 the Carrier file's type is not satisfied; 
	 *                                 -4 the Carrier file already contain watermark files;
	 *                                 -2 pack all the files failed;
	 *                                 -1 add file to carrier jpeg file failed. ) 
	 *             others means success. The return value also is the marked file count. 
	 */
	public static int AddFilesToJpegFile(String strCarrierFile, String[] strMarkFiles, String strDstFile)
	{
		// First, check the carrier file to see if it satisfied.
		if(CheckFileType(strCarrierFile) == 1)
		{
			return -5;
		}
		
		// Second, check the carrier file to see if it already contain watermark files.
		if( CheckMarkedness(strCarrierFile) == 1)
		{
			return -4;
		}
		
		// Pack all the mark files into .npk file
		String strDir = GetDirPath(strCarrierFile);
		if(strDir == null)
		{
			return -3;
		}
		String strTempNPKFile = strDir + "YFMARK_markfiles.npk";
		int iPackReturn = Pack(strMarkFiles, strTempNPKFile);
		
		// The iPackReturn should be equal with the number of marked files.
		if(iPackReturn != strMarkFiles.length)
		{
						
			return -2;
		}
		
		// Add .npk file into carrier JPEG file
		int iSaveMarkReturn = SaveMarkToFile(strCarrierFile, strTempNPKFile, strDstFile);
		if(iSaveMarkReturn != 0)
		{
			return -1;
		}
		// At last, delete the temporary .npk file
				File tempNPKFile = new File(strTempNPKFile);
				if(tempNPKFile.exists())
				{
					tempNPKFile.delete();
				}

		return iPackReturn;
	}
	
	/**
	 * Get mark files from carrier JPEG file.
	 * 
	 * @param strCarrierFile
	 *            The absolute path of the carrier JPEG file(should be end with .jpg)
	 * @param strFilesDir
	 *            The absolute path of the directory that will save all the mark files.
	 *            The directory SHOULD BE create by caller.
	 * @return 
	 *            < 0 means failed. ( -3 the carrier JPEG file doesn't contain watermark; 
	 *                                -2 get mark files from carrier JPEG file failed; 
	 *                                -1 unpack npk file failed; )
	 *            others means success. (the return value is the number of the mark files.)               
	 */
	public static int GetFilesFromJpegFile(String strCarrierFile, String strFilesDir)
	{
		// First, check the JPEG file to see if it contains watermark
		if( CheckMarkedness(strCarrierFile) != 1)
		{
			return -3;
		}
		
		// Second, get pack file(.npk) from carrier jpeg file.
		String strTempNPKFile = strFilesDir + "YFMARK_markfiles.npk";
		int igetReturn = GetMarkFromFile(strCarrierFile, strTempNPKFile);
		if(igetReturn != 0)
		{
			return -2;
		}
		
		// Third, unpack files from .npk file and save them to strFilesDir
		int iUnPackReturn = UnPack(strTempNPKFile, strFilesDir);
		if(iUnPackReturn <= 0)
		{
			return -1;
		}
		// At last, delete the temporary .npk file
			File tempNPKFile = new File(strTempNPKFile);
			if(tempNPKFile.exists())
			{
				tempNPKFile.delete();
			}

		return iUnPackReturn;
	}

	/**
	 * Get the version of the base library.
	 * 
	 * @return  version
	 */
	public static native int GetLibraryVersion();
	
	/**
	 * Check the file to see if it satisfied the requirement.
	 * 
	 * @param strImgFilePath
	 *           The absolute path of the image(at now is .JPG file)
	 * @return  0 means satisfied.   1 means NOT satisfied.
	 */
	public static native int CheckFileType(String strImgFilePath);
	
	/**
	 * Check if the file contains the watermark or not.
	 * 
	 * @param strImgFilePath 
	 *        	  The absolute path of the image(at now is .JPG file)
	 * @return  1 if it contains watermark.   0 if it doesn't contain watermark.
	 */
	public static native int CheckMarkedness(String strImgFilePath);
	
	/**
	 * Write watermark file to carrier image.(.jpg)
	 * 
	 * @param strCarrierFilePath 
	 * 			  The absolute path of the carrier image.
	 * @param strMarkFilePath
	 *        	  The absolute path of the mark file.
	 * @param strDstFilePath
	 *            
	 * @return  0 means success.  1 means failed.
	 */
	public static native int SaveMarkToFile(String strCarrierFilePath, String strMarkFilePath, String strDstFilePath);
	
	/**
	 * Extract Watermark file from carrier image.(.jpg)
	 * 
	 * @param strCarrierFilePath
	 *             The absolute path of the carrier image.
	 * @param strMarkFilePath
	 *             The absolute path of the output mark file.
	 * @return  0 means success.   1 means failed.
	 */
	public static native int GetMarkFromFile(String strCarrierFilePath, String strMarkFilePath);

	/**
	 * Pack a series of files into .npk file.
	 * 
	 * @param strPaths 
	 *            The absolute paths of the all files.
	 * @param strNPKFilePath
	 *            The packed file's absolute path(should be end with ".npk" or ".NPK")  
	 * @return    -1 means failed.
	 *            others(>0) Also is the file that packed in the .npk files' count. 
	 */
	public static native int Pack(String [] strPaths, String strNPKFilePath);
	
	/**
	 * UnPack a series of files from .npk file.
	 * 
	 * @param strNPKFilePath
	 *             The absolute path of .npk file.
	 * @param strUnPackDir
	 *             The absolute directory that the files will unpack to.
	 * @return     -1 means failed.
	 *              others(>=0) is the file count that unpack from the .npk file.
	 */
	public static native int UnPack(String strNPKFilePath, String strUnPackDir);
}
