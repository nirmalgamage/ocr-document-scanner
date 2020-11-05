package com.example.ocr_one;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ProgressBar;

import com.googlecode.tesseract.android.TessBaseAPI;

import static com.googlecode.tesseract.android.TessBaseAPI.PageSegMode.PSM_AUTO;


public final class MyTessOCR {
    private String datapath;
//    private static TessBaseAPI mTess = new TessBaseAPI(new TessBaseAPI.ProgressNotifier() {
//        @Override public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
//            ImageActivity.progressDialog.setProgress(progressValues.getPercent());
//            ImageActivity.progressDialog.show();
//        }});
//    Context context;
     Notifier nf=new Notifier();

    private  TessBaseAPI mTess=new TessBaseAPI(nf);

    class Notifier implements TessBaseAPI.ProgressNotifier{
        @Override
        public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {

        }
    }

    Context context;






    public MyTessOCR(Context context) {
        this.context = context;
        datapath =  context.getExternalFilesDir("/").getPath() + "/";
        mTess.setDebug(true);
        mTess.init(datapath, "eng");
        mTess.setPageSegMode(PSM_AUTO);

    }

    public void stopRecognition() {
        mTess.stop();
    }

    public String getOCRResult(Bitmap bitmap) {
        String whitelist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.-!?";
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whitelist);
        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

}
