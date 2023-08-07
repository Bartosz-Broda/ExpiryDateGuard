package com.bbroda.expirydateguard.ui.mvp.presenter

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.ProductScreenActivity
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.ProductScreenModel
import com.bbroda.expirydateguard.ui.mvp.view.ProductScreenView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus.TAG
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.properties.Delegates


class ProductScreenPresenter(val view: ProductScreenView, val model: ProductScreenModel, val activity:ProductScreenActivity) {

    var uid by Delegates.notNull<Int>()
    val db = ProductsDatabase.getDatabase(activity)

    init{
        val extras = activity.intent.extras
        if (extras != null) {
            uid = extras.getInt("UID")
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun viewDemandsProductInfo(event: ProductScreenView.GetInfoAboutProduct) {
        activity.lifecycleScope.launch {
            try{
                model.getProductInfo(uid,db)
            }
            catch (e: Exception) {
                Log.d(TAG, "onViewDemandsProductInfo: $e")
            }
        }

    }

    @Subscribe
    fun onProductInfoFetched(event: ProductScreenModel.ProductInfoFetched) {
        view.initiateUI(event.productName,event.productType,event.expiryDate,event.ingredients,event.nutritionInfo,event.imageUrl, activity)
    }


    @Subscribe
    fun tryingToChangeProductInfo(event: ProductScreenView.SaveNewProductInfo){
        activity.lifecycleScope.launch {
            model.changeProductInfo(event.productName,event.productType,event.expiryDate,uid,db)
        }
    }

    @Subscribe
    fun productInfoCallback(event: ProductScreenModel.ProductInfoNotChanged){
        view.productInfoWasntChanged()
    }
}
