package com.example.mobiroller_homeproject.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.mobiroller_homeproject.R
import com.example.mobiroller_homeproject.data.model.Product
import com.example.mobiroller_homeproject.data.service.ProductCRUD
import com.example.mobiroller_homeproject.utils.Utils
import com.example.mobiroller_homeproject.utils.Utils.Companion.getDefaultCategoryName
import com.example.mobiroller_homeproject.utils.Utils.Companion.getDefaultSortingPropertyName
import com.example.mobiroller_homeproject.view.adapter.ProductAdapter
import com.example.mobiroller_homeproject.view.alert_dialog.AlertViews
import com.example.mobiroller_homeproject.view_model.ProductViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_detail.view.*
import kotlinx.android.synthetic.main.alert_dialog_sort.view.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{
    private lateinit var searchView:SearchView
    private var adapter: ProductAdapter? = null
    private lateinit var productList:ArrayList<Product>
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout.isRefreshing = true

        //Toolbar init
        toolbarMain.title=resources.getString(R.string.app_name)
        toolbarMain.subtitle=resources.getString(R.string.app_sub_name)
        setSupportActionBar(toolbarMain)

        //set filter to default
        val sharedPreferences = getSharedPreferences("filter",MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFiltered",false)
        editor.putString("category", getDefaultCategoryName(this@MainActivity,0))
        editor.putString("property", getDefaultSortingPropertyName(this@MainActivity,0))
        editor.commit()

        //Add Product
        fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddProductActivity::class.java))
            this.finish()
        }
    }

    override fun onStart() {
        setInfo()
        //Set Data
        val sharedPreferences = getSharedPreferences("filter",MODE_PRIVATE)
        if(sharedPreferences.getBoolean("isFiltered",false)){
            getProductByParameter()
        }else{
            setProducts()
        }

        //Refreshing by swipe
        swipeRefreshLayout.setOnRefreshListener{
            getProductByParameter()
        }

        super.onStart()
    }

    private fun setInfo(){
        val sharedPreferences = getSharedPreferences("filter",MODE_PRIVATE)
        //Set category and property info
        val categoryInfo: String = " > "+
                sharedPreferences.getString("category", getDefaultCategoryName(this@MainActivity,0))
        val propertyInfo: String = " > "+
                sharedPreferences.getString("property", getDefaultSortingPropertyName(this@MainActivity,0)
                )
        textViewMainCateogry.text = categoryInfo
        textViewMainProperty.text = propertyInfo
    }

    private fun setProducts(){
        val productViewModel: ProductViewModel = ViewModelProviders.of(this@MainActivity).get(ProductViewModel::class.java)
        productViewModel.init()
        productViewModel.getLiveData().observe(this@MainActivity, Observer { dataList ->
            adapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
            setInfo()
        })
        productViewModel.getLiveData().value?.let { productList=it }
        adapter = ProductAdapter(this@MainActivity, productList)
        setRecyclerView()
    }

    private fun getProductByParameter(){
        val productViewModel: ProductViewModel = ViewModelProviders.of(this@MainActivity).get(ProductViewModel::class.java)
        productViewModel.loadByParameter(this@MainActivity)
        productViewModel.getLiveData().observe(this@MainActivity, Observer { dataList ->
            adapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
            setInfo()
        })
        productViewModel.getLiveData().value?.let { productList=it }
        adapter = ProductAdapter(this@MainActivity, productList)
        setRecyclerView()
    }

    private fun setRecyclerView(){
        recyclerViewMain.setHasFixedSize(true)
        recyclerViewMain.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewMain.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        val actionSearch:MenuItem = menu.findItem(R.id.action_search)
        searchView = actionSearch.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.product_search)
        searchView.setOnQueryTextListener(this@MainActivity)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sort -> {
                showAlertDialogCategory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean = true
    override fun onQueryTextChange(newText: String?): Boolean {
        var tempList:ArrayList<Product> = ArrayList()
        if(newText.isNullOrEmpty()){
            tempList=productList
        }else{
            for(p:Product in productList){
                if(p.product_name?.contains(newText) == true){
                    tempList.add(p)
                }
            }
        }
        adapter = ProductAdapter(this@MainActivity, tempList)
        adapter?.notifyDataSetChanged()
        setRecyclerView()
        return true
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.onActionViewCollapsed()
        } else {
            super.onBackPressed()
        }
    }

    private fun showAlertDialogCategory() {
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        val view: View = LayoutInflater.from(this@MainActivity).inflate(R.layout.alert_dialog_sort, null);
        alertDialogBuilder.setTitle(resources.getString(R.string.alert_sort_title))

        val categoryAdapter: ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(this@MainActivity,
                        R.array.categories,
                        android.R.layout.simple_list_item_1)
        val sortingPropertyAdapter: ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(this@MainActivity,
                        R.array.sorting_property,
                        android.R.layout.simple_list_item_1)
        view.spinnerSortCategory.adapter = categoryAdapter
        view.spinnerSortProperty.adapter = sortingPropertyAdapter

        alertDialogBuilder.setPositiveButton(resources.getString(R.string.alert_sort_apply)) { dialogInterface: DialogInterface, i: Int ->
            val category:String = view.spinnerSortCategory.selectedItem.toString()
            val sortingProperty:String = view.spinnerSortProperty.selectedItem.toString()

            val sharedPreferences = getSharedPreferences("filter", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("category", category)
            editor.putString("property", sortingProperty)
            editor.putBoolean("isFiltered", true)
            editor.commit()
            getProductByParameter()
        }

        alertDialogBuilder.setNegativeButton(resources.getString(R.string.alert_sort_cancel)) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        alertDialogBuilder.setView(view)
        alertDialogBuilder.create().show()
    }
}