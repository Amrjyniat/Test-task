package com.example.testtask.utils

import android.app.Activity
import android.app.AlertDialog
import com.example.testtask.R

fun Activity.showErrorDialog(errMsg: String) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.error))
    builder.setMessage(errMsg)
    builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
}