package com.roozbeh.toopan.acrivites

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roozbeh.toopan.R
import com.roozbeh.toopan.adapter.ImagesSalonAdapter
import com.roozbeh.toopan.adapter.PossibilitiesAdapter
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyRequest.*
import com.roozbeh.toopan.databinding.ActivityAddOrEditSalonBinding
import com.roozbeh.toopan.dialog.TimingDialog
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.*
import com.roozbeh.toopan.utility.*
import java.io.File
import java.io.IOException


class AddOrEditSalonActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddOrEditSalonBinding
    private var permissionHandler: PermissionHandler? = null
    private var nameFile: String? = null
    private var imageData: ByteArray? = null
    private var getUploadImgSalonTag: String = "getUploadImgSalonTag"
    private var getInfoAddSalonTag: String = "getInfoAddSalonTag"
    private var getInfoEditSalonTag: String = "getInfoEditSalonTag"
    private var getCitySalonTag: String = "getCitySalonTag"
    private var getDeleteImageSalonTag: String = "getDeleteImageSalonTag"
    private var lat = ""
    private var lng = ""
    private var address = ""

    private lateinit var possibilitiesAdapter: PossibilitiesAdapter
    private lateinit var itemsPossibilities: ArrayList<SalonPossibilities>
    private lateinit var imagesSalonAdapter: ImagesSalonAdapter
    private lateinit var itemsImageUploaded: ArrayList<String>

    private var salon: Salons = Salons()
    private var infoSalon: InfoSalon = InfoSalon()
    private var salonOwnerShipSelect: SalonOwnerShip? = SalonOwnerShip()
    private var salonGenderSelect: Gender? = Gender()
    private var salonTypesSelect: SalonTypes? = SalonTypes()
    private var salonPossibilitiesListSelect: ArrayList<SalonPossibilities>? = null
    private var salonStateSelect: State = State()
    private var salonCityList: ListCity = ListCity()
    private var salonCitySelect: City = City()
    private var salonsId: Int? = null
    private var isProfile = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_edit_salon)
        permissionHandler = PermissionHandler(this, this)

        handleAddOrEdit()
        binding.viewTxtInputLayoutGetTimeStartEndADSalon.setOnClickListener(this)
        binding.txtSelectOnMap.setOnClickListener(this)
        binding.viewTxtInputLayoutAddressADSalon.setOnClickListener(this)
        binding.autoTextCitySalon.setOnClickListener(this)
        binding.imgChangeProfileADSalon.setOnClickListener(this)
        binding.txtBtnUploadImageADSalon.setOnClickListener(this)


    }

    //Possibilities
    private fun initRecyclerPossibilities() {
        //Possibilities
        itemsPossibilities = ArrayList()
        possibilitiesAdapter = PossibilitiesAdapter(
            infoSalon.salonPossibilities,
            salon.salonPossibilities,
            applicationContext,
            object : PossibilitiesAdapter.OnItemClickListener {
                override fun onItemClick(selectedItems: ArrayList<SalonPossibilities>) {
                    salonPossibilitiesListSelect = selectedItems
                }

            })
        binding.recyclerPossibilitiesSalon.layoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerPossibilitiesSalon.adapter = possibilitiesAdapter
        //Possibilities


    }

    private fun showAlertDialog() {
        val dialog = TimingDialog(this)
        salon.startTime?.let { salon.endTime?.let { it1 -> dialog.setTimes(it, it1) } }
        dialog.setOnClickListener { start, end ->
            binding.etGetTimeStartEndADSalon.setText("از $start تا $end")
        }

        dialog.show()
    }

    // change image salon

    private fun popupSelectType(isProfile: Boolean) {
        this.isProfile = isProfile
        if (permissionHandler!!.isStoragePermissionGranted(this)) {
            popupDialog()
        } else {


            permissionHandler!!.requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            permissionHandler!!.setPermissionGranted(object : PermissionHandler.PermissionGranted {
                override fun onPermissionGranted() {
                    popupDialog()
                }

            })

        }
    }

    private fun popupDialog() {
        MaterialAlertDialogBuilder(
            this,
            R.style.MyTitle_ThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(resources.getString(R.string.GalleryOrCamera))
//                .setMessage(context.resources.getString(R.string.setting))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->

                // Respond to neutral button press
            }
            .setNegativeButton(resources.getString(R.string.gallery)) { dialog, which ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf("image/jpeg", "image/png")
                )
                intent.type = "*/*"
                someActivityResultLauncherForGallery.launch(intent)
            }
            .setPositiveButton(resources.getString(R.string.camera)) { dialog, which ->
                if (permissionHandler!!.isCameraPermissionGranted(this)) {
                    takePhoto()
                } else {
                    permissionHandler!!.requestPermission(Manifest.permission.CAMERA)
                    permissionHandler!!.setPermissionGranted(object :
                        PermissionHandler.PermissionGranted {
                        override fun onPermissionGranted() {

                            takePhoto()
                        }

                    })
                }


            }
            .show()

    }

    private fun takePhoto() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        val file = FileManager.generateFileName(
            "salonPhoto",
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
        )
        nameFile = file.name
        val uri = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName + ".provider",
            file
        )
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        someActivityResultLauncherForCamera.launch(takePicture)
    }


    private var someActivityResultLauncherForGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val path = data.data!!
            if (isProfile) {
                createImageDataForProfile(path)
            } else {
                createImageDataForUpload(path)
            }

        }
    }


    private var someActivityResultLauncherForCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK /*&& result.data != null*/) {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                nameFile
            )
            val uri = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file
            )
            if (isProfile) {
                createImageDataForProfile(uri)
            } else {
                createImageDataForUpload(uri)
            }


        }
    }

    @Throws(IOException::class)
    private fun createImageDataForProfile(uri: Uri) {
        binding.imgProfileADSalon.setImageURI(uri)
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    @Throws(IOException::class)
    private fun createImageDataForUpload(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
            sendImageSalon()
        }
    }

    private fun sendImageSalon() {
        salonsId?.let {
            VolleySendImageAddEditSalon.sendImage(
                object : VolleyInterface<Salons> {
                    override fun onSuccess(body: Salons) {
                        Log.e("rrr", "onSuccess: " + body )

                        initImageUploaded(body)
                    }

                    override fun onFailed(error: VolleyError?) {

                        binding.btnSaveInfoADSalon.isEnabled = true
                        binding.btnLoadingADSalon.visibility = View.GONE
                        if (error != null) {
                            error.message?.let {
                                Utils.showSnackBar(
                                    applicationContext,
                                    binding.btnSaveInfoADSalon,
                                    it,
                                    getColor(R.color.snackBar)
                                )
                            }
                        }
                    }

                },
                this,
                it,
                imageData ?: return,
                getUploadImgSalonTag
            )
        }
    }

    private fun sendImageProfile() {

        salonsId?.let {
            VolleySendProfileAddEditSalon.sendImage(
                object : VolleyInterface<Salons> {
                    override fun onSuccess(body: Salons) {
    //                    sendEditInfoRequest()

                    }

                    override fun onFailed(error: VolleyError?) {

                        binding.btnSaveInfoADSalon.isEnabled = true
                        binding.btnLoadingADSalon.visibility = View.GONE
                        if (error != null) {
                            error.message?.let {
                                Utils.showSnackBar(
                                    applicationContext,
                                    binding.btnSaveInfoADSalon,
                                    it,
                                    getColor(R.color.snackBar)
                                )
                            }
                        }
                    }

                },
                this,
                it,
                imageData ?: return,
                getUploadImgSalonTag
            )
        }
    }
    // change image salon


    //map
    private fun transactionMap() {
        val intent = Intent(this, MapActivity::class.java)
        someActivityResultLauncherForMap.launch(intent)
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )


    }

    private var someActivityResultLauncherForMap = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
//            Log.e("rrr", "resualt1: " + result.data?.getDoubleExtra("lat",0.0))
//            Log.e("rrr", "resualt2: " + result.data?.getDoubleExtra("lng",0.0))
            getLatLng(result.data)
        }
    }

    private fun getLatLng(data: Intent?) {
        lat = data?.getDoubleExtra("lat", 0.0).toString()
        lng = data?.getDoubleExtra("lng", 0.0).toString()
        address = data?.getStringExtra("address").toString()
        binding.etAddressADSalon.setText(address)
    }
    //map


    // city
    private fun checkNetForCity() {
        NetDetector.check {
            if (it) {
                requestServerCity()
            }
        }
    }

    private fun requestServerCity() {

        salonStateSelect.id?.let { it ->
            VolleyGetCity.getCity(object : VolleyInterface<ListCity> {
                override fun onSuccess(body: ListCity) {
                    salonCityList = body
                    body.cities?.let { setCity(it) }
                }

                override fun onFailed(error: VolleyError?) {
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(
                                applicationContext,
                                binding.spinnerCity,
                                it,
                                getColor(R.color.snackBar)
                            )
                        }
                    }
                }

            }, this, it, getCitySalonTag)
        }

    }

    private fun setCity(city: List<City>) {
        val item: ArrayList<String> = ArrayList()
        for (i in city.indices step 1) {
            city[i].name?.let { item.add(it) }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, item
        )

        binding.autoTextCitySalon.setAdapter(adapter)
        binding.autoTextCitySalon.postDelayed(Runnable {
            binding.autoTextCitySalon.showDropDown()
        }, 10)


        binding.autoTextCitySalon.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                Log.e("rrr", "setCity: " + id)
//            val item = parent.getItemAtPosition(position)
                salonCitySelect.id = city[id.toInt()].id
                UiHandler.keyboardDown(binding.autoTextCitySalon, this)
                binding.autoTextCitySalon.isFocusable = false
            }


    }


    // ownerShip
    private fun setOwnerShip(salonOwnerShipList: ArrayList<SalonOwnerShip>) {

        val item: ArrayList<String> = ArrayList()
        for (i in salonOwnerShipList.indices step 1) {
            salonOwnerShipList[i].type?.let { item.add(it) }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, item
        )

        binding.autoTextTypeOwnershipSalon.setAdapter(adapter)
//        binding.autoTextTypeOwnershipSalon.postDelayed(Runnable {
//            binding.autoTextTypeOwnershipSalon.showDropDown()
//        }, 10)


        binding.autoTextTypeOwnershipSalon.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
//            val item = parent.getItemAtPosition(position)
                salonOwnerShipSelect?.id = salonOwnerShipList[id.toInt()].id
                UiHandler.keyboardDown(binding.autoTextTypeOwnershipSalon, this)
                binding.autoTextTypeOwnershipSalon.isFocusable = false
            }


    }


    //gender
    private fun setGender() {
        val item: ArrayList<String> = ArrayList()
        for (i in infoSalon.sexes.indices step 1) {
            infoSalon.sexes[i].type?.let { item.add(it) }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, item
        )

//        val gender1 = genders.find { gender -> user.gender == gender.id }
//        if (gender1 != null) {
//            binding.autoTextGender.setText(gender1.type)
//        }


        binding.autoTextGenderSalon.setAdapter(adapter)

        binding.autoTextGenderSalon.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                salonGenderSelect?.id = infoSalon.sexes[id.toInt()].id
                UiHandler.keyboardDown(binding.autoTextGenderSalon, this)
                binding.autoTextGenderSalon.isFocusable = false
            }


    }

    //SalonType
    private fun setSalonType() {

        val item: ArrayList<String> = ArrayList()
        for (i in infoSalon.salonTypes.indices step 1) {
            infoSalon.salonTypes[i].type?.let { item.add(it) }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, item
        )


        binding.autoTextTypeInterfaceSalon.setAdapter(adapter)

        binding.autoTextTypeInterfaceSalon.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                salonTypesSelect?.id = infoSalon.salonTypes[id.toInt()].id
                UiHandler.keyboardDown(binding.autoTextTypeInterfaceSalon, this)
                binding.autoTextTypeInterfaceSalon.isFocusable = false
            }
    }

    //state
    fun setState(state: List<State>) {
        val states: ArrayList<String> = ArrayList()
        for (i in state.indices step 1) {
            state[i].name?.let { states.add(it) }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, states
        )

        binding.autoTextStateSalon.setAdapter(adapter)
//        binding.autoTextStateSalon.postDelayed(Runnable {
//            binding.autoTextState.setText("PREM")
//            binding.autoTextStateSalon.showDropDown()
//        }, 10)


        binding.autoTextStateSalon.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
//            val item = parent.getItemAtPosition(position)
                salonStateSelect.id = state[id.toInt()].id!!
                UiHandler.keyboardDown(binding.autoTextStateSalon, this)
                binding.autoTextStateSalon.isFocusable = false

                checkNetForCity()
            }

    }


    private fun setContent() {
        if (infoSalon.salon.images.isNotEmpty()) {
            Glide.with(this)
                .load(Constants.BASE_URL + infoSalon.salon.images)
                .placeholder(R.drawable.ic_profile)
                .into(binding.imgProfileADSalon)
        }

        binding.etNameSalonADSalon.setText(salon.name)
        binding.autoTextTypeInterfaceSalon.setText(infoSalon.salonTypes.find { salonTypes -> salonTypes.type == salon.salonTypes?.first()?.type }?.type)
        binding.autoTextTypeOwnershipSalon.setText(infoSalon.ownership.find { ownership -> ownership.id == salon.ownership }?.type)
        binding.autoTextGenderSalon.setText(infoSalon.sexes.find { gender -> gender.id == salon.sex }?.type)
        binding.autoTextStateSalon.setText(infoSalon.states.find { state -> salon.city?.state?.name == state.name }?.name)
        binding.autoTextCitySalon.setText(salon.city?.name)
        //todo address set
        binding.etAddressADSalon.setText(salon.address)
        binding.etGetTimeStartEndADSalon.setText("از ${salon.startTime} تا ${salon.endTime}")
        binding.etPriceSessionADSalon.setText(salon.amount.toString())
        binding.etPriceSessionADSalon.setText(salon.periodMin.toString())

    }


    private fun handleAddOrEdit() {
        intent.extras?.let { salonsId = it.getInt(Constants.BUNDLE_ADD_OR_EDIT_KEY) }
        checkNet()
    }


    private fun checkNet() {
        binding.refreshAESalon.visibility = View.VISIBLE
        binding.pageWhiteADSalon.visibility = View.VISIBLE
        NetDetector.check {
            if (it) {
                if (salonsId != null) {
                    salonsId?.let { it1 -> requestEditSalon(it1) }
                } else {
                    requestAddServers()
                }
            } else {
                binding.refreshAESalon.visibility = View.VISIBLE
                binding.pageWhiteADSalon.visibility = View.VISIBLE
                Utils.showSnackBar(
                    applicationContext,
                    binding.coordinatorADSalon,
                    getString(R.string.noInternet),
                    getColor(R.color.snackBar)
                )
            }
        }
    }

    private fun initImageUploaded(salon:Salons) {
        itemsImageUploaded = ArrayList()
        imagesSalonAdapter = ImagesSalonAdapter(
            salon.images,
            applicationContext,
            object : ImagesSalonAdapter.OnItemClickListener {
                override fun onItemClick(selectedItems: String) {
                    salonsId?.let { it1 ->
                        VolleyPostDeleteImageSalon.deleteImage(object :VolleyInterface<Salons>{
                            override fun onSuccess(body: Salons) {
                                initImageUploaded(body)
                            }

                            override fun onFailed(error: VolleyError?) {
                                if (error != null) {
                                    error.message?.let {
                                        Utils.showSnackBar(
                                            applicationContext,
                                            binding.coordinatorADSalon,
                                            it,
                                            getColor(R.color.snackBar)
                                        )
                                    }
                                }
                            }
                        }, applicationContext,
                            it1, selectedItems, getDeleteImageSalonTag)
                    }
                }

            })
        binding.recyclerUploadImageADSalon.layoutManager =
            StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerUploadImageADSalon.adapter = imagesSalonAdapter
    }

    private fun requestEditSalon(id: Int) {

        VolleyGetInfoForEditSalon.getInfo(object : VolleyInterface<InfoSalon> {
            override fun onSuccess(body: InfoSalon) {
                salon = body.salon
                infoSalon = body
                setContent()
                setOwnerShip(infoSalon.ownership)
                setGender()
                setSalonType()
                initRecyclerPossibilities()
                initImageUploaded(salon)
                setState(infoSalon.states)


                handleErrorEt()

                binding.refreshAESalon.visibility = View.GONE
                binding.pageWhiteADSalon.visibility = View.GONE
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.coordinatorADSalon,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }

            }

        }, id, applicationContext, getInfoEditSalonTag)

    }


    private fun requestAddServers() {
        VolleyGetInfoForMakeSalon.getInfo(object : VolleyInterface<InfoSalon> {
            override fun onSuccess(body: InfoSalon) {
                infoSalon.ownership = body.ownership
                setOwnerShip(infoSalon.ownership)
                infoSalon.sexes = body.sexes
                setGender()
                infoSalon.salonTypes = body.salonTypes
                setSalonType()
                infoSalon.salonPossibilities = body.salonPossibilities
                initRecyclerPossibilities()
                infoSalon.states = body.states
                setState(infoSalon.states)

                handleErrorEt()
                binding.refreshAESalon.visibility = View.GONE
                binding.pageWhiteADSalon.visibility = View.GONE
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.coordinatorADSalon,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }

            }

        }, applicationContext, getInfoAddSalonTag)

    }


    private fun handleErrorEt() {
        binding.etNameSalonADSalon.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.txtInputLayoutNameSalonADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutNameSalonADSalon.error = null
            }
        }

        binding.autoTextTypeInterfaceSalon.doOnTextChanged { text, start, before, count ->
            salonTypesSelect =
                infoSalon.salonTypes.find { salonType -> salonType.type == text.toString() }

            if (salonTypesSelect?.type == null) {
                binding.txtInputLayoutTypeInterfaceSalon.error = getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutTypeInterfaceSalon.error = null
            }

        }

//        binding.autoTextTypeOwnershipSalon.doOnTextChanged { text, start, before, count ->
//            salonOwnerShipSelect
//        }

    }


    override fun onClick(v: View) {
        when (v.id) {
            binding.viewTxtInputLayoutGetTimeStartEndADSalon.id -> showAlertDialog()
            binding.imgChangeProfileADSalon.id -> popupSelectType(isProfile = true)
            binding.txtBtnUploadImageADSalon.id -> popupSelectType(isProfile = false)
            binding.txtSelectOnMap.id -> transactionMap()
            binding.viewTxtInputLayoutAddressADSalon.id -> transactionMap()


            binding.autoTextCitySalon.id -> {
                Log.e("rrr", "onClick: " + salonStateSelect.id)
                if (salonStateSelect.id != null) {
//                    checkNetForCity()
                } else {
                    UiHandler.keyboardDown(binding.autoTextCitySalon, this)
                    Utils.showSnackBar(
                        this,
                        binding.autoTextCitySalon,
                        getString(R.string.selectState),
                        getColor(R.color.snackBar)
                    )
                }

            }
        }
    }
}