package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.google.gson.JsonObject
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.BodyAddOrEditSalon
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.modelApi.WeekSalonSanseResponse
import com.roozbeh.toopan.utility.Constants
import org.json.JSONObject

class VolleyPostGetSalonWeekSans {
    companion object {

        fun getSalonWeekSans(
            check: VolleyInterface<WeekSalonSanseResponse>,
            context: Context?,
            body: JsonObject,
            TAG: String?,
        ) {
            val url: String = Constants.BASE_URL + Constants.POST_SALON_WEEK_SANS

            val request: AuthRequest<WeekSalonSanseResponse> = AuthRequest<WeekSalonSanseResponse>(
                Request.Method.POST,
                url,
                WeekSalonSanseResponse::class.java,
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