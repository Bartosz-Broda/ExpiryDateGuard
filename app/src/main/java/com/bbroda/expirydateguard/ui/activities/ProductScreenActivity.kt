package com.bbroda.expirydateguard.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.ProductScreenModel
import com.bbroda.expirydateguard.ui.mvp.presenter.ProductScreenPresenter
import com.bbroda.expirydateguard.ui.mvp.view.ProductScreenView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG

class ProductScreenActivity : AppCompatActivity() {

    private lateinit var presenter: ProductScreenPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_screen_activity)

        presenter = ProductScreenPresenter(
                ProductScreenView(this, bus),
                ProductScreenModel(bus),
                this
        )

        EventBus.getDefault().register(presenter)

        if (!bus.isRegistered(presenter)){
            Log.d(TAG, "onCreate: HEHEHEHEHEHEE")
            bus.register(presenter)
        }else{
            Log.d(TAG, "onCreate: PRESENTER REGISTERED")
        }

        bus.post(ProductScreenView.GetInfoAboutProduct())
        bus.post(ProductScreenView.ViewInit())

    }

    public override fun onResume() {
        super.onResume()
        if (!bus.isRegistered(presenter)){
            bus.register(presenter)
        }
    }

    public override fun onPause() {
        bus.unregister(presenter)
        super.onPause()
    }

    public override fun onDestroy() {

        super.onDestroy()
    }
}
