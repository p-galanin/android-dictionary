package com.halo.dictionary.mvp.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.halo.dictionary.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AddEntryActivityTest {

    @Rule
    public ActivityTestRule<AddEntryActivity> activityRule = new ActivityTestRule<>(AddEntryActivity.class);

    @Test
    public void testActivityUi() {

        onView(withId(R.id.buttonSaveEntry))
                .perform(ViewActions.click());

        assertFalse(this.activityRule.getActivity().isFinishing() || this.activityRule.getActivity().isDestroyed());

        onView(withId(R.id.tvWord))
                .perform(ViewActions.replaceText("hello"))
                .check(ViewAssertions.matches(ViewMatchers.withText("hello")));

        onView(withId(R.id.tvTranslation))
                .perform(ViewActions.replaceText("привет"))
                .check(ViewAssertions.matches(ViewMatchers.withText("привет")));

        onView(withId(R.id.buttonSaveEntry))
                .perform(ViewActions.click());

        assertTrue(this.activityRule.getActivity().isFinishing() || this.activityRule.getActivity().isDestroyed());
    }
}