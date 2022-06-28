package com.roozbeh.toopan.communication.volleyPackage;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.roozbeh.toopan.utility.Utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
        this.requestBody = params != null ? gson.toJson(params) : null;
        this.listener = listener;
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
    protected VolleyError parseNetworkError(final VolleyError volleyError){
        final VolleyError[] vError = new VolleyError[1];
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            if(volleyError instanceof AuthFailureError) {
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
            }else {
                vError[0] = new VolleyError(new String(volleyError.networkResponse.data));
            }
        }

        return vError[0];
    }
}

