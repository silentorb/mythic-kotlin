#include <iostream>
#include <jni.h>
#include "font_loader.h"

extern "C" {

void throw_error(JNIEnv *env, const std::string &message) {
  jclass jc = env->FindClass("java/lang/Error");
  env->ThrowNew(jc, message.c_str());
}

struct JavaString {
    jboolean is_copy;
    const char *c_str;
    JNIEnv *env;
    jstring java_string;

    JavaString(JNIEnv *env, jstring java_string) : env(env), java_string(java_string) {
      c_str = env->GetStringUTFChars(java_string, &is_copy);
    }

    ~JavaString() {
      if (is_copy == JNI_TRUE) {
        env->ReleaseStringUTFChars(java_string, c_str);
      }
    }
};

JNIEXPORT jlong JNICALL Java_silentorb_mythic_typography_FaceLoader_loadFace(JNIEnv *env, jobject self,
                                                                   jlong freetype, jstring filename,
                                                                   jint pixelWidth, jint pixelHeight) {
  try {
    JavaString path{env, filename};
    if (!path.c_str) {
      throw_error(env, "Could not allocate memory for string");
      return 0;
    }
    else {
      auto result = load_font((FT_Library) freetype, path.c_str, (unsigned int) pixelWidth, (unsigned int) pixelHeight);
      return (jlong) result;
    }
  }
  catch (const std::exception &e) {
    throw_error(env, e.what());
    return 0;
  }
}

JNIEXPORT jobject JNICALL
Java_silentorb_mythic_typography_FaceLoader_getTextureDimensions(JNIEnv *env, jobject self, jlong face,
                                                       jint loadFlags, jint renderMode) {
  try {
    auto dimensions = get_texture_dimensions((FT_Face) face, loadFlags, static_cast<FT_Render_Mode>(renderMode));
    auto vector_class = env->FindClass("silentorb/mythic/typography/IntegerVector2");
    check_exception(env, "Could not find IntegerVector2 class.");
    auto constructor = env->GetMethodID(vector_class, "<init>", "(II)V");
    auto result = env->NewObject(vector_class, constructor, dimensions.x, dimensions.y);
    check_exception(env, "Error instantiating IntegerVector2 class.");
    return result;
  }
  catch (const std::exception &e) {
    throw_error(env, e.what());
    return nullptr;
  }
}


JNIEXPORT jobject JNICALL Java_silentorb_mythic_typography_FaceLoader_loadCharacterInfo(JNIEnv *env, jobject self,
                                                                              jlong face_address, jchar c,
                                                                              jint loadFlags, jint renderMode) {
  try {
    auto character_class = env->FindClass("silentorb/mythic/typography/GlyphInfo");
    auto constructor = env->GetMethodID(character_class, "<init>", "(IIIII)V");

    auto face = (FT_Face) face_address;
    if (FT_Load_Char(face, c, loadFlags) != 0)
      throw std::runtime_error("Failed to load glyph");

    if (!face->glyph->bitmap.buffer)
      FT_Render_Glyph(face->glyph, static_cast<FT_Render_Mode>(renderMode));

    auto glyph = face->glyph;
    auto bitmap = face->glyph->bitmap;

    return env->NewObject(character_class, constructor,
                          bitmap.width, bitmap.rows,
                          glyph->bitmap_left, glyph->bitmap_top,
                          glyph->advance.x / 64
    );
  }
  catch (const std::exception &e) {
    throw_error(env, e.what());
    return nullptr;
  }
}


JNIEXPORT void JNICALL Java_silentorb_mythic_typography_FaceLoader_renderFaces(JNIEnv *env, jobject self,
                                                                        jlong freetype, jlong face,
                                                                        jlong buffer,
                                                                        jint width,
                                                                        jint loadFlags, jint renderMode) {
  try {
    render_font((FT_Library) freetype, (FT_Face) face, (unsigned char *) buffer, width, loadFlags,
                static_cast<FT_Render_Mode>(renderMode));
  }
  catch (const std::exception &e) {
    throw_error(env, e.what());
  }
}

JNIEXPORT void JNICALL Java_silentorb_mythic_typography_FaceLoader_releaseFace(JNIEnv *env, jobject self, jlong face) {
  FT_Done_Face((FT_Face) face);
}

JNIEXPORT jlong JNICALL Java_silentorb_mythic_typography_FaceLoader_initializeFreetype(JNIEnv *env, jobject self) {
  FT_Library library = nullptr;

  if (FT_Init_FreeType(&library))
    throw_error(env, "Could not init FreeType Library");

  return (jlong) library;
}

JNIEXPORT void JNICALL Java_silentorb_mythic_typography_FaceLoader_releaseFreetype(JNIEnv *env, jobject self, jlong freetype) {
  FT_Done_FreeType((FT_Library) freetype);
}

}