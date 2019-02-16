package com.kahan.david.nearbyplaces.network

import com.kahan.david.nearbyplaces.Constants
import com.kahan.david.nearbyplaces.model.PlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by david on 15/02/2019.
 */
interface PlacesService {
    @GET("nearbysearch/json?key=${Constants.MAPS_REQUEST_API_KEY}")
    fun getNearbyPlaces(@Query("location") location: String,
                        @Query("type") type: String,
                        @Query("rankby") rankBy: String = "distance"): Call<PlacesResponse>

    //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY
    @GET("json?key=${Constants.MAPS_GEOCODE_REQUEST_API_KEY}")
    fun getCurrentAddress( @Query ("latlng") location: String): Call<PlacesResponse>
}