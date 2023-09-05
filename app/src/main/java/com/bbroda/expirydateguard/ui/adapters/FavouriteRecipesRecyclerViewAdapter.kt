package com.bbroda.expirydateguard.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.recipedatabase.Recipe
import com.bbroda.expirydateguard.ui.mvp.view.FavouriteRecipesView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import org.greenrobot.eventbus.EventBus

class FavouriteRecipesRecyclerViewAdapter(val dataSet: MutableList<Recipe>, val activityContext: Context, val products: List<Product>): RecyclerView.Adapter<FavouriteRecipesRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeNameTextView: TextView
        val recipeImage: ImageView
        val kcalTextView: TextView
        val productsQuantityTextView: TextView
        val mealTypeTextView: TextView
        val preparationTimeTextView: TextView
        val preparationTimeTitleTextView: TextView
        val ingredientsTextView: TextView
        val addToFavouriteFab: ImageButton
        val removeFromFavouriteFab: ImageButton

        init {
            //Define click listener for the ViewHolder's View
            recipeNameTextView = view.findViewById(R.id.recipe_title)
            recipeImage = view.findViewById(R.id.recipe_image_imageview)
            kcalTextView = view.findViewById(R.id.energy_per_serving)
            productsQuantityTextView = view.findViewById(R.id.ingredients_quantity)
            mealTypeTextView = view.findViewById(R.id.mealTypeTextView)
            preparationTimeTextView = view.findViewById(R.id.preparationTimeTextView)
            preparationTimeTitleTextView = view.findViewById(R.id.preparationTimeTitle)
            ingredientsTextView = view.findViewById(R.id.ingredientsTextView)
            addToFavouriteFab = view.findViewById(R.id.add_to_favourite_fab)
            removeFromFavouriteFab = view.findViewById(R.id.remove_from_favourite_fab)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_cardview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.addToFavouriteFab.visibility = View.GONE
        holder.removeFromFavouriteFab.visibility = View.VISIBLE

        holder.recipeNameTextView.text = dataSet[position].name

        val kcal = dataSet[position].calories?.toInt().toString()
        holder.kcalTextView.text = "Kcal: $kcal"


        var ingredientsQuantity = 0
        for (i in products){
            Log.d(ContentValues.TAG, "onBindViewHolder: PRODUCT: ${i.englishType}")
            Log.d(ContentValues.TAG, "onBindViewHolder: INGREDIENTS: ${dataSet[position].ingredients.toString()}")
            if (i.englishType?.let { dataSet[position].ingredients?.toString()?.lowercase()?.contains(it.lowercase()) } == true){
                ingredientsQuantity += 1
            }
        }

        val productsLacking = dataSet[position].ingredients!!.size - ingredientsQuantity
        holder.productsQuantityTextView.text = "${activityContext.getText(R.string.brakuje_ci)} $productsLacking ${activityContext.getText(R.string.produktów)}"

        val mealType = dataSet[position].mealType
        holder.mealTypeTextView.text = mealType?.get(0)

        var preparaionTime = dataSet[position].time
        if(preparaionTime != "0.0" && preparaionTime !=null){
            preparaionTime = preparaionTime.removeSuffix(".0")
            holder.preparationTimeTextView.text = "$preparaionTime ${activityContext.getString(R.string.minutes)}"
            //preparaionTime = preparaionTime!!.replace(preparaionTime.substring(preparaionTime.length -2), "")

        }else{
            //hiding textviews makes ui more responsive
            val ingredientsParams = holder.ingredientsTextView.layoutParams as ConstraintLayout.LayoutParams
            ingredientsParams.topToBottom = holder.mealTypeTextView.id
            holder.preparationTimeTextView.visibility = View.GONE
            holder.preparationTimeTitleTextView.visibility = View.GONE
        }


        val ingredients = dataSet[position].ingredients
        val ingredientsTranslated = mutableListOf<String>()
        var ingredientsString = ""

        var ingredientsStringNotTranslated = ""

        if (ingredients != null) {

            for (i in ingredients){
                ingredientsString += "- $i;"
            }

            Log.d(ContentValues.TAG, "onBindViewHolder: Ingredients NOT NULL!")
            Log.d(ContentValues.TAG, "onBindViewHolder: INGREDIENTS SIZE: ${ingredients.size}")
            val sharedPreferences = activityContext.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang", "pl")
            translateFromEnglishAndSetText(language!!, ingredientsString,holder.ingredientsTextView)

            }

        //setting image
        val imageUrl = dataSet[position].imageUrl
        try{
            if(imageUrl.isNullOrEmpty() || imageUrl =="null") {
                Glide.with(activityContext)
                    .load(R.drawable.grocery_generic_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.recipeImage)
            }else{
                Glide.with(activityContext)
                    .load(imageUrl.toUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.recipeImage)
            }}catch(e:Exception){
            Glide.with(activityContext)
                .load(R.drawable.grocery_generic_image)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.recipeImage)
            Log.d(EventBus.TAG, "initiateUI: GLIDE: $e")
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataSet[position].url))
            activityContext.startActivity(intent)
        }

        holder.removeFromFavouriteFab.setOnClickListener {

            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Alert")
            builder.setMessage(R.string.delete_product_button)

            builder.setPositiveButton("Usuń") { dialog, which ->
                EventBus.getDefault().post(FavouriteRecipesView.RemoveFromFavourite(dataSet[position]))
                dataSet.remove(dataSet[position])
                notifyItemRemoved(position)

                Toast.makeText(activityContext,
                    activityContext.getString(R.string.usunięto_ulubione), Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Anuluj") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }

    }

    private fun translateFromEnglishAndSetText(language:String, textToTranslate:String, textView: TextView){

        var textTranslated: String = ""
        // Create a translator to English:
        val options = TranslateLanguage.ENGLISH.let {
            TranslatorOptions.Builder()
                .setSourceLanguage(it)
                .setTargetLanguage(language)
                .build()
        }
        val translatorToEnglish = options.let { Translation.getClient(it) }
        val conditions = DownloadConditions.Builder()
            .build()
         translatorToEnglish.downloadModelIfNeeded(conditions).addOnSuccessListener {
            // Model downloaded successfully. Okay to start translating.
            translatorToEnglish.translate(textToTranslate)
                .addOnSuccessListener { translatedText ->
                    // Translation successful.
                    var translation = translatedText
                    translation = translation.removeSuffix(";")
                    translation = translation.replace(";","\n")
                    Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translation")
                    textView.text = translation

                    translatorToEnglish.close()
                }.addOnFailureListener { exception ->
                    // Error.
                    Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                    translatorToEnglish.close()
                }
        }.addOnFailureListener { exception ->
            // Model couldn’t be downloaded or other internal error.
            Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
            translatorToEnglish.close()
        }

    }

}