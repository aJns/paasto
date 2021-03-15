package fi.nikulaj.paasto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var fastOngoing: Boolean? = null
    var lastFastStop: Long? = null

    val timerState: MutableLiveData<FastState> by lazy {
        MutableLiveData<FastState>()
    }
    val fastStart: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>()
    }
    val targetDuration: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>()
    }

    val fastLog: MutableLiveData<Array<Fast>> by lazy {
        MutableLiveData<Array<Fast>>()
    }

    private val model: MainModel = MainModel.getModelInstance(application)

    val reminderManager = ReminderManager(application)

    fun checkState() {  // TODO: problematic, refactor
        viewModelScope.launch {
            targetDuration.value = when (model.targetDuration) {
                null -> model.getTargetDurationFromDb()
                else -> model.targetDuration
            }
            fastLog.value = getAllFinishedFasts()

            lastFastStop = model.getLastFast()?.stopTime

            val ongoing = model.hasOngoingFast()
            if (ongoing) {
                fastStart.value = model.getOngoingFastStart()
                timerState.postValue(FastState.FAST)
            } else {
                fastStart.value = null
                timerState.postValue(FastState.EAT)
            }
            fastOngoing = ongoing
        }
    }

    fun getFastTime(): Long? {
        return if (fastStart.value != null) {
            System.currentTimeMillis() - fastStart.value!!
        } else {
            null
        }
    }

    fun getTimeToTarget(): Long? {
        return if (fastStart.value != null && targetDuration.value != null) {
            val targetEnd = fastStart.value!! + targetDuration.value!!
            targetEnd - System.currentTimeMillis()
        } else {
            null
        }
    }

    fun changeFastStartTime(newTime: Long) {
        viewModelScope.launch {
            model.setOngoingFastStart(newTime)
            checkState()
        }
    }

    fun setFastTarget(target: Long) {
        targetDuration.value = target
        model.targetDuration = target
    }

    fun hasOngoingFast(): Boolean? {
        return fastOngoing
    }

    fun startStopFast() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            model.startFastAt(currentTime)
            checkState()
        }
    }

    fun saveFast(start: Long, stop: Long) {
        viewModelScope.launch {
            model.setOngoingFastStart(start)
            model.stopFastAt(stop)
            checkState()
        }
    }

    private suspend fun getAllFinishedFasts() = model.getAllFinishedFasts()
}