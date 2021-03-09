package com.example.mobiroller_homeproject.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.utils.Utils.Companion.getTrimmedDate
import com.example.mobiroller_homeproject.view.activity.MainActivity
import com.example.mobiroller_homeproject.view.alert_dialog.AlertViews.Companion.showAlertDialogDetail
import kotlinx.android.synthetic.main.card_design.view.*

class ProductAdapter(
    private val context: Context,
    private val productList:ArrayList<Product>
) : RecyclerView.Adapter<ProductAdapter.CardViewHolder>() {

    private lateinit var price:String

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUI(product: Product) {
            Glide.with(itemView.imageViewCardView).load(product.product_img_url).into(itemView.imageViewCardView)
            itemView.textViewCardViewProductName.text = product.product_name
            price=product.product_price + " " + context.resources.getString(R.string.currency)
            itemView.textViewCardViewProductPrice.text = price
            itemView.textViewCardViewDate.text = product.product_add_date?.let { getTrimmedDate(it) }

            itemView.setOnClickListener{
                showAlertDialogDetail(context,product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.card_design,null)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindUI(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}