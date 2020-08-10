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
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
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

        onView(withText("Personal Projects")).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.projects_add_project),isDisplayed())).perform(click())

        onView(withText("Save")).check(matches(isDisplayed()))

        onView(withId(R.id.project_name_edit)).perform(replaceText("ProjectUITest"))
        onView(withId(R.id.project_name_edit)).perform(closeSoftKeyboard())
        onView(withId(R.id.project_save_button)).perform(click())

//        onView(withText("ProjectUITest")).check(ViewAssertions.matches(isDisplayed()))

        onView(childAtPosition(withId(R.id.projects_recycler), 0)).perform(click())
        onView(withId(R.id.gantt_add_task)).perform(scrollTo(),click())
        onView(withId(R.id.task_name_edit)).perform(scrollTo(), replaceText("TaskUITest"))

        onView(withId(R.id.task_duration_warning)).check(matches(Matchers.not(isDisplayed())))

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
        onView(withId(R.id.task_duration_warning)).check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.task_duration_hour_edit)).perform(scrollTo(), replaceText("9999"))
        onView(allOf(withId(R.id.task_duration_hour_edit),withText("23"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.task_duration_minute_edit)).perform(scrollTo(), replaceText("9999"))
        onView(allOf(withId(R.id.task_duration_minute_edit),withText("59"))).check(matches(isDisplayed()))
        onView(withId(R.id.task_duration_warning)).check(matches(Matchers.not(isDisplayed())))

        val view = onView(
            allOf(
                withId(R.id.task_color),
                childAtPosition(childAtPosition(withId(R.id.task_color_recycler), 5), 0)
            )
        )
        view.perform(scrollTo(), click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.task_save), withText("Save"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    21
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        Espresso.pressBack()

        val appCompatButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("Leave"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton3.perform(scrollTo(), click())

        val materialCardView2 = onView(
            allOf(
                withId(R.id.item_project_card),
                childAtPosition(
                    allOf(
                        withId(R.id.item_project_background),
                        childAtPosition(
                            withId(R.id.projects_recycler),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialCardView2.perform(longClick())

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.project_remove_button), withText("Remove"),
                childAtPosition(childAtPosition(
                    withId(R.id.design_bottom_sheet),
                    0
                ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())
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
