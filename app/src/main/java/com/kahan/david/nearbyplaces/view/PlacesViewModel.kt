package com.kahan.david.nearbyplaces.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.kahan.david.nearbyplaces.Constants
import com.kahan.david.nearbyplaces.model.PlacesResponse
import com.kahan.david.nearbyplaces.model.ResponseWrapper
import com.kahan.david.nearbyplaces.network.PlacesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by david on 16/02/2019.
 */
class PlacesViewModel: ViewModel() {

    private val placesResponseData: MutableLiveData<ResponseWrapper> = MutableLiveData()
    private val currentLocationResponseData: MutableLiveData<ResponseWrapper> = MutableLiveData()
    fun getPlacesListObservable(): LiveData<ResponseWrapper> = placesResponseData
    fun getCurrentLocationObservable(): LiveData<ResponseWrapper> = currentLocationResponseData

    fun searchNearbyPlacesFor(location: String, category: String){
        var categoryString: String = ""
        if(category.split(" ").isNotEmpty()){
            val categoryWords = category.split(" ")
            categoryString = categoryWords[0]
            for (i in 1 until categoryWords.size){
                categoryString += "_${categoryWords[i]}"
            }
        } else {
            categoryString = category
        }
        getNearbyPlacesFor(location, categoryString.toLowerCase())
    }

    private fun getNearbyPlacesFor(location: String, type: String) {
        val converter = GsonConverterFactory.create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.MAPS_REQUEST_BASE_URL)
            .addConverterFactory(converter)
            .build()

        val placesService = retrofit.create(PlacesService::class.java)
        val call: Call<PlacesResponse> = placesService.getNearbyPlaces(location, type)
        call.enqueue(object : Callback<PlacesResponse> {
            override fun onFailure(call: Call<PlacesResponse>?, t: Throwable?) {
                val responseWrapper = ResponseWrapper(null)
                placesResponseData.postValue(responseWrapper)
            }

            override fun onResponse(call: Call<PlacesResponse>?, response: Response<PlacesResponse>?) {
                val responseWrapper = ResponseWrapper(response?.body())
                placesResponseData.postValue(responseWrapper)
            }

        })
    }

    fun getCurrentLocation(location: String) {
        val converter = GsonConverterFactory.create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.MAPS_GEOCODE_REQUEST_BASE_URL)
            .addConverterFactory(converter)
            .build()

        val placesService = retrofit.create(PlacesService::class.java)
        val call: Call<PlacesResponse> = placesService.getCurrentAddress(location)
        call.enqueue(object : Callback<PlacesResponse> {
            override fun onFailure(call: Call<PlacesResponse>?, t: Throwable?) {
                val responseWrapper = ResponseWrapper(null)
                currentLocationResponseData.postValue(responseWrapper)
            }

            override fun onResponse(call: Call<PlacesResponse>?, response: Response<PlacesResponse>?) {
                val responseWrapper = ResponseWrapper(response?.body())
                currentLocationResponseData.postValue(responseWrapper)
            }

        })
    }
}