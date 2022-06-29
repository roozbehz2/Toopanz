package com.roozbeh.toopan.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roozbeh.toopan.R

class SalonsFragment : Fragment() {

    companion object {
        fun newInstance() = SalonsFragment()
    }

    private lateinit var viewModel: SalonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_salons, container, false)
    }


}