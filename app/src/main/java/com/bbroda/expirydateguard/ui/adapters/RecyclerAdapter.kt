package com.bbroda.expirydateguard.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.Product
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.time.LocalDate

class RecyclerAdapter(private val dataSet: MutableList<Product>, val activityContext: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val typeTextView: TextView
        val dateTextView: TextView
        val addProductToMealButton: ImageButton
        val deleteProductButton: ImageButton
        val deleteProductFromMealButton: ImageButton
        val parentLayout: View

        init {
            // Define click listener for the ViewHolder's View
            nameTextView = view.findViewById(R.id.product_name)
            typeTextView = view.findViewById(R.id.product_type)
            dateTextView = view.findViewById(R.id.expiry_date)
            deleteProductButton = view.findViewById(R.id.delete_product_button)
            addProductToMealButton = view.findViewById(R.id.add_product_to_meal_button)
            deleteProductFromMealButton = view.findViewById(R.id.delete_product_from_meal_button)
            parentLayout = view.findViewById(R.id.parent_layout)
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
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.nameTextView.text = dataSet[position].name
        viewHolder.typeTextView.text = dataSet[position].type
        viewHolder.dateTextView.text = dataSet[position].expiryDate.toString()
        val current = LocalDate.now()
        if(dataSet[position].expiryDate < current){
            viewHolder.parentLayout.setBackgroundResource(R.drawable.recycler_item_bg_red)
        }

        //Setting onclicklistener for button
        viewHolder.deleteProductButton.setOnClickListener {

            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Alert")
            builder.setMessage("Czy na pewno chcesz usunąć ten produkt ze swojej lodówki?")

            builder.setPositiveButton("Usuń") { dialog, which ->

                EventBus.getDefault().post(MainMenuView.DeleteProduct(dataSet[position]))
                //dataSet.removeAt(position)
                dataSet.remove(dataSet[position])
                notifyItemRemoved(position)

                Toast.makeText(activityContext,
                    "Usunięto", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Anuluj") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }

        viewHolder.addProductToMealButton.setOnClickListener{
            viewHolder.addProductToMealButton.visibility = View.GONE
            viewHolder.deleteProductFromMealButton.visibility = View.VISIBLE
            viewHolder.itemView.setBackgroundColor(Color.WHITE)
            EventBus.getDefault().post(MainMenuView.AddProductToDish())
        }

        viewHolder.deleteProductFromMealButton.setOnClickListener {
            viewHolder.addProductToMealButton.visibility = View.VISIBLE
            viewHolder.deleteProductFromMealButton.visibility = View.GONE
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY)
            EventBus.getDefault().post(MainMenuView.DeleteProductFromDish())
        }

        viewHolder.parentLayout.setOnClickListener { 
            EventBus.getDefault().post(MainMenuView.OpenProductDetails(dataSet[position].uid))
            Log.d(TAG, "onBindViewHolder: ${dataSet[position].uid}")
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun showPlusButton(){

    }

}
