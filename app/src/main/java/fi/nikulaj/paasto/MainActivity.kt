package fi.nikulaj.paasto

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    lateinit var fastTime: TextView
    lateinit var fastButton: Button

    private val model: MainViewModel by viewModels()

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fastTime = findViewById(R.id.fastTime)
        fastButton = findViewById(R.id.fastButton)

        val buttonObserver = Observer<FastState> { newState ->
            val newText = when (newState) {
                FastState.EAT -> R.string.fastStart
                FastState.FAST -> R.string.fastStop
            }
            fastButton.text = getString(newText)
        }
        model.buttonState.observe(this, buttonObserver)

        model.checkState()

        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 200)
                updateTime()
            }
        })
    }

    fun fastButtonClicked(view: View) {
        model.startStopFast()
    }

    fun updateTime() {
        val time = model.getFastTime()

        val timeString: String

        timeString = if (time != null) {
            val (hours, minutes, seconds) = millisToHMS(time)

            val timeFmt = getString(R.string.timerFormat)
            timeFmt.format(hours, minutes, seconds)
        } else {
            getString(R.string.numInvalid)
        }

        fastTime.text = timeString
    }
}
