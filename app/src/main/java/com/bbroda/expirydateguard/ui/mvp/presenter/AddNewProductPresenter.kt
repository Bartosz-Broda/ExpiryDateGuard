package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
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
import org.greenrobot.eventbus.Subscribe


class AddNewProductPresenter(val view: AddNewProductView, val model: AddNewProductModel, val activity: AddNewProductActivity) {

    val db = ProductsDatabase.getDatabase(activity)
    var ingredients: String = ""
    var nutriments: String = ""
    var imageUrl: String = ""

    init{
        //It solves the problem of not opening scanner
        val moduleInstallClient = ModuleInstall.getClient(activity)
        val optionalModuleApi = GmsBarcodeScanning.getClient(activity)
        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    // Modules are present on the device...
                } else {
                    // Modules are not present on the device...
                    val moduleInstallRequest =
                        ModuleInstallRequest.newBuilder()
                            .addApi(optionalModuleApi)
                            // Add more APIs if you would like to request multiple optional modules.
                            // .addApi(...)
                            // Set the listener if you need to monitor the download progress.
                            // .setListener(listener)
                            .build()

                    moduleInstallClient
                        .installModules(moduleInstallRequest)
                        .addOnSuccessListener {
                            if (it.areModulesAlreadyInstalled()) {
                                // Modules are already installed when the request is sent.
                            }
                        }
                        .addOnFailureListener {
                            // Handle failure…
                            Log.d(TAG, "CANNOT INSTALL SCANNER: ")
                        }
                }
            }
            .addOnFailureListener {
                // Handle failure...
                Log.d(TAG, "CANNOT CHECK IF SCANNER IS INSTALLED: ")
            }
    }

    @Subscribe
    fun onSomeViewAction(event: AddNewProductView.NewProductAdded) {
        Log.d(TAG, "onSomeViewAction: XXXX - got new product")
        //newtype is shortened version of type (openfoodfacts api sometimes returns type as a longer description)
        val newType: String
        if(event.type.contains(",")){
            newType = event.type.substring( 0, event.type.indexOf(","))
        }else{
            newType = event.type
        }
            try {
                Log.d(TAG, "translateIfNeeded: product type: $newType")
                //translating type to english for searching recipe purpose
                val languageIdentifier = LanguageIdentification.getClient()
                languageIdentifier.identifyLanguage(newType)
                    .addOnSuccessListener { languageCode ->
                        if (languageCode !="und"){
                            Log.d(TAG, "translateIfNeeded: languagecode is not und. It's: $languageCode")

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
                                translatorToEnglish.translate(newType)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, newType,translatedText,ingredients, nutriments, imageUrl, db)
                                        }
                                        Log.d(TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
                                        translatorToEnglish.close()
                                    }.addOnFailureListener { exception ->
                                        // Error.
                                        Log.d(TAG, "translateIfNeeded: Can't translate! $exception")
                                        activity.lifecycleScope.launch {
                                            model.addNewProductToDatabase(event.date, event.name, newType,"",ingredients, nutriments, imageUrl, db)
                                        }
                                        translatorToEnglish.close()
                                    }
                            }.addOnFailureListener { exception ->
                                // Model couldn’t be downloaded or other internal error.
                                Log.d(TAG, "translateIfNeeded: Need another language model but can't download it!: $exception")
                                translatorToEnglish.close()
                            }
                            Log.i(TAG, "Language: $languageCode")
                        }else{
                            activity.lifecycleScope.launch {
                                model.addNewProductToDatabase(event.date, event.name, newType,"",ingredients, nutriments, imageUrl, db)
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "addNewProductToDatabase: $it")
                        // Model couldn’t be loaded or other internal error.
                        activity.lifecycleScope.launch {
                            model.addNewProductToDatabase(event.date, event.name, newType,"",ingredients, nutriments, imageUrl, db)
                        }
                    }

            } catch (e: Exception) {
                activity.lifecycleScope.launch {
                    model.addNewProductToDatabase(event.date, event.name, newType,"",ingredients, nutriments, imageUrl, db)
                }
                Log.d(TAG, "translateIfNeeded: $e")
            }


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
    fun apiCallSuccessful(event: AddNewProductModel.BarcodeScanned){
        val result = event.response.body().toString()
        //On successful call i fetch name and type of scanned product from API
        try{
            ingredients = event.response.body()?.products?.get(0)?.ingredients_text.toString()

            //Getting nutriments in a string form, converting to Map and filtering by "100g" key
            val originalNutriments: Map<String, Any> = event.response.body()?.products?.get(0)?.nutriments!!.allNutriments
            val mutableMapNutriments : MutableMap<String,Any> = mutableMapOf()
            for (i in originalNutriments.keys){
                if (i.contains("100g")){
                    originalNutriments[i]?.let { mutableMapNutriments.put(i, it) }
                }
                if (i.contains("energy_100g")||i.contains("fruits-vegetables-nuts-estimate")||i.contains("nutrition-sscore")||i.contains("nova-group")){
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }}

                if (i.contains("energy-kj_100g")){
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                    originalNutriments[i]?.let { mutableMapNutriments.put("energy (kJ)", "$it kJ") }
                }

                if (i.contains("energy-kcal_100g")){
                    originalNutriments[i]?.let { mutableMapNutriments.remove(i) }
                    originalNutriments[i]?.let { mutableMapNutriments.put("energy (kcal)", "$it kcal") }
                }
            }
            //Editing nutriments for better look
            nutriments = mutableMapNutriments.toString()
            nutriments = nutriments.replace("{"," ")
            nutriments = nutriments.removeSuffix("}")
            nutriments = nutriments.replace("_", " in ",true)
            nutriments = nutriments.replace("kJ,","kJ\n",false)
            nutriments = nutriments.replace(",","g\n",false)
            nutriments = nutriments.replace("=",": ",false)
            nutriments = nutriments.replace("-"," ",false)

            Log.d(TAG, "apiCallSuccessful: NUTRIMENTSs: $nutriments")


            val name = event.response.body()?.products?.get(0)?.product_name.toString()
            var type = event.response.body()?.products?.get(0)?.category_properties?.ciqual_food_name.toString()
            if(type == "null"){
                type = ""
            }
            imageUrl = event.response.body()!!.products[0].imageUrl.toString()
            view.onApiSuccessfulCall(result, name, type)

        }
        catch (e: java.lang.Exception){
            view.displayToastOnApiFailure()
            Log.d(TAG, "apiCallSuccessfull: Api Call not successfull: $e")
        }

        view.changeVisibilityOfProgressBar(false)
    }
    @Subscribe
    fun apiCallFailed(event: AddNewProductModel.BarcodeFailed){
        view.displayToastOnApiFailure()
        view.changeVisibilityOfProgressBar(false)
    }

    private fun scanBarcode(){

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
                view.changeVisibilityOfProgressBar(false)
            }
            .addOnCanceledListener {
                // Task canceled
                Toast.makeText(activity, "canceled", Toast.LENGTH_SHORT).show()
                view.changeVisibilityOfProgressBar(false)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d(TAG, "BARCODE FAILURE xxxx: $e")
                view.changeVisibilityOfProgressBar(false)
            }
        view.changeVisibilityOfProgressBar(true)
    }


}
