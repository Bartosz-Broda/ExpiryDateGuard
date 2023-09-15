package com.bbroda.expirydateguard.ui.mvp.view

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.ProductScreenActivity
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.Type
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.time.DateTimeException
import java.time.LocalDate

class ProductScreenView(val activity: ProductScreenActivity, val bus: EventBus) {

    private val productImage:ImageView = activity.findViewById(R.id.product_image)
    private var productNameTextView:TextView = activity.findViewById(R.id.product_name_product_screen)
    private var productTypeTextView:TextView = activity.findViewById(R.id.product_type_textview_productscreen)
    private val productTypeEditText: AutoCompleteTextView = activity.findViewById(R.id.product_type_edittext_productscreen)
    private val expiryTextView: TextView = activity.findViewById(R.id.expiry_date_textview_product_screen)
    private val expiryDateDayEditText: EditText = activity.findViewById(R.id.expiry_date_day_edittext_product_screen)
    private val expiryDateMonthEditText: EditText = activity.findViewById(R.id.expiry_date_month_edittext_product_screen)
    private val expiryDateYearEditText: EditText = activity.findViewById(R.id.expiry_date_year_edittext_product_screen)
    private val ingredientsTextView: TextView = activity.findViewById(R.id.ingredients_textview_product_screen)
    private val nutritionTextView: TextView = activity.findViewById(R.id.nutrition_textview_product_screen)
    private val editProductButton: Button = activity.findViewById(R.id.edit_product_button)
    private val approveEditImageButton: ImageButton = activity.findViewById(R.id.approve_editing_product_button)
    private val dismissEditImageButton: ImageButton = activity.findViewById(R.id.cancel_editing_product_button)
    private val imageProgressBar:ProgressBar = activity.findViewById(R.id.image_progress_bar)
    private val loadProgressBar:ProgressBar = activity.findViewById(R.id.load_progressbar)
    private val productNameEditText:EditText = activity.findViewById(R.id.product_name_product_screen_edittext)
    private val scrollView:ScrollView = activity.findViewById(R.id.scrollView2)
    /*private val translateButton:Button = activity.findViewById(R.id.translate_button)
    private val originalTextButton: Button = activity.findViewById(R.id.detranslate_button)*/
    private val textViewIngredientsPolish: TextView = activity.findViewById(R.id.ingredients_textview_product_screen_polish)
    private val nutritionTextViewPolish: TextView = activity.findViewById(R.id.nutrition_textview_product_screen_polish)

    init{

        /*productTypeTextView.minHeight = productTypeEditText.height
        productTypeTextView.minHeight = productNameEditText.height*/

        /*translateButton.setOnClickListener {
            nutritionTextView.visibility = View.GONE
            nutritionTextViewPolish.visibility = View.VISIBLE
            ingredientsTextView.visibility = View.GONE
            textViewIngredientsPolish.visibility = View.VISIBLE
            translateButton.visibility = View.GONE
            originalTextButton.visibility = View.VISIBLE
        }

        originalTextButton.setOnClickListener {
            nutritionTextView.visibility = View.VISIBLE
            nutritionTextViewPolish.visibility = View.GONE
            ingredientsTextView.visibility = View.VISIBLE
            textViewIngredientsPolish.visibility = View.GONE
            translateButton.visibility = View.VISIBLE
            originalTextButton.visibility = View.GONE
        }*/

        editProductButton.setOnClickListener {
            //changing textviews into edittexts
            editProductButton.visibility = View.GONE
            approveEditImageButton.visibility = View.VISIBLE
            dismissEditImageButton.visibility = View.VISIBLE

            productNameTextView.visibility = View.INVISIBLE
            productNameEditText.visibility = View.VISIBLE

            productTypeTextView.visibility = View.INVISIBLE
            productTypeEditText.visibility = View.VISIBLE

            expiryTextView.visibility = View.INVISIBLE
            expiryDateDayEditText.visibility = View.VISIBLE
            expiryDateMonthEditText.visibility = View.VISIBLE
            expiryDateYearEditText.visibility = View.VISIBLE

            //setting edittexts hint
            productNameEditText.setText("${productNameTextView.text}")
            productTypeEditText.setText("${productTypeTextView.text}")

            val dateSplitted:List<String> = expiryTextView.text.split("-")
            Log.d(TAG, "DATE SPLITTED: $dateSplitted")
            expiryDateDayEditText.setText(dateSplitted[2])
            expiryDateMonthEditText.setText(dateSplitted[1])
            expiryDateYearEditText.setText(dateSplitted[0])

            //setting constraints for view to be more responsive
            val scrollParams = scrollView.layoutParams as ConstraintLayout.LayoutParams
            scrollParams.topToBottom = productNameEditText.id

            val imageParams = productImage.layoutParams as ConstraintLayout.LayoutParams
            imageParams.topToTop = productNameEditText.id
            imageParams.bottomToBottom = productNameEditText.id


            val dateParams = expiryTextView.layoutParams as ConstraintLayout.LayoutParams
            dateParams.topToBottom = productTypeEditText.id
            val dayParams = expiryDateDayEditText.layoutParams as ConstraintLayout.LayoutParams
            dayParams.topToBottom = productTypeEditText.id
            val monthParams = expiryDateMonthEditText.layoutParams as ConstraintLayout.LayoutParams
            monthParams.topToBottom = productTypeEditText.id
            val yearParams = expiryDateYearEditText.layoutParams as ConstraintLayout.LayoutParams
            yearParams.topToBottom = productTypeEditText.id

        }

        dismissEditImageButton.setOnClickListener {
            editProductButton.visibility = View.VISIBLE
            approveEditImageButton.visibility = View.GONE
            dismissEditImageButton.visibility = View.GONE

            productNameTextView.visibility = View.VISIBLE
            productNameEditText.visibility = View.GONE

            productTypeTextView.visibility = View.VISIBLE
            productTypeEditText.visibility = View.GONE

            expiryTextView.visibility = View.VISIBLE
            expiryDateDayEditText.visibility = View.GONE
            expiryDateMonthEditText.visibility = View.GONE
            expiryDateYearEditText.visibility = View.GONE

            //setting constraints for view to be more responsive
            val scrollParams = scrollView.layoutParams as ConstraintLayout.LayoutParams
            scrollParams.topToBottom = productNameTextView.id

            val imageParams = productImage.layoutParams as ConstraintLayout.LayoutParams
            imageParams.topToTop = productNameTextView.id
            imageParams.bottomToBottom = productNameTextView.id


            val dateParams = expiryTextView.layoutParams as ConstraintLayout.LayoutParams
            dateParams.topToBottom = productTypeTextView.id
            val dayParams = expiryDateDayEditText.layoutParams as ConstraintLayout.LayoutParams
            dayParams.topToBottom = productTypeTextView.id
            val monthParams = expiryDateMonthEditText.layoutParams as ConstraintLayout.LayoutParams
            monthParams.topToBottom = productTypeTextView.id
            val yearParams = expiryDateYearEditText.layoutParams as ConstraintLayout.LayoutParams
            yearParams.topToBottom = productTypeTextView.id
        }

        approveEditImageButton.setOnClickListener {
            bus.post(UserWantsToChangeProductInfo())
        }

    }

    fun initiateUI(productName:String?, productType:String?, expiryDate:String, ingredients:String?, nutritionInfo:String?, translatedIngredients:String?, translatedNutrients: String?, imageUrl:String?, context: Context){

        //Loading food image
        Log.d(TAG, "initiateUI: image url: $imageUrl")
        loadProgressBar.visibility = View.GONE
        try{
        if(imageUrl.isNullOrEmpty() || imageUrl =="null") {
            Glide.with(context)
                .load(R.drawable.grocery_bg)
                .apply(RequestOptions.circleCropTransform())
                .into(productImage)
        }else{
            imageProgressBar.visibility = View.VISIBLE
            Glide.with(context)
                .load(imageUrl.toUri())
                .apply(RequestOptions.circleCropTransform())
                .into(productImage)
            imageProgressBar.visibility = View.GONE
        }}catch(e:Exception){
            Glide.with(context)
                .load(R.drawable.grocery_bg)
                .apply(RequestOptions.circleCropTransform())
                .into(productImage)
            imageProgressBar.visibility = View.GONE
            Log.d(TAG, "initiateUI: GLIDE: $e")
        }

        //Setting fields with info about product
        if (!productName.isNullOrEmpty() && productName!="null"){productNameTextView.text = productName}
        if(!productType.isNullOrEmpty() && productType != "null"){productTypeTextView.text = productType }
        expiryTextView.text = expiryDate
        if(!ingredients.isNullOrEmpty() && ingredients!="null"){ingredientsTextView.text = ingredients}
        if (!nutritionInfo.isNullOrEmpty() && nutritionInfo != "null"){nutritionTextView.text = nutritionInfo }
        if(!translatedIngredients.isNullOrEmpty() && translatedIngredients!="null"){textViewIngredientsPolish.text = translatedIngredients}
        if (!translatedNutrients.isNullOrEmpty() && translatedNutrients != "null"){nutritionTextViewPolish.text = translatedNutrients }
    }

    fun productInfoWasntChanged (){
        dismissEditImageButton.callOnClick()
        Toast.makeText(activity, "Wystąpił błąd. Dane nie zostały zaktualizowane", Toast.LENGTH_LONG).show()
    }

    fun saveProductInfo(listOfTypes: List<Type>){
        try{
            val day = if(expiryDateDayEditText.text.isEmpty()){
                Log.d(ContentValues.TAG, "emptyyy: ")
                "01".toInt()
            }else{
                expiryDateDayEditText.text.toString().toInt()
            }
            val month = expiryDateMonthEditText.text.toString().toInt()
            val year = expiryDateYearEditText.text.toString().toInt()
            val localDate = LocalDate.of(year, month, day)

            bus.post(SaveNewProductInfo(productNameEditText.text.toString(), productTypeEditText.text.toString(), localDate, listOfTypes))

            dismissEditImageButton.callOnClick()

        }catch(exception:java.lang.Exception){
            when(exception){
                is DateTimeException ->{
                    Log.d(ContentValues.TAG, "XXXX DateTimeException!!!: ${exception.message.toString()} ")
                    Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()}

                is java.lang.NumberFormatException ->{
                    Log.d(ContentValues.TAG, "XXXX NumberFormatException!!!: ${exception.message.toString()} ")
                    Toast.makeText(activity, "Make sure you entered the correct expiration date.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun setAutoCompleteTextView(foodTypes: MutableList<Type>){

        //setting autoCompleteTextView
        val ingredients = mutableListOf<String>()
        for (type in foodTypes){
            val label = activity.getString(type.stringID)
            ingredients.add(label)
        }
        Log.d(ContentValues.TAG, "setAutoCompleteTextView: $ingredients")

        val adapter = ArrayAdapter(activity,
            android.R.layout.simple_list_item_1, ingredients)
        productTypeEditText.setAdapter(adapter)
    }

    fun progressBarVisibility(boolean: Boolean){
        if (boolean){
            loadProgressBar.visibility = View.VISIBLE
        }else{
            loadProgressBar.visibility = View.GONE
        }
    }

    /*fun showTranslateButton(){
        translateButton.visibility = View.VISIBLE
    }*/

    fun showToast(text: String){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
    class GetInfoAboutProduct
    class UserWantsToChangeProductInfo()

    class SaveNewProductInfo(val productName:String, val productType:String, val expiryDate: LocalDate, val listOfTypes: List<Type>)
    class ViewInit()

}
