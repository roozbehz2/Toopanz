package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.R
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ListCity
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.modelApi.User
import com.roozbeh.toopan.utility.Constants

class VolleyPostDeleteImageSalon {
    companion object {

        fun deleteImage(
            check: VolleyInterface<Salons>,
            context: Context,
            id: Int,
            imageName: String,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.POST_DELETE_IMAGE_SALON

            val body = JsonObject()
            body.addProperty(context.resources.getString(R.string.salonId), id)
            body.addProperty(context.resources.getString(R.string.salonImageName), imageName)
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