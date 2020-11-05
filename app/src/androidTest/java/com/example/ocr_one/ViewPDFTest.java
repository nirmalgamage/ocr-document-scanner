package com.example.ocr_one;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ViewPDFTest {

    @Rule
    public ActivityTestRule<MainActivity> ViewPdfActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mactivity=null;

    @Before
    public void setUp() throws Exception {
        mactivity=ViewPdfActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View pdfView=mactivity.findViewById(R.id.pdfview);
        assertNotNull(pdfView);

    }

    @After
    public void tearDown() throws Exception {
        mactivity=null;
    }
}