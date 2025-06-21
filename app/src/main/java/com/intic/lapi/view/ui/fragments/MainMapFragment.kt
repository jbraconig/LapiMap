package com.intic.lapi.view.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.intic.lapi.R
import com.intic.lapi.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.fragment_main_map.*

class MainMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llBaseMainMap.visibility = View.INVISIBLE
        observerViewModel()
    }
    private fun observerViewModel(){
        val mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        var contador = 0
        mapViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            contador++
            if(it && contador == 1){
                Log.i("Contador","$contador")
                rlBaseMainMap.visibility = View.GONE
                llBaseMainMap.visibility = View.VISIBLE
            }
        })
    }

}