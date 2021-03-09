package com.example.mobiroller_homeproject.view_model

import android.content.Context
import androidx.lifecycle.*
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.data.service.ProductRepository

class ProductViewModel : ViewModel() {
    private var mutableData = MutableLiveData<ArrayList<Product>>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun init(){
        mutableData = ProductRepository.getProductData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun loadByParameter(context: Context){
        mutableData = ProductRepository.getProductData(context)
    }

    fun getLiveData() : LiveData<ArrayList<Product>> = mutableData
}