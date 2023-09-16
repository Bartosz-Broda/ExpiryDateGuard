package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.FoodTypesDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class AddNewProductPresenter(val view: AddNewProductView, val model: AddNewProductModel, val activity: AddNewProductActivity) {

    val db = ProductsDatabase.getDatabase(activity)
    var ingredients: String = ""
    var nutriments: String = ""
    var imageUrl: String = ""
    val foodDb = FoodTypesDatabase.getDatabase(activity)

    init {

    }

    @Subscribe
    fun onSaveClicked(event: AddNewProductView.NewProductAdded) {

        //ready to implement nutriments and ingredients translation functionality
        var translatedIngredients = ""
        var translatedNutriments = ""


        /*if(!ingredients.isNullOrEmpty() || !nutriments.isNullOrEmpty()){

                try {
                    Log.d(ContentValues.TAG, "translateIfNeeded: product ingredients: ${ingredients}")
                    //translating type to english for searching recipe purpose

                                // Create a translator to Polish:
                                val options = TranslateLanguage.fromLanguageTag("en").let {
                                    TranslatorOptions.Builder()
                                        .setSourceLanguage(it!!)
                                        .setTargetLanguage(TranslateLanguage.POLISH)
                                        .build()
                                }
                                val translatorToPolish = options.let { Translation.getClient(it) }
                                val conditions = DownloadConditions.Builder()
                                    .build()
                                translatorToPolish.downloadModelIfNeeded(conditions).addOnSuccessListener {
                                    // Model downloaded successfully. Okay to start translating.
                                    //translating text (FINALLY!)
                                    translatorToPolish.translate(ingredients)
                                        .addOnSuccessListener { translatedText ->
                                            // Translation successful.
                                            Log.d(TAG, "onSaveClicked: INGREDIENTS TRANSLATED")
                                            translatedIngredients = translatedText
                                            Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")

                                        }.addOnFailureListener { exception ->
                                            // Error.
                                            Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                        }
                                    translatorToPolish.translate(nutriments)
                                        .addOnSuccessListener { translatedText ->
                                            // Translation successful.
                                            Log.d(TAG, "onSaveClicked: NUTRIMENTS TRANSLATED")
                                            translatedNutriments = translatedText
                                            translatorToPolish.close()
                                        }.addOnFailureListener { exception ->
                                            // Error.
                                            Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                            translatorToPolish.close()
                                        }
                                }.addOnFailureListener { exception ->
                                    // Model couldn’t be downloaded or other internal error.
                                    Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                                    translatorToPolish.close()
                                }

                } catch (e: Exception) {
                    Log.d(ContentValues.TAG, "translateIfNeeded: $e")
                }
            }*/

        Log.d(TAG, "onSomeViewAction: XXXX - got new product")
        var engType = ""

        //if type in textview is equal to one of types from list, just save it
        for(type in event.list){
            val label = activity.getString(type.stringID)
            if(event.type.lowercase() == label.lowercase()){
                engType = type.typeLabelEn
                Log.d(TAG, "onSomeViewAction: ENG_TYPE: $engType")
                activity.lifecycleScope.launch {
                    model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                }
            }
        }

        //didn't find this type in database, so we translate it with translator to prevent errors
        if(engType == ""){
            Log.d(EventBus.TAG, "tryingToChangeProductInfo: ENGTYPE IS EMPTY")
            try {
                Log.d(ContentValues.TAG, "translateIfNeeded: product type: ${event.type}")
                //translating type to english for searching recipe purpose
                val languageIdentifier = LanguageIdentification.getClient()
                languageIdentifier.identifyLanguage(event.type)
                    .addOnSuccessListener { languageCode ->
                        if (languageCode !="und"){
                            Log.d(ContentValues.TAG, "translateIfNeeded: languagecode is not und. It's: $languageCode")

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
                                translatorToEnglish.translate(event.type)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        engType = translatedText
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, event.type, translatedText, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                                        }
                                        Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
                                        translatorToEnglish.close()
                                    }.addOnFailureListener { exception ->
                                        // Error.
                                        Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                                        }
                                        translatorToEnglish.close()
                                    }
                            }.addOnFailureListener { exception ->
                                // Model couldn’t be downloaded or other internal error.
                                activity.lifecycleScope.launch {
                                    model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
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
                                translatorToEnglish.translate(event.type)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        engType = translatedText
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                                        }
                                        Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
                                        translatorToEnglish.close()
                                    }.addOnFailureListener { exception ->
                                        // Error.
                                        Log.d(ContentValues.TAG, "translateIfNeeded: Can't translate! $exception")
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                                        }
                                        translatorToEnglish.close()
                                    }
                            }.addOnFailureListener { exception ->
                                // Model couldn’t be downloaded or other internal error.
                                activity.lifecycleScope.launch {
                                    model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                                }
                                Log.d(ContentValues.TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                                translatorToEnglish.close()
                            }
                            Log.i(ContentValues.TAG, "Language: $languageCode")

                            Log.d(EventBus.TAG, "tryingToChangeProductInfo: TranslateIfNeeded: LanguageCode: UND")
                        }
                    }
                    .addOnFailureListener {
                        Log.d(ContentValues.TAG, "translateifneeded: $it")
                        // Model couldn’t be loaded or other internal error.
                        activity.lifecycleScope.launch {
                            model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                        }
                    }

            } catch (e: Exception) {
                activity.lifecycleScope.launch {
                    model.addNewProductToDatabase(event.date, event.name, event.type, engType, ingredients, nutriments, imageUrl, translatedIngredients ,translatedNutriments,db)
                }
                Log.d(ContentValues.TAG, "translateIfNeeded: $e")
            }
        }

    }

    @Subscribe
    fun onFoodTypesFetchedFromDatabase(event:AddNewProductModel.FoodTypesFetched){
        view.saveProduct(event.typesList)
    }

    @Subscribe
    fun userWantsToSaveProduct(event: AddNewProductView.UserWantsToSave){
        activity.lifecycleScope.launch{model.getFoodTypesFromDatabase(foodDb)}
    }


    @Subscribe
    fun onScanProduct(event: AddNewProductView.ScanProduct) {
        Log.d(TAG, "onSomeViewAction: XXXX - Scan product please!")
        scanBarcode()
        /*val intent = Intent(activity, OCRCameraActivity::class.java)
        ContextCompat.startActivity(activity, intent, null)
        activity.onPause()*/
    }

    @Subscribe
    fun onSomeModelAction(event: AddNewProductModel.ProductAdded) {
        Log.d(TAG, "onSomeModelAction: XXXX - ProductsAdded. Product info: ${event.product}")
        activity.finish()
    }

    @Subscribe
    fun apiCallSuccessful(event: AddNewProductModel.BarcodeScanned) {
        val result = event.response.body().toString()
        //On successful call i fetch name and type of scanned product from API
        try {
            ingredients = event.response.body()?.products?.get(0)?.ingredients_text.toString()

            //Getting nutriments in a string form, converting to Map and filtering by "100g" key
            val originalNutriments: Map<String, Any> =
                event.response.body()?.products?.get(0)?.nutriments!!.allNutriments
            val mutableMapNutriments: MutableMap<String, Any> = mutableMapOf()
            for (i in originalNutriments.keys) {
                if (i.contains("100g")) {
                    originalNutriments[i]?.let { mutableMapNutriments.put(i, it) }
                }
                if (i.contains("energy_100g") || i.contains("fruits-vegetables-nuts-estimate") || i.contains(
                        "nutrition-score"
                    ) || i.contains("nova-group")
                ) {
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                }

                if (i.contains("energy-kj_100g")) {
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                    originalNutriments[i]?.let { mutableMapNutriments.put("energy (kJ)", "$it kJ") }
                }

                if (i.contains("score")) {
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                }

                if (i.contains("energy-kcal_100g")) {
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                    originalNutriments[i]?.let {
                        mutableMapNutriments.put(
                            "energy (kcal)",
                            "$it kcal"
                        )
                    }
                }
            }
            //Editing nutriments for better look
            nutriments = mutableMapNutriments.toString()
            nutriments = nutriments.replace("{", " ")
            nutriments = nutriments.removeSuffix("}")
            nutriments = nutriments.replace("_", " in ", true)
            nutriments = nutriments.replace("kJ,", "kJ\n", false)
            nutriments = nutriments.replace(",", "g\n", false)
            nutriments = nutriments.replace("=", ": ", false)
            nutriments = nutriments.replace("-", " ", false)

            Log.d(TAG, "apiCallSuccessful: NUTRIMENTSs: $nutriments")


            val name = event.response.body()?.products?.get(0)?.product_name.toString()
            var type =
                event.response.body()?.products?.get(0)?.category_properties?.ciqual_food_name.toString()
            val newType: String

            if (type == "null") {
                newType = ""
            } else if (type.contains(",")) {
                newType = type.substring(0, type.indexOf(","))
            } else if (type.contains("-")) {
                newType = type.substring(0, type.indexOf("-"))
            } else if (type.contains("(")) {
                newType = type.substring(0, type.indexOf("("))
            } else if (type.contains("/")) {
                newType = type.substring(0, type.indexOf("/"))
            } else if (type.contains("[")) {
                newType = type.substring(0, type.indexOf("["))
            } else {
                newType = type
            }

            imageUrl = event.response.body()!!.products[0].imageUrl.toString()
            view.onApiSuccessfulCall(result, name, newType)

        } catch (e: java.lang.Exception) {
            view.displayToastOnApiFailure()
            Log.d(TAG, "apiCallSuccessfull: Api Call not successfull: $e")
        }

        view.changeVisibilityOfProgressBar(false)
    }

    @Subscribe
    fun apiCallFailed(event: AddNewProductModel.BarcodeFailed) {
        view.displayToastOnApiFailure()
        view.changeVisibilityOfProgressBar(false)
    }

    @Subscribe
    fun onViewInitiated(event: AddNewProductView.ViewInint){
        activity.lifecycleScope.launch { model.initFoodTypes(activity, foodDb) }
    }

    @Subscribe
    fun onTypesFetched(event: AddNewProductModel.FoodTypesInitiated){
        view.setAutoCompleteTextView(event.typesList)
    }

    private fun scanBarcode() {
        val scanner = GmsBarcodeScanning.getClient(activity)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                val rawValue: String? = barcode.rawValue
                Log.d(TAG, "BARCODE xxx: $rawValue")
                //Toast.makeText(activity, "$rawValue", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Default).launch {
                    val mResult: Deferred<Unit> = async { model.makeApiCall(rawValue) }
                    mResult.await()
                }
                view.changeVisibilityOfProgressBar(true)
            }
            .addOnCanceledListener {
                // Task canceled
                Toast.makeText(activity, "canceled", Toast.LENGTH_SHORT).show()
                view.changeVisibilityOfProgressBar(false)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d(TAG, "BARCODE FAILURE xxxx: $e")
                var toastText = e.toString().removePrefix("com.google.mlkit.common.MlKitException: ")
                view.showToast(toastText)
                view.changeVisibilityOfProgressBar(false)
            }
    }
    private fun getlanguageFromSharedPref(context: Context):String{
        try{
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang","")
            return if (language.isNullOrBlank()){
                ""
            } else{
                Log.d(EventBus.TAG, "getlanguageFromSharedPref: $language")
                return language
            }
        }catch (e:Exception){
            Log.d(EventBus.TAG, "getlanguageFromSharedPref: EXCEPTION: $e")
            return ""
        }
    }

}
