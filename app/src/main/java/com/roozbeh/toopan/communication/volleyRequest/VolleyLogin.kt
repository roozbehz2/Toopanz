package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.R
import com.roozbeh.toopan.communication.volleyPackage.GsonRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ResponseLogin
import com.roozbeh.toopan.utility.Constants

open class VolleyLogin {

    companion object {

        fun login(
            check: VolleyInterface<ResponseLogin>,
            context: Context?,
            userName: String,
            password: String,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.LOGIN

            val body = JsonObject()
            body.addProperty(context!!.resources.getString(R.string.username), userName)
            body.addProperty(context.resources.getString(R.string.passwordServer), password)

            val request: GsonRequest<ResponseLogin> = GsonRequest<ResponseLogin>(
                Request.Method.POST,
                url,
                ResponseLogin::class.java,
                body,
                { response -> check.onSuccess(response) },
                { error -> check.onFailed(error) },
                Request.Priority.NORMAL
            )
            request.retryPolicy = DefaultRetryPolicy(10000, 1, 1.0f)
            VolleyController.INSTANCE.addToRequestQueue(context, request, TAG)


        }

    }
}