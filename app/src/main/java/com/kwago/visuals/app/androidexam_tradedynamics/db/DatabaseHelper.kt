package com.kwago.visuals.app.androidexam_tradedynamics.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, "androidexam_tradedynamic.db", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable: String = "CREATE TABLE Inventory (id integer primary key autoincrement, productName varchar(100), productUnit varchar(100), price decimal(-4.535,2), dateOfExpiration varchar(100), stock int, imagePath BLOB)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun add(inventory: Inventory){
        var db = this.writableDatabase
        var cv = ContentValues()

        cv.put("productName", inventory.productName)
        cv.put("productUnit", inventory.productUnit)
        cv.put("price", inventory.price)
        cv.put("dateOfExpiration", inventory.dateOfExpiration)
        cv.put("stock", inventory.stock)
        cv.put("imagePath", inventory.imagePath)

        db.insert("INVENTORY", null, cv)
    }

    fun readAll():MutableList<Inventory>{
        var returnList = ArrayList<Inventory>()
        var queryString: String = "SELECT * FROM INVENTORY"
        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery(queryString, null)
        if(cursor.moveToFirst()){
            do{
                var id = cursor.getInt(0)
                var productName = cursor.getString(1)
                var productUnit = cursor.getString(2)
                var price = cursor.getDouble(3)
                var dateOfExpiration = cursor.getString(4)
                var quantity = cursor.getInt(5)
                var imagePath = cursor.getBlob(6)

                var newInventory: Inventory = Inventory(id, productName, productUnit, price, dateOfExpiration, quantity, imagePath)
                returnList.add(newInventory)
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return  returnList
    }

    fun remove(inventory: Inventory){
        var db = this.writableDatabase
        var queryString = "DELETE FROM INVENTORY WHERE id = ${inventory.id}"
        var cursor = db.rawQuery(queryString, null)
        cursor.moveToFirst()
    }

    fun update(inventory: Inventory): Int{
        var db = writableDatabase
        var cv = ContentValues()

        cv.put("productName", inventory.productName)
        cv.put("productUnit", inventory.productUnit)
        cv.put("price", inventory.price)
        cv.put("dateOfExpiration", inventory.dateOfExpiration)
        cv.put("stock", inventory.stock)
        cv.put("imagePath", inventory.imagePath)

        val success = db.update("INVENTORY", cv, "id=" + inventory.id, null)
        db.close()
//        return Integer.parseInt("$success") != -1
        return  success
    }

}