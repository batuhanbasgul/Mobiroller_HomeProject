package com.example.mobiroller_homeproject.utils

import android.content.Context
import android.widget.ArrayAdapter
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import java.text.FieldPosition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Utils {
    companion object{
        fun getCurrentDate():String{
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val current: LocalDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
                val formatted = current.format(formatter)
                formatted.toString()
            } else {
                val currentCalendar = Calendar.getInstance()
                val year = currentCalendar[Calendar.YEAR]
                val month = currentCalendar[Calendar.MONTH]
                val day = currentCalendar[Calendar.DAY_OF_MONTH]
                val pm = currentCalendar[Calendar.PM]
                val minute = currentCalendar[Calendar.MINUTE]
                val second = currentCalendar[Calendar.SECOND]
                val ms = currentCalendar[Calendar.MILLISECOND]
                ("$day-$month-$year $pm:$minute:$second.$ms")
            }
        }

        fun getDefaultCategoryName(context: Context, position:Int):String{
            val categoryAdapter: ArrayAdapter<CharSequence> =
                    ArrayAdapter.createFromResource(context,
                            R.array.categories,
                            android.R.layout.list_content)
            return categoryAdapter.getItem(position).toString()
        }

        fun getDefaultSortingPropertyName(context: Context,position: Int):String{
            val sortingPropertyAdapter: ArrayAdapter<CharSequence> =
                    ArrayAdapter.createFromResource(context,
                            R.array.sorting_property,
                            android.R.layout.simple_list_item_1)
            return sortingPropertyAdapter.getItem(position).toString()
        }

        fun getTrimmedDate(date:String):String{
            var splitDate:ArrayList<String> = date.trim().split(' ') as ArrayList<String>
            return (splitDate[0])
        }

        fun generateQuery(context: Context,category:String,property:String) : HashMap<String, Any>{
            val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Product")
            val defCategory:String = getDefaultCategoryName(context,0)
            val defProperty:String = getDefaultSortingPropertyName(context,0)
            val isReversed:Boolean

            val query: Query

            if(category == defCategory && property==defProperty){
                query = dbRef
                isReversed=false
            }else if (category != defCategory && property==defProperty){
                query = dbRef.orderByChild("product_category").equalTo(category)
                isReversed=false
            }else{
                when (property) {
                    getDefaultSortingPropertyName(context,1) -> {                           //İsme göre alfabetik sırala
                        query = dbRef.orderByChild("product_name")
                        isReversed=false
                    }
                    getDefaultSortingPropertyName(context,2) -> {                           //İsme göre tersten sırala
                        query = dbRef.orderByChild("product_name")
                        isReversed=true
                    }
                    getDefaultSortingPropertyName(context,3) -> {                           //Tarihe göre sırala(Önce Yeni)
                        query = dbRef.orderByChild("product_add_date")
                        isReversed=false
                    }
                    getDefaultSortingPropertyName(context,4) -> {                           //Tarihe göre sırala(Önce Eski)
                        query = dbRef.orderByChild("product_add_date")
                        isReversed=true
                    }
                    getDefaultSortingPropertyName(context,5) -> {                           //Fiyata göre sırala(Artan)
                        query = dbRef.orderByChild("product_price")
                        isReversed=false
                    }
                    getDefaultSortingPropertyName(context,6) -> {                           //Fiyata göre sırala(Azalan)
                        query = dbRef.orderByChild("product_price")
                        isReversed=true
                    }
                    else -> {
                        query = dbRef
                        isReversed=false
                    }
                }
            }

            val result = HashMap<String, Any>()
            result["query"]=query
            result["isReversed"]=isReversed
            return result
        }
    }
}