package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.mainMenuActivity
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.mainMenuModel
import com.bbroda.expirydateguard.ui.mvp.model.mainMenuModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.mainMenuView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class mainMenuPresenter(val view: mainMenuView, val model: mainMenuModel, val activity: mainMenuActivity) {

    val db = ProductsDatabase.getDatabase(activity)

    @Subscribe
    fun onSomeViewAction(event: mainMenuView.SomeViewActionEvent) {
        Log.d(TAG, "onSomeViewAction: XXXXXX")
        model.doSomething()
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun onSomeModelAction(event: SomeModelActionEvent) {
        view.doSomething()
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun onProductDeleted(event: mainMenuModel.ProductWasDeleted) {
        view.notifyAdapter()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUIInitiated(event: mainMenuView.InitRecyclerView) {
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
    fun userWantsToDeleteProduct(event: mainMenuView.DeleteProduct) {
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
    fun onProductsObtained(event: mainMenuModel.ObtainedListOfProducts) {
        view.initRecyclerView(event.listOfProducts)
        Log.d(TAG, "onProductsObtained: XXXXXX")
    }

}
