package com.example.mobiroller_homeproject.data.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.view.activity.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class ProductCRUD {
    companion object{
        private val dbRef:DatabaseReference = FirebaseDatabase.getInstance().getReference("Product")
        private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private lateinit var uniqueName:String

        fun addProduct(context: Context, product: Product, imageUri: Uri?){
            uniqueName = UUID.randomUUID().toString()
            val imageFile:StorageReference = storageReference.child("images/$uniqueName")

            imageFile.putFile(imageUri!!).addOnSuccessListener {
                imageFile.downloadUrl.addOnSuccessListener {
                    product.product_img_url = it.toString()
                    product.product_img_name = uniqueName
                    dbRef.push().setValue(product)
                }
            }.addOnFailureListener {
                Toast.makeText(context, context.resources.getString(R.string.add_failed), Toast.LENGTH_LONG).show()
            }.addOnCompleteListener {
                Toast.makeText(context, context.resources.getString(R.string.add_success), Toast.LENGTH_LONG).show()
            }
        }

        fun updateProduct(context: Context, newProduct: Product?){
            val map = HashMap<String, Any>()
            newProduct?.let {
                map["product_name"]=newProduct.product_name!!
                map["product_price"]=newProduct.product_price!!
                map["product_category"]=newProduct.product_category!!
                map["product_description"]=newProduct.product_description!!
                map["product_description_short"]=newProduct.product_description_short!!
            }

            if(newProduct!=null){
                dbRef.child(newProduct.product_id.toString()).updateChildren(map).addOnCompleteListener {
                    Toast.makeText(context,context.resources.getString(R.string.update_success),Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context,context.resources.getString(R.string.update_failed),Toast.LENGTH_LONG).show()
                }
            }
        }

        fun updateProductWithImage(context: Context, newProduct: Product?, imageUri: Uri?){
            val map = HashMap<String, Any>()
            uniqueName = UUID.randomUUID().toString()
            val imageFile:StorageReference = storageReference.child("images/$uniqueName")
            imageFile.putFile(imageUri!!).addOnSuccessListener {
                imageFile.downloadUrl.addOnSuccessListener {
                    map["product_img_name"]= uniqueName
                    map["product_img_url"]=it.toString()
                }.addOnCompleteListener{
                    newProduct?.let {
                        map["product_name"]=newProduct.product_name!!
                        map["product_price"]=newProduct.product_price!!
                        map["product_category"]=newProduct.product_category!!
                        map["product_description"]=newProduct.product_description!!
                        map["product_description_short"]=newProduct.product_description_short!!

                        dbRef.child(newProduct.product_id.toString()).updateChildren(map).addOnCompleteListener {
                            Toast.makeText(context,context.resources.getString(R.string.update_success),Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(context,context.resources.getString(R.string.update_failed),Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        fun deleteProduct(context: Context, productKey: String){
            dbRef.child(productKey).removeValue().addOnCompleteListener {
                Toast.makeText(context, context.resources.getString(R.string.delete_success), Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, context.resources.getString(R.string.delete_failed), Toast.LENGTH_LONG).show()
            }
        }
    }
}