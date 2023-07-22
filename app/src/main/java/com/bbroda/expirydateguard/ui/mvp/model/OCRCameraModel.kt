package com.bbroda.expirydateguard.ui.mvp.model

import org.greenrobot.eventbus.EventBus

class OCRCameraModel(var bus:EventBus) {

    fun doSomething() {
        //Heavy work and then... notify the Presenter
        bus.post(SomeModelActionEvent())
    }

    class SomeModelActionEvent
}
