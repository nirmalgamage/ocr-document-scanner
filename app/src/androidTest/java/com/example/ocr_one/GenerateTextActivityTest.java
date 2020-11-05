package com.example.ocr_one;

import android.graphics.Camera;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static org.junit.Assert.*;

public class GenerateTextActivityTest {

    @Rule
    public ActivityTestRule<GenerateTextActivity> generateTextActivityTestRule=new ActivityTestRule<GenerateTextActivity>(GenerateTextActivity.class);

    private GenerateTextActivity mactivity=null;

    @Before
    public void setUp() throws Exception {
        mactivity=generateTextActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View editTextview=mactivity.findViewById(R.id.generated_text);
        assertNotNull(editTextview);
        View copybtnView=mactivity.findViewById(R.id.copy_btn);
        assertNotNull(copybtnView);


    }

    @After
    public void tearDown() throws Exception {
        mactivity=null;
    }

    @Test
    public void onBackPressed() {
        assertEquals("", CameraActivity.extracted_text_Global);

    }

    @Test
    public void editText() {
        View editbtnView=mactivity.findViewById(R.id.edit_btn);
        assertNotNull(editbtnView);

    }

    @Test
    public void appendNext() {
    }


}