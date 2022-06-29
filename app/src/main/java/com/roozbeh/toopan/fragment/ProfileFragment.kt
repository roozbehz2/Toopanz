package com.roozbeh.toopan.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.roozbeh.toopan.R
import com.roozbeh.toopan.acrivites.EditInfoUserActivity
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.communication.volleyRequest.VolleyGetUser
import com.roozbeh.toopan.databinding.FragmentProfileBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.User
import com.roozbeh.toopan.utility.ConnectionModel
import com.roozbeh.toopan.utility.ConnectionViewModel
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.Utils

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private var getUserTag: String? = "getUserProfileTag"
    private var user: User = User()

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
        binding.imgProfile.setOnClickListener(this)


    }

    override fun onResume() {
        super.onResume()
        checkNet()
    }

    private fun checkNet() {

        NetDetector.check {
            if (it) {
                if (ConnectionModel.instance.isUpdateUser) {
                    ConnectionModel.instance.isUpdateUser = false
                    ConnectionViewModel.instance.updateUser.postValue(ConnectionModel.instance.isUpdateUser)
                    requestServer()
                } else {
                    if (user != null) {
                        setContent(user!!)
                    } else {
                        requestServer()
                    }

                }
            } else {
                if (user != null) {
                    setContent(user!!)
                }
                Utils.showSnackBar(
                    requireContext(),
                    binding.cvProfile,
                    getString(R.string.noInternet),
                    requireContext().getColor(R.color.snackBar)
                )
            }
        }

    }

    private fun requestServer() {
        VolleyGetUser.getUser(
            object : VolleyInterface<User> {
                override fun onSuccess(user: User) {
                    this@ProfileFragment.user = user
                    setContent(user)
                }

                override fun onFailed(error: VolleyError?) {
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(
                                requireContext(),
                                binding.cvProfile,
                                it,
                                requireContext().getColor(R.color.snackBar)
                            )
                        }
                    }

                }

            },
            requireContext(),
            MyApplication.preferences(requireContext()).getInt(Constants.ID_KEY, -1),
            getUserTag
        )

    }

    private fun setContent(user: User) {

        Glide.with(this)
            .load(Constants.BASE_URL + user.profileImageUrl)
            .placeholder(R.drawable.ic_profile)
            .into(binding.imgProfile)
        binding.txtNameUser.text = user.name
        binding.txtPhoneUser.text = user.phoneNumber
    }


    private fun transactionEdit() {
        val intent = Intent(requireContext(), EditInfoUserActivity::class.java)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.constEditProfile.id -> transactionEdit()
            binding.imgProfile.id->
                Utils.openImageViewer(requireContext(), binding.imgProfile, Constants.BASE_URL + user.profileImageUrl)
        }
    }

    override fun onStop() {
        super.onStop()
        VolleyController.INSTANCE.getRequestQueue(requireContext()).cancelAll(getUserTag)
    }
}
