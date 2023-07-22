package com.bbroda.expirydateguard.ui.mvp.presenter

import com.bbroda.expirydateguard.ui.activities.OCRCameraActivity
import com.bbroda.expirydateguard.ui.mvp.model.OCRCameraModel
import com.bbroda.expirydateguard.ui.mvp.model.OCRCameraModel.SomeModelActionEvent
import com.bbroda.expirydateguard.ui.mvp.view.OCRCameraView
import com.bbroda.expirydateguard.ui.mvp.view.OCRCameraView.SomeViewActionEvent
import org.greenrobot.eventbus.Subscribe

class OCRCameraPresenter(val view: OCRCameraView, val model: OCRCameraModel, val activity: OCRCameraActivity) {

    @Subscribe
    fun onSomeViewAction(event: SomeViewActionEvent) {
        model.doSomething()
    }

    @Subscribe
    fun onSomeModelAction(event: SomeModelActionEvent) {
        view.doSomething()
    }
}
