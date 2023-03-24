package com.kwago.visuals.app.androidexam_tradedynamics.db

import android.content.ContentValues




data class Inventory(
    val id: Int,
    val productName: String,
    val productUnit: String,
    val price: Double,
    val dateOfExpiration: String,
    val stock: Int,
    val imagePath: ByteArray

) {
}
