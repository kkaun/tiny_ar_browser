package com.kkaun.mediator.ui.aug.framework.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import com.kkaun.tinyarbrowser.camera.CameraSurface
import com.kkaun.tinyarbrowser.data.ARDataRepository
import com.kkaun.tinyarbrowser.paintables.Marker
import com.kkaun.tinyarbrowser.view.ARView
import com.kkaun.tinyarbrowser.view.VerticalSeekBar
import com.kkaun.tinyarbrowser.R
import org.jetbrains.anko.ctx
import java.text.DecimalFormat

abstract class ARActivity : OrientationActivity(), OnTouchListener {

    companion object {
        private val TAG = "ARActivity"
        private val FORMAT = DecimalFormat("#.##")
        private val ZOOMBAR_BACKGROUND_COLOR = Color.argb(125, 55, 55, 55)
        private val END_TEXT = FORMAT.format(ARActivity.maxZoom.toDouble()) + " km"
        private val END_TEXT_COLOR = Color.WHITE
        //PREFS
        var menuEnabled = true
        var maxZoom = 5 //in km
        var useCollisionDetection = true //!
        var useRadar = true
        var useZoomBar = true
        var showRadar = false
        var showZoomBar = false
        var calcZoomLevelExponentially = false
        private var switchRadarTitle = "Hide/Show Radar"
        private var switchZoomBarTitle = "Hide/Show Zoom Bar"
        private var exitTitle = "Exit"
    }
    protected var wakeLock: WakeLock? = null
    protected var camScreen: CameraSurface? = null
    protected var mZoomBar: VerticalSeekBar? = null
    protected var menuBtnLayout: RelativeLayout? = null
    protected lateinit var menuBtn: FloatingActionButton
    protected var arView: ARView? = null
    var endLabel: TextView? = null
    open var zoomLayout: LinearLayout? = null

    lateinit var progressBarLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    private val myZoomBarOnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            updateDataOnZoom()
            camScreen!!.invalidate()
        }
        override fun onStartTrackingTouch(seekBar: SeekBar) { }
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            updateDataOnZoom()
            camScreen!!.invalidate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        camScreen = CameraSurface(this@ARActivity)
        setContentView(camScreen)
        prepareARView()
        if(useZoomBar) showZoomBar = true
        if(useRadar) showRadar = true
        prepareMenuBtnLayout()
        setPopupMenu()
        prepareZoomLayout()
        prepareProgressBarLayout()
        preparePowerManager()
    }

    fun prepareARView() {
        arView = ARView(this@ARActivity)
        arView!!.setOnTouchListener(this@ARActivity)
        val augLayout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addContentView(arView, augLayout)
    }

    fun prepareMenuBtnLayout() {
        val dpSize = 60 //in dp
        val scale = ctx.resources.displayMetrics.density
        val pixels = (dpSize * scale + 0.5f).toInt()
        menuBtnLayout = RelativeLayout(this@ARActivity)
        menuBtnLayout?.visibility = if(menuEnabled) RelativeLayout.VISIBLE else RelativeLayout.GONE
        menuBtnLayout?.setBackgroundColor(Color.TRANSPARENT)
        menuBtnLayout?.setPadding(5, 5, 5, 5)
        menuBtn = FloatingActionButton(this@ARActivity)
        menuBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) menuBtn
                .setImageDrawable(resources.getDrawable(R.drawable.ic_view_menu_white_24dp))
        else menuBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_view_menu_white_24dp))

        val fabParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        menuBtnLayout!!.addView(menuBtn, fabParams)
        val menuLayoutParams = FrameLayout.LayoutParams(pixels, pixels,
                Gravity.BOTTOM or Gravity.START)
        addContentView(menuBtnLayout, menuLayoutParams)
    }

    fun prepareProgressBarLayout() {
        val dpSize = 60 //in dp
        val scale = ctx.resources.displayMetrics.density
        val pixels = (dpSize * scale + 0.5f).toInt()
        progressBarLayout = RelativeLayout(this@ARActivity)
        progressBarLayout.visibility = View.GONE
        progressBarLayout.setBackgroundColor(Color.TRANSPARENT)
        progressBar = ProgressBar(this@ARActivity, null, android.R.attr.progressBarStyleLarge)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            progressBar.progressDrawable = (resources.getDrawable(R.drawable.progressbar_round))
        else progressBar.progressDrawable = (resources.getDrawable(R.drawable.progressbar_round))

        val progressBarParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        progressBarLayout.addView(progressBar, progressBarParams)
        val progressLayoutParams = FrameLayout.LayoutParams(pixels, pixels,
                Gravity.CENTER or Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        addContentView(progressBarLayout, progressLayoutParams)
    }

    fun setPopupMenu() {
        if (menuEnabled) {
            menuBtn.visibility = View.VISIBLE
            menuBtn.setOnClickListener {
                val popup = PopupMenu(this@ARActivity, menuBtn)
                if(useZoomBar) popup.menu.add(switchZoomBarTitle)
                if(useRadar) popup.menu.add(switchRadarTitle)
                popup.menu.add(exitTitle)
                popup.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        switchRadarTitle -> {
                            showRadar = !showRadar
                            item.title = switchRadarTitle
                            popup.menu.add(switchRadarTitle) }
                        switchZoomBarTitle -> {
                            showZoomBar =!showZoomBar
                            item.title = switchZoomBarTitle
                            zoomLayout?.visibility = if (showZoomBar)
                                LinearLayout.VISIBLE else LinearLayout.GONE
                            popup.menu.add(switchZoomBarTitle) }
                        exitTitle -> {
                            item.title = exitTitle
                            finish() } }
                    false }
                popup.show() }
        } else menuBtn.visibility = View.GONE
    }

    fun prepareZoomLayout() {
        zoomLayout = LinearLayout(this@ARActivity)
        zoomLayout!!.visibility = if (showZoomBar) LinearLayout.VISIBLE else LinearLayout.GONE
        zoomLayout!!.orientation = LinearLayout.VERTICAL
        zoomLayout!!.setPadding(5, 5, 5, 5)
        zoomLayout!!.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR)
        endLabel = TextView(this@ARActivity)
        endLabel!!.text = END_TEXT
        endLabel!!.setTextColor(END_TEXT_COLOR)
        val zoomTextParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        zoomLayout!!.addView(endLabel, zoomTextParams)
        mZoomBar = VerticalSeekBar(this@ARActivity)
        mZoomBar!!.max = 5
        mZoomBar!!.progress = 1
        mZoomBar!!.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener)
        val zoomBarParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        zoomBarParams.gravity = Gravity.CENTER_HORIZONTAL
        zoomLayout!!.addView(mZoomBar, zoomBarParams)
        val frameLayoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT, Gravity.END)
        addContentView(zoomLayout, frameLayoutParams)
        updateDataOnZoom()
    }


    fun preparePowerManager() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DimScreen")
    }

    public override fun onResume() {
        super.onResume()
        wakeLock!!.acquire()
    }

    public override fun onPause() {
        super.onPause()
        wakeLock!!.release()
    }

    override fun onSensorChanged(evt: SensorEvent) {
        super.onSensorChanged(evt)
        if (evt.sensor.type == Sensor.TYPE_ACCELEROMETER || evt.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            arView!!.postInvalidate()
        }
    }

    protected open fun updateDataOnZoom() {
        val zoomLevel = if (calcZoomLevelExponentially) calcZoomLevelExponentially()
        else calcZoomLevel()
        ARDataRepository.setRadius(zoomLevel)
        ARDataRepository.setZoomLevel(FORMAT.format(zoomLevel.toDouble()))
        ARDataRepository.setZoomProgress(mZoomBar!!.progress)
    }

    override fun onTouch(view: View, me: MotionEvent): Boolean {
        for (marker in ARDataRepository.markers) {
            if (marker.handleClick(me.x, me.y)) {
                if (me.action == MotionEvent.ACTION_UP) onMarkerTouched(marker)
                return true
            }
        }
        return super.onTouchEvent(me)
    }

    protected open fun onMarkerTouched(marker: Marker) {
        Log.w(TAG, "onMarkerTouched() not implemented.")
    }

    private fun calcZoomLevel(): Float {
        return (mZoomBar!!.progress).toFloat()
    }

    private fun calcZoomLevelExponentially(): Float {
        val myZoomLevel = (mZoomBar!!.progress).toFloat()
        val onePercent = maxZoom.toFloat() / 100f
        val tenPercent = 10f * onePercent
        val twentyPercent = 2f * tenPercent
        val eightyPercent = 4f * twentyPercent
        val out: Float
        val percent: Float
        if (myZoomLevel <= 25) {
            percent = myZoomLevel / 25f
            out = onePercent * percent
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel - 25f) / 25f
            out = onePercent + tenPercent * percent
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel - 50f) / 25f
            out = tenPercent + twentyPercent * percent
        } else {
            percent = (myZoomLevel - 75f) / 25f
            out = twentyPercent + eightyPercent * percent
        }
        return out
    }

    fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progressBarLayout.visibility = View.INVISIBLE
        progressBarLayout.visibility = View.GONE
    }

    fun useRadar(use: Boolean) {
        useRadar = use
    }
    fun useZoombar(use: Boolean) {
        useZoomBar = use
    }

    fun showRadar(show: Boolean) {
        showRadar = show
    }
    fun showZoombar(show: Boolean) {
        showZoomBar = show
    }

    fun setMenuEnabled(enabled: Boolean){
        menuEnabled = enabled
    }

    fun setZoomProgress(progress: Int) {
        if(showZoomBar) mZoomBar?.progress = progress
    }
    fun setMaxZoom(max: Int) {
        if(showZoomBar) mZoomBar?.max = max
    }

    fun setMenuSwitchRadarTitle(title: String) {
        switchRadarTitle = title
    }
    fun setMenuSwitchZoomBarTitle(title: String) {
        switchZoomBarTitle = title
    }
    fun setMenuExitActivityTitle(title: String) {
        exitTitle = title
    }

    fun setRadarBodyRadius(radius: Int) {
        if(showRadar) arView?.radar?.setRadarBodyRadius(radius.toFloat())
    }
    fun setRadarBodyColor(alpha: Int, red: Int, green: Int, blue: Int) {
        if(showRadar) arView?.radar?.setRadarBodyColor(alpha, red, green, blue)
    }
    fun setRadarTextColor(red: Int, green: Int, blue: Int) {
        if(showRadar) arView?.radar?.setTextColor(red, green, blue)
    }
    fun setRadarLineColor(red: Int, green: Int, blue: Int) {
        if(showRadar) arView?.radar?.setLineColor(red, green, blue)
    }
    fun setMarkerTextBodyRadius(radius: Int) {
        //if(showRadar) arView?.radar?.setRadarBodyRadius(radius.toFloat())
    }
    fun setMarkerTextBodyColor(alpha: Int, red: Int, green: Int, blue: Int) {
        //if(showRadar) arView?.radar?.setRadarBodyColor(alpha, red, green, blue)
    }
    fun setMarkerTextColor(red: Int, green: Int, blue: Int) {
        //if(showRadar) arView?.radar?.setTextColor(red, green, blue)
    }
    fun setMarkerTextLineColor(red: Int, green: Int, blue: Int) {
        //if(showRadar) arView?.radar?.setLineColor(red, green, blue)
    }
}