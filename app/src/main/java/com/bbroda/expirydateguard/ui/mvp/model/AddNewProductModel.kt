package com.bbroda.expirydateguard.ui.mvp.model

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.retrofit.CallResult
import com.bbroda.expirydateguard.ui.classes.retrofit.OpenFoodFactsAPI
import com.bbroda.expirydateguard.ui.classes.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.time.LocalDate

class AddNewProductModel(var bus: EventBus) {

    suspend fun addNewProductToDatabase(localDate: LocalDate, name: String, type: String, ingredients:String, nutriments:String, imageUrl:String, database: ProductsDatabase) {
        try{
            Log.d(TAG, "addNewProductToDatabase: Adding new product - xxxx")
            val product = Product(0,name, type, localDate, ingredients, nutriments, imageUrl)
            Log.d(TAG, "addNewProductToDatabase: Adding new product2 - xxxx")
            database.productDao().insertAll(product)
            Log.d(TAG, "addNewProductToDatabase: Adding new product3 - xxxx")
            Log.d(TAG, "addNewProductToDatabase: ingredients: ${product.ingredients}")
            bus.post(ProductAdded())
        }
        catch(e: java.lang.Exception){
            Log.d(TAG, "addNewProductToDatabase xxxx: ERROR: $e")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun makeApiCall(rawValue:String?){
        Log.d(TAG, "makeApiCall: RawValue Barcode: $rawValue")
        val openFoodFactsApi = RetrofitHelper.getInstance().create(OpenFoodFactsAPI::class.java)

            try {
                if(rawValue != null){
                    Log.d(TAG, "makeApiCall: TRYING......")
                    val result = openFoodFactsApi.callForProduct(mapOf(
                        "code" to rawValue,
                        "fields" to "code,product_name,category_properties,ingredients_text,nutriments,image_front_small_url"
                    ))
                    Log.d(TAG, "makeApiCall: scanBarcode: SCANNED XXXX: ${result.body().toString()}")
                    withContext(Dispatchers.Main) {
                        bus.post(BarcodeScanned(result))
                    }

                }

            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main){
                    bus.post(BarcodeFailed())
                }

                    Log.d(TAG, "makeApiCall XXXX: $e")
            }

    }

    class ProductAdded
    class BarcodeScanned(val response: Response<CallResult>)
    class BarcodeFailed
}
