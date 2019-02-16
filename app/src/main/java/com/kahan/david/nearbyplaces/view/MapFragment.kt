package com.kahan.david.nearbyplaces.view

import android.arch.lifecycle.Observer
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kahan.david.nearbyplaces.model.Place
import com.kahan.david.nearbyplaces.model.ResponseWrapper
import com.google.android.gms.maps.model.BitmapDescriptorFactory


/**
 * Created by david on 15/02/2019.
 */
class MapFragment : SupportMapFragment(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM = 15

    private var lastKnownLocation: Location? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        retainInstance = true
        getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {

        this.map = map

        // Prompt the user for permission.
        (activity as PlacesActivity).getLocationPermission()


        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocationAndNearbyPlaces()

        subscribeObservers()

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if ((activity as PlacesActivity).locationPermissionGranted) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                (activity as PlacesActivity).getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    private fun getDeviceLocationAndNearbyPlaces() {
        try {
            if ((activity as PlacesActivity).locationPermissionGranted) {
                val locationResult = (activity as PlacesActivity).fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener((activity as PlacesActivity)) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                        val location = LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        )

                        val locationString = "${location.latitude},${location.longitude}"
                        (activity as PlacesActivity).placesViewModel.searchNearbyPlacesFor(locationString, "restaurant")
                        (activity as PlacesActivity).placesViewModel.getCurrentLocation(locationString)

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings!!.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    private fun subscribeObservers() {
        val placesResponseLiveData = (activity as PlacesActivity).placesViewModel.getPlacesListObservable()
        placesResponseLiveData.observe(this,
            Observer<ResponseWrapper> { response ->
                response?.let {
                    if (response.status == ResponseWrapper.Status.OK) {
                        drawPlacesOnMapFor(response.placesList)
                    } else {
                        Toast.makeText(activity, "Places API call failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        val currentLocationResponseLiveData =
            (activity as PlacesActivity).placesViewModel.getCurrentLocationObservable()
        currentLocationResponseLiveData.observe(this,
            Observer<ResponseWrapper> { response ->
                response?.let {
                    if (response.status == ResponseWrapper.Status.OK) {
                        drawCurrentLocationOnMap(response.placesList)
                    } else {
                        Toast.makeText(activity, "Places API call failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }

    private fun drawCurrentLocationOnMap(placesList: ArrayList<Place>) {
        val bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_AZURE
        )
        val currentLocation = placesList[0]
        val latLng = LatLng(currentLocation.geometry.location.lat, currentLocation.geometry.location.lng)
        map?.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptor)
                .title(currentLocation.formattedAddress)
        )
    }

    private fun drawPlacesOnMapFor(placesList: ArrayList<Place>) {
        placesList.forEach {
            val markerOptions = MarkerOptions()
            val latLng = LatLng(it.geometry.location.lat, it.geometry.location.lng)
            markerOptions.position(latLng)
            markerOptions.title(it.name)
            markerOptions.snippet(it.address)
            map?.addMarker(markerOptions)
        }
    }

    companion object {
        fun newInstance(): MapFragment {
            val fragment = MapFragment()
            val arguments = Bundle()
            fragment.arguments = arguments
            return fragment
        }
    }
}