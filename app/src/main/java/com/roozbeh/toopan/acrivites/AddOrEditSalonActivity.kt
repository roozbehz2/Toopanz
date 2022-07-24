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
import android.widget.CompoundButton
import android.widget.RadioGroup
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
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
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
    private lateinit var nameFile: String
    private var imageData: ByteArray? = null
    private var imageDataSalon: ByteArray? = null
    private var getUploadImgSalonTag: String = "getUploadImgSalonTag"
    private var getInfoAddSalonTag: String = "getInfoAddSalonTag"
    private var getInfoEditSalonTag: String = "getInfoEditSalonTag"
    private var getCitySalonTag: String = "getCitySalonTag"
    private var getDeleteImageSalonTag: String = "getDeleteImageSalonTag"
    private var getAddSalonTag: String = "getAddSalonTag"
    private var getEditSalonTag: String = "getEditSalonTag"
    private var lat = ""
    private var lng = ""
    private var latD = 0.0
    private var lngD = 0.0
    private var address = ""
    private var startTime = ""
    private var endTime = ""

    private lateinit var possibilitiesAdapter: PossibilitiesAdapter
    private lateinit var itemsPossibilities: ArrayList<SalonPossibilities>
    private lateinit var imagesSalonAdapter: ImagesSalonAdapter
//    private lateinit var itemsImageUploaded: ArrayList<String>

    private var salon: Salons = Salons()
    private var infoSalon: InfoSalon = InfoSalon()
    private var salonOwnerShipSelect: SalonOwnerShip? = SalonOwnerShip()
    private var salonGenderSelect: Gender? = Gender()
    private var salonTypesSelect: SalonTypes? = SalonTypes()
    private var salonPossibilitiesListSelect: ArrayList<SalonPossibilities>? = null
    private var salonStateSelect: State? = State()
    private var salonCityList: ListCity = ListCity()
    private var salonCitySelect: City? = City()
    private var salonsId: Int? = null
    private var isProfile = true
    private val bodyAddOrEditSalon = BodyAddOrEditSalon()

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
        binding.btnSaveInfoADSalon.setOnClickListener(this)

        binding.toolbarADSalon.setNavigationOnClickListener {
            onBackPressed()
        }


    }

    private fun checkCurrentLocation() {

        binding.checkboxSalon.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                salonCitySelect?.id = MyApplication.preferences().getInt(Constants.CITY_ID, -1)
                salonStateSelect?.id = MyApplication.preferences().getInt(Constants.STATE_ID, -1)
                binding.autoTextStateSalon.isEnabled = false
                binding.autoTextCitySalon.isEnabled = false

            } else {
                binding.autoTextStateSalon.setText(infoSalon.states.find { state -> salon.city?.state?.name == state.name }?.name)
                salonStateSelect =
                    infoSalon.states.find { state -> salon.city?.state?.name == state.name }
                binding.autoTextCitySalon.setText(salon.city?.name)
                salonCitySelect = salon.city
                binding.autoTextStateSalon.isEnabled = true
                binding.autoTextCitySalon.isEnabled = true
            }
        }


    }

    override fun onBackPressed() {
        if (binding.btnLoadingADSalon.visibility == View.GONE) {
            super.onBackPressed()
            finish()
        }
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
            startTime = start
            endTime = end
            binding.etGetTimeStartEndADSalon.setText("از $startTime تا $endTime")
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
            Constants.MEDIA
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
                Constants.MEDIA,
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
            imageDataSalon = it.readBytes()
            sendImageSalon()
        }
    }

    private fun sendImageSalon() {
        salonsId?.let { it1 ->
            VolleySendImageAddEditSalon.sendImage(
                object : VolleyInterface<Salons> {
                    override fun onSuccess(body: Salons?) {

                        body?.let {
                            initImageUploaded(it)
                        }
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
                it1,
                imageDataSalon ?: return,
                getUploadImgSalonTag
            )
        }
    }

    private fun sendImageProfile() {

        salonsId?.let {
            VolleySendProfileAddEditSalon.sendImage(
                object : VolleyInterface<Salons> {
                    override fun onSuccess(body: Salons?) {

                        if (salonsId == null) {
                            checkNetAndRequestAdd(bodyAddOrEditSalon)
                        } else {
                            checkNetAndRequestEdit(bodyAddOrEditSalon)
                        }

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
        data?.getDoubleExtra("lat", 0.0)?.let { latD = it }
        data?.getDoubleExtra("lng", 0.0)?.let { lngD = it }
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

        salonStateSelect?.id?.let { it ->
            VolleyGetCity.getCity(object : VolleyInterface<ListCity> {
                override fun onSuccess(body: ListCity?) {
                    body?.let {
                        salonCityList = it
                    }
                    body?.cities?.let { setCity(it) }
                }

                override fun onFailed(error: VolleyError?) {
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(
                                applicationContext,
                                binding.spinnerCityADSalon,
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
//            val item = parent.getItemAtPosition(position)
                salonCitySelect?.id = city[id.toInt()].id
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
                salonStateSelect?.id = state[id.toInt()].id!!
                UiHandler.keyboardDown(binding.autoTextStateSalon, this)
                binding.autoTextStateSalon.isFocusable = false

                checkNetForCity()
            }

    }


    private fun setContent() {
        if (infoSalon.salon.avatar?.isNotEmpty() == true) {
            Glide.with(this)
                .load(Constants.BASE_URL + infoSalon.salon.avatar)
                .placeholder(R.drawable.ic_profile)
                .into(binding.imgProfileADSalon)
        }

        binding.etNameSalonADSalon.setText(salon.name)
        binding.autoTextTypeInterfaceSalon.setText(infoSalon.salonTypes.find { salonTypes -> salonTypes.type == salon.salonTypes?.first()?.type }?.type)
        binding.autoTextTypeOwnershipSalon.setText(infoSalon.ownership.find { ownership -> ownership.id == salon.ownership }?.type)
        binding.autoTextGenderSalon.setText(infoSalon.sexes.find { gender -> gender.id == salon.sex }?.type)
        binding.autoTextStateSalon.setText(infoSalon.states.find { state -> salon.city?.state?.name == state.name }?.name)
        salonStateSelect = infoSalon.states.find { state -> salon.city?.state?.name == state.name }
        binding.autoTextCitySalon.setText(salon.city?.name)
        salonCitySelect = salon.city
        //todo address set
        binding.etAddressADSalon.setText(salon.address)
        binding.etGetTimeStartEndADSalon.setText("از ${salon.startTime} تا ${salon.endTime}")
        binding.etPriceSessionADSalon.setText(salon.amount.toString())
        binding.etGetTimeOneSessionADSalon.setText(salon.periodMin.toString())

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

    private fun initImageUploaded(salon: Salons) {
//        itemsImageUploaded = ArrayList()
        if (salon.images.isNotEmpty()) {
            if (salon.images[0].isNotEmpty()) {

                imagesSalonAdapter = ImagesSalonAdapter(
                    salon.images,
                    applicationContext,
                    object : ImagesSalonAdapter.OnItemClickListener {
                        override fun onItemClick(selectedItems: String) {
                            salonsId?.let { it1 ->
                                VolleyPostDeleteImageSalon.deleteImage(
                                    object : VolleyInterface<Salons> {
                                        override fun onSuccess(body: Salons?) {
                                            body?.let {
                                                initImageUploaded(it)
                                            }
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
                                    it1, selectedItems, getDeleteImageSalonTag
                                )
                            }
                        }

                    })
                binding.recyclerUploadImageADSalon.layoutManager =
                    StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL)
                binding.recyclerUploadImageADSalon.adapter = imagesSalonAdapter
            }
        }
    }

    private fun requestEditSalon(id: Int) {

        VolleyGetInfoForEditSalon.getInfo(object : VolleyInterface<InfoSalon> {
            override fun onSuccess(body: InfoSalon?) {
                body?.let {

                    salon = it.salon
                    infoSalon = it
                }
                setContent()
                setOwnerShip(infoSalon.ownership)
                setGender()
                setSalonType()
                initRecyclerPossibilities()
                initImageUploaded(salon)
                setState(infoSalon.states)
                checkCurrentLocation()


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
            override fun onSuccess(body: InfoSalon?) {
                body?.let {

                    infoSalon.ownership = it.ownership
                    setOwnerShip(infoSalon.ownership)
                    infoSalon.sexes = it.sexes
                    setGender()
                    infoSalon.salonTypes = it.salonTypes
                    setSalonType()
                    infoSalon.salonPossibilities = it.salonPossibilities
                    initRecyclerPossibilities()
                    infoSalon.states = it.states
                    setState(infoSalon.states)
                    checkCurrentLocation()

                    handleErrorEt()
                }
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

    private fun checkErrorEtNameSalonADSalon(): Boolean {
        return if (binding.etNameSalonADSalon.text.toString().isEmpty()) {
            binding.txtInputLayoutNameSalonADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutNameSalonADSalon.error = null
            true
        }
    }

    private fun checkErrorAutoTextTypeInterfaceSalon(): Boolean {
        salonTypesSelect =
            infoSalon.salonTypes.find { salonType -> salonType.type == binding.autoTextTypeInterfaceSalon.text.toString() }

        return if (salonTypesSelect?.type == null) {
            binding.txtInputLayoutTypeInterfaceSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutTypeInterfaceSalon.error = null
            true
        }
    }

    private fun checkErrorAutoTextTypeOwnershipSalon(): Boolean {
        salonOwnerShipSelect =
            infoSalon.ownership.find { salonOwnerShip -> salonOwnerShip.type == binding.autoTextTypeOwnershipSalon.text.toString() }
        return if (salonOwnerShipSelect?.type == null) {
            binding.txtInputLayoutTypeOwnershipSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutTypeOwnershipSalon.error = null
            true
        }
    }

    private fun checkErrorAutoTextGenderSalon(): Boolean {
        salonGenderSelect =
            infoSalon.sexes.find { gender -> gender.type == binding.autoTextGenderSalon.text.toString() }
        return if (salonGenderSelect?.type == null) {
            binding.spinnerSexADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.spinnerSexADSalon.error = null
            true
        }
    }

    private fun checkErrorAutoTextStateSalon(): Boolean {
        salonStateSelect =
            infoSalon.states.find { state -> state.name == binding.autoTextStateSalon.text.toString() }

        return if (salonStateSelect?.name == null) {
            binding.spinnerStateADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.spinnerStateADSalon.error = null
            true
        }
    }

    private fun checkErrorAutoTextCitySalon(): Boolean {
        if (salonCityList.cities != null) {

            salonCitySelect =
                salonCityList.cities?.find { city -> city.name == binding.autoTextCitySalon.text.toString() }
        }

        return if (salonCitySelect?.name == null) {
            binding.spinnerCityADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.spinnerCityADSalon.error = null
            true
        }
    }

    private fun checkErrorEtAddressADSalon(): Boolean {
        return if (binding.etAddressADSalon.text.toString().isEmpty()) {
            binding.txtInputLayoutAddressADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutAddressADSalon.error = null
            true
        }
    }

    private fun checkErrorEtGetTimeStartEndADSalon(): Boolean {
        return if (binding.etGetTimeStartEndADSalon.text.toString().isEmpty()) {
            binding.txtInputLayoutGetTimeStartEndADSalon.error =
                getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutGetTimeStartEndADSalon.error = null
            true
        }
    }

    private fun checkErrorEtGetTimeOneSessionADSalon(): Boolean {
        return if (binding.etGetTimeOneSessionADSalon.text.toString().isEmpty()) {
            binding.txtInputLayoutGetTimeOneSessionADSalon.error =
                getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutGetTimeOneSessionADSalon.error = null
            true
        }
    }

    private fun checkErrorEtPriceSessionADSalon(): Boolean {
        return if (binding.etPriceSessionADSalon.text.toString().isEmpty()) {
            binding.txtInputLayoutPriceSessionADSalon.error = getString(R.string.errorRequired)
            false
        } else {
            binding.txtInputLayoutPriceSessionADSalon.error = null
            true
        }
    }


    private fun checkErrorPossibilitiesSalon(): Boolean {
        return salonPossibilitiesListSelect?.size != 0
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

        binding.autoTextTypeOwnershipSalon.doOnTextChanged { text, _, before, count ->
            salonOwnerShipSelect =
                infoSalon.ownership.find { salonOwnerShip -> salonOwnerShip.type == text.toString() }
            if (salonOwnerShipSelect?.type == null) {
                binding.txtInputLayoutTypeOwnershipSalon.error = getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutTypeOwnershipSalon.error = null
            }
        }

        binding.autoTextGenderSalon.doOnTextChanged { text, start, before, count ->
            salonGenderSelect =
                infoSalon.sexes.find { gender -> gender.type == text.toString() }
            if (salonGenderSelect?.type == null) {
                binding.spinnerSexADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.spinnerSexADSalon.error = null
            }
        }

        binding.autoTextStateSalon.doOnTextChanged { text, start, before, count ->
            salonStateSelect =
                infoSalon.states.find { state -> state.name == text.toString() }

            if (salonStateSelect?.name == null) {
                binding.spinnerStateADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.spinnerStateADSalon.error = null
            }
        }

        binding.autoTextCitySalon.doOnTextChanged { text, start, before, count ->
            salonCitySelect =
                salonCityList.cities?.find { city -> city.name == text.toString() }
            if (salonCitySelect?.name == null) {
                binding.spinnerCityADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.spinnerCityADSalon.error = null
            }
        }

        binding.etAddressADSalon.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.txtInputLayoutAddressADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutAddressADSalon.error = null
            }
        }

        binding.etGetTimeStartEndADSalon.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.txtInputLayoutGetTimeStartEndADSalon.error =
                    getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutGetTimeStartEndADSalon.error = null
            }
        }

        binding.etGetTimeOneSessionADSalon.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.txtInputLayoutGetTimeOneSessionADSalon.error =
                    getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutGetTimeOneSessionADSalon.error = null
            }
        }

        binding.etPriceSessionADSalon.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()) {
                binding.txtInputLayoutPriceSessionADSalon.error = getString(R.string.errorRequired)
            } else {
                binding.txtInputLayoutPriceSessionADSalon.error = null
            }
        }

    }

    private fun checkErrorET() {
        if (checkErrorEtNameSalonADSalon() &&
            checkErrorPossibilitiesSalon() &&
            checkErrorAutoTextTypeInterfaceSalon() &&
            checkErrorAutoTextTypeOwnershipSalon() &&
            checkErrorAutoTextGenderSalon() &&
            checkErrorAutoTextStateSalon() &&
            checkErrorAutoTextCitySalon() &&
            checkErrorEtAddressADSalon() &&
            checkErrorEtGetTimeStartEndADSalon() &&
            checkErrorEtGetTimeOneSessionADSalon() &&
            checkErrorEtPriceSessionADSalon()
        ) {
            bodyAddOrEditSalon.id = salonsId
            bodyAddOrEditSalon.name = binding.etNameSalonADSalon.text.toString()
            bodyAddOrEditSalon.ownership = salonOwnerShipSelect?.id
            bodyAddOrEditSalon.sex = salonGenderSelect?.id
            /*val address = */Address().also {
                it.address = binding.etAddressADSalon.text.toString()
                it.cityId = salonCitySelect?.id
                it.lat = latD
                it.lng = lngD
            }.run {
                bodyAddOrEditSalon.address = this
            }

            val possibilities = arrayListOf<Int>()
            if (salonPossibilitiesListSelect?.isNotEmpty() == true) {

                for (poss in salonPossibilitiesListSelect!!) {
                    poss.id?.let { possibilities.add(it) }
                }

            }
            bodyAddOrEditSalon.salonPossibilities = possibilities
            salonTypesSelect?.id?.let { bodyAddOrEditSalon.salonTypes = arrayListOf(it) }
            bodyAddOrEditSalon.startTime = startTime
            bodyAddOrEditSalon.endTime = endTime
            bodyAddOrEditSalon.periodMin =
                binding.etGetTimeOneSessionADSalon.text.toString().toInt()
            bodyAddOrEditSalon.amount = binding.etPriceSessionADSalon.text.toString().toInt()
            bodyAddOrEditSalon.ownerId = MyApplication.preferences().getInt(Constants.ID_KEY, -1)


            binding.btnSaveInfoADSalon.isEnabled = false
            binding.btnLoadingADSalon.visibility = View.VISIBLE
            NetDetector.check {
                if (it) {
                    if (imageData != null) {
                        sendImageProfile()
                    } else {
                        if (salonsId == null) {
                            checkNetAndRequestAdd(bodyAddOrEditSalon)
                        } else {
                            checkNetAndRequestEdit(bodyAddOrEditSalon)
                        }
                    }
                } else {
                    binding.btnSaveInfoADSalon.isEnabled = true
                    binding.btnLoadingADSalon.visibility = View.GONE
                    Utils.showSnackBar(
                        applicationContext,
                        binding.btnSaveInfoADSalon,
                        getString(R.string.noInternet),
                        getColor(R.color.snackBar)
                    )
                }
            }

        } else if (!checkErrorPossibilitiesSalon()) {
            Utils.showSnackBar(
                this,
                binding.viewTxtInputLayoutAddressADSalon,
                getString(R.string.oneSelectPossibilities),
                getColor(R.color.redAlarm)
            )
        } else {
            Utils.showSnackBar(
                this,
                binding.viewTxtInputLayoutAddressADSalon,
                getString(R.string.errorRequired),
                getColor(R.color.redAlarm)
            )
        }


    }

    private fun checkNetAndRequestAdd(body: BodyAddOrEditSalon) {
        VolleyPostAddSalon.addSalon(object : VolleyInterface<Salons> {
            override fun onSuccess(body: Salons?) {
                binding.btnSaveInfoADSalon.isEnabled = true
                binding.btnLoadingADSalon.visibility = View.GONE
                finish()
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext, binding.viewTxtInputLayoutAddressADSalon,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, body, getAddSalonTag)
    }

    private fun checkNetAndRequestEdit(body: BodyAddOrEditSalon) {
        VolleyPostEditSalon.editSalon(object : VolleyInterface<Salons> {
            override fun onSuccess(body: Salons?) {
                binding.btnSaveInfoADSalon.isEnabled = true
                binding.btnLoadingADSalon.visibility = View.GONE
                finish()
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext, binding.viewTxtInputLayoutAddressADSalon,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, body, getEditSalonTag)
    }


    override fun onClick(v: View) {
        when (v.id) {
            binding.viewTxtInputLayoutGetTimeStartEndADSalon.id -> showAlertDialog()
            binding.imgChangeProfileADSalon.id -> popupSelectType(isProfile = true)
            binding.txtBtnUploadImageADSalon.id -> popupSelectType(isProfile = false)
            binding.txtSelectOnMap.id -> transactionMap()
            binding.viewTxtInputLayoutAddressADSalon.id -> transactionMap()


            binding.autoTextCitySalon.id -> {
                if (salonStateSelect?.id != null) {
                    checkNetForCity()
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
            binding.btnSaveInfoADSalon.id -> checkErrorET()
        }
    }

    override fun onStop() {
        super.onStop()
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getUploadImgSalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getInfoAddSalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getInfoEditSalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getCitySalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getDeleteImageSalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getAddSalonTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getEditSalonTag)
    }
}