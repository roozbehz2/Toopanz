package com.roozbeh.toopan.acrivites

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyError
import com.roozbeh.toopan.R
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.communication.volleyRequest.VolleyLogin
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.databinding.ActivityLoginBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ResponseLogin
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.UiHandler
import com.roozbeh.toopan.utility.Utils

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val requestTag = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.btnLogin.setOnClickListener(this)
        binding.imgBtnHideShow.setOnClickListener(this)
        binding.etPasswordLogin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkNet()

            }
            false

        }


    }

    private fun checkNet() {
        binding.btnLogin.isEnabled = false
        binding.refreshLogin.visibility = View.VISIBLE
        UiHandler.keyboardDown(binding.etUsernameLogin, this)
        NetDetector.check {
            if (it) {
                requestServer()
            } else {
                binding.refreshLogin.visibility = View.INVISIBLE
                Utils.showSnackBar(binding.btnLogin, getString(R.string.noInternet))
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun requestServer() {
        VolleyLogin.login(
            object : VolleyInterface<ResponseLogin> {
                override fun onSuccess(responseLogin: ResponseLogin) {
                    binding.refreshLogin.visibility = View.INVISIBLE
                    responseLogin.user.id?.let {
                        MyApplication.preferences(applicationContext).edit().putInt(Constants.ID_KEY,
                            it
                        ).apply()
                    }
                    MyApplication.preferences(applicationContext).edit().putString(Constants.TOKEN_KEY, responseLogin.token).apply()
                    MyApplication.preferences(applicationContext).edit().putString(Constants.REFRESH_TOKEN_KEY, responseLogin.refToken)
                        .apply()
                    MyApplication.preferences(applicationContext).edit().putBoolean(Constants.LOGIN_KEY, true).apply()

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailed(error: VolleyError?) {
                    binding.refreshLogin.visibility = View.INVISIBLE
                    binding.btnLogin.isEnabled = true
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(binding.btnLogin, it)
                        }
                    }
                }

            },
            applicationContext, binding.etUsernameLogin.text.toString(),
            binding.etPasswordLogin.text.toString(), requestTag
        )


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.btnLogin.id -> {
                checkNet()
            }

            binding.imgBtnHideShow.id -> {
                if (binding.etPasswordLogin.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
                ) {
                    binding.etPasswordLogin.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.imgBtnHideShow.alpha = .8f
                } else {
                    binding.etPasswordLogin.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.imgBtnHideShow.alpha = .2f
                }
                binding.etPasswordLogin.textDirection = View.TEXT_DIRECTION_ANY_RTL
            }
        }
    }


    override fun onStop() {
        super.onStop()
        VolleyController.INSTANCE.getRequestQueue(applicationContext).cancelAll(requestTag)

    }


}