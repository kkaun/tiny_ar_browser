package com.kkaun.mediator.ui.aug.framework.projection

import com.kkaun.mediator.ui.aug.framework.util.getAngle

object PitchAzimuthCalculator {

    private val looking = Vector()
    private val lookingArray = FloatArray(3)

    @Volatile
    @get:Synchronized
    var azimuth = 0f
        private set

    @Volatile
    @get:Synchronized
    var pitch = 0f
        private set

    @Synchronized
    fun calcPitchBearing(rotationM: Matrix?) {
        if (rotationM == null) return
        looking.set(0f, 0f, 0f)
        rotationM.transpose()
        looking.set(1f, 0f, 0f)
        looking.prod(rotationM)
        looking.get(lookingArray)
        PitchAzimuthCalculator.azimuth = (getAngle(0f, 0f,
                lookingArray[0], lookingArray[2]) + 360) % 360
        rotationM.transpose()
        looking.set(0f, 1f, 0f)
        looking.prod(rotationM)
        looking.get(lookingArray)
        PitchAzimuthCalculator.pitch = -getAngle(0f, 0f,
                lookingArray[1], lookingArray[2])
    }
}