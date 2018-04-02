package com.kkaun.tinyarbrowser.projection

class Matrix {

    companion object {
        private val tmp = Matrix()
    }
    @Volatile private var a1 = 0f
    @Volatile private var a2 = 0f
    @Volatile private var a3 = 0f
    @Volatile private var b1 = 0f
    @Volatile private var b2 = 0f
    @Volatile private var b3 = 0f
    @Volatile private var c1 = 0f
    @Volatile private var c2 = 0f
    @Volatile private var c3 = 0f

    @Synchronized
    operator fun get(array: FloatArray?) {
        if (array == null || array.size != 9)
            throw IllegalArgumentException("get() array must be non-NULL and size of 9")
        array[0] = this.a1
        array[1] = this.a2
        array[2] = this.a3
        array[3] = this.b1
        array[4] = this.b2
        array[5] = this.b3
        array[6] = this.c1
        array[7] = this.c2
        array[8] = this.c3
    }

    fun set(m: Matrix?) {
        if (m == null) throw NullPointerException()
        set(m.a1, m.a2, m.a3, m.b1, m.b2, m.b3, m.c1, m.c2, m.c3)
    }

    @Synchronized
    operator fun set(a1: Float, a2: Float, a3: Float, b1: Float, b2: Float, b3: Float, c1: Float, c2: Float, c3: Float) {
        this.a1 = a1
        this.a2 = a2
        this.a3 = a3
        this.b1 = b1
        this.b2 = b2
        this.b3 = b3
        this.c1 = c1
        this.c2 = c2
        this.c3 = c3
    }

    fun toIdentity() {
        set(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
    }

    @Synchronized
    fun adj() {
        val a11 = this.a1
        val a12 = this.a2
        val a13 = this.a3
        val a21 = this.b1
        val a22 = this.b2
        val a23 = this.b3
        val a31 = this.c1
        val a32 = this.c2
        val a33 = this.c3
        this.a1 = det2x2(a22, a23, a32, a33)
        this.a2 = det2x2(a13, a12, a33, a32)
        this.a3 = det2x2(a12, a13, a22, a23)
        this.b1 = det2x2(a23, a21, a33, a31)
        this.b2 = det2x2(a11, a13, a31, a33)
        this.b3 = det2x2(a13, a11, a23, a21)
        this.c1 = det2x2(a21, a22, a31, a32)
        this.c2 = det2x2(a12, a11, a32, a31)
        this.c3 = det2x2(a11, a12, a21, a22)
    }

    fun invert() {
        val det = this.det()
        adj()
        mult(1 / det)
    }

    @Synchronized
    fun transpose() {
        val a11 = this.a1
        val a12 = this.a2
        val a13 = this.a3
        val a21 = this.b1
        val a22 = this.b2
        val a23 = this.b3
        val a31 = this.c1
        val a32 = this.c2
        val a33 = this.c3
        this.b1 = a12
        this.a2 = a21
        this.b3 = a32
        this.c2 = a23
        this.c1 = a13
        this.a3 = a31
        this.a1 = a11
        this.b2 = a22
        this.c3 = a33
    }

    private fun det2x2(a: Float, b: Float, c: Float, d: Float): Float {
        return a * d - b * c
    }

    @Synchronized
    fun det(): Float {
        return this.a1 * this.b2 * this.c3 - this.a1 * this.b3 * this.c2 - this.a2 * this.b1 * this.c3 +
                this.a2 * this.b3 * this.c1 + this.a3 * this.b1 * this.c2 - this.a3 * this.b2 * this.c1
    }

    @Synchronized
    fun mult(c: Float) {
        this.a1 = this.a1 * c
        this.a2 = this.a2 * c
        this.a3 = this.a3 * c
        this.b1 = this.b1 * c
        this.b2 = this.b2 * c
        this.b3 = this.b3 * c
        this.c1 = this.c1 * c
        this.c2 = this.c2 * c
        this.c3 = this.c3 * c
    }

    @Synchronized
    fun prod(n: Matrix?) {
        if (n == null) throw NullPointerException()
        tmp.set(this)
        this.a1 = tmp.a1 * n.a1 + tmp.a2 * n.b1 + tmp.a3 * n.c1
        this.a2 = tmp.a1 * n.a2 + tmp.a2 * n.b2 + tmp.a3 * n.c2
        this.a3 = tmp.a1 * n.a3 + tmp.a2 * n.b3 + tmp.a3 * n.c3
        this.b1 = tmp.b1 * n.a1 + tmp.b2 * n.b1 + tmp.b3 * n.c1
        this.b2 = tmp.b1 * n.a2 + tmp.b2 * n.b2 + tmp.b3 * n.c2
        this.b3 = tmp.b1 * n.a3 + tmp.b2 * n.b3 + tmp.b3 * n.c3
        this.c1 = tmp.c1 * n.a1 + tmp.c2 * n.b1 + tmp.c3 * n.c1
        this.c2 = tmp.c1 * n.a2 + tmp.c2 * n.b2 + tmp.c3 * n.c2
        this.c3 = tmp.c1 * n.a3 + tmp.c2 * n.b3 + tmp.c3 * n.c3
    }

    @Synchronized
    override fun toString(): String {
        return "(" + this.a1 + "," + this.a2 + "," + this.a3 + ")" +
                " (" + this.b1 + "," + this.b2 + "," + this.b3 + ")" +
                " (" + this.c1 + "," + this.c2 + "," + this.c3 + ")"
    }
}