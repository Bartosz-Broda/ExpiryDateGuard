package com.bbroda.expirydateguard.ui.mvp.model

import com.github.marcherdiego.mvp.events.model.BaseEventsModel

class AddNewProductModel : BaseEventsModel() {

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }

    class SomeModelActionEvent
}
