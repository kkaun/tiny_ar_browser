package com.kkaun.mediator.ui.aug.framework.camera

import com.kkaun.mediator.ui.aug.framework.projection.Vector

class CameraModel(width: Int, height: Int, init: Boolean) {

    companion object {
        private val tmp1 = FloatArray(3)
        private val tmp2 = FloatArray(3)
        val DEFAULT_VIEW_ANGLE = Math.toRadians(45.0).toFloat()
    }
    var width = 0
        private set
    var height = 0
        private set
    private var distance = 0f

    init { set(width, height, init) }

    operator fun set(width: Int, height: Int, init: Boolean) {
        this.width = width
        this.height = height
    }

    fun setViewAngle(viewAngle: Float) {
        this.distance = this.width / 2 / Math.tan((viewAngle / 2).toDouble()).toFloat()
    }

    fun projectPoint(orgPoint: Vector, prjPoint: Vector, addX: Float, addY: Float) {
        orgPoint[tmp1]
        tmp2[0] = distance * tmp1[0] / -tmp1[2]
        tmp2[1] = distance * tmp1[1] / -tmp1[2]
        tmp2[2] = tmp1[2]
        tmp2[0] = tmp2[0] + addX + (width / 2).toFloat()
        tmp2[1] = -tmp2[1] + addY + (height / 2).toFloat()
        prjPoint.set(tmp2)
    }
}