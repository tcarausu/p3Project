package com.example.aiplant.login;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.aiplant.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignUpTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void signUpTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.click_here_sign_up), withText("Click here for Sign Up"),
                        childAtPosition(
                                allOf(withId(R.id.sign_up),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                3)),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.signUpFragment_email_field),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("tcarausu@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.signUpFragment_email_field), withText("tcarausu@gmail.com"),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.name_last_name),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("name"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.name_last_name), withText("name"),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.pass_field),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.pass_field), withText("123456"),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText6.perform(pressImeActionButton());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.confirm_pass),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.confirm_pass), withText("123456"),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatEditText8.perform(pressImeActionButton());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.terms_and_conditions), withText("Terms and Conditions."),
                        childAtPosition(
                                allOf(withId(R.id.terms_and_conditions_layout),
                                        childAtPosition(
                                                withId(R.id.home_activity),
                                                6)),
                                2),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.I_agree_to_terms_and_conditions), withText("I agree"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.send_registration_instructions), withText("Send"),
                        childAtPosition(
                                allOf(withId(R.id.home_activity),
                                        childAtPosition(
                                                withId(R.id.useThisFragmentID_sign_up),
                                                0)),
                                7),
                        isDisplayed()));
        materialButton2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
