package fi.nikulaj.paasto

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText

class SetReminderActivity: AppCompatActivity() {
    private var feedingTimeInput: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setSupportActionBar(findViewById(R.id.appBar2))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        setupFastEndReminderSwitch(findViewById(R.id.fastEndReminderSwitch))
        setupFastStartReminderSwitch(findViewById(R.id.fastStartReminderSwitch))

        feedingTimeInput = findViewById(R.id.feedingTimeTextIn)
        setupFeedingTimeIn(feedingTimeInput!!)
    }

    private fun setupFastEndReminderSwitch(switch: SwitchCompat) {
        switch.setOnCheckedChangeListener { _, isOn ->  }
    }

    private fun setupFastStartReminderSwitch(switch: SwitchCompat) {
        switch.setOnCheckedChangeListener { _, isOn ->  }
    }

    private fun setupFeedingTimeIn(textEdit: EditText) {
        TODO("Not yet implemented")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}