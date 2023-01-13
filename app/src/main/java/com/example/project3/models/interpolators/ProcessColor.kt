package com.example.project3.models.interpolators

import com.example.project3.models.colorApimodels.Dominant


class ProcessColor(color: Dominant) {
    private val color: Dominant

    init {
        this.color = color
    }

    fun computeGreen(): Float {
        val x = ((color.r + color.b) / 2).toFloat()
        val del = color.g - x
        if (del < 0) return 0.00f
        return if (x == 0.00f) del
        else (del / x)
    }

    fun computeBrown(): Float {
        val x = ((color.g + color.b) / 2).toFloat()
        val del = color.r - x
        if (del < 0) return 0.00f
        return if (x == 0.00f) del
        else (del / x)
    }
    fun getRgb():String{
        return "(${color.r} ,${color.g} ,${color.b})"
    }
}