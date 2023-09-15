package com.bbroda.expirydateguard.ui.mvp.view

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.Type
import org.greenrobot.eventbus.EventBus
import java.time.DateTimeException
import java.time.LocalDate

class AddNewProductView(var activity: AddNewProductActivity,var bus: EventBus) {

    private val productTypeAutoCompleteTextView: AutoCompleteTextView = activity.findViewById(R.id.product_type_autocomplete_textview)
    private val productNameEdText: EditText = activity.findViewById(R.id.product_name_edittext)
    private val dayEdText: EditText = activity.findViewById(R.id.expiry_date_edittext_Day)
    private val monthEdText: EditText = activity.findViewById(R.id.expiry_date_edittext_Month)
    private val yearEdText: EditText = activity.findViewById(R.id.expiry_date_edittext_year)
    private val saveButton: Button = activity.findViewById(R.id.save_new_product_button)
    private val scanProductButton: Button = activity.findViewById(R.id.scanButton)
    private val apiCallProgressBar:ProgressBar = activity.findViewById(R.id.scan_progress_bar)

    init {
        saveButton.setOnClickListener {
            Log.d(TAG, "BUTTON CLICKED: XXXXXX")
            bus.post(UserWantsToSave())

        }


        dayEdText.doAfterTextChanged { if (dayEdText.text.toString().length ==2){monthEdText.requestFocus()} }
        monthEdText.doAfterTextChanged { if(monthEdText.text.toString().length == 2) {yearEdText.requestFocus()} }
        yearEdText.doAfterTextChanged { if(yearEdText.text.toString().length == 4){yearEdText.clearFocus()} }

        scanProductButton.setOnClickListener {
            bus.post(ScanProduct())
        }


        //Update UI
    }

    fun onApiSuccessfulCall(result: String, name: String, type: String){
        productNameEdText.setText(name)
        productTypeAutoCompleteTextView.setText(type)
        //Toast.makeText(activity, result, Toast.LENGTH_LONG).show()
    }

    fun displayToastOnApiFailure(){
        Toast.makeText(activity, activity.getString(R.string.cant_fetch), Toast.LENGTH_LONG).show()
    }

    fun setAutoCompleteTextView(foodTypes: MutableList<Type>){
        
        //setting autoCompleteTextView
        val ingredients = mutableListOf<String>()
        for (type in foodTypes){
            val label = activity.getString(type.stringID)
            ingredients.add(label)
        }
        Log.d(TAG, "setAutoCompleteTextView: $ingredients")

        val adapter = ArrayAdapter(activity,
            android.R.layout.simple_list_item_1, ingredients)
        productTypeAutoCompleteTextView.setAdapter(adapter)
    }

    fun changeVisibilityOfProgressBar(isVisible: Boolean){
        if (isVisible){
        apiCallProgressBar.visibility = View.VISIBLE
        }else{
            apiCallProgressBar.visibility = View.GONE
        }
    }

    fun saveProduct(list: List<Type>){
        try {
            val type = productTypeAutoCompleteTextView.text.toString()
            var name = productNameEdText.text.toString()
            if(type.isNullOrEmpty()){
                throw Exception(activity.getString(R.string.product_type_exception))
            }
            val day = if(dayEdText.text.isEmpty()){
                Log.d(TAG, "emptyyy: ")
                "01".toInt()
            }else{
                dayEdText.text.toString().toInt()
            }
            val month = monthEdText.text.toString().toInt()
            val year = yearEdText.text.toString().toInt()
            val localDate = LocalDate.of(year, month, day)
            Log.d(TAG, "doSomething: xxxx: DATE: $localDate")
            bus.post(NewProductAdded(localDate, name, type, list))
        }
        catch (exception: Exception){
            when(exception){
                is DateTimeException ->{
                    Log.d(TAG, "XXXX DateTimeException!!!: ${exception.message.toString()} ")
                    Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()}

                is java.lang.NumberFormatException ->{
                    Log.d(TAG, "XXXX NumberFormatException!!!: ${exception.message.toString()} ")
                    Toast.makeText(activity, "Something went wrong. Make sure you entered the correct expiration date.", Toast.LENGTH_LONG).show()
                }
                is Exception ->{
                    Log.d(TAG, "Exception!!!: ${exception.message.toString()} ")
                    Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_LONG).show()
                }
            }

        }
    }
    class NewProductAdded(val date: LocalDate, val name: String, val type: String, val list: List<Type>)
    class ScanProduct()

    class UserWantsToSave()
    class ViewInint()
}
