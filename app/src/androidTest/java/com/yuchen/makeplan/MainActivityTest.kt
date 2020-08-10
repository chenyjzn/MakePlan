package com.yuchen.makeplan

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        Thread.sleep(5000)

        onView(withText("Personal Projects")).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.projects_add_project),isDisplayed())).perform(click())

        onView(withText("Save")).check(matches(isDisplayed()))
        onView(withText("Remove")).check(matches(not(isDisplayed())))

        onView(withId(R.id.project_name_edit)).perform(replaceText("ProjectUITest"))
        onView(withId(R.id.project_name_edit)).perform(closeSoftKeyboard())
        onView(withId(R.id.project_save_button)).perform(click())

        onView(childAtPosition(withId(R.id.projects_recycler), 0)).perform(click())
        onView(withId(R.id.gantt_add_task)).perform(scrollTo(),click())
        onView(withId(R.id.task_name_edit)).perform(scrollTo(), replaceText("TaskUITest"))

        onView(withId(R.id.task_duration_warning)).check(matches(not(isDisplayed())))

        onView(withId(R.id.task_duration_day_edit)).perform(scrollTo(), replaceText(""))
        onView(allOf(withId(R.id.task_duration_day_edit),withText("0"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(isDisplayed()))

        onView(withId(R.id.task_duration_hour_edit)).perform(scrollTo(), replaceText(""))
        onView(allOf(withId(R.id.task_duration_hour_edit),withText("0"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(isDisplayed()))

        onView(withId(R.id.task_duration_minute_edit)).perform(scrollTo(), replaceText(""))
        onView(allOf(withId(R.id.task_duration_minute_edit),withText("0"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(isDisplayed()))

        onView(withId(R.id.task_duration_day_edit)).perform(scrollTo(), replaceText("9999"))
        onView(allOf(withId(R.id.task_duration_day_edit),withText("999"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(not(isDisplayed())))

        onView(withId(R.id.task_duration_hour_edit)).perform(scrollTo(), replaceText("9999"))
        onView(allOf(withId(R.id.task_duration_hour_edit),withText("23"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(not(isDisplayed())))

        onView(withId(R.id.task_duration_minute_edit)).perform(scrollTo(), replaceText("9999"))
        onView(allOf(withId(R.id.task_duration_minute_edit),withText("59"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(not(isDisplayed())))

        onView(childAtPosition(withId(R.id.task_color_recycler), 0)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 1)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 2)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 3)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 4)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 5)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 6)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 7)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 8)).perform(click())
        onView(childAtPosition(withId(R.id.task_color_recycler), 9)).perform(click())

        onView(withId(R.id.task_start_date_edit)).perform(scrollTo(), click())
        onView(withText("CANCEL")).check(matches(isDisplayed()))
        onView(withText("OK")).check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText("Cancel"))).perform(scrollTo(), click())

        onView(withId(R.id.task_start_time_edit)).perform(scrollTo(), click())
        onView(withText("CANCEL")).check(matches(isDisplayed()))
        onView(withText("OK")).check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText("Cancel"))).perform(scrollTo(), click())

        onView(withId(R.id.task_end_date_edit)).perform(scrollTo(), click())
        onView(withText("CANCEL")).check(matches(isDisplayed()))
        onView(withText("OK")).check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText("Cancel"))).perform(scrollTo(), click())

        onView(withId(R.id.task_end_time_edit)).perform(scrollTo(), click())
        onView(withText("CANCEL")).check(matches(isDisplayed()))
        onView(withText("OK")).check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button2), withText("Cancel"))).perform(scrollTo(), click())

        onView(withId(R.id.task_save)).perform(scrollTo(), click())

        Espresso.pressBack()

        onView(withText("Cancel")).check(matches(isDisplayed()))
        onView(withText("Leave")).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).perform(scrollTo(), click())

        onView(withId(R.id.gantt_save)).perform(scrollTo(), click())

        onView(childAtPosition(withId(R.id.projects_recycler), 0)).perform(longClick())

        onView(withText("Save")).check(matches(isDisplayed()))
        onView(withText("Remove")).check(matches(isDisplayed()))

        onView(withId(R.id.project_remove_button)).perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
