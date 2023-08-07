package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity
import com.bbroda.expirydateguard.ui.activities.ProductScreenActivity
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainMenuPresenter(val view: MainMenuView, val model: MainMenuModel, val activity: MainMenuActivity) {

    val db = ProductsDatabase.getDatabase(activity)

    init {

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
                // handler error
            }
        }
        Log.d(TAG, "onProductsRefreshed: XXXXXX")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProductAddedToDish(event: MainMenuView.AddProductToDish) {
        TODO("Tworzenie listy w bazie i dodawanie do niej tego produktu")
        Log.d(TAG, "onProductsObtained: XXXXXX")
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
        view.showChangeLang(listItmes)
    }


}
