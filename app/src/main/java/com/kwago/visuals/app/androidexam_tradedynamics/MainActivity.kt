package com.kwago.visuals.app.androidexam_tradedynamics

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.ActivityMainBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.DatabaseHelper
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAddProductActivity()
        displayProducts()

        //swipe down to refresh recyclerview
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false

            displayProducts()
        }
    }

    private fun displayProducts(){
        var databaseHelper = DatabaseHelper(applicationContext)
        var readInventory: MutableList<Inventory> = databaseHelper.readAll()
        var adapter = InventoryAdapter(readInventory)

        if(readInventory.isEmpty()){
            binding.imageView2.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
            binding.recyclerViewProducts.visibility = View.GONE
        }else{
            binding.imageView2.visibility = View.GONE
            binding.textView2.visibility = View.GONE
            binding.recyclerViewProducts.visibility = View.VISIBLE

            binding.recyclerViewProducts.adapter = adapter
            binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        }


        adapter.onItemClick ={
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("productName", it.productName)
            intent.putExtra("productUnit", it.productUnit)
            intent.putExtra("price", it.price)
            intent.putExtra("expirationDate", it.dateOfExpiration)
            intent.putExtra("quantity", it.stock)
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
        adapter.notifyDataSetChanged()
    }

    private fun openAddProductActivity(){
        binding.fabAdd.setOnClickListener(){
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}