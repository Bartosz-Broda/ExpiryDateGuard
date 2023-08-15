package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.Preference
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.PreferenceDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsInListDatabase
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import org.greenrobot.eventbus.EventBus

//import com.nerdscorner.mvplib.events.model.BaseEventsModel

class MainMenuModel(var bus: EventBus){


    suspend fun getListOfProducts(database: ProductsDatabase){

        Log.d(TAG, "getListOfProducts: XXXXXX")
        bus.post(ObtainedListOfProducts(database.productDao().getAll() as MutableList<Product>))
    }

    suspend fun removeProduct(database: ProductsDatabase, product: Product){

        database.productDao().delete(product)
        Log.d(TAG, "removeProduct: XXXXXX")
        bus.post(ProductWasDeleted())

    }

    suspend fun addProductToListDatabase(product:Product, database: ProductsInListDatabase){
        try{
            database.productDao().insertAll(product)
            Log.d(TAG, "addProductToListDatabase: ${database.productDao().getAll().size}")
            bus.post(ProductAddedToListDatabase(database.productDao().getAll().size))
        }
        catch(e: java.lang.Exception){
            Log.d(TAG, "addProductToListDatabase xxxx: ERROR: $e")
        }
    }

    suspend fun removeProductFromListDatabase(product:Product, database: ProductsInListDatabase){
        try{
            database.productDao().delete(product)
            bus.post(ProductRemovedFromListDatabase(database.productDao().getAll().size))
            Log.d(TAG, "removeProductFromListDatabase: ${database.productDao().getAll().size}")
        }
        catch(e: java.lang.Exception){
            Log.d(TAG, "removeProductFromListDatabase xxxx: ERROR: $e")
        }
    }

    suspend fun clearListDatabase(database: ProductsInListDatabase){
        try{
            database.productDao().nukeTable()
            Log.d(TAG, "clearListDatabase: ${database.productDao().getAll().size}")
        }
        catch(e: java.lang.Exception){
            Log.d(TAG, "addNewProductToDatabase xxxx: ERROR: $e")
        }
    }

    suspend fun getProductsFromListDatabase(database: ProductsInListDatabase){
        val list = database.productDao().getAll()
        bus.post(ObtainedProductsForMeal(list))
    }

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }


    suspend fun downloadLanguageModelsIfNeeded(){
        val modelManager = RemoteModelManager.getInstance()
        val polishModel = TranslateRemoteModel.Builder(TranslateLanguage.POLISH).build()
        val englishModel = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
        val conditions = DownloadConditions.Builder()
            .build()

        // Get translation models stored on the device.
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                // if there's no polish model, download it.
                if (!models.contains(polishModel)){
                    modelManager.download(polishModel, conditions)
                        .addOnSuccessListener {
                            // Model downloaded.
                            Log.d(TAG, "downloadLanguageModelsifNeeded: polish language model downloaded")
                        }
                        .addOnFailureListener {
                            // Error.
                            Log.d(TAG, "downloadLanguageModelsifNeeded: Can't download language model (pl): $it")
                        }

                }else if (!models.contains(englishModel)){
                    modelManager.download(englishModel, conditions)
                        .addOnSuccessListener {
                            // Model downloaded.
                            Log.d(TAG, "downloadLanguageModelsifNeeded: english language model downloaded")
                        }
                        .addOnFailureListener {
                            // Error.
                            Log.d(TAG, "downloadLanguageModelsifNeeded: Can't download language model (en): $it")
                        }
                }else{
                    Log.d(TAG, "downloadLanguageModelsIfNeeded: all models on board!")
                }

            }
            .addOnFailureListener {
                Log.d(TAG, "downloadLanguageModelsifNeeded: Can't get downloaded models! $it")
            }
    }


    suspend fun RetrieveFoodPreferences(database: PreferenceDatabase){

        val list = database.preferenceDao().getAll()

        if(list.isEmpty()){
            createFoodPreferences(database)
        }else {
            bus.post(FoodPreferencesObtained(list))
        }

    }

    suspend fun SaveFoodPreferences(foodPreferences: List<Preference>, database: PreferenceDatabase){
        for(i in foodPreferences){
            database.preferenceDao().updatePreference(i)
        }
    }

    suspend fun createFoodPreferences(database: PreferenceDatabase){

        val preferencesList:Map<Int, String> = mapOf(
                R.string.AlcoholFree to "alcohol-free",
                R.string.CeleryFree to "celery-free",
                R.string.DairyFree to "dairy-free",
                R.string.EggFree to "egg-free",
                R.string.FishFree to "fish-free",
                R.string.GlutenFree to "gluten-free",
                R.string.ImmunoSupportive to "immuno-supportive",
                R.string.KetoFriendly to "keto-friendly",
                R.string.KidneyFriendly to "kidney-friendly",
                R.string.Kosher to "kosher",
                R.string.LowPotassium to "low-potassium",
                R.string.LowSugar to "low-ugar",
                R.string.LupineFree to "lupine-free",
                R.string.MolluskFree to "mollusk-free",
                R.string.MustardFree to "mustard-free",
                R.string.Nooiladded to "no-oil-added",
                R.string.PeanutFree to "peanut-free",
                R.string.PorkFree to "pork-free",
                R.string.RedMeatFree to "red-meat-free",
                R.string.SesameFree to "sesame-free",
                R.string.ShellfishFree to "shellfish-free",
                R.string.SoyFree to "soy-free",
                R.string.SugarConscious to "sugar-conscious",
                R.string.SulfiteFree to "sulfite-free",
                R.string.TreeNutFree to "tree-nut-free",
                R.string.Vegan to "vegan",
                R.string.Vegetarian to "vegetarian",
                R.string.WheatFree to "wheat-free")

        for(i in preferencesList){
            val foodPreference = Preference(0,i.value,i.key,false)
            database.preferenceDao().insertAll(foodPreference)
        }

        val list = database.preferenceDao().getAll()
        Log.d(TAG, "createFoodPreferences: MutableMap: $list")
        bus.post(FoodPreferencesObtained(list))
    }

    class SomeModelActionEvent
    class ProductWasDeleted
    class ObtainedListOfProducts(val listOfProducts: MutableList<Product>)

    class ProductAddedToListDatabase(val numberOfProductsInList: Int)
    class ProductRemovedFromListDatabase(val numberOfProductsInList: Int)
    class ObtainedProductsForMeal(val productsForMeal: List<Product>)

    class FoodPreferencesObtained(val foodPreferences: List<Preference>)

}
