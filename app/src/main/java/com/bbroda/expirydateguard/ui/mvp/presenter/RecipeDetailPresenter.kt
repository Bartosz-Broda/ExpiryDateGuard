package com.bbroda.expirydateguard.ui.mvp.presenter

import com.bbroda.expirydateguard.ui.activities.RecipeDetailsActivity
import com.bbroda.expirydateguard.ui.mvp.model.RecipeDetailModel
import com.bbroda.expirydateguard.ui.mvp.model.RecipeDetailModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.RecipeDetailView
import com.bbroda.expirydateguard.ui.mvp.view.RecipeDetailView.SomeViewActionEvent
import org.greenrobot.eventbus.Subscribe

class RecipeDetailPresenter(val view: RecipeDetailView, val model: RecipeDetailModel, val activity: RecipeDetailsActivity) {

    @Subscribe
    fun onSomeViewAction(event: SomeViewActionEvent) {
        model.doSomething()
    }

    @Subscribe
    fun onSomeModelAction(event: SomeModelActionEvent) {
        view.doSomething()
    }
}
