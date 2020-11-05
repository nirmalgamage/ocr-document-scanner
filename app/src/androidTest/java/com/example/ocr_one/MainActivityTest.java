package com.example.ocr_one;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import static android.support.test.espresso.Espresso.onView;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;


public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mactivity = null;

    @Before
    public void setUp() throws Exception {
        mactivity = mainActivityActivityTestRule.getActivity();

    }

    @Test
    public void testLaunch() {
        View view_cam = mactivity.findViewById(R.id.start_cam);
        View view_gallery = mactivity.findViewById(R.id.open_gallery);
        assertNotNull(view_cam);
        assertNotNull(view_gallery);

    }

    @After
    public void tearDown() throws Exception {
        mactivity = null;
    }
}