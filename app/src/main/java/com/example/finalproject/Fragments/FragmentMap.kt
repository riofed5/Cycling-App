package com.example.finalproject.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.MapQuestRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.abs


class FragmentMap : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val waypoints = ArrayList<GeoPoint>()
    private lateinit var roadManager: RoadManager
    private var requestingLocationUpdates: Boolean = false
    private lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.finalproject.R.layout.fragment_second, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ctx = requireActivity().applicationContext
        //important! set your user agent to prevent getting banned
        //from the osm servers
        Configuration.getInstance().load(
            ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx)
        )

        //Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)

        roadManager = MapQuestRoadManager("t2CFU4eZSNbbgtJfBsp1Lz7NW3lcaPXi")
        roadManager.addRequestOption("routeType=bicycle")

        //Initialize Map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)

        checkLocationPermission()
        checkBackgroundLocationPermission()

        fusedLocationClient.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful && task.result != null) {
                val lat = task.result!!.latitude
                val lng = task.result!!.longitude

                val startPoint = GeoPoint(lat, lng)
                map.controller.setCenter(startPoint)
                waypoints.add(startPoint)
                val initPoint = GeoPoint(lat, lng)
                val marker = Marker(map)
                marker.position = initPoint

                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(marker)
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude
                        val lastAddedLocation = waypoints[waypoints.lastIndex]
                        val latProximity = abs(lastAddedLocation.latitude - lat) <= 0.0001
                        val lngProximity = abs(lastAddedLocation.longitude - lng) <= 0.0001
                        if (latProximity && lngProximity) {
                            return
                        }
                        val updatedPoint = GeoPoint(lat, lng)
                        map.controller.setCenter(updatedPoint)
                        waypoints.add(updatedPoint)
                        var road: Road?
                        lifecycleScope.launch(Dispatchers.IO) {
                            road = getRoad()
                            val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road)
                            map.overlays.add(roadOverlay)
                            map.invalidate()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        if (!requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        checkLocationPermission()
        requestingLocationUpdates = true
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        map.onPause()
    }

    private fun stopLocationUpdates() {
        requestingLocationUpdates = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0
            )
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0
            )
        }
    }

    suspend fun getRoad(): Road =
        withContext(Dispatchers.IO) {
            return@withContext roadManager.getRoad(waypoints)
        }
}



