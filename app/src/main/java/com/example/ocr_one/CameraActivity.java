package com.example.ocr_one;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.InstallCallbackInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.googlecode.tesseract.android.TessBaseAPI.PageSegMode.PSM_AUTO;
//import org.opencv.android.JavaCameraView;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;

public class CameraActivity extends AppCompatActivity  {
    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = "CameraActivity";
    public  static final int RequestPermissionCode  = 1 ;

    Button makeRead;
    Button binarizeBtn_cam;
    Button rotateBtn_cam;

    TextView readTextView;
    ImageView camera_imageview;
    public static String extracted_text_Global="";
    //Bitmap bitmap;
    EditText generated_text;
    JavaCameraView javaCameraView;
    Mat usedMat;
    MyTessOCR mTessOCR;
    Bitmap bitmap;
    public Bitmap unbinarizedBitmap_cam;
    public Bitmap binarizedBitmap_cam;
    public static ArrayList<Bitmap> bitmapArray;
    Button makeNormal, makeGray, makeCanny, makeDilate, makeErode;
    File tempr_output;
    public ProgressDialog progressDialog;

    AsyncOcrTask task;

    public static boolean textCreated=true;







    // Override the BaseLoaderCallback-method from OpenCV, to manage initialization properly




        //disable installation
        public void prepareTessData() {
            try {
                File dir = getExternalFilesDir(TESS_DATA);
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                    }
                }
                String fileList[] = getAssets().list("tessdata");

                for (String fileName : fileList) {
                    String pathToDataFile = dir + "/" + fileName;
                    if (!(new File(pathToDataFile)).exists()) {
                        InputStream in = getAssets().open("tessdata/" + fileName);
                        OutputStream out = new FileOutputStream(pathToDataFile);
                        byte[] buff = new byte[1024];
                        int len;
                        while ((len = in.read(buff)) > 0) {
                            out.write(buff, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        OpenCVLoader.initDebug();
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        openCamera();
        prepareTessData();


        mTessOCR = new MyTessOCR(this);

      //  javaCameraView = findViewById(R.id.cam_view);
      //  javaCameraView.setVisibility(SurfaceView.VISIBLE);
       // javaCameraView.setCvCameraViewListener(this);

        ///buttons here!!!!
        //makeNormal = (Button) findViewById(R.id.normal_mode);
        //makeGray = (Button) findViewById(R.id.gray_mode);
        //makeCanny = (Button) findViewById(R.id.canny_mode);
        //makeDilate = (Button) findViewById(R.id.dilate_mode);
        //makeErode = (Button) findViewById(R.id.erose_mode);
        makeRead = (Button) findViewById(R.id.camera_image_read);
        binarizeBtn_cam=(Button)findViewById(R.id.binarize_btn_cam);
        rotateBtn_cam=(Button)findViewById(R.id.rotate_btn_cam);
        camera_imageview=(ImageView)findViewById(R.id.camera_imageView);
       // readTextView = (TextView) findViewById(R.id.read_text_view);
        generated_text=(EditText)findViewById(R.id.generated_text);

        final Context context=this;


        makeRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we'll do stuff here
                task = new AsyncOcrTask();

                task.execute();

                Bitmap bmp=bitmap.copy(bitmap.getConfig(),true);

                Bitmap imageMap= ImageProcessing.identifyImages(bmp);
                ImageProcessing.saveImage(context,imageMap);




                //generated_text.setText(extracted_text);

               // readTextView.setText(extracted_text);
            }
        });

//        makeNormal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenCvMaker.setNormal();
//
//            }
//        });
//        makeGray.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenCvMaker.toggleGray();
//            }
//        });
//        makeDilate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenCvMaker.toggleDilate();
//            }
//        });
//        makeErode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenCvMaker.toggleErode();
//            }
//        });
//        makeCanny.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OpenCvMaker.toggleCanny();
//            }
//        });


    }


    ////
    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        tempr_output=new File(dir, "CameraContentDemo.jpeg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempr_output));

        startActivityForResult(intent, 100);

        //Intent it=new Intent("com.android.camera.action.CROP");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
           //Uri imageUri=data.getData();
          // bitmap=(Bitmap)data.getExtras().get("data");

            //For Crop
           // Uri cropimageuri=Uri.fromFile(tempr_output);
           // CropImage.activity(cropimageuri).start(this);


            ///
         ////The previous implmentation for full image
           String filePath = tempr_output.getAbsolutePath();

              bitmap=BitmapFactory.decodeFile(filePath);

           Log.d("pathOfImage",filePath);

             ///end of implementation for full image

            try{


                //for compression....
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,30,stream);

                byte[] byteArray = stream.toByteArray();
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                bitmap=compressedBitmap;
                camera_imageview.setImageBitmap(bitmap);
                unbinarizedBitmap_cam=bitmap.copy(bitmap.getConfig(),false);

                // Display the compressed bitmap in ImageView



            }catch(Exception e){
                Log.d("Wtf",e.getMessage());
                e.printStackTrace();
            }







        }
        else if(requestCode==400){



        }
        else{
            finish();
        }
    }




    public boolean binarizeBtn_cam(View view){
        if(binarizeBtn_cam.getText().toString().equals("Binarize")){
            bitmap=ImageProcessing.opencvAdaptiveThreshold(bitmap);
            binarizeBtn_cam.setText("Undo");
            camera_imageview.setImageBitmap(bitmap);
            return true;

        }
        else{
            bitmap=unbinarizedBitmap_cam.copy(unbinarizedBitmap_cam.getConfig(),true);

            camera_imageview.setImageBitmap(bitmap);
            binarizeBtn_cam.setText("Binarize");
            return false;
        }


    }

    public void rotateBitmap_cam(View view){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Toast.makeText(this,"Make Sure texts are horizontal and upside!",Toast.LENGTH_SHORT).show();
        bitmap= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        camera_imageview.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if(!task.isCancelled()){
            Toast.makeText(this,"You stopped the task!",Toast.LENGTH_SHORT).show();

        }
        task.cancel(true);

        finish();



    }

    ////////////////////////////////////////////////////////////////////
    private  class AsyncOcrTask extends AsyncTask<Void, Void, String> {

        //Bitmap bitmap;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(CameraActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Extract Text");
            progressDialog.setMessage("Completing...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            //progressDialog.setProgress(0);
            progressDialog.show();
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();



            if(textCreated) {
                Intent intent = new Intent(CameraActivity.this, GenerateTextActivity.class);
                startActivity(intent);

            }
            else if(!extracted_text_Global.equals("")){
                Toast.makeText(CameraActivity.this,"No text detected! Redirecting to previous text",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CameraActivity.this, GenerateTextActivity.class);
                startActivity(intent);
            }
            else{





                AlertDialog.Builder builder=new AlertDialog.Builder(CameraActivity.this);
                builder.setTitle("No text");
                builder.setMessage("OCR DOC Scanner cannot identify text within the image you provided!");
                builder.setPositiveButton("Exit the task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ************Open pdf Code
                        //Intent i=new Intent(CameraActivity.this,MainActivity.class);
                        //startActivity(i);
                        finish();

                    }
                });

                builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when No button clicked

                    }
                });
                builder.setCancelable(false);

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();



            }





        }

        @Override
        protected String doInBackground(Void... voids){
            String temp = "not working";
            try {

                int w = bitmap.getWidth();
                int h = bitmap.getHeight();


                Matrix mtx = new Matrix();
                mtx.postRotate(0);

                //progress implementation

                class Notifier implements TessBaseAPI.ProgressNotifier{
                    @Override
                    public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
                        progressDialog.setProgress(progressValues.getPercent());
                        Log.d("Progress-item", "progress--: "+progressValues.getPercent());
                    }
                }
                Notifier nf=new Notifier();

                TessBaseAPI mTess=new TessBaseAPI(nf);
                String datapath =  CameraActivity.this.getExternalFilesDir("/").getPath() + "/";
                mTess.setDebug(true);
                mTess.init(datapath, "eng");
                mTess.setPageSegMode(PSM_AUTO);

               // String whitelist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-!?";
                String whitelist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-!?;/@%&*()[]{}";

                mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whitelist);

                if(!isCancelled()) {

                    mTess.setImage(bitmap);
                    String result = mTess.getHOCRText(0);
                    String s = Html.fromHtml(result).toString();
                    temp = s;
                }








                //temp = mTessOCR.getOCRResult(bitmap);
            }catch(Exception ex){
                Log.d("Exception",ex.getMessage());
            }

            if(isCancelled()){
                extracted_text_Global="";

            }
            else {
                if(temp.equals("")){
                    textCreated=false;

                }
                else{
                    textCreated=true;
                }
                extracted_text_Global = extracted_text_Global + temp;
                Log.d("taghere", temp);
            }

            return temp;
        }
    }

    /////permission stuff

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,
                Manifest.permission.CAMERA))
        {

            Toast.makeText(CameraActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(CameraActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(CameraActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(CameraActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }




}






