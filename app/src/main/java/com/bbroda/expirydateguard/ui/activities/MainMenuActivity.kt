package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel
import com.bbroda.expirydateguard.ui.mvp.presenter.MainMenuPresenter
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import org.greenrobot.eventbus.EventBus


class MainMenuActivity : AppCompatActivity() {

    private lateinit var presenter: MainMenuPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)

        presenter = MainMenuPresenter(
            MainMenuView(this, bus),
            MainMenuModel(bus),
            this
        )

        EventBus.getDefault().register(presenter)
        bus.post(MainMenuView.InitRecyclerView())

        //można dać opcję z sharedpref - jeśli otwarte menu boczcne to backpress robi tylko reload main menu
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Whatever you want
                // when back pressed
                println("Back button pressed")
                finish()
            }
        })

    }

    public override fun onResume() {
        super.onResume()
        if (!bus.isRegistered(presenter)){
            bus.register(presenter)
        }
        bus.post(MainMenuView.ReloadProducts())
    }

    public override fun onPause() {
        bus.unregister(presenter)
        super.onPause()
    }

    public override fun onDestroy() {

        super.onDestroy()
    }

}
