package com.kwago.visuals.app.androidexam_tradedynamics

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.ActivityEditProductBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.DatabaseHelper
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory
import java.text.SimpleDateFormat
import java.util.*

class EditProductActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener {
    lateinit var binding : ActivityEditProductBinding

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvEditDateOfExpiration.setOnClickListener(){
            val datePicker = DatePickerDialog(
                applicationContext,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            ).show()
        }

        displayFormattedDate(calendar.timeInMillis).toString()

        var id: Int = intent.getIntExtra("id", 0)
        var productName: String? = intent.getStringExtra("productName")
        var productUnit: String? = intent.getStringExtra("productUnit")
        var price: Int = intent.getIntExtra("price", 0)
        var expirationDate: String? = intent.getStringExtra("expirationDate")
        var quantity: Int = intent.getIntExtra("quantity", 0)
        var imagePath: String? = intent.getStringExtra("imagePath")

        binding.etEditProductName.setText(productName)
        binding.etEditProductUnit.setText(productUnit)
        binding.etEditPrice.setText(price.toString())
        binding.tvEditDateOfExpiration.text = expirationDate
        binding.etEditQuantity.setText(quantity.toString())
        binding.etEditImagePath.setText(imagePath)

        binding.btnUpdate.setOnClickListener(){

            var databaseHelper = DatabaseHelper(applicationContext)

            var uProductName = binding.etEditProductName.text.toString()
            var uProductUnit = binding.etEditProductUnit.text.toString()
            var uPrice = binding.etEditPrice.text.toString().toInt()
            var uExpirationDate = binding.tvEditDateOfExpiration.text.toString()
            var uQuantity = binding.etEditQuantity.text.toString().toInt()
            var uImagePath = binding.etEditImagePath.text.toString()
            val inventory: Inventory = Inventory(id, uProductName, uProductUnit, uPrice, uExpirationDate, uQuantity, uImagePath)

            databaseHelper.update(inventory)

            Toast.makeText(applicationContext, "Successfully Updated!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun displayFormattedDate(timeStamp : Long) {
        binding.tvEditDateOfExpiration.text = dateFormat.format(timeStamp)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
    }
}