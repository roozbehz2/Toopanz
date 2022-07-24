package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.BodyAddOrEditSalon
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.utility.Constants

class VolleyPostAddSalon {
    companion object {

        fun addSalon(
            check: VolleyInterface<Salons>,
            context: Context?,
            body: BodyAddOrEditSalon,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.POST_ADD_EDIT_SALON

            val request: AuthRequest<Salons> = AuthRequest<Salons>(
                Request.Method.POST,
                url,
                Salons::class.java,
                body,
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