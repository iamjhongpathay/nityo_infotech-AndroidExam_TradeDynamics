package com.kwago.visuals.app.androidexam_tradedynamics.db

data class Inventory(
    val id: Int,
    val productName: String,
    val productUnit: String,
    val price: Int,
    val dateOfExpiration: String,
    val quantity: Int,
    val imagePath: String) {
}