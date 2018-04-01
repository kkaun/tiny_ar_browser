package com.kkaun.tinyarbrowser.util

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kkaun.tinyarbrowser.R
import com.kkaun.tinyarbrowser.java.JActivity
import com.kkaun.tinyarbrowser.kotlin.KActivity
import java.util.*

open class SplashActivity : AppCompatActivity() {

    companion object {
        private val PERMISSIONS_REQUEST = 1234
    }
    private val timeoutMillis = 1000
    private var startTimeMillis: Long = 0

    /**
     * By setting this flag to false you make Java Sample Activity running instead of Kotlin
     */
    private var runKotlin: Boolean = true

    private val nextActivityClass: Class<*>
        get() = if (runKotlin) KActivity::class.java
        else JActivity::class.java

    private val requiredPermissions: Array<String>
        get() {
            var permissions: Array<String>? = null
            try {
                permissions = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_PERMISSIONS).requestedPermissions
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return if (permissions == null) {
                arrayOf()
            } else {
                permissions.clone()
            }
        }

    private fun startNextActivity() {
        var delayMillis = timeoutMillis - (System.currentTimeMillis() - startTimeMillis)
        if (delayMillis < 0) delayMillis = 0
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, nextActivityClass))
            finish() }, delayMillis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startTimeMillis = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= 23) checkPermissions()
        else startNextActivity()
    }


    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) checkPermissions()
    }

    @TargetApi(23)
    private fun checkPermissions() {
        val ungrantedPermissions = requiredPermissionsStillNeeded()
        if (ungrantedPermissions.isEmpty()) startNextActivity()
        else requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST)
    }

    @TargetApi(23)
    private fun requiredPermissionsStillNeeded(): Array<String> {
        val permissions = HashSet<String>()
        permissions += requiredPermissions
        val i = permissions.iterator()
        while (i.hasNext()) {
            val permission = i.next()
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(SplashActivity::class.java.simpleName, "Permission: $permission already granted.")
                i.remove()
            } else {
                Log.d(SplashActivity::class.java.simpleName,
                        "Permission: $permission not yet granted.")
            }
        }
        return permissions.toTypedArray()
    }
}