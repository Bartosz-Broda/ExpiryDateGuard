package com.bbroda.expirydateguard.ui.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.RecipeDetailModel
import com.bbroda.expirydateguard.ui.mvp.presenter.RecipeDetailPresenter
import com.bbroda.expirydateguard.ui.mvp.view.RecipeDetailView
import org.greenrobot.eventbus.EventBus

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var presenter: RecipeDetailPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product_activity)

        presenter = RecipeDetailPresenter(
            RecipeDetailView(this, bus),
            RecipeDetailModel(bus),
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
