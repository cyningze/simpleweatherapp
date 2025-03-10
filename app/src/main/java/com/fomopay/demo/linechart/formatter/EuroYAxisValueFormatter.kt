package com.fomopay.demo.linechart.formatter

import android.graphics.RectF
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Formatter that adds a € sign after the number.
 */
class EuroYAxisValueFormatter : YAxisValueFormatter {

    private val formatter: NumberFormat = DecimalFormat("#.##€")

    override fun getFormattedValue(value: Float, dataBounds: RectF, gridYDivisions: Int): String {
        return formatter.format(value.toDouble())
    }
}
