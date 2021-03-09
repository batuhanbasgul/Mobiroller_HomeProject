package com.example.mobiroller_homeproject.data.model

import java.io.Serializable

data class Product(
        var product_id:String? = "",
        var product_img_url:String? = "",
        var product_img_name:String? = "",
        var product_name:String? = "",
        var product_price:String? = "",
        var product_description_short:String? = "",
        var product_description:String? = "",
        var product_category:String? = "",
        var product_add_date:String? = ""
) : Serializable