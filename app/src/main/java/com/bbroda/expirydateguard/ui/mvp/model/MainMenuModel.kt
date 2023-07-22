package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.Product
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
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

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }

    class SomeModelActionEvent

    class ProductWasDeleted

    class ObtainedListOfProducts(val listOfProducts: MutableList<Product>)
}
