package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.AboutActivity
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.activities.FavouriteRecipesActivity
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity
import com.bbroda.expirydateguard.ui.activities.ProductScreenActivity
import com.bbroda.expirydateguard.ui.activities.RecipesScreenActivity
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.PreferenceDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsInListDatabase
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import com.bbroda.expirydateguard.ui.workers.NotificationWorker
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainMenuPresenter(val view: MainMenuView, val model: MainMenuModel, val activity: MainMenuActivity) {

    val db = ProductsDatabase.getDatabase(activity)
    val listDB = ProductsInListDatabase.getDatabase(activity)
    val preferenceDB = PreferenceDatabase.getDatabase(activity)

    init {
        activity.lifecycleScope.launch {
            model.downloadLanguageModelsIfNeeded()
        }

        val appContext = activity.applicationContext

        val sharedPreferences = activity.getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val notifications = sharedPreferences.getBoolean("Notifications",true)
        if(notifications){NotificationWorker.schedule(appContext, 9, 0, 0)}
    }

    @Subscribe
    fun onFabClicked(event: MainMenuView.AddProduct) {
        Log.d(TAG, "onSomeViewAction: adding new product")
        val intent = Intent(activity.baseContext, AddNewProductActivity::class.java)
        startActivity(activity, intent, null)
        activity.onPause()
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun onProductDeleted(event: MainMenuModel.ProductWasDeleted) {
        view.notifyAdapter()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUIInitiated(event: MainMenuView.InitRecyclerView) {
        Log.d(TAG, "onUIInitiated: XXXXXX")
        activity.lifecycleScope.launch {
            try{
                model.getListOfProducts(db)
            }
            catch (e: Exception) {
                // handler error
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun userWantsToDeleteProduct(event: MainMenuView.DeleteProduct) {
        activity.lifecycleScope.launch {
            try{
                model.removeProduct(db,event.product)
            }
            catch (e: Exception) {
                // handler error
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProductsObtained(event: MainMenuModel.ObtainedListOfProducts) {
        view.initRecyclerView(event.listOfProducts)
        Log.d(TAG, "onProductsObtained: XXXXXX")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshNeeded(event: MainMenuView.ReloadProducts) {
        activity.lifecycleScope.launch {
            try{
                model.getListOfProducts(db)
            }
            catch (e: Exception) {
                Log.d(TAG, "onRefreshNeeded: $e")
                // handler error
            }
        }
        Log.d(TAG, "onProductsRefreshed: XXXXXX")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProductAddedToDish(event: MainMenuView.AddProductToRecipe) {
        activity.lifecycleScope.launch {
            try{
                model.addProductToListDatabase(event.product, listDB)
            }catch (e:Exception){
                Log.d(TAG, "onProductAddedToDish: $e")
            }
        }
    }

    @Subscribe
    fun onProductRemovedFromDish(event: MainMenuView.DeleteProductFromDish){
        activity.lifecycleScope.launch {
            try{
                model.removeProductFromListDatabase(event.product, listDB)
            }catch (e:Exception){
                Log.d(TAG, "onProductAddedToDish: $e")
            }
        }
    }

    @Subscribe
    fun onProductAddedToListDatabase(event: MainMenuModel.ProductAddedToListDatabase){
        view.changeNumberOfProductsOnList(event.numberOfProductsInList)
    }

    @Subscribe
    fun onProductRemovedFromListDataBase(event: MainMenuModel.ProductRemovedFromListDatabase){
        view.changeNumberOfProductsOnList(event.numberOfProductsInList)
    }

    @Subscribe
    fun onClearListDatabase(event:MainMenuView.ClearProductsInListDatabase){
        activity.lifecycleScope.launch {
            try{
                model.clearListDatabase(listDB)
            }catch (e:Exception){
                Log.d(TAG, "onProductAddedToDish: $e")
            }
        }
    }

    @Subscribe
    //made for noticing that ui was changed and back button needs to change its functionality
    //1st use of back button doesn't close the app, it needs to bring back original ui
    fun onUserAddingProductsToMeal(event:MainMenuView.UserIsAddingProductsToMeal){
        val editor = activity.getSharedPreferences("UI", Context.MODE_PRIVATE).edit()
        editor.putBoolean("Recipies_1", true)
        editor.apply()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDetailsOpened(event: MainMenuView.OpenProductDetails) {
        Log.d(TAG, "onDetailsOpened: opening product details")
        val intent = Intent(activity.baseContext, ProductScreenActivity::class.java)
        intent.putExtra("UID",event.primaryKey)
        startActivity(activity, intent, null)
        activity.onPause()

    }


    @Subscribe
    fun resetSharedPref(event: MainMenuView.SetSharedPrefTo0){
        val editor = activity.getSharedPreferences("UI", Context.MODE_PRIVATE).edit()
        editor.putBoolean("Recipies_1", false)
        editor.apply()
    }

    @Subscribe
    fun changeAppLanguage(event: MainMenuView.ChangeLanguage) {
        val editor = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", event.lang)
        editor.apply()
        Log.d(TAG, "setLocate language: finish")

        activity.recreate()
    }

    @Subscribe
    fun onShowLanguageChoice(event: MainMenuView.ShowLanguageChoice){
        val listItmes = arrayOf(activity.getString(R.string.jezyk_pl), activity.getString(R.string.jezyk_en))
        Log.d(TAG, "onShowLanguageChoice: show!")
        view.showChangeLang(listItmes)
    }

    @Subscribe
    fun onProductsForMealApproved(event:MainMenuView.ApproveProductsForMeal){
        activity.lifecycleScope.launch {model.getProductsFromListDatabase(listDB) }

    }

    @Subscribe
    fun onProductsForMealObtained(event:MainMenuModel.ObtainedProductsForMeal){
        if (event.productsForMeal.isEmpty()){
            view.showToast(activity.getString(R.string.toast_brak_produktów))
        }else{
            var ingredients = ""
            for (i in event.productsForMeal){
                ingredients += " ${i.englishType.toString()}"
            }
            Log.d(TAG, "onProductsForMealObtained: Ingredients: $ingredients")

            Log.d(TAG, "onProductsForMealObtained: opening list of recipes")
            val intent = Intent(activity.baseContext, RecipesScreenActivity::class.java)
            intent.putExtra("ingredients", ingredients)
            intent.putExtra("ingredients_quantity", event.productsForMeal.size)
            startActivity(activity, intent, null)
            activity.onPause()
        }
    }

    @Subscribe
    fun onFoodPreferencesChanged(event: MainMenuView.StoreFoodPrefInSharedPref){
        activity.lifecycleScope.launch { model.SaveFoodPreferences(event.foodPreferences, preferenceDB) }
    }


    @Subscribe
    fun onFoodPreferencesDemand(event: MainMenuView.GetFoodPrefFromSharedPref){
        activity.lifecycleScope.launch { model.RetrieveFoodPreferences(preferenceDB) }

    }

    @Subscribe
    fun onFoodPreferencesRetrieved(event:MainMenuModel.FoodPreferencesObtained){
        view.showFoodPreferences(event.foodPreferences)
    }

    fun createFoodPreferences(){
       activity.lifecycleScope.launch{model.createFoodPreferences(preferenceDB)}
    }

    @Subscribe
    fun showAboutScreen(event: MainMenuView.ShowAboutScreen){
        val intent = Intent(activity.baseContext, AboutActivity::class.java)
        startActivity(activity, intent, null)
        activity.onPause()
    }

    @Subscribe
    fun onMyRecipiesClicked(event: MainMenuView.ShowMyRecipies){
        val intent = Intent(activity.baseContext, FavouriteRecipesActivity::class.java)
        startActivity(activity, intent, null)
        activity.onPause()
    }

    @Subscribe
    fun onShowNotificationOptions(event:MainMenuView.ShowNotificationOptions){
         model.retrieveNotificationPreferences(activity)
    }

    @Subscribe
    fun onNotificationSettingsObtained(event: MainMenuModel.NotificationPreferencesObtained){
        view.showNotificationOptions(event.isNotificationEnabled)
    }


}
