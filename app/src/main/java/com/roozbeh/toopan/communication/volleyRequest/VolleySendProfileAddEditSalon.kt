package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.communication.volleyPackage.VolleyMultipartRequest
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.ListCity
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.modelApi.User
import com.roozbeh.toopan.utility.Constants
import org.json.JSONObject

class VolleySendProfileAddEditSalon {
    companion object {

        fun sendImage(
            check: VolleyInterface<Salons>,
            context: Context?,
            id: Int,
            image: ByteArray,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.POST_UPLOAD_PROFILE_SALON + id


            val request: VolleyMultipartRequest<Salons> = VolleyMultipartRequest<Salons>(
                Request.Method.POST,
                url,
                Salons::class.java,
                image,
                true,
                { response -> check.onSuccess(response) },
                { error -> check.onFailed(error) },
                context
            )
            request.retryPolicy = DefaultRetryPolicy(10000, 1, 1.0f)
            VolleyController.INSTANCE.addToRequestQueue(context, request, TAG)


        }

    }
}