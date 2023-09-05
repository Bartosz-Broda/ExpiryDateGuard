package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.presenter.AddNewProductPresenter
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import org.greenrobot.eventbus.EventBus

class AddNewProductActivity : AppCompatActivity() {

    private lateinit var presenter: AddNewProductPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product_activity)

        presenter = AddNewProductPresenter(
                AddNewProductView(this, bus),
                AddNewProductModel(bus),
                this
        )

        EventBus.getDefault().register(presenter)

        bus.post(AddNewProductView.ViewInint())
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
        Log.d(EventBus.TAG, "onDestroy: ACTIVITY NEWPRODUCT DESTROY")
        super.onDestroy()
    }

    override fun onStop() {
        Log.d(EventBus.TAG, "onDestroy: ACTIVITY NEWPRODUCT STOP")
        super.onStop()
    }
}
