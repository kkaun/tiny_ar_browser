package com.kkaun.tinyarbrowser.camera

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraSurface(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    companion object {
        private var camera: Camera? = null
    }

    init {
        try {
            val holder = holder
            holder.addCallback(this)
            //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            if (camera != null) {
                try { camera!!.stopPreview()
                } catch (ex: Exception) { ex.printStackTrace() }
                try { camera!!.release()
                } catch (ex: Exception) { ex.printStackTrace() }
                camera = null
            }
            camera = Camera.open()
            camera!!.setPreviewDisplay(holder)
        } catch (ex: Exception) {
            try {
                if (camera != null) {
                    try { camera!!.stopPreview()
                    } catch (ex1: Exception) { ex.printStackTrace() }
                    try { camera!!.release()
                    } catch (ex2: Exception) { ex.printStackTrace() }
                    camera = null
                }
            } catch (ex3: Exception) { ex.printStackTrace() }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        try {
            if (camera != null) {
                try { camera!!.stopPreview()
                } catch (ex: Exception) { ex.printStackTrace() }
                try { camera!!.release()
                } catch (ex: Exception) { ex.printStackTrace() }
                camera = null
            }
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        try {
            val parameters = camera!!.parameters
            try {
                var supportedSizes: List<Camera.Size>? = null
                supportedSizes = CameraCompatibility.getSupportedPreviewSizes(parameters)
                val ff = w.toFloat() / h
                var bff = 0f
                var bestw = 0
                var besth = 0
                for (element in supportedSizes!!) {
                    val cff = element.width.toFloat() / element.height
                    if (ff - cff <= ff - bff && element.width <= w && element.width >= bestw) {
                        bff = cff
                        bestw = element.width
                        besth = element.height
                    }
                }
                if (bestw == 0 || besth == 0) {
                    bestw = 480
                    besth = 320
                }
                parameters.setPreviewSize(bestw, besth)
            } catch (ex: Exception) {
                parameters.setPreviewSize(480, 320)
            }
            camera!!.parameters = parameters
            camera!!.startPreview()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}