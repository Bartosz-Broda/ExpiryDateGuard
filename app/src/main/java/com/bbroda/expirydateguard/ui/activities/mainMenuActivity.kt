package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.mainMenuModel
import com.bbroda.expirydateguard.ui.mvp.presenter.mainMenuPresenter
import com.bbroda.expirydateguard.ui.mvp.view.mainMenuView
import org.greenrobot.eventbus.EventBus


class mainMenuActivity : AppCompatActivity() {


    private lateinit var presenter: mainMenuPresenter
    private val bus = EventBus.getDefault()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)

        presenter = mainMenuPresenter(
            mainMenuView(this, bus),
            mainMenuModel(bus),
            this
        )

        EventBus.getDefault().register(presenter)
        bus.post(mainMenuView.InitRecyclerView())
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
