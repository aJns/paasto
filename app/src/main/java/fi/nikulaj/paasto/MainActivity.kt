package fi.nikulaj.paasto

import ViewAdapter
import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private var fastStart: Long? = null
    private val model: MainViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_reminders -> {
            val intent = Intent(this, SetReminderActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appBar))

        createNotificationChannel()

        val startTimeObserver = Observer<Long?> { newTime ->
            fastStart = newTime
        }
        model.fastStart.observe(this, startTimeObserver)

        model.checkState()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = ViewAdapter(this)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.timer)
                1 -> getString(R.string.log)
                else -> TODO("not implemented")
            }
        }.attach()

        val stateObserver = Observer<FastState> { newState ->
            when (newState) {
                FastState.EAT -> {
                    val feedingTimeHours = model.reminderManager.feedingTimeDuration!!
                    val feedingTimeMillis = feedingTimeHours *60*60*1000
                    val targetReachedAt = feedingTimeMillis + (model.lastFastStop ?: return@Observer)
                    val timeToTarget: Long = targetReachedAt - System.currentTimeMillis()
                    if (timeToTarget > 0) {
                        model.reminderManager
                                .scheduleNotifications(this, NotificationType.TimeSinceLastFast,
                                        targetReachedAt, "$feedingTimeHours h")
                    }
                }
                FastState.FAST -> {
                    val timeToTarget: Long = model.getTimeToTarget() ?: return@Observer
                    if (timeToTarget > 0) {
                        val targetReachedAt = System.currentTimeMillis() + timeToTarget
                        model.reminderManager
                                .scheduleNotifications(this, NotificationType.FastTargetReached,
                                        targetReachedAt, getTimeStringFromMillis(targetReachedAt))
                    }
                }
            }
        }
        model.timerState.observe(this, stateObserver)
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
        dateTimePicker.showWithCallback(
                supportFragmentManager,
                dateTimePicker.tag,
                fastStart!!,
                callback
        )
    }

    fun fastTargetClicked(view: View) {
        val fastDurDiag = FastDurationDialog()
        fastDurDiag.show(supportFragmentManager, fastDurDiag.tag)
    }

}
