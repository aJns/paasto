package fi.nikulaj.paasto

import ViewAdapter
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    companion object {
        var db: AppDatabase? = null
        fun getDatabase(): AppDatabase? {
            return db
        }
    }

    private var fastStart: Long? = null
    private val model: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appBar))

        if (db == null) {
            db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "fast-db").build()
        }

        val startTimeObserver = Observer<Long?> { newTime ->
            fastStart = newTime
        }
        model.fastStart.observe(this, startTimeObserver)

        model.checkState()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = ViewAdapter(this)
    }

    fun fastButtonClicked(view: View) {
        if (model.hasOngoingFast() == true) {
            EndFastDialog.show(supportFragmentManager, EndFastDialog.tag)
        } else {
            model.startStopFast()
        }
    }

    fun startTimeClicked(view: View) {
        val dateTimePicker = DateTimePickerDialog()
        val callback: (Long) -> Unit = { newStart: Long ->
            model.changeFastStartTime(newStart)
        }
        dateTimePicker.showWithCallback(supportFragmentManager, dateTimePicker.tag, fastStart!!, callback)
    }

    fun fastTargetClicked(view: View) {
        val fastDurDiag = FastDurationDialog()
        fastDurDiag.show(supportFragmentManager, fastDurDiag.tag)
    }

}
