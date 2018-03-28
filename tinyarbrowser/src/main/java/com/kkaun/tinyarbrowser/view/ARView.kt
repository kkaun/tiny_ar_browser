package com.kkaun.mediator.ui.aug.framework.view

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.kkaun.mediator.ui.aug.framework.activity.ARActivity
import com.kkaun.mediator.ui.aug.framework.data.ARDataRepository
import com.kkaun.mediator.ui.aug.framework.paintables.Marker
import com.kkaun.mediator.ui.aug.framework.paintables.Radar
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ARView(context: Context) : View(context) {

    companion object {
        private val drawing = AtomicBoolean(false)
        private val locationArray = FloatArray(3)
        private val cache = ArrayList<Marker>()
        private val updated = TreeSet<Marker>()
        private val COLLISION_ADJUSTMENT = 100

        private fun adjustForCollisions(canvas: Canvas, collection: List<Marker>) {
            updated.clear()
            for (marker1 in collection) {
                if (updated.contains(marker1) || !marker1.isInView) continue
                var collisions = 1
                for (marker2 in collection) {
                    if (marker1 == marker2 || updated.contains(marker2) || !marker2.isInView) continue
                    if (marker1.isMarkerOnMarker(marker2)) {
                        marker2.location.get(locationArray)
                        val y = locationArray[1]
                        val h = (collisions * COLLISION_ADJUSTMENT).toFloat()
                        locationArray[1] = y + h
                        marker2.location.set(locationArray)
                        marker2.update(canvas, 0f, 0f)
                        collisions++
                        updated.add(marker2)
                    }
                }
                updated.add(marker1)
            }
        }
    }

    val radar = Radar()

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        if (drawing.compareAndSet(false, true)) {
            var collection = ARDataRepository.markers
            cache.clear()
            for (marker in collection) {
                marker.update(canvas, 0f, 0f)
                if (marker.isOnRadar) cache.add(marker)
            }
            collection = cache
            if (ARActivity.useCollisionDetection) adjustForCollisions(canvas, collection)
            val iter = collection.listIterator(collection.size)
            while (iter.hasPrevious()) {
                val marker = iter.previous()
                marker.draw(canvas)
            }
            if (ARActivity.showRadar) radar.draw(canvas)
            drawing.set(false)
        }
    }
}