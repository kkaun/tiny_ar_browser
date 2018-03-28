package com.kkaun.mediator.ui.aug.framework.data

import com.kkaun.mediator.ui.aug.framework.paintables.ARMarker
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Кира on 04.02.2018.
 */

class CacheDataSource() {

    var markersCache = CopyOnWriteArrayList<ARMarker>()
    fun setData(data: CopyOnWriteArrayList<ARMarker>) { this.markersCache = data }
}