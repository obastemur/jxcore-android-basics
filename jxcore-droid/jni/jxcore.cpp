// License information is available from LICENSE file

#define __STDC_LIMIT_MACROS
#include <stdlib.h>
#include <string.h>
#include <sstream>
#include "jxcore_droid.h"
#include "JniHelper.h"
#include <android/log.h>
#include "android/asset_manager.h"
#include "android/asset_manager_jni.h"

void ConvertResult(JXResult *result, std::string &to_result) {
  switch (result->type_) {
    case RT_Null:
      to_result = "null";
      break;
    case RT_Undefined:
      to_result = "undefined";
      break;
    case RT_Boolean:
      to_result = JX_GetBoolean(result) ? "true" : "false";
      break;
    case RT_Int32: {
      std::stringstream ss;
      ss << JX_GetInt32(result);
      to_result = ss.str();
    } break;
    case RT_Double: {
      std::stringstream ss;
      ss << JX_GetDouble(result);
      to_result = ss.str();
    } break;
    case RT_Buffer: {
      // (TODO) Convert To JS ArrayBuffer
      // It's ready for iOS, use that implementation

      // this one is buggy. just a proof of concept
      to_result = JX_GetString(result);
    } break;
    case RT_JSON:
    case RT_String: {
      to_result = JX_GetString(result);
    } break;
    case RT_Error: {
      // buggy. proof of concept
      to_result = JX_GetString(result);
    } break;
    default:
      to_result = "null";
      return;
  }
}

static void callback(JXResult *results, int argc) {
  JXResult retval = results[0];

  std::string str;
  ConvertResult(&retval, str);

  jxcore::Callback(str.c_str());
}

AAssetManager *assetManager;
static void assetExistsSync(JXResult *results, int argc) {
  const char *filename = JX_GetString(&results[0]);
  bool found = false;
  AAsset *asset =
      AAssetManager_open(assetManager, filename, AASSET_MODE_UNKNOWN);
  if (asset) {
    found = true;
    AAsset_close(asset);
  }

  JX_SetBoolean(&results[argc], found);
}

static void assetReadSync(JXResult *results, int argc) {
  const char *filename = JX_GetString(&results[0]);

  AAsset *asset =
      AAssetManager_open(assetManager, filename, AASSET_MODE_UNKNOWN);
  if (asset) {
    off_t fileSize = AAsset_getLength(asset);
    void *data = malloc(fileSize);
    int read_len = AAsset_read(asset, data, fileSize);
    JX_SetBuffer(&results[argc], (char *)data, read_len);
    AAsset_close(asset);
    return;
  }

  const char *err = "File doesn't exist";
  JX_SetError(&results[argc], err, strlen(err));
}

std::string files_json;
static void assetReadDirSync(JXResult *results, int argc) {
  JX_SetJSON(&results[argc], files_json.c_str(), files_json.length());
}

extern "C" {

jint JNI_OnLoad(JavaVM *vm, void *reserved) { return jxcore::Initialize(vm); }

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_prepareEngine(JNIEnv *env, jobject thiz,
                                                  jstring home, jstring files) {
  static bool initialized = false;

  const char *hfiles = env->GetStringUTFChars(files, 0);
  const char *hfolder = env->GetStringUTFChars(home, 0);

  if (!initialized) {  // silly but does the job
    initialized = true;
    JX_Initialize(hfolder, callback);
  }

  JX_InitializeNewEngine();
  files_json = hfiles;

  env->ReleaseStringUTFChars(home, hfolder);
  env->ReleaseStringUTFChars(files, hfiles);

  JX_DefineExtension("assetExistsSync", assetExistsSync);
  JX_DefineExtension("assetReadSync", assetReadSync);
  JX_DefineExtension("assetReadDirSync", assetReadDirSync);
}

JNIEXPORT jstring JNICALL
Java_com_nubisa_jxcore_MainActivity_evalEngine(JNIEnv *env, jobject thiz,
                                               jstring contents) {
  const char *data = env->GetStringUTFChars(contents, 0);

  JXResult jxresult;
  JX_Evaluate(data, 0, &jxresult);
  std::string str_result;
  ConvertResult(&jxresult, str_result);

  JX_FreeResultData(&jxresult);
  env->ReleaseStringUTFChars(contents, data);

  return env->NewStringUTF(str_result.c_str());
}

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_defineMainFile(JNIEnv *env, jobject obj,
                                                   jstring contents) {
  const char *data = env->GetStringUTFChars(contents, 0);
  JX_DefineMainFile(data);
  env->ReleaseStringUTFChars(contents, data);
}

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_defineFile(JNIEnv *env, jobject obj,
                                               jstring filename, jstring data) {
  const char *name = env->GetStringUTFChars(filename, 0);
  const char *file = env->GetStringUTFChars(data, 0);

  JX_DefineFile(name, file);

  env->ReleaseStringUTFChars(filename, name);
  env->ReleaseStringUTFChars(data, file);
}

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_startEngine(JNIEnv *env, jobject thiz) {
  JX_StartEngine();
}

JNIEXPORT jint JNICALL
Java_com_nubisa_jxcore_MainActivity_loopOnce(JNIEnv *env, jobject thiz) {
  return JX_LoopOnce();
}

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_stopEngine(JNIEnv *env, jobject thiz) {
  JX_StopEngine();
}

JNIEXPORT void JNICALL
Java_com_nubisa_jxcore_MainActivity_setNativeContext(JNIEnv *env, jobject thiz,
                                                     jobject context,
                                                     jobject j_assetManager) {
  jxcore::JniHelper::setClassLoaderFrom(context);
  assetManager = AAssetManager_fromJava(env, j_assetManager);
}
}
