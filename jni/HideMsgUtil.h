#include <stdio.h>


int yfOpenFile(char*filename);
int yfCreateFile(char*filename);
unsigned int yfSeekFile(int fd, unsigned int seekPos, unsigned int Method);
long yfTell(int fd);
unsigned int yfReadFile(int fd, void* lpData, unsigned int bfSize);
unsigned int yfWriteFile(int fd, void* lpData, unsigned int bfSize);
void yfCloseFile(int fd);
