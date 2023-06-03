package com.bbroda.expirydateguard.ui.mvp.presenter

import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel
import com.bbroda.expirydateguard.ui.mvp.model.AddNewProductModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView
import com.bbroda.expirydateguard.ui.mvp.view.AddNewProductView.SomeViewActionEvent
import com.github.marcherdiego.mvp.events.presenter.BaseActivityPresenter
import org.greenrobot.eventbus.Subscribe

class AddNewProductPresenter(view: AddNewProductView, model: AddNewProductModel) :
    BaseActivityPresenter<AddNewProductView, AddNewProductModel>(view, model) {

    @Subscribe
    fun onSomeViewAction(event: SomeViewActionEvent) {
        model.doSomething()
    }

    @Subscribe
    fun onSomeModelAction(event: SomeModelActionEvent) {
        view.doSomething()
    }
}
