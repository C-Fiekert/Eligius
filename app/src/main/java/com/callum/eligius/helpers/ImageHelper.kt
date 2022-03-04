package com.callum.eligius.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.callum.eligius.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a Profile Picture")
    intentLauncher.launch(chooseFile)
}