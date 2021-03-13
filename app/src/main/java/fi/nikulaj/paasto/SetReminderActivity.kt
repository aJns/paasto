package fi.nikulaj.paasto

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.lang.NumberFormatException

class SetReminderActivity : AppCompatActivity() {
    private val TAG = "SetReminderActivity"
    private var feedingTimeInput: EditText? = null
    private var feedingTimeLabel: TextView? = null

    private val reminderManager = ReminderManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setSupportActionBar(findViewById(R.id.appBar2))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        feedingTimeLabel = findViewById(R.id.feedingTimeLabel)
        feedingTimeInput = findViewById(R.id.feedingTimeTextIn)

        setupFastEndReminderSwitch(findViewById(R.id.fastEndReminderSwitch))
        setupFastStartReminderSwitch(findViewById(R.id.fastStartReminderSwitch))
        setupFeedingTimeIn(feedingTimeInput!!)

        reminderManager.scheduleNotifications()
    }

    private fun setupFastEndReminderSwitch(switch: SwitchCompat) {
        switch.setOnCheckedChangeListener { _, isOn ->
            reminderManager.notifyFastEnd = isOn
            reminderManager.scheduleNotifications()
        }
        switch.isChecked = reminderManager.notifyFastEnd ?: false
    }

    private fun setupFastStartReminderSwitch(switch: SwitchCompat) {
        switch.setOnCheckedChangeListener { _, isOn ->
            reminderManager.notifyFastStart = isOn
            feedingTimeInput!!.isEnabled = isOn
            feedingTimeLabel!!.isEnabled = isOn
            reminderManager.scheduleNotifications()
        }

        switch.isChecked = reminderManager.notifyFastStart ?: false
        feedingTimeInput!!.isEnabled = switch.isChecked
        feedingTimeLabel!!.isEnabled = switch.isChecked
    }

    private fun setupFeedingTimeIn(textEdit: EditText) {
        textEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                try {
                    val hours = Integer.parseInt(p0.toString())
                    reminderManager.feedingTimeDuration = hours
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "failed to parse feeding time")
                }
                reminderManager.scheduleNotifications()
            }
        })
        textEdit.setText("%d".format(reminderManager.feedingTimeDuration))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}