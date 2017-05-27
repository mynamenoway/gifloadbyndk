#include <stdlib.h>
#include <jni.h>
#include "gif_lib.h"
#include <android/log.h>


#define LOG_TAG "libgifHelp"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define dispose(ext) (((ext)->Bytes[0] & 0x1c) >> 2)
#define argb(a,r,g,b) ( ((a) & 0xff) << 24 ) | ( ((b) & 0xff) << 16 ) | ( ((g) & 0xff) << 8 ) | ((r) & 0xff)
#define delay(ext) (10*((ext)->Bytes[2] << 8 | (ext)->Bytes[1]))
typedef struct Gifbean {
    int frame_duration;
    int frame_current;
    int total_duration;
    int total_frames;
} mGifbean;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_suipu_gifndkdemo_GifLoader_loadGIFc(JNIEnv *env, jobject thiz, jstring path_) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);
    int err;
    GifFileType* GifFiletype = DGifOpenFileName(path, &err);
    DGifSlurp(GifFiletype);
    mGifbean * gifbean = malloc(sizeof(mGifbean));
    gifbean->frame_duration = 0;
    gifbean->frame_current = 0;
    gifbean->total_duration = 0;
    gifbean->total_frames = 0;
    GifFiletype->UserData = gifbean;

    gifbean->total_frames = GifFiletype->ImageCount;
    for (int i=0; i < GifFiletype->ExtensionBlockCount; i++) {
        ExtensionBlock *ext =GifFiletype->ExtensionBlocks;
        if (ext->Function == 0xf9) {
            gifbean->total_duration += delay(ext);
        }
    }
    gifbean->frame_duration = gifbean->total_duration / gifbean->total_frames;


    (*env)->ReleaseStringUTFChars(env, path_, path);
    return (long long) GifFiletype;
}

JNIEXPORT jint JNICALL
Java_com_suipu_gifndkdemo_GifLoader_getWidth(JNIEnv *env, jobject instance,
                                                           jlong GifHandler) {

    // TODO
    GifFileType * gifFileType = (GifFileType *) GifHandler;
    return gifFileType->SWidth;

}

JNIEXPORT jint JNICALL
Java_com_suipu_gifndkdemo_GifLoader_getHeight(JNIEnv *env, jobject instance,
                                                            jlong GifHandler) {

    GifFileType * gifFileType = (GifFileType *) GifHandler;
    return gifFileType->SHeight;

}

JNIEXPORT jint JNICALL
Java_com_suipu_gifndkdemo_GifLoader_updateFrame(JNIEnv *env, jobject instance,
                                                              jlong GifHandler, jobject bitmap) {

    // TODO
    GifFileType * gifFileType = (GifFileType *) GifHandler;
    mGifbean *gifbean = gifFileType->UserData;
    AndroidBitmapInfo bitmapInfo;
    void *pixels;
    int ret;
    if (AndroidBitmap_getInfo(env, bitmap,  &bitmapInfo)<0){
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return -1;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0){
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return -1;
    }
    drawFrame(gifFileType,&bitmapInfo, (int *)pixels, gifbean->frame_current, false);
    LOGE("AndroidBitmap_drawFrame() failed ! error=%d");
    AndroidBitmap_unlockPixels(env, bitmap);
    gifbean->frame_current+=1;
    if (gifbean->frame_current >= gifbean->total_frames) {
        gifbean->frame_current = 0;
    }

    return gifbean->frame_duration;
}



#ifdef __cplusplus
}
#endif


