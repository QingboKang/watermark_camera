#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../HideMsgUtil.h"
#include "HideMsgJPEG.h"


//
//	��ȡһ��֡
//
int read_frame(int fd, unsigned short* type, unsigned short* fsize, char* data, unsigned dsize, unsigned char method)
{
    unsigned int	cur_pos = 0;			//	���浱ǰλ�õ�ָ��
    unsigned int	dwByteRead = 0;
    unsigned short	sTag = 0;
    unsigned short	sSize = 0;
    //	����ָ��
    cur_pos = yfTell(fd);
    //	��ȡ������
    dwByteRead = yfReadFile(fd, &sTag, sizeof(unsigned short));
    if (!dwByteRead)
    {
        return 1;
    }
    //	��ȡ�δ�С
    dwByteRead = yfReadFile(fd, &sSize, sizeof(unsigned short));
    if (!dwByteRead)
        return 1;

    sSize = ConvertEndian(sSize) - sizeof(unsigned short);

    if (data != NULL && dsize >= sSize) 
	{

        //	��ȡ������
        dwByteRead = yfReadFile(fd, data, sSize);
        if (!dwByteRead)
            return 1;

    }
    else
	{

        yfSeekFile(fd, sSize, SEEK_CUR);
    }

    //	�����ΪPEEK�������û�ԭ����ָ��λ��
    if (LM_METHOD_PEEK == method) {

        yfSeekFile(fd, cur_pos, SEEK_SET);
    }

    *type = sTag;
    *fsize = sSize;

    return 0;
}

//
//	ת�� �ֽ���
//
unsigned short ConvertEndian(unsigned short s)
{
    return ((s&0xFF) <<8 )| (s>>8);
}

//
//	д��ˮӡ�ļ�
//
int write_mark_data(int hMarkFile, int hDestFile)
{
    unsigned char*	lpData = NULL;				//	�ڴ�����
    unsigned int dwByteRead = 0;
    unsigned int dwByteWrite = 0;
    unsigned int dwRealRead = 0;
    unsigned int dwIndex = 0;
    unsigned char comment_tag[2] = {0xff, 0xfe};
    unsigned short tag_size = BLOCK_SIZE - 2;
    unsigned short write_size = 0;
    JPEGMARKHEADER* pXec_Hdr = NULL;

    //	�����ڴ�
    lpData = (unsigned char*)malloc(BLOCK_SIZE);		//	�̶���С��Ϊ32K
    if (!lpData)
    {
        return 1;
    }
    while (1)
    {
        memset(lpData, 0, BLOCK_SIZE);
        memcpy(lpData, comment_tag, 2);				//	FF FE
        write_size = ConvertEndian(tag_size);
        memcpy(lpData+2, &write_size, 2);			//	��С
        pXec_Hdr = (JPEGMARKHEADER*)(lpData + 4);		
        pXec_Hdr->dwMagic = IMAGE_JPEGMAGIC;
        pXec_Hdr->nIndex  = dwIndex++;
        dwRealRead = BLOCK_SIZE - 4 - sizeof(JPEGMARKHEADER);
        dwByteRead = yfReadFile(hMarkFile, lpData + 4 + sizeof(JPEGMARKHEADER), dwRealRead);
        pXec_Hdr->nSize   = dwByteRead;					//	������С
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
//	����ļ���ʽ�Ƿ����Ҫ��
//
int  check_file_type(char* filename)
{
    int				fd = 0;				//	�ļ�
    unsigned short		sSOI = 0;
    JPEG_APP0			jpeg_app0 = {0};
    unsigned int				dwByteRead = 0;				//	��ȡ�ֽ���
    unsigned short		sTag	=	0;
    unsigned short		sSize	=	0;
    fd = yfOpenFile(filename);

    if (fd == 0)
    {
        return 1;
    }

    //	��ȡSOI
    dwByteRead = yfReadFile(fd, &sSOI, sizeof(unsigned short));

    if (sSOI != SOI_FLAG)
    {
        return 1;
    }
    //	��ȡJPEGͷ
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
//	����Ƿ����ˮӡ
//
int  check_markedness(char* strFileName)
{

    int			fd = 0;
    unsigned int				dwByteRead = 0;				//	��ȡ�ֽ���
    unsigned short		sSOI = 0;
    unsigned short		sTag = 0;					//	���־
    unsigned short		sBlockSize = 0;				//	���С

    unsigned int		xec_flag = 0;			//	XEC��־
    unsigned int		ulCurrentPos = 0;

    fd = yfOpenFile(strFileName);					//	���ļ�


    if (fd == 0) {
        return 1;
    }
    dwByteRead = yfReadFile(fd, &sSOI, sizeof(unsigned short));		//	��ȡ�ļ�ͷ

    while(dwByteRead)
    {
        if (read_frame(fd, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
            break;

        if (sTag == DQT_FLAG)
            break;

        if (sTag == COMMENT_FLAG) {

            ulCurrentPos = yfSeekFile(fd, 0, SEEK_CUR);
            //	���XEC��־
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
//	���ؿ��԰�����ˮӡ��С
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
//	���ض�̬��汾
//
int  get_library_version()
{
    return LIBRARY_VERSION;
}

//
//	д��ˮӡ
//
int  save_mark_to_file(char* fileCarrier, char* fileMark, char* dstfile)
{
    int				fp_src = 0;			//	�����ļ�
    int				fp_mark	= 0;			//	Ƕ���ļ�
    int				fp_dst	= 0;			//	����ļ�

    unsigned int				dwByteRead = 0;				//	��ȡ�ֽ���
    unsigned int				dwByteWrite = 0;			//	д����ֽ���
    unsigned short		sSOI = 0;
    unsigned short		sTag = 0;					//	���־
    unsigned short		sBlockSize = 0;				//	���С
    unsigned short		wTagSize = 0;
    unsigned char*		lpData = NULL;
    int bFailed = 0; // parse jpeg frame falied flag
    long test_pos = 0;

    fp_src = yfOpenFile(fileCarrier);					//	�������ļ�
    fp_mark	= yfOpenFile(fileMark);						//	��ˮӡ�ļ�
    fp_dst	= yfCreateFile(dstfile);						//	����Ŀ���ļ�


    if (fp_src == -1 ||
        fp_mark == -1 ||
        fp_dst == -1)
    {
        return 1;
    }

    yfReadFile(fp_src, &sSOI, sizeof(unsigned short));		//	��ȡ�ļ�ͷ
    yfWriteFile(fp_dst, &sSOI, sizeof(unsigned short));		//	д��
    //	д��ˮӡǰ����Ϣ
    while(1)
    {
        test_pos = yfTell(fp_src);
        //	��ȡһ��֡��Ϣ
        if (read_frame(fp_src, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
        {
            bFailed = 1;
            break;
        }

        if (sBlockSize <= 0)
            return 1;

        //	�����DQT��������ѭ������Ϊˮӡ��Ϣд��DQT��ǰ��
        if (sTag == DQT_FLAG) {

            break;
        }

        //	�����ע����Ϣ����ȥ��������Ӱ����򣬻�����ȥ����������д���ˮӡ
        if (sTag == COMMENT_FLAG) {

            yfSeekFile(fp_src, sBlockSize + 4, SEEK_CUR);
            continue;
        }

        //
        //	�����ǣ���д�����ļ�
        //

        //	�����ڴ�
        lpData = (unsigned char*)malloc(sBlockSize);
        memset(lpData, 0, sBlockSize);

        if (lpData == NULL)
        {
            bFailed = 1;
            break;
        }

        //	��ȡ֡����
        if (read_frame(fp_src, &sTag, &sBlockSize, lpData, sBlockSize, LM_METHOD_READ))
        {
            bFailed = 1;
            break;
        }

        //	д�����ļ�
        yfWriteFile(fp_dst, &sTag, sizeof(unsigned short));
        wTagSize = ConvertEndian(sBlockSize + 2);
        yfWriteFile(fp_dst, &wTagSize, sizeof(unsigned short));
        yfWriteFile(fp_dst, lpData, sBlockSize);

        //	�ͷ��ڴ�
        free(lpData);
        lpData = NULL;
    }
    if (bFailed)
    {
        yfCloseFile(fp_src);
        yfCloseFile(fp_mark);
        yfCloseFile(fp_dst);
        return 2; //����JPEG ֡��Ϣʧ��.
    }
    //
    //	д��ˮӡ
    //
    write_mark_data(fp_mark, fp_dst);
    //
    //	��ȫʣ�µ�����
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
    int fp_src = 0;			//	�����ļ�
    int fp_dst	= 0;			//	����ļ�
    unsigned int dwByteRead = 0;				//	��ȡ�ֽ���
    unsigned int dwByteWrite = 0;			//	д����ֽ���
    unsigned short sSOI = 0;
    unsigned short sTag = 0;					//	���־
    unsigned short sBlockSize = 0;				//	���С
    unsigned char* lpData = NULL;
    JPEGMARKHEADER*	pXec_Hdr = NULL;
    fp_src = yfOpenFile(fileCarrier);					//	�������ļ�
    fp_dst	= yfCreateFile(fileMark);						//	����Ŀ���ļ�

    if (fp_src	== 0 || fp_dst == 0)
    {
        return 1;
    }

    yfReadFile(fp_src, &sSOI, sizeof(unsigned short));		//	��ȡ�ļ�ͷ

    //	д��ˮӡǰ����Ϣ
    while(1)
    {
        //	��ȡһ��֡��Ϣ
        if (read_frame(fp_src, &sTag, &sBlockSize, NULL, 0, LM_METHOD_PEEK))
            return 1;

        if (sBlockSize <= 0)
            return 1;

        //	�����DQT��������ѭ������Ϊˮӡ��Ϣд��DQT��ǰ��
        if (sTag == DQT_FLAG) {

            break;
        }

        //	�����ע����Ϣ�����ȡλ��
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
