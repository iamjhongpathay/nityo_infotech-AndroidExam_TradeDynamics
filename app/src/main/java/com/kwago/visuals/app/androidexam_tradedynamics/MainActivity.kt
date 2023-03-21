package com.kwago.visuals.app.androidexam_tradedynamics

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.ActivityMainBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.DatabaseHelper
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: ActivityMainBinding
    lateinit var inventory: Inventory

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayFormattedDate(calendar.timeInMillis).toString()

        var databaseHelper = DatabaseHelper(applicationContext)
        var readInventory: MutableList<Inventory> = databaseHelper.readAll()
        var adapter = InventoryAdapter(readInventory)

        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)


        binding.tvDateOfExpiration.setOnClickListener(){
            val datePicker = DatePickerDialog(
                applicationContext,
               this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            ).show()
        }

        binding.btnSave.setOnClickListener(){
            var productName = binding.etProductName.text.toString()
            var productUnit = binding.etProductUnit.text.toString()
            var price = binding.etPrice.text.toString().toInt()
            var dateOfExpiration = binding.tvDateOfExpiration.text.toString()
            var quantity = binding.etQuantity.text.toString().toInt()
            var imagePath = binding.etImagePath.text.toString()

            inventory = Inventory(0, productName, productUnit, price, dateOfExpiration, quantity, imagePath)
            var databaseHelper = DatabaseHelper(applicationContext)
            databaseHelper.add(inventory)

            adapter.inventory.add(inventory)
            adapter.notifyDataSetChanged()

            Toast.makeText(applicationContext, "New Data Saved!", Toast.LENGTH_LONG).show()

        }

        adapter.onItemClick ={
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("productName", it.productName)
            intent.putExtra("productUnit", it.productUnit)
            intent.putExtra("price", it.price)
            intent.putExtra("expirationDate", it.dateOfExpiration)
            intent.putExtra("quantity", it.quantity)
            intent.putExtra("imagePath", it.imagePath)
            startActivity(intent)
        }

        adapter.onProductDelete = { item: Inventory, position: Int ->
            AlertDialog.Builder(this).setMessage("Do you want to delete this product? \n'${item.productName}'")
                .setPositiveButton("Delete"){ dialog, item2 ->
                    var databaseHelper = DatabaseHelper(applicationContext)
                    databaseHelper.remove(item)

                    adapter.inventory.removeAt(position)
                    adapter.notifyDataSetChanged()

                    Toast.makeText(applicationContext, "Successful Deleted!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel"){dialog, item ->
                }.show()
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
    }

    private fun displayFormattedDate(timeStamp : Long) {
        binding.tvDateOfExpiration.text = dateFormat.format(timeStamp)
    }
}