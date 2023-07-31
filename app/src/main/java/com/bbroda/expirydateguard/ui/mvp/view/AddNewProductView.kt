package com.bbroda.expirydateguard.ui.mvp.view

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import org.greenrobot.eventbus.EventBus
import java.time.DateTimeException
import java.time.LocalDate

class AddNewProductView(var activity: AddNewProductActivity,var bus: EventBus) {

    private val productTypeEdText: EditText = activity.findViewById(R.id.product_type_edittext)
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
            try {
                val type = productTypeEdText.text.toString()
                val name = productNameEdText.text.toString()
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
                bus.post(NewProductAdded(localDate, name, type))
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
                }

            }
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
        productTypeEdText.setText(type)
        Toast.makeText(activity, result, Toast.LENGTH_LONG).show()
    }

    fun displayToastOnApiFailure(){
        Toast.makeText(activity, "Can't fetch information about this product", Toast.LENGTH_LONG).show()
    }

    fun changeVisibilityOfProgressBar(isVisible: Boolean){
        if (isVisible){
        apiCallProgressBar.visibility = View.VISIBLE
        }else{
            apiCallProgressBar.visibility = View.GONE
        }
    }
    class NewProductAdded(val date: LocalDate, val name: String, val type: String)
    class ScanProduct()
}
