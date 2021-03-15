package fi.nikulaj.paasto

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlin.math.abs

class TimerFragment : Fragment() {

    var targetReachedView: TextView? = null
    var fastStart: Long? = null
    var targetTime: Long? = null
    var fastState: FastState? = null

    private val countDownModeKey = "countdown_mode_remaining"

    private var showRemaining: Boolean? = null
        get() {
            if (field == null) {
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                field = sharedPref.getBoolean(countDownModeKey, true)
            }
            return field
        }
        set(value) {
            if (field != value) {
                field = value
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean(countDownModeKey, value!!)
                    apply()
                }
            }
        }

    lateinit var fastTime: TextView

    private val model: MainViewModel by activityViewModels()

    val handler = Handler()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fastButton = view.findViewById<Button>(R.id.fastButton)
        val noFastLabel = view.findViewById<TextView>(R.id.timeSinceLastLabel)
        val buttonObserver = Observer<FastState> { newState ->
            val (newText, tooltipVis) = when (newState) {
                FastState.EAT -> Pair(R.string.fast_start, View.VISIBLE)
                FastState.FAST -> Pair(R.string.fast_stop, View.INVISIBLE)
            }
            fastButton.text = getString(newText)
            noFastLabel.visibility = tooltipVis
            fastState = newState
        }
        model.buttonState.observe(viewLifecycleOwner, buttonObserver)

        val fastStartTime = view.findViewById<Button>(R.id.startTime)
        val startTimeObserver = Observer<Long?> { newTime ->
            fastStartTime.text = getDateStringFromMillis(requireActivity(), newTime)
            fastStart = newTime
        }
        model.fastStart.observe(viewLifecycleOwner, startTimeObserver)

        val targetDuration = view.findViewById<Button>(R.id.targetDuration)
        val targetDurationObserver = Observer<Long?> { newTarget ->
            targetDuration.text = if (newTarget != null) {
                val (hours, _, _) = millisToHMS(newTarget)
                val timeFmt = getString(R.string.hour_format)
                timeFmt.format(hours)
            } else {

                getString(R.string.num_invalid)
            }
            targetTime = newTarget
        }
        model.targetDuration.observe(viewLifecycleOwner, targetDurationObserver)

        fastTime = view.findViewById(R.id.fastTime)
        fastTime.setOnClickListener { showRemaining = !showRemaining!! }

        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)
                updateTime()
                updateTargetReachedTime()
            }
        })

        targetReachedView = view.findViewById(R.id.targetReachedAt)
    }

    private fun longToHMSString(time: Long?, useNeutralFormat: Boolean = false): String {
        return if (time != null) {
            val (hours, minutes, seconds) = millisToHMS(kotlin.math.abs(time))

            val timeFmt = when {
                useNeutralFormat -> {
                    getString(R.string.timer_format)
                }
                time < 0 -> {
                    getString(R.string.neg_timer_format)
                }
                else -> {
                    getString(R.string.pos_timer_format)
                }
            }
            timeFmt.format(hours, minutes, seconds)
        } else {
            getString(R.string.num_invalid)
        }
    }

    private fun updateTargetReachedTime() {
        val targetReachedAt =
            when (targetTime) {
                null -> null
                else -> when (fastStart) {
                    null -> System.currentTimeMillis() + targetTime!!
                    else -> fastStart!! + targetTime!!
                }
            }
        targetReachedView!!.text = getDateStringFromMillis(requireActivity(), targetReachedAt)
    }

    fun updateTime() {
        var neutral = false
        val time = when (fastState) {
            FastState.FAST ->
                if (showRemaining!!) {
                    when (val timeToTarget = model.getTimeToTarget()) {
                        null -> null
                        else -> -timeToTarget
                    }
                } else {
                    neutral = true
                    model.getFastTime()
                }
            FastState.EAT -> when (model.lastFastStop) {
                null -> null
                else -> {
                    neutral = true
                    abs(System.currentTimeMillis() - model.lastFastStop!!)
                }
            }
            else -> null
        }
        fastTime.text = longToHMSString(time, neutral)
    }

}