package com.intic.lapi.view.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.intic.lapi.R
import com.intic.lapi.viewmodel.AutocompleteSearchViewModel
import kotlinx.android.synthetic.main.fragment_search_map.*


class SearchMapFragment : Fragment() {
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_map, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerViewModel(view.context)
        (tvSearch.editText as? AutoCompleteTextView)?.setOnItemClickListener { p0, p1, p2, p3 ->
            checkLocation(view)
        }
        (tvSearch.editText as? AutoCompleteTextView)?.setOnKeyListener(object: View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if((p2?.action == KeyEvent.ACTION_DOWN) && (p1 == KeyEvent.KEYCODE_ENTER)){
                    checkLocation(view)
                    return true
                }
                return false
            }
        })
    }
    private fun observerViewModel(context: Context){
        val autocsViewModel = ViewModelProvider(requireActivity()).get(AutocompleteSearchViewModel::class.java)
        var contador = 0
        autocsViewModel.listLocate.observe(viewLifecycleOwner,Observer<List<String>>(){
            contador++
            if(contador==1){
                val adapter = ArrayAdapter(context, R.layout.item_search, it)
                (tvSearch.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
    }
    private fun checkLocation(view: View){
        val texto = (tvSearch.editText as? AutoCompleteTextView)?.text
        val autoViewModel = ViewModelProvider(requireActivity()).get(AutocompleteSearchViewModel::class.java)
        autoViewModel.checkLocation(texto.toString())
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}