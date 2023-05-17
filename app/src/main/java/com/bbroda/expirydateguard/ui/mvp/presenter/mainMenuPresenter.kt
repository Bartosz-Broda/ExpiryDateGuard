package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.ui.activities.mainMenuActivity
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.mainMenuModel
import com.bbroda.expirydateguard.ui.mvp.model.mainMenuModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.mainMenuView
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUIInitiated(event: mainMenuView.InitRecyclerView) {
        Log.d(TAG, "onUIInitiated: XXXXXX")
        model.getListOfProducts(db)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProductsObtained(event: mainMenuModel.ObtainedListOfProducts) {
        view.initRecyclerView(event.listOfProducts)
        Log.d(TAG, "onProductsObtained: XXXXXX")
    }

}
