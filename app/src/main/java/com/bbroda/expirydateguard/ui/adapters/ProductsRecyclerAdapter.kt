package com.bbroda.expirydateguard.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.time.LocalDate

class ProductsRecyclerAdapter(val dataSet: MutableList<Product>, val activityContext: Context) :
    RecyclerView.Adapter<ProductsRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val typeTextView: TextView
        val dateTextView: TextView
        val addProductToMealButton: ImageButton
        val deleteProductButton: ImageButton
        val deleteProductFromMealButton: ImageButton
        val parentLayout: View
        val infoButton: ImageButton

        init {
            // Define click listener for the ViewHolder's View
            nameTextView = view.findViewById(R.id.product_name)
            typeTextView = view.findViewById(R.id.product_type)
            dateTextView = view.findViewById(R.id.expiry_date)
            deleteProductButton = view.findViewById(R.id.delete_product_button)
            addProductToMealButton = view.findViewById(R.id.add_product_to_meal_button)
            deleteProductFromMealButton = view.findViewById(R.id.delete_product_from_meal_button)
            parentLayout = view.findViewById(R.id.parent_layout)
            infoButton = view.findViewById(R.id.info_button_product)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if(dataSet[position].name.isNullOrEmpty()){
            viewHolder.nameTextView.text = activityContext.getString(R.string.no_name)
        }else{
            viewHolder.nameTextView.text = dataSet[position].name
        }

        viewHolder.typeTextView.text = dataSet[position].type
        viewHolder.dateTextView.text = dataSet[position].expiryDate.toString()
        val current = LocalDate.now()
        if(dataSet[position].expiryDate < current){
            viewHolder.dateTextView.text = "${viewHolder.dateTextView.text} ${activityContext.getString(R.string.expired)}"
            viewHolder.dateTextView.setTextColor(activityContext.getColor(R.color.textRed))
            viewHolder.dateTextView.setTypeface(null, Typeface.BOLD)
        }
        else if(dataSet[position].expiryDate < current.plusDays(3)){
            viewHolder.dateTextView.text = "${viewHolder.dateTextView.text} ${activityContext.getString(R.string.expiration_close)}"
            viewHolder.dateTextView.setTextColor(activityContext.getColor(R.color.textOrange))
            viewHolder.dateTextView.setTypeface(null, Typeface.BOLD)
        }

        //Setting onclicklistener for trashcan button
        viewHolder.deleteProductButton.setOnClickListener {
            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Alert")
            builder.setMessage(R.string.delete_product_button)

            builder.setPositiveButton("Usuń") { dialog, which ->
                EventBus.getDefault().post(MainMenuView.DeleteProduct(dataSet[position]))
                //dataSet.removeAt(position)
                dataSet.remove(dataSet[position])
                notifyItemRemoved(position)

                Toast.makeText(activityContext,
                    activityContext.getString(R.string.usunięto), Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Anuluj") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        // + button
        viewHolder.addProductToMealButton.setOnClickListener{
            viewHolder.addProductToMealButton.visibility = View.GONE
            viewHolder.deleteProductFromMealButton.visibility = View.VISIBLE
            //viewHolder.parentLayout.setBackgroundResource(R.drawable.recycler_tem_bg_green)
            EventBus.getDefault().post(MainMenuView.AddProductToRecipe(dataSet[position]))
        }

        // - button
        viewHolder.deleteProductFromMealButton.setOnClickListener {
            viewHolder.addProductToMealButton.visibility = View.VISIBLE
            viewHolder.deleteProductFromMealButton.visibility = View.GONE
            if(dataSet[position].expiryDate < current){
                viewHolder.parentLayout.setBackgroundResource(R.drawable.recycler_item_no_background)
            }else{
                viewHolder.parentLayout.setBackgroundResource(R.drawable.recycler_item_no_background)
            }
            EventBus.getDefault().post(MainMenuView.DeleteProductFromDish(dataSet[position]))
        }

        //info button
        viewHolder.infoButton.setOnClickListener {
            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Info")
            builder.setMessage(R.string.info_button)

            builder.setNegativeButton("Ok") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        viewHolder.parentLayout.setOnClickListener {
            //if statement prevents opening product details when user is in proccess of adding products to list for a recipes
            //if(!activityContext.getSharedPreferences("UI", Context.MODE_PRIVATE).getBoolean("Recipies_1", false)) {
                EventBus.getDefault().post(MainMenuView.OpenProductDetails(dataSet[position].uid))
                Log.d(TAG, "onBindViewHolder: ${dataSet[position].uid}")
            //}
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
