package com.roozbeh.toopan.communication.volleyPackage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.roozbeh.toopan.app.MyApplication;
import com.roozbeh.toopan.communication.volleyRequest.VolleyRefreshToken;
import com.roozbeh.toopan.interfaces.VolleyInterface;
import com.roozbeh.toopan.modelApi.ResponseLogin;
import com.roozbeh.toopan.modelApi.User;
import com.roozbeh.toopan.utility.Constants;
import com.roozbeh.toopan.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by shuaib1413 on 19/04/2016.
 */
public class AuthRequest<T> extends Request<T> {

    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final String requestBody;
    private final Context context;
    private final int method;
    private final Response.ErrorListener errorListener;
    private final boolean requiresAuth;
    private final String url;

    /**
     * Make a POST/GET request and return a parsed object from JSON.
     */
    public AuthRequest(int method, String url, Class<T> clazz, Object params, boolean requiresAuth, Context context,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        /*GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        gson = builder.create();*/
        this.clazz = clazz;
        this.url = url;
        this.requestBody = params != null ? gson.toJson(params) : null;
        this.listener = listener;
        this.context = context;
        this.method = method;
        this.errorListener = errorListener;
        this.requiresAuth = requiresAuth;
        if (requiresAuth) {
            Map<String, String> authHeader = new HashMap<>();
//            Log.e("looooog", AppUtils.getSignedInToken(mContext));
            authHeader.put("Authorization", "Bearer " + Utils.getSignedInToken(context));
            this.headers = authHeader;
        } else {
            this.headers = null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(
                    (clazz == null) ? null : gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(final VolleyError volleyError) {
        final VolleyError[] vError = new VolleyError[1];
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            if (volleyError instanceof AuthFailureError) {

                try {
                    JSONObject errorResponse = new JSONObject(new String(volleyError.networkResponse.data, StandardCharsets.UTF_8));
                    // 411 == expire token
                    if (errorResponse.getString("errorCode").equals("411")) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("refToken",
                                MyApplication.Companion.preferences(context).getString(Constants.REFRESH_TOKEN_KEY, ""));
//                        Log.e("rrr", "parseNetworkError:3 " + jsonObject);
//todo cancel all request
//                        VolleyController.INSTANCE.getRequestQueue(context)...

                        VolleyRefreshToken.Companion.getToken(new VolleyInterface<ResponseLogin>() {
                            @Override
                            public void onSuccess(ResponseLogin responseLogin) {
                                MyApplication.Companion.preferences(context).edit().putString(
                                        Constants.TOKEN_KEY, responseLogin.getToken()).apply();

                                MyApplication.Companion.preferences(context).edit().putString(
                                        Constants.REFRESH_TOKEN_KEY, responseLogin.getRefToken()).apply();

                                AuthRequest<T> authRequest = new AuthRequest<>(method, url, clazz, requestBody, requiresAuth,
                                        context, listener, errorListener);

                                authRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0F));
                                VolleyController.INSTANCE.addToRequestQueue(context, authRequest, "oldRequest");
                            }

                            @Override
                            public void onFailed(@Nullable VolleyError error) {
//                                Log.e("rrr", "onFailed: " + error.getMessage());
//                                Log.e("rrr", "onFailed: " + error.networkResponse.statusCode);
                            }
                        }, context, jsonObject, "refreshTokenTag");
                        // 412 = expire refresh token
                    } else if (errorResponse.getString("errorCode").equals("412")) {
                        Utils.reset();
                    }

                } catch (JSONException e) {
                    Log.e("rrr", "parseNetworkError: " + " volley auth catch ");
                    e.printStackTrace();
                }





                /*VolleySignInTask.signIn(new SignInListener() {
                    @Override
                    public void onSuccess(SignUpResponse response) {
                        AppUtils.saveUser(response, mContext);
                        vError[0] = new VolleyError(mContext.getString(R.string.re_auth_try_again));
                    }

                    @Override
                    public void onFailed(VolleyError error) {
                        vError[0] = new VolleyError(new String(error.networkResponse.data));
                    }
                }, AppUtils.getSignedInUser(mContext), mContext);*/
            } else {
                vError[0] = new VolleyError(new String(volleyError.networkResponse.data, StandardCharsets.UTF_8));
            }
        }
//        return vError[0];
        return volleyError;
    }
}

