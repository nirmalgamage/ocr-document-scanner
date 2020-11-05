package com.example.ocr_one;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static java.lang.System.out;

public class ImageProcessing {

    public static Bitmap opencvAdaptiveThreshold(Bitmap bitmap){
        Mat tmp = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, tmp);
        Mat gray = new Mat(bitmap.getWidth(), bitmap.getHeight(),     CvType.CV_8UC1);
        Imgproc.cvtColor(tmp, gray, Imgproc.COLOR_RGB2GRAY);
        Mat destination = new Mat(gray.rows(),gray.cols(),gray.type());
        Imgproc.adaptiveThreshold(gray, destination, 255 ,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,15, 15);

        Utils.matToBitmap(destination, bitmap);
        return bitmap;


    }



    public static Bitmap identifyImages(Bitmap btmap){
        Mat mat=new Mat(btmap.getWidth(),btmap.getHeight(),CvType.CV_8UC1);
        Utils.bitmapToMat(btmap,mat);

        Mat gray =new Mat(btmap.getWidth(),btmap.getHeight(),CvType.CV_8UC1);
        Imgproc.cvtColor(mat,gray,Imgproc.COLOR_RGB2GRAY);
        Size kernalSize=new Size(8,8);


        Mat kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,kernalSize );
        Imgproc.dilate(gray,gray,kernel);

        Imgproc.threshold(gray,gray,254,255,Imgproc.THRESH_TOZERO);
        Imgproc.threshold(gray,gray,0,255,Imgproc.THRESH_BINARY_INV);


       Imgproc.morphologyEx(gray,gray,Imgproc.MORPH_CLOSE,kernel);
      Imgproc.morphologyEx(gray,gray,Imgproc.MORPH_OPEN,kernel);

        List<MatOfPoint> contours=new ArrayList<MatOfPoint>();  ///whether vector not shure
        Imgproc.findContours(gray,contours,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE); //new Mat() not sure

        List<MatOfPoint> squares=new ArrayList<MatOfPoint>(contours.size());
        Log.d("square size","size "+squares.size());


        Mat mask=new Mat(gray.rows(),gray.cols(),CvType.CV_8UC1, new Scalar(0));///Scalar not SURe
        Iterator<MatOfPoint> iterator=contours.iterator();
        Iterator<MatOfPoint> square_iterator=squares.iterator();
        int j=0;
        while(iterator.hasNext()){
           MatOfPoint contour=iterator.next();
           if(Imgproc.contourArea(contour)>2000){
               MatOfPoint2f mfo=new MatOfPoint2f(contour.toArray());
               MatOfPoint2f sqo=new MatOfPoint2f();
               Imgproc.approxPolyDP(mfo ,sqo,50,true);
               Imgproc.drawContours(mask,squares,j,new Scalar(255),-1);
               j++;
           }

        }











        //  Mat finalMzat=new Mat();

        Mat finalMzat=new Mat(gray.rows(),gray.cols(),CvType.CV_8UC1);
        mat.copyTo(finalMzat,mask);



        Utils.matToBitmap(gray,btmap);

         return btmap;





    }



    public static void saveImage(Context context, Bitmap bitmap){
       String name = "identify_Img_12345.jpg";
       String path=Environment.getExternalStorageDirectory()+"/OCR-DOC-Scanner/"+name;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }












    ////below are unwanted......as we knew///





    ////////////////////




}
