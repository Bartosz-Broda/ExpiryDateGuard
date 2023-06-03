package com.bbroda.expirydateguard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.Product
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import org.greenrobot.eventbus.EventBus

class RecyclerAdapter(private val dataSet: MutableList<Product>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val dateTextView: TextView
        val deleteProductButton: ImageButton

        init {
            // Define click listener for the ViewHolder's View
            nameTextView = view.findViewById(R.id.product_name)
            dateTextView = view.findViewById(R.id.expiry_date)
            deleteProductButton = view.findViewById(R.id.delete_product_button)
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
        viewHolder.dateTextView.text = dataSet[position].expiryDate.toString()
        //Setting onclicklistener for button

        //Setting onclicklistener for button
        viewHolder.deleteProductButton.setOnClickListener {
            EventBus.getDefault().post(MainMenuView.DeleteProduct(dataSet[position]))
            //dataSet.removeAt(position)
            dataSet.remove(dataSet[position])
            notifyItemRemoved(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
