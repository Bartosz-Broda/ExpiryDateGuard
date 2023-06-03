package com.bbroda.expirydateguard.ui.activities

import android.os.Bundle


import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.presenter.AddNewProductPresenter
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.github.marcherdiego.mvp.events.activity.BaseActivity

class AddNewProductActivity : BaseActivity<AddNewProductPresenter>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_product_activity)

        presenter = AddNewProductPresenter(
                AddNewProductView(this),
                AddNewProductModel()
        )
    }
}
