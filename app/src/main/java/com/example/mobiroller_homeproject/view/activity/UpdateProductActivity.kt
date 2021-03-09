package com.example.mobiroller_homeproject.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.data.service.ProductCRUD
import com.example.mobiroller_homeproject.data.service.ProductCRUD.Companion.updateProduct
import com.example.mobiroller_homeproject.data.service.ProductCRUD.Companion.updateProductWithImage
import com.example.mobiroller_homeproject.utils.CheckData
import com.example.mobiroller_homeproject.utils.CheckData.Companion.checkProductInfo
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_update_product.*

class UpdateProductActivity : AppCompatActivity() {
    private lateinit var product: Product
    private val pickImageRequest = 71
    private var imageUri: Uri? = null
    private lateinit var categoryAdapter: ArrayAdapter<CharSequence>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)
        product= intent.getSerializableExtra("product") as Product

        //toolbar
        toolbarUpdate.title=resources.getString(R.string.product_update)
        toolbarUpdate.subtitle=resources.getString(R.string.product_update_info)
        setSupportActionBar(toolbarUpdate)

        //spinner
        categoryAdapter =
            ArrayAdapter.createFromResource(
                this@UpdateProductActivity,
                R.array.categories,
                android.R.layout.simple_list_item_1
            )
        spinnerUpdateCategory.adapter=categoryAdapter

        imageViewUpdate.setOnClickListener {
            pickImage()
        }

        setProductInfo()
        buttonUpdateUpdate.setOnClickListener {
            setProductNewInfo()
            val checkInfo:String = checkProductInfo(this@UpdateProductActivity, product)
            if (imageUri==null){
                if(checkInfo != "1"){
                    Snackbar.make(imageViewAdd,checkInfo,Snackbar.LENGTH_LONG).show()
                }else{
                    updateProduct(this@UpdateProductActivity,product)
                    startActivity(Intent(this@UpdateProductActivity,MainActivity::class.java))
                    this.finish()
                }
            }else{
                if(checkInfo != "1"){
                    Snackbar.make(imageViewAdd,checkInfo,Snackbar.LENGTH_LONG).show()
                }else{
                    updateProductWithImage(this@UpdateProductActivity,product,imageUri)
                    startActivity(Intent(this@UpdateProductActivity,MainActivity::class.java))
                    this.finish()
                }
            }
        }
        buttonUpdateCancel.setOnClickListener {
            startActivity(Intent(this@UpdateProductActivity, MainActivity::class.java))
            this.finish()
        }
    }

    private fun setProductInfo(){
        Glide.with(imageViewUpdate).load(product.product_img_url).into(imageViewUpdate)
        editTextUpdateProductName.setText(product.product_name)
        editTextUpdatePrice.setText(product.product_price)
        editTextUpdateDescription.setText((product.product_description))
        editTextUpdateDescriptionShort.setText(product.product_description_short)
        if (product.product_category != null) {
            val spinnerPosition: Int = categoryAdapter.getPosition((product.product_category))
            spinnerUpdateCategory.setSelection(spinnerPosition)
        }
    }

    private fun setProductNewInfo(){
        product.product_name=editTextUpdateProductName.text.toString()
        product.product_description_short=editTextUpdateDescriptionShort.text.toString()
        product.product_description=editTextUpdateDescription.text.toString()
        product.product_price=editTextUpdatePrice.text.toString()
        product.product_category=spinnerUpdateCategory.selectedItem.toString()
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageViewUpdate.setImageURI(imageUri)
            textViewUpdateImage.text=""
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this@UpdateProductActivity, MainActivity::class.java))
        this.finish()
    }
}