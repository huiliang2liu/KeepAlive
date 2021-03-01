#include <unistd.h>
#include <sys/file.h>
#include <jni.h>
#include <stdio.h>
#include <malloc.h>

#include "common.h"

int hold_file_lock(const char *lock_file_path) {
    int descriptor = open(lock_file_path, O_RDONLY);
    if (descriptor == -1) {
        // LOGD("hold_file_lock(), file not exists");
        return FALSE;
    }

    int lockRet = -1;
    int count = 0;
    while (lockRet == -1 && count < 100) {
        count += 1;
        lockRet = flock(descriptor, LOCK_EX | LOCK_NB);
    }

    if (lockRet == -1) {
        close(descriptor);
        // LOGD("hold_file_lock(), file is locked");
        return FALSE;
    }

    // LOGD("success to lock file");
    return TRUE;
}

int wait_file_lock(const char *lock_file_path) {
    int descriptor = open(lock_file_path, O_RDONLY);
    if (descriptor == -1) {
        // LOGD("wait_file_lock(), file not exists");
        return FALSE;
    }

    int lockRet = 0;
    int count = 0;
    while (lockRet != -1 && count < 600 * 10000) {
        lockRet = flock(descriptor, LOCK_EX | LOCK_NB);
        if (lockRet != -1) {
            flock(descriptor, LOCK_UN);
            usleep(100); //0.1ms
        }
    }

    if (lockRet != -1) {
        // LOGD("wait_file_lock(), wait file not lock");
        close(descriptor);
        return FALSE;
    }

    // LOGD("wait_file_lock(), wait file");
    flock(descriptor, LOCK_EX);
    close(descriptor);
    return TRUE;
}

// hold lock file
JNIEXPORT jint JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun1(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = (*env)->GetStringUTFChars(env, path, JNI_FALSE);
    int ret = hold_file_lock(ch_path);
    (*env)->ReleaseStringUTFChars(env, path, ch_path);
    return ret;
}

// wait lock file
JNIEXPORT jint JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun2(JNIEnv *env, jobject thiz, jstring path) {
    const char *ch_path = (*env)->GetStringUTFChars(env, path, JNI_FALSE);
    int ret = wait_file_lock(ch_path);
    (*env)->ReleaseStringUTFChars(env, path, ch_path);
    return ret;
}


jstring createJString(JNIEnv *env, const char *str1, const char *str2) {
    char *tmp = (char *) malloc(strlen(str1) + strlen(str2) + 1);
    if (tmp == NULL) {
        return NULL;
    }

    strcpy(tmp, str1);
    strcat(tmp, str2);
    jstring result = (*env)->NewStringUTF(env, tmp);
    free(tmp);

    return result;
}


// shell export 1
JNIEXPORT jstring JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun3(JNIEnv *env, jobject thiz, jstring path) {
//    if (!is_signature_pass()) {
//        return (*env)->NewStringUTF(env, "sh am start -W ...");
//    }

    const char *ch_path = (*env)->GetStringUTFChars(env, path, JNI_FALSE);

    char export[] = "export CLASSPATH=$CLASSPATH:";
    jstring result = createJString(env, export, ch_path);

    (*env)->ReleaseStringUTFChars(env, path, ch_path);
    return result;
}

// shell export 2
JNIEXPORT jstring JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun4(JNIEnv *env, jobject thiz, jstring path) {
//    if (!is_signature_pass()) {
//        return (*env)->NewStringUTF(env, "sh am start -n ...");
//    }

    const char *ch_path = (*env)->GetStringUTFChars(env, path, JNI_FALSE);

    char export[] = "export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:";
    jstring result = createJString(env, export, ch_path);

    (*env)->ReleaseStringUTFChars(env, path, ch_path);
    return result;
}

// shell export 3
JNIEXPORT jstring JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun5(JNIEnv *env, jobject thiz, jstring path) {
//    if (!is_signature_pass()) {
//        return (*env)->NewStringUTF(env, "sh am start -s ...");
//    }

    const char *ch_path = (*env)->GetStringUTFChars(env, path, JNI_FALSE);

    char export[] = "export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:";
    jstring result = createJString(env, export, ch_path);

    (*env)->ReleaseStringUTFChars(env, path, ch_path);
    return result;
}

// shell fork process
JNIEXPORT jstring JNICALL
Java_com_keep_alive_daemon_utils_DaemonNative_nativeFun6(JNIEnv *env, jobject thiz,
                                                        jstring data, jstring name) {
//    if (!is_signature_pass()) {
//        return (*env)->NewStringUTF(env, "sh am start -o ...");
//    }

    const char *ch_data = (*env)->GetStringUTFChars(env, data, JNI_FALSE);
    const char *ch_name = (*env)->GetStringUTFChars(env, name, JNI_FALSE);

    char *app;
    if (access("/system/bin/app_process32", F_OK) == 0) {
        app = "app_process32";
    } else {
        app = "app_process";
    }

    char cls[] = "com.keep.alive.daemon.core.DaemonEntry";

    jstring result = NULL;
    char *tmp = (char *) malloc((strlen(ch_data) + 512) * sizeof(char));
    if (tmp != NULL) {
        sprintf(tmp, "%s / %s %s --application --nice-name=%s --daemon &",
                app, cls, ch_data, ch_name);

        result = (*env)->NewStringUTF(env, tmp);
        free(tmp);
    }

    (*env)->ReleaseStringUTFChars(env, data, ch_data);
    (*env)->ReleaseStringUTFChars(env, name, ch_name);
    return result;
}