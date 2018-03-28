package com.kkaun.mediator.ui.aug.framework.projection

object LowPassFilter {

    private val ALPHA_DEFAULT = 0.333f
    private val ALPHA_STEADY = 0.001f
    private val ALPHA_START_MOVING = 0.6f
    private val ALPHA_MOVING = 0.9f

    fun filter(low: Float, high: Float, current: FloatArray?, previous: FloatArray?): FloatArray {
        if (current == null || previous == null)
            throw NullPointerException("Input and prev float arrays must not be null")
        if (current.size != previous.size)
            throw IllegalArgumentException("Input and prev must be the same length")
        val alpha = computeAlpha(low, high, current, previous)
        for (i in current.indices)
            previous[i] = previous[i] + alpha * (current[i] - previous[i])
        return previous
    }

    private fun computeAlpha(low: Float, high: Float, current: FloatArray, previous: FloatArray): Float {
        if (previous.size != 3 || current.size != 3) return ALPHA_DEFAULT
        val x1 = current[0]
        val y1 = current[1]
        val z1 = current[2]
        val x2 = previous[0]
        val y2 = previous[1]
        val z2 = previous[2]
        val distance = Math.sqrt(Math.pow((x2 - x1).toDouble(), 2.0) +
                Math.pow((y2 - y1).toDouble(), 2.0) +
                Math.pow((z2 - z1).toDouble(), 2.0)).toFloat()
        if (distance < low) return ALPHA_STEADY
        else if (distance >= low || distance < high) return ALPHA_START_MOVING
        return ALPHA_MOVING
    }
}