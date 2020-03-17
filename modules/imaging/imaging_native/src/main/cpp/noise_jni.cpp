#include "noise.h"
#include <jni.h>

int test() {
  return 15;
}

extern "C" {

JNIEXPORT jint JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_test(JNIEnv *env, jobject self) {
  return 15;
}

JNIEXPORT jlong
JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_newNoiseContext(JNIEnv *env, jobject self, jlong seed) {
  return (jlong) newOpenSimplexNoise(seed);
}

JNIEXPORT void
JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_deleteNoiseContext(JNIEnv *env, jobject self,
                                                                               jlong context) {
  open_simplex_noise_delete((NoiseContext *) context);
}

JNIEXPORT jdouble
JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_noise2d(JNIEnv *env, jobject self, jlong context, jdouble x,
                                                                    jdouble y) {
  return (jlong) openSimplexNoise2d((NoiseContext *) context, x, y);
}

JNIEXPORT void
JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_fillNoiseBuffer2d(JNIEnv *env, jobject self, jlong context,
                                                                              jlong buffer, jint dimensionsX,
                                                                              jint dimensionsY, jint octaves) {
  fillNoiseBuffer2d((NoiseContext *) context, (float *) buffer, dimensionsX, dimensionsY, octaves);
}

JNIEXPORT jlong JNICALL Java_silentorb_mythic_imaging_operators_NoiseNative_getAddress(JNIEnv *env, jobject self, jobject buffer) {
  return (jlong) env->GetDirectBufferAddress(buffer);
}

}
