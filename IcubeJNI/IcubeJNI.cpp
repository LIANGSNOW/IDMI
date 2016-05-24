// IcubeJNI.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <string>
#include <iostream>
#include "nus_iss_action_JNIInterface.h"
using namespace cv;
JNIEXPORT void JNICALL Java_nus_iss_action_JNIInterface_convertImg
(JNIEnv *env, jobject obj, jstring inputPath, jstring outputPath)
{
	const char* nativeInputPath = env->GetStringUTFChars(inputPath, 0);
	env->ReleaseStringUTFChars(inputPath, nativeInputPath);
	std::cout << nativeInputPath << "\n";
	const char* nativeOutputPath = env->GetStringUTFChars(outputPath, 0);
	env->ReleaseStringUTFChars(outputPath, nativeOutputPath);

	Mat img;
	img = imread(nativeInputPath, 1);
	Mat gray;
	cvtColor(img, gray, CV_BGR2GRAY);
	imwrite(nativeOutputPath, gray);
}
