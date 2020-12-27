package com.example.lab6.ui.fragments

import android.app.Instrumentation
import android.view.View
import android.widget.RelativeLayout
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.lab6.R
import com.example.lab6.ui.activites.TestActivity
import kotlinx.android.synthetic.main.activity_test.view.*
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

class MediaFragmentTest {

    @get:Rule
    var mActivityTestRule : ActivityTestRule<TestActivity> = ActivityTestRule(TestActivity::class.java)

    private var mActivity: TestActivity? = null

    @Before
    fun setUp() {
        mActivity = mActivityTestRule.activity
    }

    @Test
    fun testLaunch() {
        var rlContainer : RelativeLayout = mActivity!!.findViewById(R.id.test_container)

        assertNotNull(rlContainer)

        var mediaFragment = MediaFragment()

        mActivity?.supportFragmentManager?.beginTransaction()?.add(rlContainer.id, mediaFragment)?.commitAllowingStateLoss()

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        var view : View = mediaFragment.view!!.findViewById(R.id.moviesFragment)

        assertNotNull(view)
    }

    @After
    fun tearDown() {
        mActivity = null
        mActivity = null
    }
}