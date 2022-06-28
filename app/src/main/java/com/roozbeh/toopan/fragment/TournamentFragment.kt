package com.roozbeh.toopan.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roozbeh.toopan.R

class TournamentFragment : Fragment() {

    companion object {
        fun newInstance() = TournamentFragment()
    }

    private lateinit var viewModel: TournamentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tournament, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TournamentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}