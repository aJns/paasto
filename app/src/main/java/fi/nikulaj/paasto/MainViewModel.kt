package fi.nikulaj.paasto

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(appContext: Context) : ViewModel() {

    var fastStart: Long? = null

    val mainModel by lazy {
        MainModel(appContext)
    }

    val buttonState: MutableLiveData<FastState> by lazy {
        MutableLiveData<FastState>()
    }

    fun checkState() {
        if (mainModel.hasOngoingFast()) {
            fastStart = mainModel.getOngoingFastStart()
            buttonState.postValue(FastState.FAST)
        } else {
            fastStart = null
            buttonState.postValue(FastState.EAT)
        }
    }

    fun getFastTime(): Long? {
        return if (fastStart != null) {
            System.currentTimeMillis() - fastStart!!
        } else {
            null
        }
    }

    fun startStopFast() {
        val currentTime = System.currentTimeMillis()
        if (mainModel.hasOngoingFast()) {
            mainModel.stopFastAt(currentTime)
        } else {
            mainModel.startFastAt(currentTime)
        }
        checkState()
    }

}