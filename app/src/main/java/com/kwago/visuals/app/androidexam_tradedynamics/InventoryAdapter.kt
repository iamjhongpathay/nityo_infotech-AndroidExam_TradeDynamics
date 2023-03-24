package com.kwago.visuals.app.androidexam_tradedynamics

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.RowProductsBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory
import java.io.ByteArrayInputStream


class InventoryAdapter(var inventory: MutableList<Inventory>): RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    var onProductDelete : ((Inventory, Int) -> Unit) ? = null
    var onItemClick : ((Inventory) -> Unit)? = null

    inner class InventoryViewHolder(var binding: RowProductsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowProductsBinding.inflate(layoutInflater, parent, false)
        return InventoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return inventory.size
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.binding.apply {
            tvRowProductName.text = inventory[position].productName
            tvRowProductUnit.text = inventory[position].productUnit
            tvRowPrice.text = inventory[position].price.toString()
            tvRowDateOfExpiration.text = inventory[position].dateOfExpiration.toString()
            tvRowStock.text = inventory[position].stock.toString()

            var inventoryCost = inventory[position].stock * inventory[position].price
            tvRowInventoryCost.text = inventoryCost.toString()

            val inputStream = ByteArrayInputStream(inventory[position].imagePath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            ivRowProductImage.setImageBitmap(bitmap)

            btnDeleteIcon.setOnClickListener(){
                onProductDelete?.invoke(inventory[position], position)
            }

            btnEditIcon.setOnClickListener(){
                onItemClick?.invoke(inventory[position])
            }
        }
    }
}