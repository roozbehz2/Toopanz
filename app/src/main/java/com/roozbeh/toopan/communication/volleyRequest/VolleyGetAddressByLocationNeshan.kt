package com.roozbeh.toopan.communication.volleyRequest

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.roozbeh.toopan.communication.volleyPackage.AuthRequest
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.AddressNeshan
import com.roozbeh.toopan.modelApi.User
import org.neshan.common.model.LatLng
import org.neshan.servicessdk.search.model.NeshanSearchResult

class VolleyGetAddressByLocationNeshan {
    companion object {

        fun getAddressNeshan(
            check: VolleyInterface<AddressNeshan>,
            context: Context?,
            latLng: LatLng,
            TAG: String?,
        ) {
            val requestURL = "https://api.neshan.org/v4/reverse?lat=" + latLng.latitude.toString() + "&lng=" + latLng.longitude.toString()


            val request: AuthRequest<AddressNeshan > = AuthRequest<AddressNeshan>(
                Request.Method.GET,
                requestURL,
                AddressNeshan::class.java,
                null,
                false,
                context,
                { response -> check.onSuccess(response) },
                { error -> check.onFailed(error) }
            )
            request.retryPolicy = DefaultRetryPolicy(10000, 1, 1.0f)
            VolleyController.INSTANCE.addToRequestQueue(context, request, TAG)


        }

    }
}