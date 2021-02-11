package fi.nikulaj.paasto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var fastOngoing: Boolean? = null

    val buttonState: MutableLiveData<FastState> by lazy {
        MutableLiveData<FastState>()
    }
    val fastStart: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>()
    }
    val targetDuration: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>()
    }

    fun checkState() {
        viewModelScope.launch {
            val ongoing = MainModel.hasOngoingFast()
            if (ongoing) {
                fastStart.value = MainModel.getOngoingFastStart()
                buttonState.postValue(FastState.FAST)
            } else {
                fastStart.value = null
                buttonState.postValue(FastState.EAT)
            }
            fastOngoing = ongoing
        }
        targetDuration.value = 18 * 60 * 60 * 1000
    }

    fun getFastTime(): Long? {
        return if (fastStart.value != null) {
            System.currentTimeMillis() - fastStart.value!!
        } else {
            null
        }
    }

    fun getTimeToTarget(): Long? {
        return if (fastStart.value != null) {
            val targetEnd = fastStart.value!! + targetDuration.value!!
            targetEnd - System.currentTimeMillis()
        } else {
            null
        }
    }

    fun changeFastStartTime(newTime: Long) {
        viewModelScope.launch {
            MainModel.setOngoingFastStart(newTime)
            checkState()
        }
    }

    fun hasOngoingFast(): Boolean? {
        return fastOngoing
    }

    fun startStopFast() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            MainModel.startFastAt(currentTime)
            checkState()
        }
    }

    fun saveFast(start: Long, stop: Long)
    {
        viewModelScope.launch {
            MainModel.setOngoingFastStart(start)
            MainModel.stopFastAt(stop)
            checkState()
        }
    }

}