package com.example.mobiroller_homeproject.utils

import android.content.Context
import android.net.Uri
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.utils.Utils.Companion.getDefaultCategoryName

class CheckData {
    companion object{
        fun checkProductInfoWithImage(context: Context, product: Product, imageUri:Uri?):String{
            when {
                imageUri==null -> {
                    return context.getString(R.string.empty_space_image)
                }
                product.product_name?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_name)
                }
                product.product_price?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_price)
                }
                product.product_description_short?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_description)
                }
                product.product_category == getDefaultCategoryName(context,0) -> {
                    return context.getString(R.string.empty_space_category)
                }
                else -> {
                    return "1"
                }
            }
        }

        fun checkProductInfo(context: Context, product: Product):String{
            when {
                product.product_name?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_name)
                }
                product.product_price?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_price)
                }
                product.product_description_short?.trim().isNullOrEmpty() -> {
                    return context.getString(R.string.empty_space_description)
                }
                product.product_category == getDefaultCategoryName(context,0) -> {
                    return context.getString(R.string.empty_space_category)
                }
                else -> {
                    return "1"
                }
            }
        }
    }
}