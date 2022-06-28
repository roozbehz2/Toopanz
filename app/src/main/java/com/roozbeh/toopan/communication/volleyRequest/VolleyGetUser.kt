package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.User
import com.roozbeh.toopan.utility.Constants

class VolleyGetUser {
    companion object {

        fun getUser(
            check: VolleyInterface<User>,
            context: Context?,
            id: Int,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.GET_USER + id


            val request: AuthRequest<User> = AuthRequest<User>(
                Request.Method.GET,
                url,
                User::class.java,
                null,
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