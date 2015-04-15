// License information is available from LICENSE file

#ifndef JXCORE_H
#define JXCORE_H
#include "JniHelper.h"
#include "../jxcore-binaries/jx.h"
#include <android/log.h>

#define ALOG_TAG "jxcore-app-log"
#define log_console(...) \
  __android_log_print(ANDROID_LOG_DEBUG, ALOG_TAG, __VA_ARGS__)
#define warn_console(...) \
  __android_log_print(ANDROID_LOG_WARN, ALOG_TAG, __VA_ARGS__)
#define error_console(...) \
  __android_log_print(ANDROID_LOG_ERROR, ALOG_TAG, __VA_ARGS__)

namespace jxcore {

void Callback(const char *json) {
  JniMethodInfo t;
  if (JniHelper::getStaticMethodInfo(t, "com/nubisa/jxcore/MainActivity",
                                     "callback", "(Ljava/lang/String;)V")) {
    jstring strX = t.env->NewStringUTF(json);
    t.env->CallStaticVoidMethod(t.classID, t.methodID, strX);
    t.env->DeleteLocalRef(t.classID);
    t.env->DeleteLocalRef(strX);
  }
}

void OnThread(const int attached) {
  if (attached == 0) {
    JniHelper::getEnv();  // attach current thread
  } else {
    JniHelper::getJavaVM()->DetachCurrentThread();
  }
}

int Initialize(JavaVM *vm) {
  JniHelper::setJavaVM(vm);

  return JNI_VERSION_1_4;
}
}

#endif
