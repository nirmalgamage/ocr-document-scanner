package com.example.ocr_one;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule.*;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class CameraActivityTest {

    @Rule
    public IntentsTestRule<CameraActivity> intentsRule = new IntentsTestRule<>(CameraActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void validateCameraAction(){

//        Bitmap bitmap = BitmapFactory.decodeResource(
//                InstrumentationRegistry.getTargetContext().getResources(),
//                R.mipmap.ic_launcher);
//
//
//
//        Intent dataResult=new Intent();
//        dataResult.putExtra("data",bitmap);
//
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, dataResult);
//
//        intending(toPackage("com.android.camera2")).respondWith(result);

       // onView(withId(R.id.start_cam)).perform(click());

      //  intended(toPackage("com.android.camera2"));







    }



    @After
    public void tearDown() throws Exception {
    }
}