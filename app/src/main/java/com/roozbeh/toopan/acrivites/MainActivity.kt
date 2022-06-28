package com.roozbeh.toopan.acrivites

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.roozbeh.toopan.R
import com.roozbeh.toopan.databinding.ActivityMainBinding
import com.roozbeh.toopan.fragment.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val profileFragmentTag = "profileFragment"
    private val notificationsFragmentTag = "notificationsFragment"
    private val tournamentFragmentTag = "tournamentFragment"
    private val salonsFragmentTag = "salonsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.imgBack.setOnClickListener(this)
        initFragments()
    }

    private fun initFragments() {
        val salonsFragment = SalonsFragment()
        val tournamentFragment = TournamentFragment()
        val notificationsFragment = NotificationsFragment()
        val profileFragment = ProfileFragment()

        binding.bottomNavigation.selectedItemId = R.id.salonsFragment
        makeCurrentFragment(salonsFragment, salonsFragmentTag,"لیست سالن ها",false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.salonsFragment -> makeCurrentFragment(salonsFragment, salonsFragmentTag,"لیست سالن ها", false)
                R.id.tournamentFragment -> makeCurrentFragment(
                    tournamentFragment,
                    tournamentFragmentTag,
                    "لیست مسابقات",
                    false
                )
                R.id.notificationsFragment -> makeCurrentFragment(
                    notificationsFragment,
                    notificationsFragmentTag,
                    "اعلانات",
                    false
                )
                R.id.profileFragment -> makeCurrentFragment(
                    profileFragment,
                    profileFragmentTag,
                    getString(R.string.profile),
                    false
                )
            }
            true
        }
    }


    private fun makeCurrentFragment(fragment: Fragment,tag: String,  title: String, visibility: Boolean) =
        supportFragmentManager.beginTransaction().apply {
            setTitleToolbar(title, visibility)
            setCustomAnimations(R.anim.fade_in_new, R.anim.fade_out_new)
                .replace(R.id.navHostFrame, fragment, tag)
                .commit()

        }

    companion object {
        private lateinit var binding: ActivityMainBinding
        fun setTitleToolbar(title: String, visibility: Boolean) {

            binding.txtTitleMain.text = title
            if (visibility) {
                binding.imgBack.visibility = View.VISIBLE
                binding.txtTitleMain.gravity = (Gravity.START + Gravity.CENTER)
            } else {
                binding.txtTitleMain.gravity = Gravity.CENTER
                binding.imgBack.visibility = View.INVISIBLE
            }

            if (title == "پروفایل") {
                binding.constToolAppBar.visibility = View.GONE
            } else {
                binding.constToolAppBar.visibility = View.VISIBLE
            }
        }

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.imgBack.id -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {

        val profileFragment: ProfileFragment? =
            supportFragmentManager.findFragmentByTag(profileFragmentTag) as ProfileFragment?
        if (profileFragment != null) {
            binding.constToolAppBar.visibility = View.GONE
        } else {
            binding.constToolAppBar.visibility = View.VISIBLE
        }

        super.onBackPressed()
    }

}