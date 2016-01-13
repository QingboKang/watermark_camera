#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../HideMsgUtil.h"
#include "HideMsgJPEG.h"


//
//	读取一个帧
//
int read_frame(int fd, unsigned short* type, unsigned short* fsize, char* data, unsigned dsize, unsigned char method)
{
    unsigned int	cur_pos = 0;			//	保存当前位置的指针
    unsigned int	dwByteRead = 0;
    unsigned short	sTag = 0;
    unsigned short	sSize = 0;
    //	保存指针
    cur_pos = yfTell(fd);
    //	读取段类型
    dwByteRead = yfReadFile(fd, &sTag, sizeof(unsigned short));
    if (!dwByteRead)
    {
        return 1;
    }
    //	读取段大小
    dwByteRead = yfReadFile(fd, &sSize, sizeof(unsigned short));
    if (!dwByteRead)
        return 1;

    sSize = ConvertEndian(sSize) - sizeof(unsigned short);

    if (data != NULL && dsize >= sSize) 
	{

        //	读取段数据
        dwByteRead = yfReadFile(fd, data, sSize);
        if (!dwByteRead)
            return 1;

    }
    else
	{

        yfSeekFile(fd, sSize, SEEK_CUR);
    }

    //	如果仅为PEEK，则设置回原来的指针位置
    if (LM_METHOD_PEEK == method) {

        yfSeekFile(fd, cur_pos, SEEK_SET);
    }

    *type = sTag;
    *fsize = sSize;

    return 0;
}

//
//	转换 字节序
//
unsigned short ConvertEndian(unsigned short s)
{
    return ((s&0xFF) <<8 )| (s>>8);
}

//
//	写入水印文件
//
int write_mark_data(int hMarkFile, int hDestFile)
{
    unsigned char*	lpData = NULL;				//	内存数据
    unsigned int dwByteRead = 0;
    unsigned int dwByteWrite = 0;
    unsigned int dwRealRead = 0;
    unsigned int dwIndex = 0;
    unsigned char comment_tag[2] = {0xff, 0xfe};
    unsigned short tag_size = BLOCK_SIZE - 2;
    unsigned short write_size = 0;
    JPEGMARKHEADER* pXec_Hdr = NULL;

    //	申请内存
    lpData = (unsigned char*)malloc(BLOCK_SIZE);		//	固定大小，为32K
    if (!lpData)
    {
        return 1;
    }
    while (1)
    {
        memset(lpData, 0, BLOCK_SIZE);
        memcpy(lpData, comment_tag, 2);				//	FF FE
        write_size = ConvertEndian(tag_size);
        memcpy(lpData+2, &write_size, 2);			//	大小
        pXec_Hdr = (JPEGMARKHEADER*)(lpData + 4);		
        pXec_Hdr->dwMagic = IMAGE_JPEGMAGIC;
        pXec_Hdr->nIndex  = dwIndex++;
        dwRealRead = BLOCK_SIZE - 4 - sizeof(JPEGMARKHEADER);
        dwByteRead = yfReadFile(hMarkFile, lpData + 4 + sizeof(JPEGMARKHEADER), dwRealRead);
        pXec_Hdr->nSize   = dwByteRead;					//	完整大小
        if (!dwByteRead)
        {
            break;
        }
        dwByteWrite = yfWriteFile(hDestFile, lpData, BLOCK_SIZE);
    }

    free(lpData);
    lpData = NULL;
    return 0;
}


//////////////////////////////////////////////////////////////////////////

//
//	检查文件格式是否符合要求
//
int  check_file_type(char* filename)
{
    int				fd = 0;				//	文件
    unsigned short		sSOI = 0;
    JPEG_APP0			jpeg_app0 = {0};
    unsigned int				dwByteRead = 0;				//	读取字节数
    unsigned short		sTag	=	0;
    unsigned short		sSize	=	0;
    fd = yfOpenFile(filename);

    if (fd == 0)
    {
        return 1;
    }

    //	读取SOI
    dwByteRead = yfReadFile(fd, &sSOI, sizeof(unsigned short));

    if (sSOI != SOI_FLAG)
    {
        return 1;
    }
    //	读取JPEG头
//     if (!read_frame(hFile, &sTag, &sSize, (UCHAR*)&jpeg_app0, sizeof(JPEG_APP0), LM_METHOD_READ))
//     {
// 	    if (sTag != APP_FLAG)
// 		    return 1;
// 
// 	    if (sSize != 14)
// 		    return 1;
// 
// 	    if (memcmp(jpeg_app0.bsJFIF, "JFIF", 4))
// 		    return 1;
// 
//     } else {
// 
// 	    return 1;
//     }
    yfCloseFile(fd);
    return 0;
}

//
//	检查是否包含水印
//
int  check_markedness(char* strFileName)
{

    int			fd = 0;
    unsigned int				dwByteRead = 0;				//	读取字节数
    unsigned short		sSOI = 0;
    unsigned short		sTag = 0;					//	块标志
    unsigned short		sBlockSize = 0;				//	块大小

    unsigned int		xec_flag = 0;			//	XEC标志
    unsigned int		ulCurrentPos = 0;

    fd = yfOpenFile(strFileName);					//	打开文件


    if (fd == 0) {
        return 1;
    }
    dwByteRead = yfReadFile(fd, &sSOI, sizeof(unsigned short));		//	读取文件头

    while(dwByteRead)
    {
        if (read_frame(fd, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
            break;

        if (sTag == DQT_FLAG)
            break;

        if (sTag == COMMENT_FLAG) {

            ulCurrentPos = yfSeekFile(fd, 0, SEEK_CUR);
            //	检查XEC标志
            yfSeekFile(fd, 4, SEEK_CUR);
            yfReadFile(fd, &xec_flag, 4);

            if (IMAGE_JPEGMAGIC == xec_flag)
                return 1;

            yfSeekFile(fd, ulCurrentPos, SEEK_SET);
        }

        yfSeekFile(fd, sBlockSize + 4, SEEK_CUR);
    }
    yfCloseFile(fd);

    return 0;
}

//
//	返回可以包含的水印大小
//
int  get_free_space(char* strFileName)
{
    long size = 0;
    int fd = yfOpenFile(strFileName);
    if (fd != -1)
    {
        yfSeekFile(fd, 0, SEEK_END);
        size = yfTell(fd);
        size = size/2;
    }
    return size;
}

//
//	返回动态库版本
//
int  get_library_version()
{
    return LIBRARY_VERSION;
}

//
//	写入水印
//
int  save_mark_to_file(char* fileCarrier, char* fileMark, char* dstfile)
{
    int				fp_src = 0;			//	载体文件
    int				fp_mark	= 0;			//	嵌入文件
    int				fp_dst	= 0;			//	输出文件

    unsigned int				dwByteRead = 0;				//	读取字节数
    unsigned int				dwByteWrite = 0;			//	写入的字节数
    unsigned short		sSOI = 0;
    unsigned short		sTag = 0;					//	块标志
    unsigned short		sBlockSize = 0;				//	块大小
    unsigned short		wTagSize = 0;
    unsigned char*		lpData = NULL;
    int bFailed = 0; // parse jpeg frame falied flag
    long test_pos = 0;

    fp_src = yfOpenFile(fileCarrier);					//	打开载体文件
    fp_mark	= yfOpenFile(fileMark);						//	打开水印文件
    fp_dst	= yfCreateFile(dstfile);						//	创建目标文件


    if (fp_src == -1 ||
        fp_mark == -1 ||
        fp_dst == -1)
    {
        return 1;
    }

    yfReadFile(fp_src, &sSOI, sizeof(unsigned short));		//	读取文件头
    yfWriteFile(fp_dst, &sSOI, sizeof(unsigned short));		//	写入
    //	写入水印前的信息
    while(1)
    {
        test_pos = yfTell(fp_src);
        //	读取一个帧信息
        if (read_frame(fp_src, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
        {
            bFailed = 1;
            break;
        }

        if (sBlockSize <= 0)
            return 1;

        //	如果是DQT表，则跳出循环，因为水印信息写在DQT表前面
        if (sTag == DQT_FLAG) {

            break;
        }

        //	如果是注释信息，则去掉，避免影响程序，还可以去除其他程序写入的水印
        if (sTag == COMMENT_FLAG) {

            yfSeekFile(fp_src, sBlockSize + 4, SEEK_CUR);
            continue;
        }

        //
        //	都不是，则写入新文件
        //

        //	申请内存
        lpData = (unsigned char*)malloc(sBlockSize);
        memset(lpData, 0, sBlockSize);

        if (lpData == NULL)
        {
            bFailed = 1;
            break;
        }

        //	读取帧数据
        if (read_frame(fp_src, &sTag, &sBlockSize, lpData, sBlockSize, LM_METHOD_READ))
        {
            bFailed = 1;
            break;
        }

        //	写入新文件
        yfWriteFile(fp_dst, &sTag, sizeof(unsigned short));
        wTagSize = ConvertEndian(sBlockSize + 2);
        yfWriteFile(fp_dst, &wTagSize, sizeof(unsigned short));
        yfWriteFile(fp_dst, lpData, sBlockSize);

        //	释放内存
        free(lpData);
        lpData = NULL;
    }
    if (bFailed)
    {
        yfCloseFile(fp_src);
        yfCloseFile(fp_mark);
        yfCloseFile(fp_dst);
        return 2; //解析JPEG 帧信息失败.
    }
    //
    //	写入水印
    //
    write_mark_data(fp_mark, fp_dst);
    //
    //	补全剩下的数据
    //
    lpData = (unsigned char*)malloc(BLOCK_SIZE);
    while (1)
    {
        memset(lpData, 0, BLOCK_SIZE);
        test_pos = yfTell(fp_src);
        dwByteRead = yfReadFile(fp_src, lpData, BLOCK_SIZE);

        if (!dwByteRead)
            break;

        yfWriteFile(fp_dst, lpData, dwByteRead);
    }
    free(lpData);
    lpData = NULL;
    if (lpData) {

        free(lpData);
        lpData = NULL;
    }
    yfCloseFile(fp_src);
    yfCloseFile(fp_mark);
    yfCloseFile(fp_dst);
    return 0;
}

int  get_mark_from_file(char* fileCarrier, char* fileMark)
{
    int fp_src = 0;			//	载体文件
    int fp_dst	= 0;			//	输出文件
    unsigned int dwByteRead = 0;				//	读取字节数
    unsigned int dwByteWrite = 0;			//	写入的字节数
    unsigned short sSOI = 0;
    unsigned short sTag = 0;					//	块标志
    unsigned short sBlockSize = 0;				//	块大小
    unsigned char* lpData = NULL;
    JPEGMARKHEADER*	pXec_Hdr = NULL;
    fp_src = yfOpenFile(fileCarrier);					//	打开载体文件
    fp_dst	= yfCreateFile(fileMark);						//	创建目标文件

    if (fp_src	== 0 || fp_dst == 0)
    {
        return 1;
    }

    yfReadFile(fp_src, &sSOI, sizeof(unsigned short));		//	读取文件头

    //	写入水印前的信息
    while(1)
    {
        //	读取一个帧信息
        if (read_frame(fp_src, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
            return 1;

        if (sBlockSize <= 0)
            return 1;

        //	如果是DQT表，则跳出循环，因为水印信息写在DQT表前面
        if (sTag == DQT_FLAG) {

            break;
        }

        //	如果是注释信息，则读取位置
        if (sTag == COMMENT_FLAG) {

            lpData = (unsigned char*)malloc(BLOCK_SIZE);
            memset(lpData, 0, BLOCK_SIZE);

            if (read_frame(fp_src, &sTag, &sBlockSize, lpData, BLOCK_SIZE, LM_METHOD_READ)) {

                free(lpData);
                lpData = NULL;
                return 1;
            }

            pXec_Hdr = (JPEGMARKHEADER*)lpData;

            yfWriteFile(fp_dst, lpData + sizeof(JPEGMARKHEADER), pXec_Hdr->nSize);


            free(lpData);
            lpData = NULL;

            continue;
        }

        yfSeekFile(fp_src, sBlockSize + 4, SEEK_CUR);
    }
    if (lpData) {

        free(lpData);
        lpData = NULL;
    }

    yfCloseFile(fp_src);
    yfCloseFile(fp_dst);
    return 0;
}
