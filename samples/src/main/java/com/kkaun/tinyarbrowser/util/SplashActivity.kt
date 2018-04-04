package com.kkaun.tinyarbrowser.util

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.kkaun.tinyarbrowser.R
import com.kkaun.tinyarbrowser.data.ARDataRepository
import com.kkaun.tinyarbrowser.java.JActivity
import com.kkaun.tinyarbrowser.kotlin.KActivity
import java.util.*

open class SplashActivity : AppCompatActivity(), ColdLocationRequestHelper.ColdLocationReceiver {

    companion object {
        private val PERMISSIONS_REQUEST = 1234
    }
    private val timeoutMillis = 500
    private var startTimeMillis: Long = 0
    private lateinit var progressHolder: RelativeLayout

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
            try { permissions = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_PERMISSIONS).requestedPermissions
            } catch (e: PackageManager.NameNotFoundException) { e.printStackTrace() }
            return if (permissions == null) arrayOf()
            else permissions.clone()
        }

    private fun requestLocationForResult() {
        ColdLocationRequestHelper.requestColdLocationUpdate(this@SplashActivity, this@SplashActivity)
    }

    private fun startNextActivity() {
        var delayMillis = timeoutMillis - (System.currentTimeMillis() - startTimeMillis)
        if (delayMillis < 0) delayMillis = 0
        Handler().postDelayed({
            val b = Bundle()
            b.putParcelableArrayList("place_ar_markers", getFreshMockData(
                    ARDataRepository.getCurrentLocation()))
            val intent = Intent(this@SplashActivity, nextActivityClass)
            intent.putExtras(b)
            startActivity(intent)
            progressHolder.visibility = View.INVISIBLE
            progressHolder.visibility = View.GONE
            finish() }, delayMillis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progressHolder = findViewById(R.id.progress_bar_holder)
        progressHolder.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= 23) checkPermissions()
        else requestLocationForResult()
    }


    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) checkPermissions()
    }

    @TargetApi(23)
    private fun checkPermissions() {
        val ungrantedPermissions = requiredPermissionsStillNeeded()
        if (ungrantedPermissions.isEmpty()) requestLocationForResult()
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
            } else Log.d(SplashActivity::class.java.simpleName, "Permission: $permission not yet granted.")
        }
        return permissions.toTypedArray()
    }


    override fun onColdLocationReceived(location: Location) {
        ARDataRepository.setCurrentLocation(location)
        startNextActivity()
    }

    override fun onColdLocationFailure() {
        Toast.makeText(this@SplashActivity, "Cold location retrieving failed! " +
                "Perhaps some phone location services are disabled", 2000.toInt()).show()
    }
    override fun onProviderDisabled() {
        Toast.makeText(this@SplashActivity, "Cold location retrieving failed! " +
                "Perhaps some phone location services are disabled", 2000.toInt()).show()
    }
}