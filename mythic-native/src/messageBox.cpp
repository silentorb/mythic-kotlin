#pragma once

#include <jni.h>
#include <windows.h>

extern "C" {

JNIEXPORT jlong
JNICALL Java_silentorb_mythic_desktop_Native_messageBox(JNIEnv *env, jobject self,
                                                        jlong hwnd,
                                                        jstring title,
                                                        jstring message) {
  const char*  wideTitle = env->GetStringUTFChars(title, 0);
  const char*  wideMessage = env->GetStringUTFChars(message, 0);
  int result = MessageBoxA(
    (HWND)hwnd,
    wideMessage,
    wideTitle,
    MB_ICONEXCLAMATION | MB_OK
  );
  env->ReleaseStringUTFChars(title, wideTitle);
  env->ReleaseStringUTFChars(message, wideMessage);
  if (result == 0) {
    return GetLastError();
  }
  else {
    return result;
  }
}

}
