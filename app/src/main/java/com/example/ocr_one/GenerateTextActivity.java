package com.example.ocr_one;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GenerateTextActivity extends AppCompatActivity {
    EditText generated_text;
    private static final int STORAGE_CODE=1000;
    Button edit_btn;
    Button copy_btn;
    Button saveaspdf_btn;
    Button append_btn;


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        CameraActivity.extracted_text_Global="";
    }








    @Override
    public boolean onSupportNavigateUp(){
        CameraActivity.extracted_text_Global="";

        finish();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_text);
        generated_text=(EditText) findViewById(R.id.generated_text);
        generated_text.setText(CameraActivity.extracted_text_Global);

        copy_btn=(Button)findViewById(R.id.copy_btn);
        saveaspdf_btn=(Button)findViewById(R.id.savetopdf);
        append_btn=(Button)findViewById(R.id.append_btn);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

            case STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission granted....now we can call to save pdf
                    saveIt();
                }else{
                    Toast.makeText(this,"Permission Denied!!",Toast.LENGTH_SHORT).show();

                }
            }

        }
    }

    public void editText(View view){
        edit_btn=(Button)findViewById(R.id.edit_btn);
        if(edit_btn.getText().equals("Edit")){
            edit_btn.setText("Done");

            generated_text.setClickable(true);
            generated_text.setCursorVisible(true);
            generated_text.setEnabled(true);
            generated_text.setFocusableInTouchMode(true);

            //btn configuration
            copy_btn.setEnabled(false);
            saveaspdf_btn.setEnabled(false);
            append_btn.setEnabled(false);



        }
        else{
            edit_btn.setText("Edit");
            generated_text.setFocusable(false);

            generated_text.setClickable(false);
            generated_text.setCursorVisible(true);

            //btn configuration
            copy_btn.setEnabled(true);
            saveaspdf_btn.setEnabled(true);
            append_btn.setEnabled(true);

            CameraActivity.extracted_text_Global=generated_text.getText().toString();

        }





    }

    public void copyText(View view){

        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Source Text", CameraActivity.extracted_text_Global);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this,"Text copied to clipboard",Toast.LENGTH_SHORT).show();




    }
    public void appendNext(View view){
        Intent intent=new Intent(GenerateTextActivity.this,MainActivity.class);
        startActivity(intent);
        Toast.makeText(this,"Select the next image to append text!", Toast.LENGTH_SHORT).show();

    }
    static String filePath;


    public void savePDF(View view){

        ///permission Stuff
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                //Permission is not granted...request for it
                String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,STORAGE_CODE);
            }
            else{
                //Permission already granted
                saveIt();
            }
        }else{
            saveIt();
        }



        //////permission end stuff

    }
    private void saveIt(){
        Document mdoc=new Document();
        String fileName=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File app_directory=new File(Environment.getExternalStorageDirectory()+"/OCR-DOC-Scanner");
        if(!app_directory.exists()){
            app_directory.mkdir();
        }

          filePath= Environment.getExternalStorageDirectory()+"/OCR-DOC-Scanner/"+fileName+".pdf";

        try{
            PdfWriter.getInstance(mdoc,new FileOutputStream(filePath));
            mdoc.open();
            String acquiredText=generated_text.getText().toString();
            mdoc.add(new Paragraph(acquiredText));

            mdoc.close();
            Toast.makeText(this,fileName+" is saved to "+filePath,Toast.LENGTH_SHORT).show();

            //************ Next is Yes NO Dialog for Open the PDF

            AlertDialog.Builder builder=new AlertDialog.Builder(GenerateTextActivity.this);
            builder.setTitle("Open PDF");
            builder.setMessage("Successfully created the PDF document. \n Would you like to open it?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // ************Open pdf Code
                    Intent i=new Intent(GenerateTextActivity.this,ViewPDF.class);
                    startActivity(i);

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when No button clicked

                }
            });

            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();








        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error!!!",Toast.LENGTH_SHORT).show();

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error!!!",Toast.LENGTH_SHORT).show();

        }
    }

}
