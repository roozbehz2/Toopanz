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
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roozbeh.toopan.R
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.communication.volleyRequest.*
import com.roozbeh.toopan.databinding.ActivityEditInfoUserBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.*
import com.roozbeh.toopan.utility.*
import java.io.File
import java.io.IOException


class EditInfoUserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditInfoUserBinding
    private var getUserTag: String? = "getUserTag"
    private var getGenderTag: String? = "getGenderTag"
    private var getStateTag: String? = "getStateTag"
    private var getCityTag: String? = "getCityTag"
    private var getUpdateProfileTag: String? = "getUpdateProfileTag"
    private var getUploadProfileTag: String? = "getUploadProfileTag"
    private var user: User = User()
    private var genders: ListGender = ListGender()
    private var stateId = 0
    private var cityId = 0
    private var genderId = 0
    private var imageData: ByteArray? = null
    private lateinit var nameFile :String

    //    var cameraPhotoFilePath: Uri? = null
    private var permissionHandler: PermissionHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_info_user)
        permissionHandler = PermissionHandler(this, this)
        binding.btnSaveInfo.setOnClickListener(this)
        binding.pageWhiteEditProfile.setOnClickListener(this)
        binding.imgChangeProfile.setOnClickListener(this)
        binding.imgProfileEdit.setOnClickListener(this)

        checkNet()
        binding.autoTextState.setOnClickListener {
//            checkNetForState()
        }

        binding.autoTextCity.setOnClickListener {
            if (stateId != 0) {
//                checkNetForCity()
            } else {
                UiHandler.keyboardDown(binding.autoTextCity, this)
                Utils.showSnackBar(
                    this,
                    binding.btnSaveInfo,
                    getString(R.string.selectState),
                    getColor(R.color.snackBar)
                )
            }

        }

        binding.toolbarEditProfile.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun checkNet() {
        binding.refreshEditProfile.visibility = View.VISIBLE
        binding.pageWhiteEditProfile.visibility = View.VISIBLE
        binding.btnSaveInfo.isEnabled = false
        NetDetector.check {
            if (it) {
                requestServer()
            } else {
                Utils.showSnackBar(
                    this,
                    binding.btnSaveInfo,
                    getString(R.string.noInternet),
                    getColor(R.color.snackBar)
                )
            }
        }
    }


    private fun requestServer() {
        VolleyGetUser.getUser(
            object : VolleyInterface<User> {
                override fun onSuccess(user: User?) {
                    binding.pageWhiteEditProfile.visibility = View.GONE
                    binding.refreshEditProfile.visibility = View.GONE
                    binding.btnSaveInfo.isEnabled = true
                    user?.let {
                        setContent(it)
                    }
                    requestServerState()
                    reqGender()
                }

                override fun onFailed(error: VolleyError?) {
                    binding.pageWhiteEditProfile.visibility = View.GONE
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(
                                applicationContext,
                                binding.btnSaveInfo,
                                it,
                                getColor(R.color.snackBar)
                            )
                        }
                    }

                }

            },
            this,
            MyApplication.preferences().getInt(Constants.ID_KEY, -1),
            getUserTag
        )

    }

    private fun reqGender() {
        VolleyGetGender.getGender(object : VolleyInterface<ListGender> {
            override fun onSuccess(gender: ListGender?) {
                gender?.let {
                    genders = it
                    setGender()
                }
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.btnSaveInfo,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, getGenderTag)
    }

    private fun setContent(user: User) {
        this.user = user
//        binding.imgProfileEdit.setImageResource(R.drawable.ic_profile)
        Glide.with(this)
            .load(Constants.BASE_URL + user.profileImageUrl)
            .placeholder(R.drawable.ic_profile)
            .into(binding.imgProfileEdit)
        binding.etUsernameEditProfile.setText(user.name)
        binding.etPhoneEditProfile.setText(user.phoneNumber)
        binding.etNationalCodeEditProfile.setText(user.nationCode)
        binding.etAccountNumberEditProfile.setText(user.depositNumber)
        binding.etCardNumberEditProfile.setText(user.cardNumber)
        binding.autoTextState.setText(user.city?.state!!.name)
        stateId = user.city?.state!!.id!!
        genderId = user.gender!!

        binding.autoTextCity.setText(user.city?.name)
    }


    //state
//    private fun checkNetForState() {
//        NetDetector.check {
//            if (it) {
//                requestServerState()
//            }
//        }
//    }

    private fun requestServerState() {

        VolleyGetState.getState(object : VolleyInterface<ListState> {
            override fun onSuccess(body: ListState?) {
                body?.states?.let { setState(it) }
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.btnSaveInfo,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, getStateTag)

    }


    fun setState(state: List<State>) {
        val states: ArrayList<String> = ArrayList()
        for (i in state.indices step 1) {
            state[i].name?.let { states.add(it) }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, states
        )

        binding.autoTextState.setAdapter(adapter)
//        binding.autoTextState.postDelayed(Runnable {
////            binding.autoTextState.setText("PREM")
//            binding.autoTextState.showDropDown()
//        }, 10)


        binding.autoTextState.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
//            val item = parent.getItemAtPosition(position)
                stateId = state[id.toInt()].id!!
                UiHandler.keyboardDown(binding.autoTextState, this)
                binding.autoTextState.isFocusable = false
                binding.autoTextCity.text.clear()
                checkNetForCity()
            }

    }


    // city
    private fun checkNetForCity() {
        NetDetector.check {
            if (it) {
                requestServerCity()
            }
        }
    }

    private fun requestServerCity() {

        VolleyGetCity.getCity(object : VolleyInterface<ListCity> {
            override fun onSuccess(city: ListCity?) {
                city?.cities?.let { setCity(it) }
            }

            override fun onFailed(error: VolleyError?) {
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.btnSaveInfo,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, stateId, getCityTag)

    }


    private fun setCity(city: List<City>) {
        val states: ArrayList<String> = ArrayList()
        for (i in city.indices step 1) {
            city[i].name?.let { states.add(it) }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, states
        )

        binding.autoTextCity.setAdapter(adapter)
        binding.autoTextCity.postDelayed(Runnable {
//            binding.autoTextState.setText("PREM")
            binding.autoTextCity.showDropDown()
        }, 10)


        binding.autoTextCity.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
//            val item = parent.getItemAtPosition(position)
                cityId = city[id.toInt()].id!!
                UiHandler.keyboardDown(binding.autoTextCity, this)
                binding.autoTextCity.isFocusable = false
            }


    }

    //gender
    private fun setGender() {
        val states: ArrayList<String> = ArrayList()
        for (i in genders.indices step 1) {
            genders[i].type?.let { states.add(it) }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.select_dialog_item, states
        )

        val gender1 = genders.find { gender -> user.gender == gender.id }


        if (gender1 != null) {
            binding.autoTextGender.setText(gender1.type)
        }


        binding.autoTextGender.setAdapter(adapter)
//        binding.autoTextGender.postDelayed(Runnable {
////            binding.autoTextState.setText("PREM")
//            binding.autoTextGender.showDropDown()
//        }, 10)


        binding.autoTextGender.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                genderId = genders[id.toInt()].id!!
                UiHandler.keyboardDown(binding.autoTextGender, this)
                binding.autoTextGender.isFocusable = false
            }


    }

    private fun sendEditInfo() {

        binding.btnSaveInfo.isEnabled = false
        binding.btnLoadingProfile.visibility = View.VISIBLE
        user.id = MyApplication.preferences().getInt(Constants.ID_KEY, -1)
        user.name = binding.etUsernameEditProfile.text.toString()
        user.phoneNumber = binding.etPhoneEditProfile.text.toString()
        user.nationCode = binding.etNationalCodeEditProfile.text.toString()
//              user!!.nationCode = binding.autoTextState.
        user.cityId = cityId
        user.gender = genderId
        user.depositNumber = binding.etAccountNumberEditProfile.text.toString()
        user.cardNumber = binding.etCardNumberEditProfile.text.toString()

        NetDetector.check {
            if (it) {
                if (imageData != null) {
                    sendImageProfile()
                } else {
                    sendEditInfoRequest()
                }
            } else {
                binding.btnSaveInfo.isEnabled = true
                binding.btnLoadingProfile.visibility = View.GONE
                Utils.showSnackBar(
                    applicationContext,
                    binding.btnSaveInfo,
                    getString(R.string.noInternet),
                    getColor(R.color.snackBar)
                )
            }
        }


    }

    private fun sendEditInfoRequest() {

        VolleyPostUpdateProfile.updateProfile(object : VolleyInterface<User> {
            override fun onSuccess(body: User?) {
                body?.let { user1 ->
                    setContent(user1)
                    user1.city?.id?.let {
                        MyApplication.preferences().edit().putInt(Constants.CITY_ID,
                            it
                        ).apply()
                    }
                    user1.city?.state?.id?.let {
                        MyApplication.preferences().edit().putInt(Constants.STATE_ID,
                            it
                        ).apply()
                    }
                }
                binding.btnSaveInfo.isEnabled = true
                binding.btnLoadingProfile.visibility = View.GONE
                ConnectionModel.instance.isUpdateUser = true
                ConnectionViewModel.instance.updateUser.postValue(ConnectionModel.instance.isUpdateUser)

                Utils.showSnackBar(
                    applicationContext,
                    binding.btnSaveInfo,
                    getString(R.string.updateIsSuccessfully),
                    getColor(R.color.green)
                )
            }

            override fun onFailed(error: VolleyError?) {
                binding.btnSaveInfo.isEnabled = true
                binding.btnLoadingProfile.visibility = View.GONE
                if (error != null) {
                    error.message?.let {
                        Utils.showSnackBar(
                            applicationContext,
                            binding.btnSaveInfo,
                            it,
                            getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, this, user, getUpdateProfileTag)
    }


    private fun popupSelectType() {

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
            "MyPhoto",
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
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val path = data.data!!
            createImageData(path)
        }
    }


    private var someActivityResultLauncherForCamera = registerForActivityResult(
        StartActivityForResult()
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
            createImageData(uri)

        }
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        binding.imgProfileEdit.setImageURI(uri)
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    private fun sendImageProfile() {

        VolleySendImageEditProfile.sendImage(
            object : VolleyInterface<User> {
                override fun onSuccess(body: User?) {
                    sendEditInfoRequest()

                }

                override fun onFailed(error: VolleyError?) {

                    binding.btnSaveInfo.isEnabled = true
                    binding.btnLoadingProfile.visibility = View.GONE
                    if (error != null) {
                        error.message?.let {
                            Utils.showSnackBar(
                                applicationContext,
                                binding.btnSaveInfo,
                                it,
                                getColor(R.color.snackBar)
                            )
                        }
                    }
                }

            },
            this,
            MyApplication.preferences().getInt(Constants.ID_KEY, -1),
            imageData ?: return,
            getUploadProfileTag
        )
    }


    //onClick
    override fun onClick(p0: View?) {

        when (p0!!.id) {
            binding.pageWhiteEditProfile.id -> {}
            binding.btnSaveInfo.id -> {
                sendEditInfo()
            }
            binding.imgChangeProfile.id -> popupSelectType()
            binding.imgProfileEdit.id -> {
                Log.e("rrr", "onClick: " + user.profileImageUrl)
                Utils.openImageViewer(
                    this,
                    binding.imgProfileEdit,
                    Constants.BASE_URL + user.profileImageUrl
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getUserTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getGenderTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getStateTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getCityTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getUpdateProfileTag)
        VolleyController.INSTANCE.getRequestQueue(this).cancelAll(getUploadProfileTag)
    }

}