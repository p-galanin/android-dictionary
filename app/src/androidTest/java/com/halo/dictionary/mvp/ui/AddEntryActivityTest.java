package com.halo.dictionary.mvp.ui;

import com.halo.dictionary.R;
import com.halo.dictionary.periodic.PreferencesModule;
import com.halo.dictionary.repository.impl.PreferencesStorage;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import dagger.hilt.components.SingletonComponent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@LargeTest
@UninstallModules(PreferencesModule.class)
@HiltAndroidTest
public class AddEntryActivityTest {

    @Rule
    public ActivityTestRule<AddEntryActivity> activityRule = new ActivityTestRule<>(AddEntryActivity.class);

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Inject
    PreferencesStorage preferencesStorage;

    @Before
    public void init() {
        hiltRule.inject();
    }

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

    @Module
    @InstallIn(SingletonComponent.class)
    public static class FakePreferencesModule {

        @Provides
        @Singleton
        PreferencesStorage providePreferencesStorage() {
            return new PreferencesStorage() {
                private final Map<String, Integer> storage = new HashMap<>();

                @Override
                public void saveInt(@NotNull String key, int value) {
                    storage.put(key, value);
                }

                @Override
                public int getInt(@NotNull String key, int defaultValue) {
                    Integer value = storage.get(key);
                    return value == null ? defaultValue : value;
                }
            };
        }
    }
}