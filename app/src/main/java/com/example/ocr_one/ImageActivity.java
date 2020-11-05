package com.example.ocr_one;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.googlecode.tesseract.android.TessBaseAPI;

import static com.googlecode.tesseract.android.TessBaseAPI.PageSegMode.PSM_AUTO;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;

public class ImageActivity extends AppCompatActivity {
    static ImageView imageView;
    Bitmap bitmap;
    Bitmap grayBitmap;
    Button imageRead;
    Button binarize_btn;
    Mat usedMat;
    MyTessOCR mTessOCR;
    //public TessBaseAPI mTess;
    public  ProgressDialog progressDialog;
    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = "ImageActivity";
    public  Bitmap unBinarizedBitMap;
    public static Bitmap binarizedBitMap;
    public boolean completed=false;

    public static boolean textCreated=true;


    ImageActivity.AsyncOcrTask task;

  ////////////

    ////Base Loader Stufff is here....!!!!!!!!!!
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    //javaCameraView.enableView();
                    //usedMat=new Mat();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

        @Override
        public void onPackageInstall(final int operation, final InstallCallbackInterface callback) {
            switch (operation) {
                case InstallCallbackInterface.NEW_INSTALLATION: {
                    Log.i(TAG, "Tried to install OpenCV Manager package, but I still don't believe that you need it...");
                    break;
                }
                default: {
                    super.onPackageInstall(operation, callback);
                    break;
                }
            }
        }
    };
    ////////

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
        setContentView(R.layout.activity_image);
        OpenCVLoader.initDebug();
        openGallery();
        if(imageView==null){
            imageView=(ImageView)findViewById(R.id.imageView);

        }
        else{
            imageView=null;
            imageView=(ImageView)findViewById(R.id.imageView);
        }
        binarize_btn=(Button)findViewById(R.id.binarize_btn);
        prepareTessData();





        mTessOCR=new MyTessOCR(this );
        imageRead=(Button)findViewById(R.id.image_read);

        final Context context=this;

        ////////implementation part
        imageRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                /////////////////////////
               // bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(),Bitmap.Config.ARGB_4444);
               // usedMat=new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC4);

              //  Utils.bitmapToMat(bitmap,usedMat);


                // we'll do stuff here
                 task = new ImageActivity.AsyncOcrTask();
                //incase you need an older implementation.....
                //String extracted_text=task.doInBackground();
                //CameraActivity.extracted_text_Global=CameraActivity.extracted_text_Global+extracted_text;
               // task.execute();
                //end of older implementation..!

                task.execute();
                ///for identify images
                Bitmap bmp=bitmap.copy(bitmap.getConfig(),true);

               Bitmap imageMap= ImageProcessing.identifyImages(bmp);
               ImageProcessing.saveImage(context,imageMap);



                //generated_text.setText(extracted_text);

                // readTextView.setText(extracted_text);
            }
        });



    }


    ////for open from gallery
    public void openGallery(){

        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,100);

    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data!=null){
            Uri imageUri=data.getData();

            String s=getRealPathFromURI(imageUri );
            //imageView.setImageURI(imageUri);
            try{


                bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,30,stream);

              byte[] byteArray = stream.toByteArray();
              Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                bitmap=compressedBitmap;
                unBinarizedBitMap=bitmap.copy(bitmap.getConfig(),false);

                //bitmap=opencvAdaptiveThreshold(bitmap);
                //bitmap=opencvDenoise(bitmap);

               // bitmap=ImageProcessing.toBinarize(bitmap);
                 //bitmap=adjustedContrast(bitmap,70.0);
               //but needed  bitmap=toGrayscale(bitmap);
               // bitmap=GetBinaryBitmap(bitmap);
                // Display the compressed bitmap in ImageView
                imageView.setImageBitmap(bitmap);


                ///
              // bitmap=BitmapFactory.decodeResource(getResources(),R.id.imageView);
                //End of first Try...

                //The second Try....
//                 InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                   bitmap = BitmapFactory.decodeStream(imageStream);
//                  imageStream.close();

                //End of Second Try...

                ////Another Try bro....




                ///It ends....


            }catch(Exception e){
                e.printStackTrace();
            }

          //  imageView.setImageBitmap(bitmap);
        }
        else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!task.isCancelled()){
            Toast.makeText(this,"You stopped the task!",Toast.LENGTH_SHORT).show();

        }
        task.cancel(true);


    }

    public void convertToGray(View view){
        Mat rbga=new Mat();
        Mat grayMat=new Mat();

        BitmapFactory.Options o=new BitmapFactory.Options();
        o.inDither=false;
        o.inSampleSize=4;
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        grayBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        Utils.bitmapToMat(bitmap,rbga);
        Imgproc.cvtColor(rbga,grayMat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(grayMat,grayBitmap);
        imageView.setImageBitmap(grayBitmap);
        usedMat=rbga;
    }

    public Bitmap allImgProcessing(Bitmap bitmap){


        return bitmap;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap opencvAdaptiveThreshold(Bitmap bitmap){
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

    public static boolean isBinarized=false;
    public void binarizeBtn(View view){

            if(binarize_btn.getText().toString().equals("Binarize")){
                bitmap=ImageProcessing.opencvAdaptiveThreshold(bitmap);
                binarize_btn.setText("Undo");
                imageView.setImageBitmap(bitmap);
        }
        else{
            bitmap=unBinarizedBitMap.copy(unBinarizedBitMap.getConfig(),true);

            imageView.setImageBitmap(bitmap);
            binarize_btn.setText("Binarize");
        }

    }

    public void rotateBitmap(View view){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Toast.makeText(this,"Make Sure texts are horizontal and upside!",Toast.LENGTH_SHORT).show();
        bitmap= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(bitmap);
    }


    public Bitmap opencvDenoise(Bitmap bitmap){
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
        // Convert
        Utils.bitmapToMat(bitmap, mat);

        Mat out = new Mat();
        Mat tmp = new Mat();
        Mat kernel = new Mat(new Size(3,3), CvType.CV_8UC1, new Scalar(255));
//        Mat kernel = new Mat(image.size(), CvType.CV_8UC1, new Scalar(255));

       // Imgproc.morphologyEx(mat, tmp, Imgproc.MORPH_OPEN, kernel);
       // Imgproc.morphologyEx(tmp, out, Imgproc.MORPH_CLOSE, kernel);

        Imgproc.morphologyEx(mat, out, Imgproc.MORPH_CLOSE, kernel);
        Utils.matToBitmap(out,bitmap);
        return bitmap;

    }

    //////AcyncOCR part

    private class AsyncOcrTask extends AsyncTask<Void, Void, String> {

        //Bitmap bitmap;
         PdfDocument pdf=new PdfDocument();


        @Override
        protected String doInBackground(Void... voids){
            String temp = "not working";
            //bitmap = Bitmap.createBitmap(usedMat.cols(),usedMat.rows(), Bitmap.Config.ARGB_4444);
            try {
               // bitmap = Bitmap.createBitmap(usedMat.cols(), usedMat.rows(), Bitmap.Config.ARGB_4444);
               // Utils.matToBitmap(usedMat, bitmap);

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting rotation, according to our changes in OpenCV library,
                // because we only changed the rotation in the preview, not in the actual inputStream
                Matrix mtx = new Matrix();
                mtx.postRotate(0);

                // Rotating Bitmap
               // bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);


              /// temp = mTessOCR.getOCRResult(bitmap);
                class Notifier implements TessBaseAPI.ProgressNotifier{
                    @Override
                    public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
                        progressDialog.setProgress(progressValues.getPercent());
                        Log.d("Progress-item", "progress--: "+progressValues.getPercent());
                    }
                }
                Notifier nf=new Notifier();

                TessBaseAPI mTess=new TessBaseAPI(nf);
                String datapath =  ImageActivity.this.getExternalFilesDir("/").getPath() + "/";
                mTess.setDebug(true);
                mTess.init(datapath, "eng");
                mTess.setPageSegMode(PSM_AUTO);

                //String whitelist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-!?";
                String whitelist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-!?;/@%&*()[]{}";

                mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whitelist);

                if(!isCancelled()) {
                    mTess.setImage(bitmap);
                    String result = mTess.getHOCRText(0);
                    String s = Html.fromHtml(result).toString();
                    temp = s;
                }






            }catch(Exception ex){
                Log.d("Exception",ex.getMessage());
            }
            Log.d("taghere",temp);
            if(isCancelled()){
                CameraActivity.extracted_text_Global="";

            }
            else {
                if(temp.equals("")){
                    textCreated=false;
                }
                else{
                    textCreated=true;
                }
                CameraActivity.extracted_text_Global = CameraActivity.extracted_text_Global + temp;
            }
            return temp;

        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(ImageActivity.this);
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
                Intent intent = new Intent(ImageActivity.this, GenerateTextActivity.class);
                startActivity(intent);

            }
            else if(!CameraActivity.extracted_text_Global.equals("")){

                Intent intent = new Intent(ImageActivity.this, GenerateTextActivity.class);
                startActivity(intent);
                Toast.makeText(ImageActivity.this,"No text detected! Redirecting to previous text",Toast.LENGTH_SHORT).show();
            }
            else{





                AlertDialog.Builder builder=new AlertDialog.Builder(ImageActivity.this);
                builder.setTitle("No text");
                builder.setMessage("OCR DOC Scanner cannot identify text within the image you provided!");
                builder.setPositiveButton("Exit the task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ************Open pdf Code
                        Intent i=new Intent(ImageActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    }
                });


                builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when No button clicked

                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);



            }


        }


    }




    //Image Rescaling and noise outing MEthod...just see it it works..
    private Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    /////////

    /////Binarize Image Bitmaps......
    private Bitmap GetBinaryBitmap(Bitmap bitmap_src)
    {
        Bitmap bitmap_new=bitmap_src.copy(bitmap_src.getConfig(), true);



        for(int x=0; x<bitmap_new.getWidth(); x++)
        {
            for(int y=0; y<bitmap_new.getHeight(); y++)
            {
                int color=bitmap_new.getPixel(x, y);
                color=GetNewColor(color);
                bitmap_new.setPixel(x, y, color);
            }
        }

        return bitmap_new;
    }


    private double GetColorDistance(int c1, int c2)
    {
        int db=Color.blue(c1)-Color.blue(c2);
        int dg=Color.green(c1)-Color.green(c2);
        int dr=Color.red(c1)-Color.red(c2);


        double d=Math.sqrt(  Math.pow(db, 2) + Math.pow(dg, 2) +Math.pow(dr, 2)  );
        return d;
    }

    private int GetNewColor(int c)
    {
        double dwhite=GetColorDistance(c,Color.WHITE);
        double dblack=GetColorDistance(c,Color.BLACK);

        if(dwhite<=dblack)
        {
            return Color.WHITE;

        }
        else
        {
            return Color.BLACK;

        }


    }


    /////


}
