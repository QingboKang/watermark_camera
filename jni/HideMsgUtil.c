#include "HideMsgUtil.h"
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>


int yfOpenFile(char*filename)
{
    //    return _open(filename, );
    int fd;
    fd = open(filename, O_RDONLY);
    return fd;
}

int yfCreateFile(char*filename)
{
    int fd = open(filename, O_WRONLY|O_CREAT|O_TRUNC);
    return fd;
}

unsigned int yfSeekFile(int fd, unsigned int seekPos, unsigned int Method)
{
//    SEEK_CUR
    return lseek(fd, seekPos, Method);

}
long yfTell(int fd)
{
    return lseek(fd, 0L, 1);
}

//
//	读取数据
//
unsigned int yfReadFile(int fd, void* lpData, unsigned int bfSize)
{
    return (unsigned int)read(fd, lpData, bfSize);
}

//
//	写数据
//
unsigned int yfWriteFile(int fd, void* lpData, unsigned int bfSize)
{
    return (unsigned int)write(fd, lpData, bfSize);
}

void yfCloseFile(int fd)
{
    close(fd);
}
