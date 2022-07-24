package com.roozbeh.toopan.acrivites

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.VolleyError
import com.carto.styles.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.roozbeh.toopan.R
import com.roozbeh.toopan.adapter.SearchAdapter
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyRequest.VolleyGetAddressByLocationNeshan
import com.roozbeh.toopan.communication.volleyRequest.VolleyGetSearchNeshan
import com.roozbeh.toopan.databinding.ActivityMapBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.AddressNeshan
import com.roozbeh.toopan.utility.*
import org.json.JSONObject
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.style.NeshanMapStyle
import org.neshan.servicessdk.search.model.Item
import org.neshan.servicessdk.search.model.NeshanSearchResult
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class MapActivity : AppCompatActivity(), SearchAdapter.OnSearchItemListener {

    private val searchTag = "searchTag"
    private lateinit var binding: ActivityMapBinding
    /*private lateinit var db: HistoryDao*/
//    private val viewModelDB: ViewModelDB by viewModels<ViewModelDB>()

    // save current map style
    @NeshanMapStyle
    private var mapStyle = 0
    private var selectedMarker: Marker? = null
    private var marker: Marker? = null
    private lateinit var items: ArrayList<Item>
    private lateinit var adapter: SearchAdapter

    //    private lateinit var centerMarker: Marker
    private val markers = ArrayList<Marker>()

    // marker animation style
    private var animSt: AnimationStyle? = null

    // location updates interval - 1 sec
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

    // than your app can handle
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

    // used to track request permissions
    private val REQUEST_CODE = 123


    // User's current location
    private var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastUpdateTime: String? = null

    // boolean flag to toggle the ui
    private var mRequestingLocationUpdates: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // starting app in full screen
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)


    }

    override fun onStart() {
        super.onStart()
        // everything related to ui is initialized here
        initLayoutReferences()
        // Initializing user location
        initLocation()
        startReceivingLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Initializing layout references (views, map and map events)
    private fun initLayoutReferences() {
        // Initializing views
        initViews()

        // Initializing mapView element
        initMap()
        binding.fabAddLocMap.hide()
        // when long clicked on map, a marker is added in clicked location
        binding.mapNeshan.setOnMapLongClickListener { latLng ->
            binding.mapNeshan.addMarker(
                createMarker(latLng)
            )
            runOnUiThread {
                binding.fabAddLocMap.show()
            }
        }

        binding.fabAddLocMap.setOnClickListener {
//            val intent = Intent(this, AddOrEditSalonActivity::class.java)

            runOnUiThread {
                binding.fabAddLocMap.isEnabled = false
            }
            requestConvertLocToAddress()
        }

        binding.themePreview.setOnClickListener {
            changeStyle(it)
        }

        // Initializing theme preview
        validateThemePreview()

        //listen for search text change
        binding.etSearchOnMap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                checkNet(s.toString())
                /*searchDatabase(s.toString())*/
            }
        })

        binding.etSearchOnMap.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    UiHandler.keyboardDown(binding.etSearchOnMap, this@MapActivity)
                    checkNet(binding.etSearchOnMap.text.toString())
                    /*searchDatabase(binding.etSearchOnMap.text.toString())*/
                }
                return false
            }
        })
    }

    private fun requestConvertLocToAddress() {


        selectedMarker?.latLng?.let { it1 ->
            VolleyGetAddressByLocationNeshan.getAddressNeshan(object :VolleyInterface<AddressNeshan>{
                override fun onSuccess(body: AddressNeshan?) {

                    selectedMarker?.latLng?.latitude?.let { it1 -> intent.putExtra("lat", it1) }
                    selectedMarker?.latLng?.longitude?.let { it1 -> intent.putExtra("lng", it1) }
                    intent.putExtra("address", body?.formatted_address)
                    setResult(RESULT_OK, intent)
                    overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    finish()
                }

                override fun onFailed(error: VolleyError?) {
                    binding.fabAddLocMap.isEnabled = true
                    Utils.showSnackBar(
                        this@MapActivity, binding.etSearchOnMap, "ارتباط برقرار نشد!",
                        getColor(R.color.snackBar)
                    )
                }

            }, this, it1, "")
        }
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        items = java.util.ArrayList()
        adapter = SearchAdapter(items, this@MapActivity)
        binding.recyclerSearchMap.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearchMap.adapter = adapter
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        val latLng = LatLng(35.767234, 51.330743)
        binding.mapNeshan.moveCamera(latLng, 0f)
        binding.mapNeshan.setZoom(14f, 0f)
        binding.mapNeshan.isPoiEnabled = true
        if (MyApplication.preferences().getInt(Constants.MAP_STYLE, 0) == 0) {
            MyApplication.preferences().edit()
                .putInt(Constants.MAP_STYLE, binding.mapNeshan.mapStyle).apply()
        }
        mapStyle = MyApplication.preferences().getInt(Constants.MAP_STYLE, 0)
        binding.mapNeshan.mapStyle = mapStyle

//        centerMarker = Marker(latLng, getCenterMarkerStyle())
//        binding.mapNeshan.addMarker(centerMarker)
    }

    private fun checkNet(s: String) {
        NetDetector.check {
            if (it) {
                search(s)
            } else {
                Utils.showSnackBar(
                    applicationContext,
                    binding.etSearchOnMap,
                    getString(R.string.noInternet),
                    getColor(R.color.snackBar)
                )
            }
        }
    }

    private fun search(term: String) {
        val searchPosition: LatLng = binding.mapNeshan.cameraTargetPosition
//        updateCenterMarker(searchPosition)
        VolleyGetSearchNeshan.getSearchNeshan(object : VolleyInterface<NeshanSearchResult> {
            override fun onSuccess(body: NeshanSearchResult?) {

                if (body?.count == 403) {
                    Utils.showSnackBar(
                        this@MapActivity, binding.etSearchOnMap, "کلید دسترسی نامعتبر",
                        getColor(R.color.snackBar)
                    )
                    return
                }

                if (body?.items != null) {
                    items = (body.items as ArrayList<Item>)
                    if (items.isEmpty()) {
                        binding.recyclerSearchMap.visibility = View.GONE
                    } else {
                        binding.recyclerSearchMap.visibility = View.VISIBLE
                    }
                    adapter.updateList(items)
                }

            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {

                    val errorResponse =
                        JSONObject(String(error.networkResponse.data, StandardCharsets.UTF_8))
                    Log.e("rrr", "parseNetworkError: $errorResponse")
                }
                Utils.showSnackBar(
                    this@MapActivity, binding.etSearchOnMap, "ارتباط برقرار نشد!",
                    getColor(R.color.snackBar)
                )
            }

        }, this, searchPosition, term, searchTag)
    }

//    private fun updateCenterMarker(LatLng: LatLng) {
//        centerMarker.latLng = LatLng
//    }

//    private fun getCenterMarkerStyle(): MarkerStyle? {
//        val markerStyleBuilder = MarkerStyleBuilder()
//        markerStyleBuilder.size = 50f
//        markerStyleBuilder.bitmap = com.carto.utils.BitmapUtils.createBitmapFromAndroidBitmap(
//            BitmapFactory.decodeResource(
//                resources, R.drawable.center_marker
//            )
//        )
//        return markerStyleBuilder.buildStyle()
//    }

    private fun validateThemePreview() {
        when (mapStyle) {
            NeshanMapStyle.STANDARD_DAY -> binding.themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.map_style_standard_night, theme
                )
            )
            NeshanMapStyle.NESHAN_NIGHT -> binding.themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.map_style_standard_day, theme
                )
            )
            NeshanMapStyle.NESHAN -> binding.themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.map_style_neshan,
                    theme
                )
            )
        }
    }


    fun changeStyle(view: View?) {
        when (mapStyle) {
            NeshanMapStyle.STANDARD_DAY -> mapStyle = NeshanMapStyle.NESHAN_NIGHT
            NeshanMapStyle.NESHAN_NIGHT -> mapStyle = NeshanMapStyle.NESHAN
            NeshanMapStyle.NESHAN -> mapStyle = NeshanMapStyle.STANDARD_DAY
        }
        runOnUiThread { validateThemePreview() }
        binding.mapNeshan.mapStyle = mapStyle
        MyApplication.preferences().edit().putInt(Constants.MAP_STYLE, mapStyle).apply()
    }

    // This method gets a LatLng as input and adds a marker on that position
    private fun createMarker(loc: LatLng): Marker {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        animSt = animStBl.buildStyle()

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_cluster_marker_blue
            )
        )
        // AnimationStyle object - that was created before - is used here
        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()
        if (selectedMarker != null) {
            binding.mapNeshan.removeMarker(selectedMarker)
        }
        // Creating marker
        selectedMarker = Marker(loc, markSt)
        return selectedMarker as Marker
    }

    private fun clearMarkers() {
        binding.mapNeshan.clearMarkers()
        markers.clear()
    }

    private fun addMarker(LatLng: LatLng?, size: Float): Marker {
        selectedMarker = Marker(LatLng, getMarkerStyle(size))
        binding.mapNeshan.addMarker(selectedMarker)
        markers.add(selectedMarker!!)
        return selectedMarker as Marker
    }

    private fun getMarkerStyle(size: Float): MarkerStyle? {
        val styleCreator = MarkerStyleBuilder()
        styleCreator.size = size
        styleCreator.bitmap = com.carto.utils.BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_cluster_marker_blue
            )
        )
        binding.fabAddLocMap.show()
        return styleCreator.buildStyle()
    }

    override fun onSearchItemClick(LatLng: LatLng?, item: Item) {


        UiHandler.keyboardDown(binding.etSearchOnMap, this)
        clearMarkers()
        binding.recyclerSearchMap.visibility = View.GONE
        adapter.updateList(java.util.ArrayList())
        binding.mapNeshan.moveCamera(LatLng, 0f)
        binding.mapNeshan.setZoom(16f, 0f)
        addMarker(LatLng, 30f)

        /*val myItem = ItemsSearchNeshan()
        myItem.title = item.title
        myItem.address = item.address
        myItem.latitude = item.location.latitude
        myItem.longitude = item.location.longitude
        CoroutineScope(IO).launch {
            Log.e("rrr", "onSearchItemClick:myItem " + myItem)
            ViewModelDB.instance.insertData(myItem)
            val i = ViewModelDB.instance.readData
            Log.e("rrr", "onSearchItemClick i : $i")
            i.collect {
                for (b in it) {
                    Log.e("rrr", "onSearchItemClick: b " + b.id)
                }

            }
        }*/

    }


    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                userLocation = locationResult.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                onLocationChange()
            }
        }
        mRequestingLocationUpdates = false
        locationRequest = LocationRequest.create()
        locationRequest.numUpdates = 10
        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun startReceivingLocationUpdates() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mRequestingLocationUpdates = true
                    startLocationUpdates()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied()) {
                        // open device settings when the permission is
                        // denied permanently
                        openSettings()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationSettingsRequest?.let {
            settingsClient.checkLocationSettings(it).addOnSuccessListener(this) {
                Log.i("rrr", "All location settings are satisfied.")
                locationCallback?.let { it1 ->
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        it1,
                        Looper.myLooper()
                    )
                }
                onLocationChange()
            }
                .addOnFailureListener(this)
                { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(
                                "rrr",
                                "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings "
                            )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this, REQUEST_CODE)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i(
                                    "rrr",
                                    "PendingIntent unable to execute request."
                                )
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                            Log.e(
                                "rrr",
                                errorMessage
                            )
//                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    onLocationChange()
                }
        }
    }

    private fun stopLocationUpdates() {
        // Removing location updates
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
                .addOnCompleteListener(
                    this
                ) {

                }
        }
    }


    private fun onLocationChange() {
        if (userLocation != null) {
            addUserMarker(LatLng(userLocation!!.latitude, userLocation!!.longitude))
        }
    }


    private fun addUserMarker(loc: LatLng) {
        //remove existing marker from map
        if (marker != null) {
            binding.mapNeshan.removeMarker(marker)
        }
        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = com.carto.utils.BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, org.neshan.mapsdk.R.drawable.ic_marker
            )
        )
        val markSt = markStCr.buildStyle()

        // Creating user marker
        marker = Marker(loc, markSt)

        // Adding user marker to map!
        binding.mapNeshan.addMarker(marker)
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID, null
        )
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun focusOnUserLocation(view: View?) {
        if (userLocation != null) {
            binding.mapNeshan.moveCamera(
                LatLng(userLocation!!.latitude, userLocation!!.longitude), 0.25f
            )
            binding.mapNeshan.setZoom(15f, 0.25f)
        }
    }
    /* private fun searchDatabase(query: String) {
         val searchQuery = "%$query%"


         val item = arrayListOf<Item>()
         ViewModelDB.instance.searchDatabase(searchQuery).observe(this) { list ->
                 list?.let {
 //                    Log.e("rrr", "searchDatabase:item " + item.first().title )
 //                    Log.e("rrr", "searchDatabase:list " + list.first().title )
                     for (i in 0..list.size step 1) {
                         if (i == 2) break
                         item[i].title = list[i].title
                         item[i].address = list[i].address
                         item[i].location.latitude = list[i].latitude!!
                         item[i].location.longitude = list[i].longitude!!
                         var i = ItemsSearchNeshan(list[i].title, list[i].address, list[i].latitude!!, list[i].longitude!!)
                         item.add(i)
                     }
                 }

         }

         adapter.historyAddList(item)
     }*/

}