package com.example.mobiroller_homeproject.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.data.service.ProductCRUD
import com.example.mobiroller_homeproject.utils.CheckData
import com.example.mobiroller_homeproject.utils.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_product.*
import java.util.*

class AddProductActivity : AppCompatActivity() {
    private lateinit var product:Product
    private val pickImageRequest = 71
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        //toolbar
        toolbarAdd.title=resources.getString(R.string.product_add)
        toolbarAdd.subtitle=resources.getString(R.string.product_enter_info)
        setSupportActionBar(toolbarAdd)

        //spinner
        val categoryAdapter: ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(this@AddProductActivity,
                        R.array.categories,
                        android.R.layout.simple_list_item_1)
        spinnerAddCategory.adapter=categoryAdapter

        //Pick image from device
        imageViewAdd.setOnClickListener {
            pickImage()
        }

        //Upload Data
        buttonAddAdd.setOnClickListener{
            product= Product("",
                    "",
                    "",
                    editTextAddProductName.text.toString(),
                    editTextAddPrice.text.toString(),
                    editTextAddDescriptionShort.text.toString(),
                    editTextAddDescription.text.toString(),
                    spinnerAddCategory.selectedItem.toString(),
                    Utils.getCurrentDate())

            val checkInfo:String = CheckData.checkProductInfoWithImage(this@AddProductActivity, product, imageUri)
            if(checkInfo != "1"){
                Snackbar.make(imageViewAdd,checkInfo,Snackbar.LENGTH_LONG).show()
            }else{
                ProductCRUD.addProduct(this@AddProductActivity,product,imageUri)
                startActivity(Intent(this@AddProductActivity,MainActivity::class.java))
                this.finish()
            }
        }

        buttonAddCancel.setOnClickListener {
            startActivity(Intent(this@AddProductActivity, MainActivity::class.java))
            this.finish()
        }

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
            imageViewAdd.setImageURI(imageUri)
            textViewAddImage.text=""
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this@AddProductActivity, MainActivity::class.java))
        this.finish()
    }
}