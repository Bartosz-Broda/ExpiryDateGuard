package com.bbroda.expirydateguard.ui.mvp.model

import android.util.Log
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.time.LocalDate

class ProductScreenModel (var bus: EventBus) {

    suspend fun getProductInfo(uid: Int, database: ProductsDatabase){
        val product: Product = database.productDao().loadByID(uid)
        bus.post(ProductInfoFetched(product.name,product.type,product.expiryDate.toString(),product.ingredients, product.nutriments, product.imageUrl))
    }


    suspend fun changeProductInfo(productName: String?, productType: String?, expiryDate:LocalDate, uid: Int, database: ProductsDatabase){

        try{
            val product: Product = database.productDao().loadByID(uid)
            product.name = productName
            product.type = productType
            product.expiryDate = expiryDate
            database.productDao().updateProduct(product)
            bus.post(ProductInfoFetched(product.name,product.type,product.expiryDate.toString(),product.ingredients,product.nutriments,product.imageUrl))
            //bus.post(ProductInfoChanged(true))
        }catch (e:java.lang.Exception){
            Log.d(TAG, "changeProductInfo: $e")
            bus.post(ProductInfoNotChanged())
        }
    }

    class ProductInfoFetched(val productName:String?, val productType:String?, val expiryDate:String, val ingredients:String?, val nutritionInfo:String?, val imageUrl:String?)
    class ProductInfoNotChanged()
}
