package com.kahan.david.nearbyplaces.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kahan.david.nearbyplaces.R
import com.kahan.david.nearbyplaces.model.Place
import com.kahan.david.nearbyplaces.model.ResponseWrapper
import kotlinx.android.synthetic.main.places_list_fragment.*

/**
 * Created by david on 15/02/2019.
 */
class NearbyPlacesListFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var placesList: List<Place>
    private lateinit var adapter: PlacesListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.places_list_fragment, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        places_list.layoutManager = layoutManager

        val placesResponseLiveData = (activity as PlacesActivity).placesViewModel.getPlacesListObservable()
        placesResponseLiveData.observe(this,
            Observer<ResponseWrapper> { response ->
                response?.let {
                    if (response.status == ResponseWrapper.Status.OK) {
                        placesList = response.placesList
                        updateList()
                    } else {
                        places_list.visibility = View.GONE
                    }
                }
            })

    }

    private fun updateList() {
        adapter = PlacesListAdapter(placesList)
        places_list.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(): NearbyPlacesListFragment {
            val fragment = NearbyPlacesListFragment()
            val arguments = Bundle()
            fragment.arguments = arguments
            return fragment
        }
    }
}
