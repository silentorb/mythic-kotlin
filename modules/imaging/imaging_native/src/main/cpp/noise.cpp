#include "noise.h"
#include <jni.h>

int test() {
    return 15;
}

extern "C" {

JNIEXPORT jint JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_test(JNIEnv *env, jobject self) {
    return 15;
}

}
