package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.RecipesScreenModel
import com.bbroda.expirydateguard.ui.mvp.presenter.RecipesScreenPresenter
import com.bbroda.expirydateguard.ui.mvp.view.RecipesScreenView
import org.greenrobot.eventbus.EventBus

class RecipesScreenActivity : AppCompatActivity() {

    private lateinit var presenter: RecipesScreenPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipes_screen_activity)

        presenter = RecipesScreenPresenter(
            RecipesScreenView(this, bus),
            RecipesScreenModel(bus, this),
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
