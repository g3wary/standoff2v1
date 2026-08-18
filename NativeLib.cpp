#include <jni.h>
#include <string>

extern "C" {

JNIEXPORT jint JNICALL
Java_com_standoff2_cheat_MemoryScanner_findPid(JNIEnv *env, jobject thiz, jstring name) {
    return 9999; // заглушка
}

JNIEXPORT jboolean JNICALL
Java_com_standoff2_cheat_MemoryScanner_attachProcess(JNIEnv *env, jobject thiz, jint pid) {
    return true;
}

JNIEXPORT void JNICALL
Java_com_standoff2_cheat_MemoryScanner_detachProcess(JNIEnv *env, jobject thiz) {
}

JNIEXPORT jlong JNICALL
Java_com_standoff2_cheat_MemoryScanner_readMemory(JNIEnv *env, jobject thiz, jlong addr) {
    return 0x100;
}

}
