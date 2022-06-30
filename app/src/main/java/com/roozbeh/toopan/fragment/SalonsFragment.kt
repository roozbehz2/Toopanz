package com.roozbeh.toopan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.roozbeh.toopan.app.MyApplication.Companion.preferences
import com.roozbeh.toopan.databinding.FragmentSalonsBinding

class SalonsFragment : Fragment() {

    private lateinit var binding: FragmentSalonsBinding
    companion object {
        fun newInstance() = SalonsFragment()
    }

    private lateinit var viewModel: SalonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSalonsBinding.inflate(layoutInflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}