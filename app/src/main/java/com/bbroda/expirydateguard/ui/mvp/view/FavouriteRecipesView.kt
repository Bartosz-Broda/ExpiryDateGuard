package com.bbroda.expirydateguard.ui.mvp.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.FavouriteRecipesActivity
import com.bbroda.expirydateguard.ui.adapters.FavouriteRecipesRecyclerViewAdapter
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.recipedatabase.Recipe
import org.greenrobot.eventbus.EventBus

class FavouriteRecipesView(val activity: FavouriteRecipesActivity, bus: EventBus) {
    private val recyclerView: RecyclerView? = activity.findViewById(R.id.recyclerview_recipes_favourite)
    private val noRecipesTextview: TextView = activity.findViewById(R.id.no_recipes_found_TextView_favourite)
    private val noRecipesImage: ImageView = activity.findViewById(R.id.no_favourite_recipes_icon)

    lateinit var adapter: FavouriteRecipesRecyclerViewAdapter
    init{
    }
    fun initRecyclerView(recipes: MutableList<Recipe>, products: List<Product>) {
        Log.d(ContentValues.TAG, "initRecyclerView: INIT RECYCLERVIEW")
        adapter = FavouriteRecipesRecyclerViewAdapter(recipes, activity, products)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshRecipies(position: Int){
        recyclerView?.adapter?.notifyDataSetChanged()

        val itemsCount = recyclerView?.adapter?.itemCount
        if(itemsCount == 0){
            noRecipesImage.visibility = View.VISIBLE
            noRecipesTextview.visibility = View.VISIBLE
        }
    }

    fun showNoRecipesInfo(){
        noRecipesTextview.visibility = View.VISIBLE
        noRecipesImage.visibility = View.VISIBLE
    }

    class SomeViewActionEvent
    class RemoveFromFavourite(val recipe: Recipe, val position: Int)
}
