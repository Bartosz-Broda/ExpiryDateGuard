package com.bbroda.expirydateguard.ui.mvp.model

import org.greenrobot.eventbus.EventBus

class RecipeDetailModel(var bus:EventBus) {

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }

    class SomeModelActionEvent
}
