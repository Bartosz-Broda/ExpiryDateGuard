package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.ProductScreenActivity
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.FoodTypesDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.ProductScreenModel
import com.bbroda.expirydateguard.ui.mvp.view.ProductScreenView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus.TAG
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.properties.Delegates


class ProductScreenPresenter(val view: ProductScreenView, val model: ProductScreenModel, val activity:ProductScreenActivity) {

    var uid by Delegates.notNull<Int>()
    val db = ProductsDatabase.getDatabase(activity)
    val foodDb = FoodTypesDatabase.getDatabase(activity)

    init{
        val extras = activity.intent.extras
        if (extras != null) {
            uid = extras.getInt("UID")
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun viewDemandsProductInfo(event: ProductScreenView.GetInfoAboutProduct) {
        activity.lifecycleScope.launch {
            try{
                model.getProductInfo(uid,db)
            }
            catch (e: Exception) {
                Log.d(TAG, "onViewDemandsProductInfo: $e")
            }
        }

    }

    @Subscribe
    fun onViewInitiated(event: ProductScreenView.ViewInit){
        activity.lifecycleScope.launch { model.getFoodTypesForAutoCompleteTxtView(foodDb) }
    }

    @Subscribe
    fun onProductInfoFetched(event: ProductScreenModel.ProductInfoFetched) {
        view.initiateUI(event.productName,event.productType,event.expiryDate,event.ingredients,event.nutritionInfo,event.imageUrl, activity)
    }


    @Subscribe
    fun tryingToChangeProductInfo(event: ProductScreenView.SaveNewProductInfo){

        var engType = ""

        for(type in event.listOfTypes){
            val label = activity.getString(type.stringID)
            if(event.productType == label){
                Log.d(ContentValues.TAG, "onSomeViewAction: ENG_TYPE: $engType")
                engType = type.typeLabelEn

                activity.lifecycleScope.launch {
                    model.changeProductInfo(event.productName,event.productType,engType,event.expiryDate,uid,db)
                }
            }
        }

        //didn't find this type in database, so we translate it with translator to prevent errors
        if(engType == ""){
            Log.d(TAG, "tryingToChangeProductInfo: ENGTYPE IS EMPTY")
            try {
                Log.d(ContentValues.TAG, "translateIfNeeded: product type: ${event.productType}")
                //translating type to english for searching recipe purpose
                val languageIdentifier = LanguageIdentification.getClient()
                languageIdentifier.identifyLanguage(event.productType)
                    .addOnSuccessListener { languageCode ->
                        if (languageCode !="und"){
                            Log.d(ContentValues.TAG, "translateIfNeeded: languagecode is not en. It's: $languageCode")

                            // Create a translator to English:
                            val options = TranslateLanguage.fromLanguageTag(languageCode).let {
                                TranslatorOptions.Builder()
                                    .setSourceLanguage(it!!)
                                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                                    .build()
                            }
                            val translatorToEnglish = options.let { Translation.getClient(it) }
                            val conditions = DownloadConditions.Builder()
                                .build()
                            translatorToEnglish.downloadModelIfNeeded(conditions).addOnSuccessListener {
                                // Model downloaded successfully. Okay to start translating.
                                //translating text (FINALLY!)
                                translatorToEnglish.translate(event.productType)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        activity.lifecycleScope.launch {
                                            model.changeProductInfo(event.productName,event.productType,translatedText,event.expiryDate,uid,db)
                                        }
                                        Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
                                        translatorToEnglish.close()
                                    }.addOnFailureListener { exception ->
                                        // Error.
                                        Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                        activity.lifecycleScope.launch {
                                            model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                                        }
                                        translatorToEnglish.close()
                                    }
                            }.addOnFailureListener { exception ->
                                // Model couldn’t be downloaded or other internal error.
                                activity.lifecycleScope.launch {
                                    model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                                }
                                Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                                translatorToEnglish.close()
                            }
                            Log.i(ContentValues.TAG, "Language: $languageCode")
                        }else{
                            //if translator cannot detect language, we assume for testing purpouse that it was in polish
                            Log.d(ContentValues.TAG, "translateIfNeeded: languagecode is not en. It's: $languageCode")

                            // Create a translator to English:
                            val options = TranslateLanguage.POLISH.let {
                                TranslatorOptions.Builder()
                                    .setSourceLanguage(it)
                                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                                    .build()
                            }
                            val translatorToEnglish = options.let { Translation.getClient(it) }
                            val conditions = DownloadConditions.Builder()
                                .build()
                            translatorToEnglish.downloadModelIfNeeded(conditions).addOnSuccessListener {
                                // Model downloaded successfully. Okay to start translating.
                                //translating text (FINALLY!)
                                translatorToEnglish.translate(event.productType)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        activity.lifecycleScope.launch {
                                            model.changeProductInfo(event.productName,event.productType,translatedText,event.expiryDate,uid,db)
                                        }
                                        Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
                                        translatorToEnglish.close()
                                    }.addOnFailureListener { exception ->
                                        // Error.
                                        Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                        activity.lifecycleScope.launch {
                                            model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                                        }
                                        translatorToEnglish.close()
                                    }
                            }.addOnFailureListener { exception ->
                                // Model couldn’t be downloaded or other internal error.
                                activity.lifecycleScope.launch {
                                    model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                                }
                                Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                                translatorToEnglish.close()
                            }
                            Log.i(ContentValues.TAG, "Language: $languageCode")

                            Log.d(TAG, "tryingToChangeProductInfo: TranslateIfNeeded: LanguageCode: UND")
                        }
                    }
                    .addOnFailureListener {
                        Log.d(ContentValues.TAG, "translateifneeded: $it")
                        // Model couldn’t be loaded or other internal error.
                        activity.lifecycleScope.launch {
                            model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                        }
                    }

            } catch (e: Exception) {
                activity.lifecycleScope.launch {
                    model.changeProductInfo(event.productName,event.productType,"",event.expiryDate,uid,db)
                }
                Log.d(ContentValues.TAG, "translateIfNeeded: $e")
            }
        }
    }

    @Subscribe
    fun userWantsToChangeProductInfo(event: ProductScreenView.UserWantsToChangeProductInfo){
        activity.lifecycleScope.launch {  model.getFoodTypesFromDatabase(foodDb)}
    }

    @Subscribe
    fun foodTypesFetched(event: ProductScreenModel.FoodTypesFetched){
        view.saveProductInfo(event.list)
    }

    @Subscribe
    fun onTypesFetched(event: ProductScreenModel.TypesFetched){
        view.setAutoCompleteTextView(event.list.toMutableList())
    }

    @Subscribe
    fun productInfoCallback(event: ProductScreenModel.ProductInfoNotChanged){
        view.productInfoWasntChanged()
    }
}
