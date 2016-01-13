LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include ../includeOpenCV.mk

ifeq ("$(wildcard $(OPENCV_MK_PATH))","")

#try to load OpenCV.mk from default install location

include $(TOOLCHAIN_PREBUILT_ROOT)/user/share/OpenCV/OpenCV.mk

else

include $(OPENCV_MK_PATH)

endif

# IMAGE FILTER LIB

LOCAL_MODULE    := WatermarkFilter
LOCAL_LDLIBS    := -llog
 
LOCAL_SRC_FILES := WatermarkFilter.cpp 
 
include $(BUILD_SHARED_LIBRARY)


# NPK LIB
include $(CLEAR_VARS)

LOCAL_MODULE  := libnpk
LOCAL_CFLAGS  := -DNPK_DEV=1
LOCAL_SRC_FILES := external/tea/tea.c \
                   external/xxtea/xxtea.c \
                   external/zlib/adler32.c \
                   external/zlib/compress.c \
                   external/zlib/crc32.c \
                   external/zlib/deflate.c \
                   external/zlib/infback.c \
                   external/zlib/inffast.c \
                   external/zlib/inflate.c \
                   external/zlib/inftrees.c \
                   external/zlib/trees.c \
                   external/zlib/uncompr.c \
                   external/zlib/zutil.c \
				   npk/npk.c \
                   npk/npk_common.c \
                   npk/npk_dev.c \

include $(BUILD_STATIC_LIBRARY)                     

# MAIN LIB
include $(CLEAR_VARS)

LOCAL_MODULE      := WatermarkBaseLib
LOCAL_LDLIBS    := -llog
LOCAL_STATIC_LIBRARIES := libnpk
LOCAL_SRC_FILES   := WatermarkBaseLib.c \
                     JPEG/HideMsgJPEG.c \
                     HideMsgUtil.c 
                     
include $(BUILD_SHARED_LIBRARY)