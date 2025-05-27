package com.example.watchme;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager2.widget.ViewPager2;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void testAutoSlideBanner() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPagerBanner);
            int initialItem = viewPager.getCurrentItem();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                int nextItem = viewPager.getCurrentItem();
                assertTrue(nextItem > initialItem);
            }, 6000);
        });
    }

    @Test
    public void testGenresLayoutIsDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.layoutGenres)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testUserSwipeDelaysAutoSlide() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPagerBanner);
            int startItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(startItem + 1, true);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                int nextItem = viewPager.getCurrentItem();
                assertTrue(nextItem > startItem + 1);
            }, 6000);
        });
    }


    @Test
    public void testCategoryIsVisible() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.layoutGenres)).check(matches(isDisplayed()));
        }
    }
}