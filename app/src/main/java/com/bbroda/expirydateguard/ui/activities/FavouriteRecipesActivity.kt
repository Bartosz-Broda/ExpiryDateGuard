package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.FavouriteRecipesModel
import com.bbroda.expirydateguard.ui.mvp.presenter.FavouriteRecipesPresenter
import com.bbroda.expirydateguard.ui.mvp.view.FavouriteRecipesView
import org.greenrobot.eventbus.EventBus

class FavouriteRecipesActivity : AppCompatActivity() {

    private lateinit var presenter: FavouriteRecipesPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourite_recipes_activity)

        presenter = FavouriteRecipesPresenter(
            FavouriteRecipesView(this, bus),
            FavouriteRecipesModel(bus),
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
