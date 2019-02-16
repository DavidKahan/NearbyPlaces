package com.kahan.david.nearbyplaces.view

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


import com.kahan.david.nearbyplaces.R

/**
 * Created by david on 15/02/2019.
 */
class PlacesActivity : AppCompatActivity(){
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var locationPermissionGranted: Boolean = false

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var placesViewModel: PlacesViewModel

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.places_activtiy)

        mapFragment = MapFragment.newInstance()

        supportFragmentManager.beginTransaction().add(R.id.container, mapFragment).commit()
        supportFragmentManager.executePendingTransactions()

        placesViewModel = ViewModelProviders.of(this)[PlacesViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.places_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.toggle) {
            toggleFragment(item)
        }
        return true
    }

    private fun toggleFragment(item: MenuItem){
        if(item.title.toString().equals("list", ignoreCase = true)){
            item.title = "map"
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_map_black_24dp)
            supportFragmentManager.beginTransaction().replace(R.id.container, NearbyPlacesListFragment.newInstance())
                .commit()
        }else {
            item.title = "list"
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_list_black_24dp)
            supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment)
                .commit()
        }
        supportFragmentManager.executePendingTransactions()
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        mapFragment.updateLocationUI()
    }
}
