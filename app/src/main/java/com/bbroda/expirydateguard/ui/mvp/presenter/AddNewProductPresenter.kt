package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.AddNewProductActivity
import com.bbroda.expirydateguard.ui.classes.ProductsDatabase
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe


class AddNewProductPresenter(val view: AddNewProductView, val model: AddNewProductModel, val activity: AddNewProductActivity) {

    val db = ProductsDatabase.getDatabase(activity)

    init{
        //It solves the problem of not opening scanner
        val moduleInstallClient = ModuleInstall.getClient(activity)
        val optionalModuleApi = GmsBarcodeScanning.getClient(activity)
        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    // Modules are present on the device...
                } else {
                    // Modules are not present on the device...
                    val moduleInstallRequest =
                        ModuleInstallRequest.newBuilder()
                            .addApi(optionalModuleApi)
                            // Add more APIs if you would like to request multiple optional modules.
                            // .addApi(...)
                            // Set the listener if you need to monitor the download progress.
                            // .setListener(listener)
                            .build()

                    moduleInstallClient
                        .installModules(moduleInstallRequest)
                        .addOnSuccessListener {
                            if (it.areModulesAlreadyInstalled()) {
                                // Modules are already installed when the request is sent.
                            }
                        }
                        .addOnFailureListener {
                            // Handle failure…
                            Log.d(TAG, "CANNOT INSTALL SCANNER: ")
                        }
                }
            }
            .addOnFailureListener {
                // Handle failure...
                Log.d(TAG, "CANNOT CHECK IF SCANNER IS INSTALLED: ")
            }
    }

    @Subscribe
    fun onSomeViewAction(event: AddNewProductView.NewProductAdded) {

        Log.d(TAG, "onSomeViewAction: XXXX - got new product")
        activity.lifecycleScope.launch {
            try {
                model.addNewProductToDatabase(event.date, event.name, event.type, db)
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
            var type = event.response.body()?.products?.get(0)?.category_properties?.ciqual_food_name.toString()
            if(type == "null"){
                type = ""
            }
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
