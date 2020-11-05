/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_ocr_one_OpenCvMaker */
#include <opencv2/opencv.hpp>
#include <stdio.h>

using namespace cv;
using namespace std;

#ifndef _Included_com_example_ocr_one_OpenCvMaker
#define _Included_com_example_ocr_one_OpenCvMaker
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_ocr_one_OpenCvMaker
 * Method:    makeGray
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_ocr_1one_OpenCvMaker_makeGray
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_ocr_one_OpenCvMaker
 * Method:    makeDilate
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_ocr_1one_OpenCvMaker_makeDilate
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_ocr_one_OpenCvMaker
 * Method:    makeErode
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_ocr_1one_OpenCvMaker_makeErode
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_ocr_one_OpenCvMaker
 * Method:    makeCanny
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_example_ocr_1one_OpenCvMaker_makeCanny
  (JNIEnv *, jclass, jlong, jlong);


  ///////////////////////
  int toErode(Mat input, Mat& output);
      int toCanny(Mat input, Mat& output);
      int toDilate(Mat input, Mat& output);
      int toGray(Mat input, Mat& output);

#ifdef __cplusplus
}
#endif
#endif