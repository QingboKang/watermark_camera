#ifndef _HIDEMSGJPEG_H_
#define _HIDEMSGJPEG_H_
#include <stdio.h>
/*yfmk - for yufei warter mark magic dword*/
#define IMAGE_JPEGMAGIC 0x6b6D6679 
/*	��汾 */
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



//	ˮӡ��С����
#define DEFAULT_MAX_SIZE		0x03200000			//	50M
#define BLOCK_SIZE				0x00008000			//	32K

//	��汾
#define LIBRARY_VERSION			10

/*++

	JPEG�ṹ����

| FF D8 | APP[0-N] | ... | [MARK]... | DQT
--*/

//
//	����ֵ
//
#define				SOI_FLAG				0xD8FF		//	Start Of Image	(FF D8)
#define				APP_FLAG				0xE0FF		//	APP (FF E0)
#define				DQT_FLAG				0xDBFF		//	Define Quantization Table (FF DB)
#define				COMMENT_FLAG			0xFEFF		//	Comment (FF FE)

//
//	�ṹ��
//
#pragma pack(1)

typedef struct _JPEG_APP0
{
	unsigned char	bsJFIF[5];		//	'JFIF'
	unsigned char	bsOther[9];		//	����Ҫ
} JPEG_APP0, *PJPEG_APP0;


#pragma pack()

//////////////////////////////////////////////////////////////////////////

//
//	�ڲ�����
//

#define		LM_METHOD_PEEK		0x00
#define		LM_METHOD_READ		0x01
// #ifdef WIN32
// #
// #endif
//
//	��ȡһ��֡
//	
//	LM_METHOD_PEEK		��ȡ�����ǲ��ƶ��ļ�ָ��
//
//	LM_METHOD_READ		��ȡ�����ƶ��ļ�ָ��
//
int read_frame(int hFile, unsigned short* type, unsigned short* fsize, char* data, unsigned dsize, unsigned char method);

//
//	ת�� �ֽ���
//
unsigned short ConvertEndian(unsigned short s);

//
//	д��ˮӡ�ļ�
//
int write_mark_data(int hMarkFile, int hDestFile);

//////////////////////////////////////////////////////////////////////////

//
//	��������
//

//
//	����ļ���ʽ�Ƿ����Ҫ��
//
//	ע�⣺�˺�����Ҫ���20�ֽ��ļ�ͷ
//
int  check_file_type(char* strFileName);

//
//	����Ƿ����ˮӡ
//
int  check_markedness(char* strFileName);

//
//	���ؿ��԰�����ˮӡ��С
//
int  get_free_space(char* strFileName);

//
//	���ض�̬��汾
//
int  get_library_version();

//
//	��ȡˮӡ
//
int  get_mark_from_file(
							   char* fileCarrier,			//	�����ļ�
							   char* fileMark				//	�����ˮӡ�ļ�
							   );

//
//	д��ˮӡ
//
int  save_mark_to_file(
							  char* fileCarrier,			//	�����ļ�
							  char* fileMark,				//	д���ˮӡ�ļ�
							  char* dstfile					//	д�������ļ�
							  );		
#endif
