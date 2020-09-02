package life.mibo.android

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import life.mibo.android.ui.login.RegisterActivity
import org.hamcrest.CoreMatchers.anything
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(RegisterActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("life.mibo.android", appContext.packageName)
    }

    @Test
    fun register_activity_rules() {
        Espresso.onView(withId(R.id.et_first_name)).perform(typeText("Sumeet"), closeSoftKeyboard())

        Espresso.onView(withId(R.id.et_last_name)).perform(typeText("Kumar"))

        Espresso.onView(withId(R.id.et_email)).perform(typeText("sumeet.kumar@mibo.life"))

        Espresso.onView(withId(R.id.et_password)).perform(typeText("123Qwe@@"))

        Espresso.onView(withId(R.id.et_confirm_password)).perform(typeText("123Qwe@@"))

        Espresso.onView(withId(R.id.tv_gender)).perform(click());

        Espresso.onView(withText("Male")).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click());

        Espresso.onView(withId(R.id.tv_dob)).perform(click());

        Espresso.onView(withText("OK")).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click());

        Espresso.onView(withId(R.id.et_city))
            .perform(typeText("Dubai"))

        Espresso.onView(withId(R.id.tv_country)).perform(click());

        //Espresso.onView(withText("India")).inRoot(isDialog())
        //    .check(matches(isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(30, click()));
       // Espresso.onData("India")
        onData(anything()).inAdapterView(withId(R.id.country_dialog_lv)).atPosition(20).perform(click());

        Espresso.onView(withId(R.id.et_phone_number))
            .perform(typeText("58 552 4744"))


    }
}
