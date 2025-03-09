package com.fomopay.demo.linechart.animation

import android.animation.Animator
import com.fomopay.demo.linechart.LineChartView

/**
 * This interface is for animate LineChartView when it changes.
 */
interface LineChartAnimator {
    /**
     * Returns an Animator that performs the desired animation.
     * Must call LineChart#setAnimationPath for each animation frame.
     *
     * @param lineChartView The LineChart instance
     */
    fun getAnimation(lineChartView: LineChartView): Animator?
}
