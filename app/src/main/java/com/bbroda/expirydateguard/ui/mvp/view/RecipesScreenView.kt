package com.bbroda.expirydateguard.ui.mvp.view

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.RecipesScreenActivity
import com.bbroda.expirydateguard.ui.adapters.RecipesRecyclerViewAdapter
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.recipeRetrofit.Recipes
import org.greenrobot.eventbus.EventBus

class RecipesScreenView(val activity: RecipesScreenActivity, val bus: EventBus) {


    private val progressBar: ProgressBar? = activity.findViewById(R.id.loading_recipes_progressbar)
    private val recyclerView: RecyclerView? = activity.findViewById(R.id.recyclerview_recipes)
    private val noRecipesTextview: TextView? = activity.findViewById(R.id.no_recipes_found_TextView)



    lateinit var adapter: RecipesRecyclerViewAdapter

    init {
        progressBar!!.visibility = View.VISIBLE
    }

    fun initRecyclerView(recipes: MutableList<Recipes>, products: List<Product>) {

        Log.d(ContentValues.TAG, "initRecyclerView: INIT RECYCLERVIEW")
        adapter = RecipesRecyclerViewAdapter(recipes, activity, products)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = adapter
        recyclerView!!.visibility = View.VISIBLE
    }


    fun showNoRecipiesTextView(){
        noRecipesTextview!!.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progressBar!!.visibility = View.GONE
    }

    fun showToast(text: String){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(text: String){
        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle("Alert")
        mBuilder.setMessage(text)
        mBuilder.setNegativeButton("OK") { dialog, which ->
            dialog.dismiss()
            activity.finish()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }


    class SomeViewActionEvent
    class OpenRecipeDetails(val recipe: Recipes)
    class AddToFavourite(val recipe: Recipes)
}
