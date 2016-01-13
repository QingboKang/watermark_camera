
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include "JPEG/HideMsgJPEG.h"

#include <string.h>

#include "npk/npk.h"
#include "npk/npk_base.h"
#include "npk/npk_dev.h"
#include "npk/npk_conf.h"


char* yfGetFileName(char*pFileName)
{
    int len = strlen(pFileName);
    int i;
    for (i = len - 1; i >= 0; i--)
    {
        if (pFileName[i] == '/' || pFileName[i] == '\\')
        {
            return pFileName + i + 1;
        }
    }
    return NULL;
}

int CheckFileName(char * pFileName)
{
    if( strstr(pFileName, ".npk") == NULL &&
        strstr(pFileName, ".NPK") == NULL )
    {
        return -1;
    }
    return 1;
}

char* jstringTostring(JNIEnv* env, jstring jstr)
{
       char* rtn = NULL;
       jclass clsstring = (*env)->FindClass(env, "java/lang/String");
       jstring strencode = (*env)->NewStringUTF(env, "utf-8");
       jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
       jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid, strencode);
       jsize alen = (*env)->GetArrayLength(env, barr);
       jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
       if (alen > 0)
       {
                 rtn = (char*)malloc(alen + 1);
                 memcpy(rtn, ba, alen);
                 rtn[alen] = 0;
       }
       (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
       return rtn;
}

 /*
  *
  */
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_GetLibraryVersion(JNIEnv * env, jobject obj);
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_CheckFileType(JNIEnv * env, jobject obj, jstring strImgFilePath);
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_CheckMarkedness(JNIEnv * env, jobject obj, jstring strImgFilePath);
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_SaveMarkToFile(JNIEnv * env, jobject obj,
							jstring strCarrierFilePath, jstring strMarkFilePath, jstring strDstFilePath );
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_GetMarkFromFile(JNIEnv * env, jobject obj,
							jstring strCarrierFilePath, jstring strMarkFilePath );
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_Pack(JNIEnv * env, jobject obj, jobjectArray array, jstring strNPKFilePath);
	JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_UnPack(JNIEnv * env, jobject obj, jstring strNPKFilePath, jstring strUnPackDir);

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_GetLibraryVersion(JNIEnv * env, jobject obj)
{
	int version = get_library_version();

	return version;
}

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_CheckFileType(JNIEnv * env, jobject obj, jstring strImgFilePath)
{
	char * filename;
	filename = jstringTostring(env, strImgFilePath);

	if(filename == NULL)
	{
		return -1;
	}
	LOGD("filename: %s", filename);

	int n = check_file_type(filename);

	LOGD("check_file_type return: %d", n);

	return n;
}

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_CheckMarkedness(JNIEnv * env, jobject obj, jstring strImgFilePath)
{
	char * filename;
	filename = jstringTostring(env, strImgFilePath);

	if(filename == NULL)
	{
		return -1;
	}
	LOGD("filename: %s", filename);

	int n = check_markedness(filename);

	LOGD("check_markedness return: %d", n);

	return n;
}

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_SaveMarkToFile(JNIEnv * env, jobject obj,
						jstring strCarrierFilePath, jstring strMarkFilePath, jstring strDstFilePath )
{
	char * fileCarrier = jstringTostring(env, strCarrierFilePath);
	char * fileMark = jstringTostring(env, strMarkFilePath);
	char * dstfile = jstringTostring(env, strDstFilePath);

	LOGD("fileCarrier: %s", fileCarrier);
	LOGD("fileMark: %s", fileMark);
	LOGD("dstfile: %s", dstfile);

	int n = save_mark_to_file(fileCarrier, fileMark, dstfile);

	return n;
}

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_GetMarkFromFile(JNIEnv * env, jobject obj,
						jstring strCarrierFilePath, jstring strMarkFilePath )
{
	char * fileCarrier = jstringTostring(env, strCarrierFilePath);
	char * fileMark = jstringTostring(env, strMarkFilePath);

	int n = get_mark_from_file(fileCarrier, fileMark);

	return n;
}


JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_Pack(JNIEnv * env, jobject obj, jobjectArray array, jstring strNPKFilePath)
{
	// 字符串数组的长度
	int size = (*env)->GetArrayLength(env, array);

	LOGD("Length: %d", size);

	int teakey[4] = {1024, 2, 3, 4};   // 解密的时候使用
    NPK_PACKAGE pack;
    NPK_ENTITY entity;
    int status = npk_package_alloc(&pack, teakey); //在内存中分配一个npk文件.

    LOGD("status: %d", status);

   // npk_package_init(pack);

    // 分配npk文件失败
    if(status != NPK_SUCCESS )
    {
    	return -1;
    }

	char * pPackedFileName = jstringTostring(env, strNPKFilePath);
	// 检查打包的文件后缀名(.npk  .NPK)
	if(CheckFileName(pPackedFileName) != 1)
	{
		return -1;
	}

	int iSuccess = 0;
	int j = 0;
	int ret = 0;
	for (j = 0; j < size; j++)
	{
		jstring string = ((*env)->GetObjectArrayElement(env, array, j));
		char * name = jstringTostring(env, string);

		LOGD("%s", name);
        ret = npk_package_add_file(pack, name, yfGetFileName(name), &entity);

        if(ret == NPK_SUCCESS)
        {
        	iSuccess++;
        }

        (*env)->ReleaseStringUTFChars(env, string, name);
	}
    ret = npk_package_save(pack, pPackedFileName, true);
    if(ret != NPK_SUCCESS)
    {
    	return -1;
    }
    npk_package_close(pack);

    LOGD("iSuccess: %d", iSuccess);
	return iSuccess;
}

JNIEXPORT jint JNICALL Java_com_yufei_watermark_BaseLib_UnPack(JNIEnv * env, jobject obj, jstring strNPKFilePath, jstring strUnPackDir)
{
	char *pPackName = jstringTostring(env, strNPKFilePath);
	char *pUnPackDir = jstringTostring(env, strUnPackDir);

	int iLen = strlen(pUnPackDir);
	char pPathDir[256];
	memcpy(pPathDir, pUnPackDir, iLen);
	pPathDir[iLen] = '\0';


	LOGD("pPackName: %s", pPackName);
	LOGD("pUnPackDir: %s", pUnPackDir);

    int teakey[4] = {1024,2,3,4};  // 解密的时候使用.
    NPK_PACKAGE pack;
    NPK_ENTITY entity;

    // 枚举一个目录。
    pack = npk_package_open(pPackName, teakey);
    if(!pack)
    {
    	return -1;
    }
    entity = npk_package_get_first_entity(pack);
    if(!entity)
    {
    	return -1;
    }

    int count = 0;
    int count_success = 0;
    while(entity != NULL)
    {
        NPK_CSTR pEntityName = strcat( pPathDir, npk_entity_get_name(entity) );
        size_t size = npk_entity_get_size(entity);
        LOGD("index %03d name %s size %d\n", count++, pEntityName, size);
        if (pEntityName && strlen(pEntityName) > 0)
        {
            char * buf = (char*)malloc(size);
            if (buf)
            {
                memset(buf, 0, size);
                npk_entity_read(entity, buf);
                FILE *fp = fopen(pEntityName, "wb");
                if (fp)
                {
                    fwrite(buf, size, 1, fp);
                    fclose(fp);
                    LOGD("write file %s size %d success.\n", pEntityName, size);
                    count_success++;
                }
                else
                {
                	LOGD("write file i/o error %s\n", pEntityName);
                }
            }
            else
            {
            	LOGD("malloc error client wanna allocate size is %d\n", size);
            }
        }
        else
        {
            LOGD ("file entity error current is %s\n", pEntityName);
        }
        entity = npk_entity_next(entity);
    	pPathDir[iLen] = '\0';
    }
    npk_package_close(pack);

    return count_success;
}
