package com.example.lovemal

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lovemal.databinding.ActivityMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException


class MapsActivity : AppCompatActivity(), SensorEventListener {
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var binding: ActivityMapsBinding
    private var lastLocation: GeoPoint = GeoPoint(latitud, longitud)
    private lateinit var lastLocation1: MyLocationNewOverlay
    var check: Boolean = false
    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var lastMarker: GeoPoint = GeoPoint(latitud, longitud)
    lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val selectedName = intent.getStringExtra("veterinaria")

        handlePermissions()
        val view = binding.root
        setContentView(view)

        Configuration.getInstance().setUserAgentValue(BuildConfig.BUILD_TYPE)

        map = binding.osmMap
        roadManager = OSRMRoadManager(this, "ANDROID")
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        setupLocationOverlay(mapController)

        // Actualizar texto del botón
        val buttonText = "Ir a $selectedName"
        binding.nextVet.text = buttonText

        binding.nextVet.setOnClickListener {
            if (selectedName != null) {
                buscarUbicacion(selectedName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.osmMap.onResume()
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            binding.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
    }

    override fun onPause() {
        super.onPause()
        binding.osmMap.onPause()
    }

    private fun setupLocationOverlay(mapController: IMapController) {
        val locationProvider = GpsMyLocationProvider(this)
        locationOverlay = MyLocationNewOverlay(locationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        locationOverlay.runOnFirstFix {
            runOnUiThread {
                val currentLocation = locationOverlay.myLocation
                val latitude = currentLocation.latitude
                val longitude = currentLocation.longitude
                mapController.setZoom(15.0)
                mapController.setCenter(locationOverlay.myLocation)

                if (locationOverlay.myLocation != null) {
                    check = true
                    lastLocation = GeoPoint(latitude, longitude)
                    lastLocation1 = locationOverlay
                    map.overlays.add(locationOverlay)
                }
            }
        }
    }

    private fun buscarUbicacion(nombre: String) {
        val location = obtenerDireccionNombre(nombre)
        if (location != null) {
            val mapControl = map.controller
            mapControl.setCenter(location)
            mapControl.setZoom(20.0)

            val marker = Marker(map)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                marker.title = address.getAddressLine(0) // Establecer el título del marcador con la dirección
            }

            map.overlays.add(marker)

            val location1 = GeoPoint(location.latitude, location.longitude)
            lastMarker = location1
            if (lastLocation != null) {
                drawRoute(lastLocation, lastMarker)
            }
        }
    }

    private fun obtenerDireccionNombre(text: String): GeoPoint? {
        val coder = Geocoder(this)
        val addressList: List<Address>?
        try {
            addressList = coder.getFromLocationName(text, 1)
            if (addressList.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "No se encontraron resultados", Toast.LENGTH_LONG).show()
                return null
            }
            val location = addressList[0]
            return GeoPoint(location.latitude, location.longitude)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        GlobalScope.launch(Dispatchers.IO) {
            val road = roadManager.getRoad(routePoints)
            withContext(Dispatchers.Main) {
                Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
                Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
                if (map != null) {
                    roadOverlay?.let { map.overlays.remove(it) }
                    roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay?.outlinePaint?.color = Color.RED
                    roadOverlay?.outlinePaint?.strokeWidth = 10f
                    map.overlays.add(roadOverlay)
                    val distance = start.distanceToAsDouble(finish)
                    Toast.makeText(
                        this@MapsActivity,
                        "Distancia al punto : $distance metros",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handlePermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // El permiso ya ha sido concedido
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}