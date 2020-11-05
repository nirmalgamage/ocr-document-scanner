package com.example.ocr_one;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

public class ViewPDF extends AppCompatActivity implements OnPageChangeListener {

    private TextView mTextMessage;
    private com.joanzapata.pdfview.PDFView pdfView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   // mTextMessage.setText(R.string.title_home);
                    CameraActivity.extracted_text_Global="";
                    Intent mainIntent=new Intent(ViewPDF.this,MainActivity.class);
                    startActivity(mainIntent);
                    return true;
                case R.id.navigation_dashboard:
                   // mTextMessage.setText(R.string.title_dashboard);

                    return true;
                case R.id.navigation_notifications:
                   // mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    int pageNumber=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        pdfView=(PDFView)findViewById(R.id.pdfview);



        ////Impl
        pdfView.fromFile(new File(GenerateTextActivity.filePath))
                .defaultPage(pageNumber)
                .showMinimap(false)
                .enableSwipe(true)
                .onPageChange(this)
                .load();



    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber=page;

    }
}
