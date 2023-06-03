package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainMenuPresenter(val view: MainMenuView, val model: MainMenuModel, val activity: MainMenuActivity) {

    val db = ProductsDatabase.getDatabase(activity)

    @Subscribe
    fun onFabClicked(event: MainMenuView.AddProduct) {
        Log.d(TAG, "onSomeViewAction: adding new product")
        val intent = Intent(activity, AddNewProductActivity::class.java)
        startActivity(activity, intent, null)
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun onSomeModelAction(event: SomeModelActionEvent) {
        view.doSomething()
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

}
