#ifndef MARS_DAEMON_COMMON_H
#define MARS_DAEMON_COMMON_H

#include <android/log.h>

#define LOG_TAG "PROCESS_DAEMON"

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define TRUE    1
#define FALSE   0

#endif
