package fi.nikulaj.paasto

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import java.io.DataInput
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        var db: AppDatabase? = null
        fun getDatabase(): AppDatabase? {
            return db
        }
    }


    lateinit var fastTime: TextView
    lateinit var fastButton: Button
    lateinit var fastStartTime: Button
    lateinit var targetDuration: Button

    private val model: MainViewModel by viewModels()

    val handler = Handler()
    private val fragMan = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appBar))

        if (db == null) {
            db =
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "fast-db").build()
        }

        fastTime = findViewById(R.id.fastTime)
        fastButton = findViewById(R.id.fastButton)
        fastStartTime = findViewById(R.id.startTime)
        targetDuration = findViewById(R.id.targetDuration)

        val buttonObserver = Observer<FastState> { newState ->
            val newText = when (newState) {
                FastState.EAT -> R.string.fast_start
                FastState.FAST -> R.string.fast_stop
            }
            fastButton.text = getString(newText)
        }
        model.buttonState.observe(this, buttonObserver)

        val startTimeObserver = Observer<Long?> { newTime ->
            fastStartTime.text = if (newTime != null) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = newTime
                val fmtr = SimpleDateFormat.getDateTimeInstance()

                fmtr.format(cal.time)
            }
            else {

                getString(R.string.num_invalid)
            }
        }
        model.fastStart.observe(this, startTimeObserver)

        val targetDurationObserver = Observer<Long?> { newTarget ->
            targetDuration.text = if (newTarget != null) {
                val (hours, _, _) = millisToHMS(newTarget)
                val timeFmt = getString(R.string.hour_format)
                timeFmt.format(hours)
            } else {

                getString(R.string.num_invalid)
            }
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
        dateTimePicker.show(fragMan, dateTimePicker.tag)
    }

    fun longToHMSString(time: Long?): String {
        return if (time != null) {
            val (hours, minutes, seconds) = millisToHMS(kotlin.math.abs(time))

            val timeFmt = if (time > 0) {
                getString(R.string.neg_timer_format)
            } else {
                getString(R.string.pos_timer_format)
            }
            timeFmt.format(hours, minutes, seconds)
        } else {
            getString(R.string.num_invalid)
        }
    }

    fun updateTime() {
        val time = model.getTimeToTarget()
        fastTime.text = longToHMSString(time)
    }
}
