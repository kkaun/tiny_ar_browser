package com.kkaun.mediator.ui.aug.framework.projection

class Vector @JvmOverloads constructor(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    private val matrixArray = FloatArray(9)

    @Volatile
    @get:Synchronized
    @set:Synchronized
    var x = 0f
    @Volatile
    @get:Synchronized
    @set:Synchronized
    var y = 0f
    @Volatile
    @get:Synchronized
    @set:Synchronized
    var z = 0f

    init { set(x, y, z) }

    @Synchronized
    operator fun get(array: FloatArray?) {
        if (array == null || array.size != 3)
            throw IllegalArgumentException("get() array must be non-NULL and size of 3")
        array[0] = this.x
        array[1] = this.y
        array[2] = this.z
    }

    fun set(v: Vector?) {
        if (v == null) return
        set(v.x, v.y, v.z)
    }

    fun set(array: FloatArray?) {
        if (array == null || array.size != 3)
            throw IllegalArgumentException("get() array must be non-NULL and size of 3")
        set(array[0], array[1], array[2])
    }

    @Synchronized
    operator fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    @Synchronized
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        val v = other as Vector?
        return v!!.x == this.x && v.y == this.y && v.z == this.z
    }

    @Synchronized
    fun add(x: Float, y: Float, z: Float) {
        this.x += x
        this.y += y
        this.z += z
    }

    fun add(v: Vector?) {
        if (v == null) return
        add(v.x, v.y, v.z)
    }

    fun sub(v: Vector?) {
        if (v == null) return
        add(-v.x, -v.y, -v.z)
    }

    @Synchronized
    fun mult(s: Float) {
        this.x *= s
        this.y *= s
        this.z *= s
    }

    @Synchronized
    fun divide(s: Float) {
        this.x /= s
        this.y /= s
        this.z /= s
    }

    @Synchronized
    fun length(): Float {
        return Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z).toDouble()).toFloat()
    }

    fun norm() {
        divide(length())
    }

    @Synchronized
    fun cross(u: Vector?, v: Vector?) {
        if (v == null || u == null) return

        val x = u.y * v.z - u.z * v.y
        val y = u.z * v.x - u.x * v.z
        val z = u.x * v.y - u.y * v.x
        this.x = x
        this.y = y
        this.z = z
    }

    @Synchronized
    fun prod(m: Matrix?) {
        if (m == null) return
        m[matrixArray]
        val xTemp = matrixArray[0] * this.x + matrixArray[1] * this.y + matrixArray[2] * this.z
        val yTemp = matrixArray[3] * this.x + matrixArray[4] * this.y + matrixArray[5] * this.z
        val zTemp = matrixArray[6] * this.x + matrixArray[7] * this.y + matrixArray[8] * this.z
        this.x = xTemp
        this.y = yTemp
        this.z = zTemp
    }

    @Synchronized
    override fun toString(): String {
        return "x = " + this.x + ", y = " + this.y + ", z = " + this.z
    }
}