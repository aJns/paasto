package fi.nikulaj.paasto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var fastStart: Long? = null

    val buttonState: MutableLiveData<FastState> by lazy {
        MutableLiveData<FastState>()
    }

    fun checkState() {
        viewModelScope.launch {
            if (MainModel.hasOngoingFast()) {
                fastStart = MainModel.getOngoingFastStart()
                buttonState.postValue(FastState.FAST)
            } else {
                fastStart = null
                buttonState.postValue(FastState.EAT)
            }
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
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            if (MainModel.hasOngoingFast()) {
                MainModel.stopFastAt(currentTime)
            } else {
                MainModel.startFastAt(currentTime)
            }
            checkState()
        }
    }

}