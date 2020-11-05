package com.example.ocr_one;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import static android.support.test.espresso.Espresso.onView;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class MainActivityTestForActivityLaunchings {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mactivity=null;
    Instrumentation.ActivityMonitor monitor_C=getInstrumentation().addMonitor(CameraActivity.class.getName(),null ,false);
    Instrumentation.ActivityMonitor monitor_i=getInstrumentation().addMonitor(ImageActivity.class.getName(),null ,false);



    @Before
    public void setUp() throws Exception {
        mactivity=mainActivityActivityTestRule.getActivity();

    }

    @Test
    public void testLaunchForCameraActivity(){
        assertNotNull(mactivity.findViewById(R.id.start_cam));
        onView(withId(R.id.start_cam)).perform(click());

        Activity cameraActivity=getInstrumentation().waitForMonitorWithTimeout(monitor_C,5000);
        assertNotNull(cameraActivity);

    }

    @Test
    public void testLaunchForImageActivity(){
        assertNotNull(mactivity.findViewById(R.id.open_gallery));
        onView(withId(R.id.open_gallery)).perform(click());
        Activity imageActivity=getInstrumentation().waitForMonitorWithTimeout(monitor_i,5000);
        assertNotNull(imageActivity);


    }

    @After
    public void tearDown() throws Exception {
        mactivity=null;
    }
}