package com.example.mobiroller_homeproject.data.service

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.utils.Utils.Companion.generateQuery
import com.example.mobiroller_homeproject.utils.Utils.Companion.getDefaultCategoryName
import com.example.mobiroller_homeproject.utils.Utils.Companion.getDefaultSortingPropertyName
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProductRepository {
    companion object{
        private val productList = ArrayList<Product>()
        private val mutableLiveData = MutableLiveData<ArrayList<Product>>()

        @JvmName("getProductData")
        fun getProductData():MutableLiveData<ArrayList<Product>>{
            if(productList.size==0){
                loadProductData()
            }

            mutableLiveData.value= productList
            return mutableLiveData
        }

        @JvmName("getProductDataByParameter")
        fun getProductData(context: Context):MutableLiveData<ArrayList<Product>>{
            productList.clear()

            val sharedPreferences = context.getSharedPreferences("filter",MODE_PRIVATE)
            val category = sharedPreferences.getString("category",getDefaultCategoryName(context,0))
            val property = sharedPreferences.getString("property",getDefaultSortingPropertyName(context,0))

            val map:HashMap<String,Any> = generateQuery(context,category!!,property!!)
            if(category!=getDefaultCategoryName(context,0)){
                loadProductDataByQuery(map["query"] as Query, map["isReversed"] as Boolean,category)
            }else{
                loadProductDataByQuery(map["query"] as Query, map["isReversed"] as Boolean)
            }

            mutableLiveData.value= productList
            return mutableLiveData
        }

        private fun loadProductData(){
            val dbRef:DatabaseReference = FirebaseDatabase.getInstance().reference
            val query:Query = dbRef.child("Product")

            val dataListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dataSnapshot:DataSnapshot in snapshot.children){
                        dataSnapshot.getValue(Product::class.java)?.let {
                            it.product_id = dataSnapshot.key
                            productList.add(it)
                        }
                        mutableLiveData.postValue(productList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            query.addListenerForSingleValueEvent(dataListener)
        }

        private fun loadProductDataByQuery(query: Query,isReversed:Boolean){
            val dataListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dataSnapshot:DataSnapshot in snapshot.children){
                        dataSnapshot.getValue(Product::class.java)?.let {
                            it.product_id = dataSnapshot.key
                            productList.add(it)
                        }
                        if(isReversed){
                            productList.reverse()
                        }
                        mutableLiveData.postValue(productList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            query.addListenerForSingleValueEvent(dataListener)
        }

        private fun loadProductDataByQuery(query: Query,isReversed:Boolean,category:String){
            val dataListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dataSnapshot:DataSnapshot in snapshot.children){
                        dataSnapshot.getValue(Product::class.java)?.let {
                            it.product_id = dataSnapshot.key
                            if(it.product_category==category)
                                productList.add(it)
                        }
                        if(isReversed){
                            productList.reverse()
                        }
                        mutableLiveData.postValue(productList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            query.addListenerForSingleValueEvent(dataListener)
        }
    }
}
