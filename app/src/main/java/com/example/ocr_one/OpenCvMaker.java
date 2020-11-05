package com.example.ocr_one;

public class OpenCvMaker {

    private static boolean canny;
    private static boolean dilate;
    private static boolean erode;
    private static boolean gray;
    private static boolean normal;

    private native static int makeGray(long matAddrInput, long matAddrOutput);
    private native static int makeDilate(long matAddrInput, long matAddrOutput);
    private native static int makeErode(long matAddrInput, long matAddrOutput);
    private native static int makeCanny(long matAddrInput, long matAddrOutput);

    public static void doStuffWithPic(long matAddrInput, long matAddrOutput){
        if(isDilate()) makeDilate(matAddrInput, matAddrOutput);
        if(isErode()) makeErode(matAddrInput, matAddrOutput);
        if(isGray()) makeGray(matAddrInput, matAddrOutput);
        if(isCanny()) makeCanny(matAddrInput, matAddrOutput);
    }

    public static void toggleCanny() {
        canny = !canny;
    }

    public static void toggleDilate() {
        dilate = !dilate;
    }

    public static void toggleErode() {
        erode = !erode;
    }

    public static void toggleGray() {
        gray = !gray;
    }

    // normal resets all the other modifier
    public static void setNormal() {
        canny = false;
        dilate = false;
        erode = false;
        gray = false;
        normal = true;
    }

    public static boolean isCanny() {
        return canny;
    }

    public static boolean isDilate() {
        return dilate;
    }

    public static boolean isErode() {
        return erode;
    }

    public static boolean isGray() {
        return gray;
    }

    public static boolean isNormal() {
        return normal;
    }

}