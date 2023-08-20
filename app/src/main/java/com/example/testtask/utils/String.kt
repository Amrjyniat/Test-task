package com.example.testtask.utils

import android.util.Patterns

fun String.isValidAndNotEmptyEmail() =
    if (isNotEmpty()) Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches() else false