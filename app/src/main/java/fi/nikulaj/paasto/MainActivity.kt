package fi.nikulaj.paasto

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room

class MainActivity : AppCompatActivity() {
    companion object {
        var db: AppDatabase? = null
        fun getDatabase(): AppDatabase? {
            return db
        }
    }

    var fastStart: Long? = null
    var targetTime: Long? = null

    var showRemaining: Boolean? = null
        get() {
            if (field == null) {
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                field = sharedPref.getBoolean(getString(R.string.countdown_mode_remaining), true)
            }
            return field
        }
        set(value) {
            if (field != value) {
                field = value
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.countdown_mode_remaining), value!!)
                    apply()
                }
            }
        }

    lateinit var fastTime: TextView

    private val model: MainViewModel by viewModels()

    val handler = Handler()
    private val fragMan = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appBar))

        if (db == null) {
            db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "fast-db").build()
        }

        fastTime = findViewById(R.id.fastTime)

        val fastButton = findViewById<Button>(R.id.fastButton)
        val buttonObserver = Observer<FastState> { newState ->
            val newText = when (newState) {
                FastState.EAT -> R.string.fast_start
                FastState.FAST -> R.string.fast_stop
            }
            fastButton.text = getString(newText)
        }
        model.buttonState.observe(this, buttonObserver)

        val fastStartTime = findViewById<Button>(R.id.startTime)
        val startTimeObserver = Observer<Long?> { newTime ->
            fastStartTime.text = getDateStringFromMillis(this, newTime)
            fastStart = newTime
            updateTargetReachedTime()
        }
        model.fastStart.observe(this, startTimeObserver)

        val targetDuration = findViewById<Button>(R.id.targetDuration)
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
        model.targetDuration.observe(this, targetDurationObserver)

        model.checkState()

        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)
                updateTime()
            }
        })
    }

    fun fastButtonClicked(view: View) {
        if (model.hasOngoingFast() == true) {
            EndFastDialog.show(fragMan, EndFastDialog.tag)
        } else {
            model.startStopFast()
        }
    }

    fun startTimeClicked(view: View) {
        val dateTimePicker = DateTimePickerDialog()
        val callback: (Long) -> Unit = { newStart: Long ->
            model.fastStart.value = newStart
        }
        dateTimePicker.showWithCallback(fragMan, dateTimePicker.tag, fastStart!!, callback)
    }

    fun fastTargetClicked(view: View) {
        val fastDurDiag = FastDurationDialog()
        fastDurDiag.show(fragMan, fastDurDiag.tag)
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
        val targetReachedView = findViewById<TextView>(R.id.targetReachedAt)
        val targetReachedAt =
                if (fastStart != null && targetTime != null) {
                    fastStart!! + targetTime!!
                } else {
                    null
                }
        targetReachedView.text = getDateStringFromMillis(this, targetReachedAt)
    }

    fun updateTime() {
        val time = if (showRemaining!!) {
            model.getTimeToTarget()
        } else {
            model.getFastTime()
        }
        fastTime.text = longToHMSString(time)
    }


    fun toggleCountdownMode(view: View) {
        showRemaining = !showRemaining!!
    }

}
