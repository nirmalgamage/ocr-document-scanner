package com.example.ocr_one;

import android.widget.Button;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void binarize_btn_isCorrect(){

        Button btn=(Button)new CameraActivity().findViewById(R.id.binarize_btn_cam);
        for(int i=1;i<=6;i++) {
            if(i%2==1) {
                assertEquals(true, btn.callOnClick());

            }
            else{
                assertEquals(false,btn.callOnClick());
            }

        }

    }
}
