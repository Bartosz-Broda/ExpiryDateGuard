package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.OCRCameraModel
import com.bbroda.expirydateguard.ui.mvp.presenter.OCRCameraPresenter
import com.bbroda.expirydateguard.ui.mvp.view.OCRCameraView
import org.greenrobot.eventbus.EventBus

class OCRCameraActivity : AppCompatActivity() {

    private lateinit var presenter: OCRCameraPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product_activity)

        presenter = OCRCameraPresenter(
            OCRCameraView(this, bus),
            OCRCameraModel(bus),
            this
        )

        EventBus.getDefault().register(presenter)
        //bus.post(MainMenuView.InitRecyclerView())
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
