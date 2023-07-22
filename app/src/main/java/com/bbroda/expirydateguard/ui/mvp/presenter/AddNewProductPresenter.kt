package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe


class AddNewProductPresenter(val view: AddNewProductView, val model: AddNewProductModel, val activity: AddNewProductActivity) {

    val db = ProductsDatabase.getDatabase(activity)

    @Subscribe
    fun onSomeViewAction(event: AddNewProductView.NewProductAdded) {

        Log.d(TAG, "onSomeViewAction: XXXX - got new product")
        activity.lifecycleScope.launch {
            try {
                model.addNewProductToDatabase(event.date, event.name, db)
            } catch (e: Exception) {
                // handler error
            }
        }

    }

    @Subscribe
    fun onScanProduct(event: AddNewProductView.ScanProduct) {
        Log.d(TAG, "onSomeViewAction: XXXX - Scan product please!")
        scanBarcode()
        /*val intent = Intent(activity, OCRCameraActivity::class.java)
        ContextCompat.startActivity(activity, intent, null)
        activity.onPause()*/
    }

    @Subscribe
    fun onSomeModelAction(event: AddNewProductModel.ProductAdded) {
        Log.d(TAG, "onSomeModelAction: XXXX - ProductsAdded")
        activity.finish()
    }

    @Subscribe
    fun apiCallSuccessful(event: AddNewProductModel.BarcodeScanned){
        val result = event.response.body().toString()
        //On successful call i fetch name and type of scanned product from API
        try{
            val name = event.response.body()?.products?.get(0)?.product_name.toString()
            val type = event.response.body()?.products?.get(0)?.category_properties?.ciqual_food_name.toString()
            view.onApiSuccessfulCall(result, name, type)
        }
        catch (e: java.lang.Exception){
            view.displayToastOnApiFailure()
            Log.d(TAG, "apiCallSuccessfull: No name or something: $e")
        }
    }
    @Subscribe
    fun apiCallFailed(event: AddNewProductModel.BarcodeFailed){
        view.displayToastOnApiFailure()
    }

    fun scanBarcode(){
        val scanner = GmsBarcodeScanning.getClient(activity)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                val rawValue: String? = barcode.rawValue
                Log.d(TAG, "BARCODE xxx: $rawValue")
                //Toast.makeText(activity, "$rawValue", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Default).launch {
                    val mResult: Deferred<Unit> = async { model.makeApiCall(rawValue) }
                    mResult.await()
                }

            }
            .addOnCanceledListener {
                // Task canceled
                Toast.makeText(activity, "canceled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d(TAG, "BARCODE FAILURE xxxx: $e")
            }
    }


}
