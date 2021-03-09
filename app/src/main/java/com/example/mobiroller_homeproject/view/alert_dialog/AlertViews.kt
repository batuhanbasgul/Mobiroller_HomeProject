package com.example.mobiroller_homeproject.view.alert_dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.data.service.ProductCRUD
import com.example.mobiroller_homeproject.data.service.ProductCRUD.Companion.deleteProduct
import com.example.mobiroller_homeproject.utils.Utils
import com.example.mobiroller_homeproject.utils.Utils.Companion.getTrimmedDate
import com.example.mobiroller_homeproject.view.activity.MainActivity
import com.example.mobiroller_homeproject.view.activity.UpdateProductActivity
import kotlinx.android.synthetic.main.alert_dialog_detail.view.*
import kotlinx.android.synthetic.main.alert_dialog_sort.view.*


class AlertViews{
    companion object {
        fun showAlertDialogDetail(context: Context,product: Product){
            val alertDialogBuilder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.alert_dialog_detail, null)

            Glide.with(view.imageViewDetail).load(product.product_img_url).into(view.imageViewDetail)
            view.textViewDetailProductName.text = product.product_name
            view.textViewDetailCategory.text = product.product_category
            view.textViewDetailDescriptionShort.text = product.product_description_short

            if(!product.product_add_date.isNullOrEmpty())
                view.textViewDetailProductDate.text = Utils.getTrimmedDate(product.product_add_date!!)

            val tempStr = product.product_price+" "+context.resources.getString(R.string.currency)
            view.textViewDetailProductPrice.text = tempStr

            if(product.product_description.isNullOrEmpty()){
                view.textViewDetailDescription.text = context.resources.getString(R.string.no_description)
            }else{
                view.textViewDetailDescription.text = product.product_description
            }

            alertDialogBuilder.setPositiveButton(context.resources.getString(R.string.update)) { dialogInterface: DialogInterface, i: Int ->
                val intent:Intent = Intent(context, UpdateProductActivity::class.java)
                intent.putExtra("product", product)
                context.startActivity(intent)
                (context as Activity).finish()
            }

            alertDialogBuilder.setNegativeButton(context.resources.getString(R.string.delete)) { dialogInterface: DialogInterface, i: Int ->
                if(!product.product_id.isNullOrEmpty()){
                    ProductCRUD.deleteProduct(context, product.product_id!!)
                }else{
                    Toast.makeText(context, context.resources.getString(R.string.delete_failed), Toast.LENGTH_LONG).show()
                }
            }

            alertDialogBuilder.setView(view)
            alertDialogBuilder.create().show()
        }
    }
}