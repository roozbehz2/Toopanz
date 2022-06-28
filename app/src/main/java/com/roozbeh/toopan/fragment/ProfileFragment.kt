package com.roozbeh.toopan.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.roozbeh.toopan.R
import com.roozbeh.toopan.acrivites.EditInfoUserActivity
import com.roozbeh.toopan.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private val editInfoUserFragmentTag = "editInfoUserFragment"

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.constEditProfile.setOnClickListener(this)
        binding.constTransactionProfile.setOnClickListener(this)
        binding.constAboutProfile.setOnClickListener(this)
        binding.constExitProfile.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.constEditProfile.id -> {
                transactionEdit()

            }
        }
    }

    private fun transactionEdit() {
        val intent = Intent(requireContext(), EditInfoUserActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right)


    }

}