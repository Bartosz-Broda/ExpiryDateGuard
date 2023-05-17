package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.Product
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import org.greenrobot.eventbus.EventBus
import java.time.LocalDate

//import com.nerdscorner.mvplib.events.model.BaseEventsModel

class mainMenuModel(var bus: EventBus){


    suspend fun getListOfProducts(database: ProductsDatabase){

        Log.d(TAG, "getListOfProducts: XXXXXX")


        val product = Product(0,"jabłka", LocalDate.of( 1985 , 1 , 1 ))
        val product2 = Product(0,"pomarańcze", LocalDate.of( 1985 , 1 , 1 ))
        val product3 = Product(0,"łosoś", LocalDate.of( 1985 , 1 , 1 ))
        val product4 = Product(0,"pomidory", LocalDate.of( 1985 , 1 , 1 ))
        val product5 = Product(0,"szynka", LocalDate.of( 1985 , 1 , 1 ))

        database.productDao().insertAll(product, product2, product3, product4, product5)

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
