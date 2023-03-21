package com.kwago.visuals.app.androidexam_tradedynamics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kwago.visuals.app.androidexam_tradedynamics.databinding.RowProductsBinding
import com.kwago.visuals.app.androidexam_tradedynamics.db.Inventory

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
            tvProductName.text = inventory[position].productName
            tvProductUnit.text = inventory[position].productUnit
            tvPrice.text = inventory[position].price.toString()
            tvRowDateOfExpiration.text = inventory[position].dateOfExpiration.toString()
            tvStock.text = inventory[position].quantity.toString()
            var inventoryCost = inventory[position].quantity * inventory[position].price
            tvInventoryCost.text = inventoryCost.toString()

            btnDeleteIcon.setOnClickListener(){
                onProductDelete?.invoke(inventory[position], position)
            }

            btnEditIcon.setOnClickListener(){
                onItemClick?.invoke(inventory[position])
            }
        }
    }
}