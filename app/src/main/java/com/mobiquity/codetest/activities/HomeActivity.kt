package com.mobiquity.codetest.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mobiquity.codetest.R
import com.mobiquity.codetest.adapters.CitiesListAdapter
import com.mobiquity.codetest.database.AppDatabase
import com.mobiquity.codetest.database.CitiesDao
import com.mobiquity.codetest.database.CitiesInfo
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity(), OnMapReadyCallback, CitiesListAdapter.AdapterListener {

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_CAMERA_POSITION = "camera_position"
    }

    private var citiesDao: CitiesDao? = null
    private var citiesListAdapter: CitiesListAdapter? = null
    private var citiesListData = ArrayList<CitiesInfo>()
    private var markerData = ArrayList<Marker>()
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        var layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        listCities.setLayoutManager(layoutManager)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "CitiesInfo"
        ).build()
        citiesDao = db.citiesDao()
        initMap()
    }

    override fun onResume() {
        super.onResume()
        Executors.newSingleThreadExecutor().execute(Runnable {
            citiesListData = citiesDao?.getAll() as ArrayList<CitiesInfo>
            Handler(Looper.getMainLooper()).post(Runnable {
                citiesListAdapter = CitiesListAdapter(this, citiesListData, this)
                listCities.adapter = citiesListAdapter
            })
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
        }
        super.onSaveInstanceState(outState)
    }

    fun initMap() {
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        // [START maps_current_place_map_fragment]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission()
        // [END_EXCLUDE]

        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                addMarker(p0!!)
            }
        })

        for (i in 0 until citiesListData?.size!!) {
            var marker = map?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        citiesListData?.get(i)?.latitude!!,
                        citiesListData?.get(i)?.longitude!!
                    )
                ).title(citiesListData?.get(i)?.cityName)
            )
            markerData.add(marker)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]

    fun addMarker(latLng: LatLng) {
        Executors.newSingleThreadExecutor().execute(Runnable {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val addresses: List<Address> =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val obj: Address = addresses[0]
                var citiesInfo = CitiesInfo(
                    obj.locality,
                    obj.getAddressLine(0),
                    latLng.latitude,
                    latLng.longitude
                );
                citiesDao?.insertAll(citiesInfo)
                Handler(Looper.getMainLooper()).post(Runnable {
                    var marker =
                        map?.addMarker(MarkerOptions().position(latLng).title(obj.locality))!!
                    markerData.add(marker)
                    onResume()
                })
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        })
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            updateLocationUI()
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    updateLocationUI()
                    getDeviceLocation()
                }
            }
        }
    }
    // [END maps_current_place_on_request_permissions_result]


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_clear -> {
                Executors.newSingleThreadExecutor().execute(Runnable {
                    citiesDao?.deleteAll()
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            applicationContext,
                            "all bookmarks Removed Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                    for (i in 0 until markerData.size) {
                        markerData.get(i).remove()
                    }
                    markerData.clear()
                    onResume()
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDelete(citiesInfo: CitiesInfo) {
        Executors.newSingleThreadExecutor().execute(Runnable {
            citiesDao?.delete(citiesInfo)
            var index = citiesListData.indexOf(citiesInfo)
            citiesListData?.remove(citiesInfo)
            Handler(Looper.getMainLooper()).post(Runnable {
                onResume()
                if (index >= 0) {
                    markerData.get(index).remove()
                    markerData.removeAt(index)
                }
                Toast.makeText(applicationContext, "Removed Successfully", Toast.LENGTH_LONG).show()
            })
        })

    }

}