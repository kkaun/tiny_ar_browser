package com.kkaun.mediator.ui.aug.framework.data

import com.kkaun.mediator.ui.aug.framework.paintables.IconMarker
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Кира on 04.02.2018.
 */

class CacheDataSource() {

    var markersCache = CopyOnWriteArrayList<IconMarker>()
    fun setData(data: CopyOnWriteArrayList<IconMarker>) { this.markersCache = data }
}