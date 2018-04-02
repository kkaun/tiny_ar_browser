package com.kkaun.tinyarbrowser.util


fun getAngle(center_x: Float, center_y: Float, post_x: Float, post_y: Float): Float {
    val tmpv_x = post_x - center_x
    val tmpv_y = post_y - center_y
    val d = Math.sqrt((tmpv_x * tmpv_x + tmpv_y * tmpv_y).toDouble()).toFloat()
    val cos = tmpv_x / d
    var angle = Math.toDegrees(Math.acos(cos.toDouble())).toFloat()

    angle = if (tmpv_y < 0) angle * -1 else angle

    return angle
}


fun calcColorRange(color: Int): Int {
    return if (color in 0..255) color else 255
}
