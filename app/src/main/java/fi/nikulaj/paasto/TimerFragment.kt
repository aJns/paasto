package fi.nikulaj.paasto

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.room.Room

class TimerFragment : Fragment() {

    var targetReachedView: TextView? = null
    var fastStart: Long? = null
    var targetTime: Long? = null

    var showRemaining: Boolean? = null
        get() {
            if (field == null) {
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                field = sharedPref.getBoolean(getString(R.string.countdown_mode_remaining), true)
            }
            return field
        }
        set(value) {
            if (field != value) {
                field = value
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.countdown_mode_remaining), value!!)
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
        val buttonObserver = Observer<FastState> { newState ->
            val newText = when (newState) {
                FastState.EAT -> R.string.fast_start
                FastState.FAST -> R.string.fast_stop
            }
            fastButton.text = getString(newText)
        }
        model.buttonState.observe(viewLifecycleOwner, buttonObserver)

        val fastStartTime = view.findViewById<Button>(R.id.startTime)
        val startTimeObserver = Observer<Long?> { newTime ->
            fastStartTime.text = getDateStringFromMillis(requireActivity(), newTime)
            fastStart = newTime
            updateTargetReachedTime()
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
            updateTargetReachedTime()
        }
        model.targetDuration.observe(viewLifecycleOwner, targetDurationObserver)

        fastTime = view.findViewById(R.id.fastTime)
        fastTime.setOnClickListener { showRemaining = !showRemaining!! }

        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)
                updateTime()
            }
        })

        targetReachedView = view.findViewById(R.id.targetReachedAt)
    }

    fun longToHMSString(time: Long?): String {
        return if (time != null) {
            val (hours, minutes, seconds) = millisToHMS(kotlin.math.abs(time))

            val timeFmt =
                if (!showRemaining!!) {
                    getString(R.string.timer_format)
                } else if (time > 0) {
                    getString(R.string.neg_timer_format)
                } else {
                    getString(R.string.pos_timer_format)
                }
            timeFmt.format(hours, minutes, seconds)
        } else {
            getString(R.string.num_invalid)
        }
    }

    fun updateTargetReachedTime() {
        val targetReachedAt =
            if (fastStart != null && targetTime != null) {
                fastStart!! + targetTime!!
            } else {
                null
            }
        targetReachedView!!.text = getDateStringFromMillis(requireActivity(), targetReachedAt)
    }

    fun updateTime() {
        val time = if (showRemaining!!) {
            model.getTimeToTarget()
        } else {
            model.getFastTime()
        }
        fastTime.text = longToHMSString(time)
    }

}