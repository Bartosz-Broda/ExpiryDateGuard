package com.bbroda.expirydateguard.ui.adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.recipeRetrofit.Recipes
import com.bbroda.expirydateguard.ui.mvp.view.RecipesScreenView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import org.greenrobot.eventbus.EventBus

class RecipesRecyclerViewAdapter(val dataSet: MutableList<Recipes>, val activityContext: Context, val ingredientsQuantity: Int): RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeNameTextView: TextView
        val recipeImage: ImageView
        val kcalTextView: TextView
        val productsQuantityTextView: TextView
        val mealTypeTextView: TextView
        val preparationTimeTextView: TextView
        val preparationTimeTitleTextView: TextView
        val ingredientsTextView: TextView

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
        holder.recipeNameTextView.text = dataSet[position].recipe?.label

        val kcal = dataSet[position].recipe?.calories?.toInt().toString()
        holder.kcalTextView.text = "Kcal: $kcal"

        val productsLacking = dataSet[position].recipe!!.ingredientLines!!.size - ingredientsQuantity
        holder.productsQuantityTextView.text = "${activityContext.getText(R.string.brakuje_ci)} $productsLacking ${activityContext.getText(R.string.produktów)}"

        val mealType = dataSet[position].recipe!!.mealType
        holder.mealTypeTextView.text = mealType?.get(0)

        var preparaionTime = dataSet[position].recipe!!.time
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


        val ingredients = dataSet[position].recipe!!.ingredientLines
        var ingredientsString = ""

        if (ingredients != null) {
            val sharedPreferences = activityContext.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang", "")

            for (i in ingredients) {
                ingredientsString += "$i,\n"
            }

            // Create a translator to English:
            val options = TranslateLanguage.ENGLISH.let {
                TranslatorOptions.Builder()
                    .setSourceLanguage(it)
                    .setTargetLanguage(language!!)
                    .build()
            }
            val translatorFromEnglish = options.let { Translation.getClient(it) }
            val conditions = DownloadConditions.Builder()
                .build()
            translatorFromEnglish.downloadModelIfNeeded(conditions).addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                //translating text (FINALLY!)
                translatorFromEnglish.translate(ingredientsString)
                    .addOnSuccessListener { translatedText ->
                        // Translation successful.
                        var translationReadyToShow = ""

                        val textList: MutableList<String> = translatedText.split(",").toMutableList()
                        textList.removeLast()
                        Log.d(ContentValues.TAG, "onBindViewHolder: textlist SIZE: ${textList.size}")

                        for (i in textList) {
                            translationReadyToShow += "$i, \n"
                        }
                        //translationReadyToShow = translationReadyToShow!!.replace(translationReadyToShow.substring(translationReadyToShow.length -2), "")

                        holder.ingredientsTextView.text = translationReadyToShow
                        Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translationReadyToShow")
                        translatorFromEnglish.close()
                    }.addOnFailureListener { exception ->
                        // Error.
                        Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                        holder.ingredientsTextView.text = ingredientsString
                        translatorFromEnglish.close()
                    }
            }.addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                holder.ingredientsTextView.text = ingredientsString
                translatorFromEnglish.close()
            }
        }



        //setting image
        val imageUrl = dataSet[position].recipe?.image
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
            EventBus.getDefault().post(RecipesScreenView.OpenRecipeDetails(dataSet[position]))
        }

    }

}