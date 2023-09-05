package com.bbroda.expirydateguard.ui.mvp.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.view.View
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
    private val noRecipesTextview: TextView? = activity.findViewById(R.id.no_recipes_found_TextView_favourite)

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
    fun refreshRecipies(){
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    fun showNoRecipiesTextView(){
        noRecipesTextview?.visibility = View.VISIBLE
    }
    class SomeViewActionEvent
    class RemoveFromFavourite(val recipe: Recipe)
}
