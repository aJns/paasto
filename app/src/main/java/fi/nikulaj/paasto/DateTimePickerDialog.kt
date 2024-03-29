package fi.nikulaj.paasto

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.util.*

class DateTimePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private var callback: ((Long) -> Unit)? = null

    lateinit var datePicker: DatePickerDialog
    lateinit var timePicker: TimePickerDialog

    val cal = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        datePicker = DatePickerDialog(requireActivity(), this, year, month, day)
        timePicker =
                TimePickerDialog(activity, this, hour, min, DateFormat.is24HourFormat(activity))

        return datePicker
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        cal.set(year, month, day)
        datePicker.dismiss()
        timePicker.show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)

        val newStart = cal.timeInMillis

        callback!!(newStart)
        dismiss()
    }

    fun showWithCallback(
            fragmentManager: FragmentManager,
            tag: String?,
            defaultTime: Long,
            callback: (Long) -> Unit
    ) {
        cal.timeInMillis = defaultTime
        show(fragmentManager, tag)
        this.callback = callback
    }


}