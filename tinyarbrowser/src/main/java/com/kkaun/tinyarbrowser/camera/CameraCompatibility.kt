package com.kkaun.mediator.ui.aug.framework.camera

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.Display
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object CameraCompatibility {
    private var getSupportedPreviewSizes: Method? = null
    private var mDefaultDisplay_getRotation: Method? = null

    init { initCompatibility() }

    private fun initCompatibility() {
        try {
            getSupportedPreviewSizes = Camera.Parameters::class.java.getMethod("getSupportedPreviewSizes")
            mDefaultDisplay_getRotation = Display::class.java.getMethod("getRotation")
        } catch (nsme: NoSuchMethodException) {
            Log.d("CameraCompatibility", nsme.cause.toString())
        }
    }

    fun getSupportedPreviewSizes(params: Camera.Parameters): List<Camera.Size>? {
        var retList: List<Camera.Size>? = null
        try {
            val retObj = getSupportedPreviewSizes!!.invoke(params)
            if (retObj != null) {
                retList = retObj as List<Camera.Size>
            }
        } catch (ite: InvocationTargetException) {
            val cause = ite.cause
            when (cause) {
                is RuntimeException -> throw cause
                is Error -> throw cause
                else -> throw RuntimeException(ite)
            }
        } catch (ie: IllegalAccessException) {
            ie.printStackTrace()
        }
        return retList
    }

    fun getRotation(activity: Activity): Int {
        var result = 1
        try {
            val display = (activity.getSystemService(
                    Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val retObj = mDefaultDisplay_getRotation!!.invoke(display)
            if (retObj != null) result = retObj as Int
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }
}