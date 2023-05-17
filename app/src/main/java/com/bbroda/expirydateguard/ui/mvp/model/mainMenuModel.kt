package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.Product
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import org.greenrobot.eventbus.EventBus
import java.time.LocalDate

//import com.nerdscorner.mvplib.events.model.BaseEventsModel

class mainMenuModel(var bus: EventBus){


    fun getListOfProducts(database: ProductsDatabase){

        Log.d(TAG, "getListOfProducts: XXXXXX")


        val product = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))
        val product2 = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))
        val product3 = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))
        val product4 = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))
        val product5 = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))


        database.productDao().insertAll(product, product2, product3, product4, product5)

        bus.post(ObtainedListOfProducts(database.productDao().getAll() as MutableList<Product>))
    }

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }

    class SomeModelActionEvent

    class ObtainedListOfProducts(val listOfProducts: MutableList<Product>)
}
