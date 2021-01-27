package fi.nikulaj.paasto

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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

    val fastEndDialog: AlertDialog? by lazy {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(R.string.confirm_fast_end)
            setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialog, id ->
                        model.startStopFast()
                    })
            setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog, do nothing
                    })
        }
        builder.create()
    }

    lateinit var fastTime: TextView
    lateinit var fastButton: Button

    private val model: MainViewModel by viewModels()

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (db == null) {
            db =
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "fast-db").build()
        }

        fastTime = findViewById(R.id.fastTime)
        fastButton = findViewById(R.id.fastButton)

        val buttonObserver = Observer<FastState> { newState ->
            val newText = when (newState) {
                FastState.EAT -> R.string.fast_start
                FastState.FAST -> R.string.fast_stop
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
        if (model.hasOngoingFast() == true) {
            fastEndDialog?.show()
        } else {
            model.startStopFast()
        }
    }

    fun updateTime() {
        val time = model.getFastTime()

        val timeString: String

        timeString = if (time != null) {
            val (hours, minutes, seconds) = millisToHMS(time)

            val timeFmt = getString(R.string.timer_format)
            timeFmt.format(hours, minutes, seconds)
        } else {
            getString(R.string.num_invalid)
        }

        fastTime.text = timeString
    }
}
