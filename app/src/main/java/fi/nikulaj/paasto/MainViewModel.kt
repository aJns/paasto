package fi.nikulaj.paasto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var fastStart: Long? = null
    private var fastOngoing: Boolean? = null

    val buttonState: MutableLiveData<FastState> by lazy {
        MutableLiveData<FastState>()
    }

    fun checkState() {
        viewModelScope.launch {
            if (MainModel.hasOngoingFast()) {
                fastStart = MainModel.getOngoingFastStart()
                buttonState.postValue(FastState.FAST)
                fastOngoing = true
            } else {
                fastStart = null
                buttonState.postValue(FastState.EAT)
                fastOngoing = false
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

    fun hasOngoingFast(): Boolean? {
        return fastOngoing
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