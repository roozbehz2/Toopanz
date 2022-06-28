package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ListState
import com.roozbeh.toopan.utility.Constants

class VolleyGetState {
    companion object {

        fun getState(
            check: VolleyInterface<ListState>,
            context: Context?,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.GET_STATE


            val request: AuthRequest<ListState> = AuthRequest<ListState>(
                Request.Method.GET,
                url,
                ListState::class.java,
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