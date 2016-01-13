#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <opencv/highgui.h>
using namespace cv;

#include <android/log.h>

#define LOG "MySurfaceView_jni"
#define LOGD(...) __android_log_print(ANDROID_LOG_ERROR, LOG, __VA_ARGS__ )

extern "C" {

#define MR(Y,U,V) (1.164383 * (Y - 16 ) + (1.596027)*(V-128))
#define MG(Y,U,V) (1.164383 * (Y - 16 ) - (0.391762)*(U-128) - (0.812969)*(V-128) )
#define MB(Y,U,V) (1.164383 * (Y - 16 ) + (2.01723 * (U-128)))

/**
 * function: Convert YUV420sp(NV21) data to RGB ColorSpace data.
 * param: pYUV -- the pointer point to YUV420sp data
 * param: pRGB -- the pointer point to the RGB data, must be
                  allocated memory by caller. (datasize = 3*width*height)
 * param: height -- the height of the image.
 * param: width -- the width of the image.
 * return: 0 -- success.   others -- failed.
 */
int YUV420SP_C_BGR( char* pYUV, unsigned char* pRGB, int height, int width)
{
    if(!pRGB)
    {
        return -1;
    }
    char* pY = pYUV;
    char* pUV = pYUV + height*width;

    unsigned char* pBGR = NULL;
    unsigned char R = 0;
    unsigned char G = 0;
    unsigned char B = 0;
    unsigned char Y = 0;
    unsigned char Y1 = 0;
    unsigned char U = 0;
    unsigned char V = 0;
    double tmp = 0;
    for ( int i = 0; i < height; i += 2 )
    {
        for ( int j = 0; j < width; ++j )
        {
            pBGR = pRGB+ i*width*3 + j*3;

            Y = *(pY + i * width + j);
            Y1 = *(pY + (i+1) * width + j);
            V = *pUV;
            U = *(pUV + 1);

            //B
            tmp = MB(Y, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            B = (unsigned char)tmp;

            //G
            tmp = MG(Y, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            G = (unsigned char)tmp;
            //R
            tmp = MR(Y, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            R = (unsigned char)tmp;

            *pBGR     = B;
            *(pBGR+1) = G;
            *(pBGR+2) = R;

            // second
            //B
            tmp = MB(Y1, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            B = (unsigned char)tmp;
            //G
            tmp = MG(Y1, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            G = (unsigned char)tmp;
            //R
            tmp = MR(Y1, U, V);
            if(tmp > 255) tmp = 255;
            if(tmp < 0) tmp = 0;
            R = (unsigned char)tmp;

            *(pBGR + width * 3) = B;
            *(pBGR + width * 3 + 1) = G;
            *(pBGR + width * 3 + 2) = R;

            if( (j+1)%2 == 0)
            {
                pUV += 2;
            }
        }
    }
    return 0;
}

char* jstringTostring(JNIEnv* env, jstring jstr)
{
       char* rtn = NULL;
       jclass clsstring = env->FindClass("java/lang/String");
       jstring strencode = env->NewStringUTF("utf-8");
       jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
       jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
       jsize alen = env->GetArrayLength(barr);
       jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
       if (alen > 0)
       {
                 rtn = (char*)malloc(alen + 1);
                 memcpy(rtn, ba, alen);
                 rtn[alen] = 0;
       }
       env->ReleaseByteArrayElements(barr, ba, 0);
       return rtn;
}

int WriteToBinaryFile(char * filePath, char * pData, int iDataLength)
{
	FILE* fp = fopen( filePath, "wb+");
	if(fp == NULL)
	{
		return -1;
	}

	fwrite( pData, sizeof(char), iDataLength, fp );

	fclose(fp);

	return 0;
}

JNIEXPORT jint JNICALL Java_cc_yufei_watermarkfilter_LibWatermarkFilter_Filter(
         JNIEnv* env, jobject obj, jbyteArray buf, int buflen, jbyteArray filterbytes, int w, int h,
         jstring strImgFilePath );
         
JNIEXPORT jint JNICALL Java_cc_yufei_watermarkfilter_LibWatermarkFilter_Filter(
         JNIEnv* env, jobject obj, jbyteArray buf, int buflen, jbyteArray filterbytes, int w, int h,
         jstring strImgFilePath )
{
	jbyte *cbuf;
	cbuf = env->GetByteArrayElements(buf, false);

    Mat myimg(h, w, CV_8UC1, (unsigned char *)cbuf);

	CvMat cvsrcMat = myimg;
	IplImage* pImgGray = cvCreateImage(cvSize(w,h),8, 1);
   
	cvGetImage(&cvsrcMat, pImgGray);

    char* pch_path1 = jstringTostring(env, strImgFilePath);
	 
    IplImage * img_ARGB = cvCreateImage( cvGetSize(pImgGray), pImgGray->depth, 4 );

    // equalize histogram
    cvEqualizeHist(pImgGray, pImgGray);

    cvCvtColor(pImgGray, img_ARGB, CV_GRAY2RGBA);
    
   // jbyte* pjbImgData = (jbyte*)img_ARGB->imageData;
    int iImgDataLen = w * h * 4;
	env->SetByteArrayRegion( filterbytes, 0, iImgDataLen, (jbyte*)img_ARGB->imageData);
	
	cvReleaseImage(&pImgGray);
	cvReleaseImage(&img_ARGB);
	free(cbuf);
	myimg.release();

    return 0;
}         


}
