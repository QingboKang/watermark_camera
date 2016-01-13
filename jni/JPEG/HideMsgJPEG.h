#ifndef _HIDEMSGJPEG_H_
#define _HIDEMSGJPEG_H_
#include <stdio.h>
/*yfmk - for yufei warter mark magic dword*/
#define IMAGE_JPEGMAGIC 0x6b6D6679 
/*	库版本 */
#define LIBRARY_VERSION			10

// For Android
#define DEBUG 1

#if DEBUG
#include <android/log.h>
#   define LOG  "myjni"
#   define LOGD(...) __android_log_print(ANDROID_LOG_ERROR, LOG, __VA_ARGS__)
#else
#   define LOGD(...)  do {} while(0)
#endif


typedef struct tagJPEGMARKHEADER
{
    unsigned int dwMagic;			/*"yfmk"	0x5845435f*/
    unsigned int nSize;
    unsigned int nIndex;
} JPEGMARKHEADER, *PJPEGMARKHEADER;
#define BLOCK_SIZE				0x00008000			/*	32K*/



//	水印大小数据
#define DEFAULT_MAX_SIZE		0x03200000			//	50M
#define BLOCK_SIZE				0x00008000			//	32K

//	库版本
#define LIBRARY_VERSION			10

/*++

	JPEG结构描述

| FF D8 | APP[0-N] | ... | [MARK]... | DQT
--*/

//
//	定义值
//
#define				SOI_FLAG				0xD8FF		//	Start Of Image	(FF D8)
#define				APP_FLAG				0xE0FF		//	APP (FF E0)
#define				DQT_FLAG				0xDBFF		//	Define Quantization Table (FF DB)
#define				COMMENT_FLAG			0xFEFF		//	Comment (FF FE)

//
//	结构体
//
#pragma pack(1)

typedef struct _JPEG_APP0
{
	unsigned char	bsJFIF[5];		//	'JFIF'
	unsigned char	bsOther[9];		//	不需要
} JPEG_APP0, *PJPEG_APP0;


#pragma pack()

//////////////////////////////////////////////////////////////////////////

//
//	内部函数
//

#define		LM_METHOD_PEEK		0x00
#define		LM_METHOD_READ		0x01
// #ifdef WIN32
// #
// #endif
//
//	读取一个帧
//	
//	LM_METHOD_PEEK		读取，但是不移动文件指针
//
//	LM_METHOD_READ		读取，并移动文件指针
//
int read_frame(int hFile, unsigned short* type, unsigned short* fsize, char* data, unsigned dsize, unsigned char method);

//
//	转换 字节序
//
unsigned short ConvertEndian(unsigned short s);

//
//	写入水印文件
//
int write_mark_data(int hMarkFile, int hDestFile);

//////////////////////////////////////////////////////////////////////////

//
//	导出函数
//

//
//	检查文件格式是否符合要求
//
//	注意：此函数主要检查20字节文件头
//
int  check_file_type(char* strFileName);

//
//	检查是否包含水印
//
int  check_markedness(char* strFileName);

//
//	返回可以包含的水印大小
//
int  get_free_space(char* strFileName);

//
//	返回动态库版本
//
int  get_library_version();

//
//	提取水印
//
int  get_mark_from_file(
							   char* fileCarrier,			//	载体文件
							   char* fileMark				//	输出的水印文件
							   );

//
//	写入水印
//
int  save_mark_to_file(
							  char* fileCarrier,			//	载体文件
							  char* fileMark,				//	写入的水印文件
							  char* dstfile					//	写入后的新文件
							  );		
#endif
