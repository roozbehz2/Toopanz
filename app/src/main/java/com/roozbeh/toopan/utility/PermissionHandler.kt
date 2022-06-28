package com.roozbeh.toopan.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import com.roozbeh.toopan.utility.PermissionHandler.PermissionGranted
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.roozbeh.toopan.utility.PermissionHandler
import com.karumi.dexter.PermissionToken
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.listener.PermissionRequest
import com.roozbeh.toopan.R

class PermissionHandler(private val context: Context, private val activity: Activity) {
    private var permissionGranted: PermissionGranted? = null
    fun setPermissionGranted(permissionGranted: PermissionGranted?) {
        this.permissionGranted = permissionGranted
    }

    fun requestPermission(vararg strings: String?) {
        Dexter.withContext(context)
            .withPermissions(*strings)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        permissionGranted!!.onPermissionGranted()
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        showPermissionSettingsDialog(context, activity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    interface PermissionGranted {
        fun onPermissionGranted()
    }

        fun isStoragePermissionGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }

        fun isLocationPermissionGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun isRecordAudioGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun isPhonePermissionGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun isCameraPermissionGranted(context: Context?): Boolean {
            return ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun showPermissionSettingsDialog(context: Context, activity: Activity) {

              MaterialAlertDialogBuilder(context)
                .setTitle(context.resources.getString(R.string.permissionTittleDialog))
//                .setMessage(context.resources.getString(R.string.setting))
                .setNeutralButton(context.resources.getString(R.string.cancel)) { dialog, which ->

            // Respond to neutral button press
        }
//        .setNegativeButton(context.resources.getString(R.string.decline)) { dialog, which ->
//            // Respond to negative button press
//        }
        .setPositiveButton(context.resources.getString(R.string.setting)) { dialog, which ->
            // Respond to positive button press
            openPermissionSettings(context, activity);
        }
        .show()


            /*Dialog dialog = new MainDialog(context);
        dialog.setTitle(context.getString(R.string.dialog_permission_title));
        dialog.setDescription(context.getString(R.string.dialog_permission_message));
        dialog.setPositiveButtonText(context.getString(R.string.go_to_settings));
        dialog.setNegativeButtonText(context.getString(android.R.string.cancel));
        dialog.setOnClickListener(new Dialog.OnClickListener() {
            @Override
            public void onPositiveClick() {
                dialog.dismiss();
                openPermissionSettings(context, activity);
            }

            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }

            @Override
            public void onCloseClick() {

            }
        });
        dialog.show();*/
        }

        private fun openPermissionSettings(context: Context, activity: Activity) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            context.startActivity(intent)
        }

}