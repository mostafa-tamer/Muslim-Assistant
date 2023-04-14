package com.example.barcodeReader.utils

data class ErrorMessage(
    var errorExist: Boolean = false,
    var title: String = "",
    var message: String = ""
)