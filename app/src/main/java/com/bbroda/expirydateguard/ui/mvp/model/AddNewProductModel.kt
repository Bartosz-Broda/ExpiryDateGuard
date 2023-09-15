package com.bbroda.expirydateguard.ui.mvp.model

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.FoodTypesDatabase
import com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase.Type
import com.bbroda.expirydateguard.ui.classes.groceryRetrofit.CallResult
import com.bbroda.expirydateguard.ui.classes.groceryRetrofit.OpenFoodFactsAPI
import com.bbroda.expirydateguard.ui.classes.groceryRetrofit.RetrofitHelper
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.time.LocalDate
import java.util.Locale




class AddNewProductModel(var bus: EventBus) {

    suspend fun addNewProductToDatabase(localDate: LocalDate, name: String, type: String, englishType:String, ingredients:String, nutriments:String, imageUrl:String, ingredientsTranslated:String, nutrimentsTranslated: String, database: ProductsDatabase) {

        try{
            val product = Product(0,name, type ,englishType,localDate, ingredients, nutriments, imageUrl, ingredientsTranslated, nutrimentsTranslated)
            database.productDao().insertAll(product)
            Log.d(TAG, "addNewProductToDatabase: $product")
            bus.post(ProductAdded(product))
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
                        "fields" to "code,product_name,category_properties,ingredients_text_en,nutriments,image_front_small_url"
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

    @SuppressLint("DiscouragedApi")
    suspend fun initFoodTypes(activityContext: Context, database: FoodTypesDatabase){

        Log.d(TAG, "initFoodTypes: started")
        val types = activityContext.resources.getStringArray(R.array.ingredients)
        val packageName = activityContext.packageName
        val typesList = mutableListOf<Type>()

        val plLocale = Locale("pl")
        val enLocale = Locale ("en")
        val polResources = getLocalizedResources(activityContext, plLocale)
        val enResources = getLocalizedResources(activityContext, enLocale)

        for(type in types){
            val id = activityContext.resources.getIdentifier(type, "string", packageName)
            val labelPol = polResources?.getString(id)
            val labelEn = enResources?.getString(id)

            val typeObj = Type(0, labelPol!!,id, labelEn!!)
            typesList.add(typeObj)
            Log.d(TAG, "initFoodTypes: type loaded: ${activityContext.getString(id)}")
        }

        //Adding types to database so i can retrieve them when and where i need to
        if(database.typesDao().getAll().size != typesList.size){
            database.typesDao().nukeTable()
            database.typesDao().insertAll(typesList)
        }

        Log.d(TAG, "initFoodTypes: loading finished")
        bus.post(FoodTypesInitiated(typesList))
    }

    suspend fun getFoodTypesFromDatabase(database: FoodTypesDatabase){
        val types = database.typesDao().getAll()
        bus.post(FoodTypesFetched(types))
    }
    fun getLocalizedResources(context: Context, desiredLocale: Locale?): Resources? {
        var conf: Configuration = context.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(desiredLocale)
        val localizedContext = context.createConfigurationContext(conf)
        return localizedContext.resources
    }

    class ProductAdded(val product: Product)
    class BarcodeScanned(val response: Response<CallResult>)
    class BarcodeFailed
    class FoodTypesInitiated(val typesList: MutableList<Type>)
    class FoodTypesFetched(val typesList: List<Type>)
}
