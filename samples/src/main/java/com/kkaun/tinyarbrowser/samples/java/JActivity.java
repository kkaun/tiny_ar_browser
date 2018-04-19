package com.kkaun.tinyarbrowser.samples.java;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.kkaun.tinyarbrowser.activity.ARActivity;
import com.kkaun.tinyarbrowser.data.ARDataRepository;
import com.kkaun.tinyarbrowser.data.CacheDataSource;
import com.kkaun.tinyarbrowser.paintables.Marker;
import com.kkaun.tinyarbrowser.samples.util.ARMarkerTransferable;
import com.kkaun.tinyarbrowser.samples.util.DemoUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Кира on 28.03.2018.
 */

public class JActivity extends ARActivity {

    private static final String TAG = "JActivity";
    private static ExecutorService exec = new ThreadPoolExecutor(1, 1,
            20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));
    private static CacheDataSource markersDataSource = new CacheDataSource();
    private ArrayList<ARMarkerTransferable> markerTOs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRadarBodyColor(200, 138, 138, 138);
        setRadarLineColor(255, 255, 255);
        setRadarBodyRadius(100);
        setRadarTextColor(255, 255, 255);

        if (savedInstanceState != null) getExtraData(savedInstanceState);
        else getExtraData(getIntent().getExtras());
        ARDataRepository.populateARData(markersDataSource.getMarkersCache());
    }

    private void getExtraData(Bundle extras) {
        if(extras != null && extras.containsKey("place_ar_markers"))
            markerTOs = extras.getParcelableArrayList("place_ar_markers");
        else markerTOs = new ArrayList<>();
        markersDataSource.setData(DemoUtils.convertTOsInMarkers(this, markerTOs));
    }

    /**
     * Location retrieving mechanism can be replaced by any side location library or your own code
     */
    @Override
    public void onLocationChanged(@NotNull Location location) {
        super.onLocationChanged(location);
        updateData(location);
    }

    /**
     * Provide your own implementation of marker's click if needed
     */
    @Override
    protected void onMarkerTouched(@NotNull Marker marker) {
        super.onMarkerTouched(marker);
        Toast t = Toast.makeText(getApplicationContext(), "Clicked" + marker.getName(), Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    @Override
    protected void updateDataOnZoom() {
        super.updateDataOnZoom();
        Location lastLocation = ARDataRepository.getCurrentLocation();
        updateData(lastLocation);
    }

    private void updateData(final Location lastLocation) {
        try { exec.execute(new Runnable() {
                @Override
                public void run() {
                    markerTOs = DemoUtils.getFreshMockData(lastLocation);
                    markersDataSource.setData(DemoUtils.convertTOsInMarkers(JActivity.this, markerTOs));
                    ARDataRepository.populateARData(markersDataSource.getMarkersCache());
                }});
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.", e); }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("place_ar_markers", markerTOs);
    }
}
