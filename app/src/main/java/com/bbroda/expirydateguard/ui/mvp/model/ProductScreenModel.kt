package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.time.LocalDate

class ProductScreenModel (var bus: EventBus) {

    suspend fun getProductInfo(uid: Int, database: ProductsDatabase){
        val product: Product = database.productDao().loadByID(uid)
        bus.post(ProductInfoFetched(product.name,product.type,product.expiryDate.toString(),product.ingredients, product.nutriments, product.imageUrl))
    }


    suspend fun changeProductInfo(productName: String?, productType: String?, englishType:String, expiryDate:LocalDate, uid: Int, database: ProductsDatabase) {
        try{
            val product: Product = database.productDao().loadByID(uid)
            product.name = productName
            product.type = productType
            product.expiryDate = expiryDate
            product.englishType = englishType
            database.productDao().updateProduct(product)
            bus.post(ProductInfoFetched(product.name,product.type,product.expiryDate.toString(),product.ingredients,product.nutriments,product.imageUrl))
            Log.d(TAG, "changeProductInfo: Translateif... Product Info changed: $product")
        //bus.post(ProductInfoChanged(true))
        }catch (e:java.lang.Exception){
            Log.d(TAG, "changeProductInfo: $e")
            bus.post(ProductInfoNotChanged())
        }
    }

    private fun translateIfNeeded(productType: String): String{
        Log.d(ContentValues.TAG, "translateIfNeeded: product type: $productType")
        var englishTranslation: String = productType
        //translating type to english for searching recipe purpose
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(productType)
            .addOnSuccessListener { languageCode ->
                if (languageCode !="en"){
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
                        translatorToEnglish.translate(productType)
                            .addOnSuccessListener { translatedText ->
                                // Translation successful.
                                englishTranslation = translatedText
                                Log.d(ContentValues.TAG, "translateIfNeeded: TRANSLATED TEXT: $translatedText")
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
                    Log.i(ContentValues.TAG, "Language: $languageCode")
                }
            }
            .addOnFailureListener {
                Log.d(ContentValues.TAG, "addNewProductToDatabase: $it")
                // Model couldn’t be loaded or other internal error.
                // ...
            }
        Log.d(ContentValues.TAG, "translateIfNeeded: englishTranslation: $englishTranslation")
        return englishTranslation
    }

    class ProductInfoFetched(val productName:String?, val productType:String?, val expiryDate:String, val ingredients:String?, val nutritionInfo:String?, val imageUrl:String?)
    class ProductInfoNotChanged()
}
