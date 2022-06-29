package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ListCity
import com.roozbeh.toopan.modelApi.ResponseLogin
import com.roozbeh.toopan.modelApi.User
import com.roozbeh.toopan.utility.Constants

class VolleyRefreshToken {
    companion object {

        fun getToken(
            check: VolleyInterface<ResponseLogin>,
            context: Context?,
            refreshToken: JsonObject,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.POST_REFRESH_TOKEN
//            val jsonObject : JsonObject? = null
//            jsonObject?.addProperty("refToken", refreshToken)

            val request: AuthRequest<ResponseLogin> = AuthRequest<ResponseLogin>(
                Request.Method.POST,
                url,
                ResponseLogin::class.java,
                refreshToken,
                true,
                context,
                { response -> check.onSuccess(response) },
                { error -> check.onFailed(error) }
            )
            request.retryPolicy = DefaultRetryPolicy(10000, 1, 1.0f)
            VolleyController.INSTANCE.addToRequestQueue(context, request, TAG)


        }

    }
}