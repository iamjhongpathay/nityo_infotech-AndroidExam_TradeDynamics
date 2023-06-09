package com.kwago.visuals.app.androidexam_tradedynamics

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.ActivityEditProductBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.DatabaseHelper
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditProductActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener {

    lateinit var binding : ActivityEditProductBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private lateinit var inventory: Inventory
    private var selectedImage: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openDatePicker()
        displayExpiryDate(calendar.timeInMillis).toString()

        binding.btnCancelEdit.setOnClickListener(){finish()}
        binding.btnEditAddPhoto.setOnClickListener(){showDataSourceChoicesDialog()}

        var id: Int = intent.getIntExtra("id", 0)
        var productName: String? = intent.getStringExtra("productName")
        var productUnit: String? = intent.getStringExtra("productUnit")
        var price: Double = intent.getDoubleExtra("price", 0.0)
        var expirationDate: String? = intent.getStringExtra("expirationDate")
        var quantity: Int = intent.getIntExtra("quantity", 0)
        var imagePath: ByteArray? = intent.getByteArrayExtra("imagePath")

        val inputStream = ByteArrayInputStream(imagePath)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        binding.etEditProductName.setText(productName)
        binding.etEditProductUnit.setText(productUnit)
        binding.etEditPrice.setText(price.toString())
        binding.tvEditExpiryDate.text = expirationDate
        binding.etEditStock.setText(quantity.toString())
        binding.ivEditPhotoPlaceHolder.setImageBitmap(bitmap)

        binding.btnUpdate.setOnClickListener(){
            var databaseHelper = DatabaseHelper(applicationContext)

            var uProductName = binding.etEditProductName.text.toString()
            var uProductUnit = binding.etEditProductUnit.text.toString()
            var uPrice = binding.etEditPrice.text.toString().toDouble()
            var uExpirationDate = binding.tvEditExpiryDate.text.toString()
            var uStock = binding.etEditStock.text.toString().toInt()

            val resized = Bitmap.createScaledBitmap(
                selectedImage,
                100,
                100,
                true
            )
            val stream = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            inventory = Inventory(id, uProductName, uProductUnit, uPrice, uExpirationDate, uStock, byteArray)

            databaseHelper.update(inventory)

            Toast.makeText(applicationContext, "Successfully Updated!", Toast.LENGTH_LONG).show()
        }
    }

    private fun openDatePicker(){
        binding.tvEditExpiryDate.setOnClickListener(){
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun displayExpiryDate(timeStamp : Long) {
        binding.tvEditExpiryDate.text = dateFormat.format(timeStamp)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        displayExpiryDate(calendar.timeInMillis).toString()
    }

    private fun showDataSourceChoicesDialog() {
        AlertDialog.Builder(this).setMessage("Choose data source of image.")
            .setPositiveButton("Camera"){ dialog, item ->
                showCamera()
            }
            .setNegativeButton("Gallery"){ dialog, item ->
                showGallery()
            }.show()
    }

    private fun showGallery() {
        Dexter.withContext(applicationContext).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                //if the user granted the permission to open gallery app...
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //...the gallery app will launch
                galleryLauncher.launch(galleryIntent)
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(applicationContext, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
                goToSettingsDialog()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        }).onSameThread().check()
    }

    //show the camera app also the permission to use the camera
    private fun showCamera() {
        Dexter.withContext(applicationContext).withPermission(
            android.Manifest.permission.CAMERA
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                //if the user granted the permission to use camera app...
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //...the camera app will launched
                cameraLauncher.launch(cameraIntent)
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(applicationContext, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
                goToSettingsDialog()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        }).onSameThread().check()
    }

    private fun goToSettingsDialog() {
        AlertDialog.Builder(this).setMessage("it seems your permission has been denied. Go to settings to enable permission.")
            .setPositiveButton("Go to Settings"){ dialog, item ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                var uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel"){ dialog, item ->
                dialog.dismiss()
            }.show()
    }

    //the camera app will launched
    val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.extras.let{
                selectedImage = result.data?.extras?.get("data") as Bitmap
                binding.ivEditPhotoPlaceHolder.setImageBitmap(selectedImage)
            }
        }
    }

    //the gallery app will launched
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.let{
                val galleryImage = result.data?.data
                selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, galleryImage)
                binding.ivEditPhotoPlaceHolder.setImageBitmap(selectedImage)
            }
        }
    }
}