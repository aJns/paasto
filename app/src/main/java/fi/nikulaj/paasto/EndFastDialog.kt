package fi.nikulaj.paasto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels

object EndFastDialog : DialogFragment() {

    private val model: MainViewModel by activityViewModels()

    private var fastStart: Long? = null
    private var fastEnd: Long? = null

    lateinit var supportFragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_end_fast_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supportFragmentManager = requireActivity().supportFragmentManager

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        yesButton.setOnClickListener(onYesListener)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener(onCancelListener)

        fastStart = model.fastStart.value
        val fastStartTime = view.findViewById<Button>(R.id.fastStartTime)
        fastStartTime.text = getDateStringFromMillis(requireActivity(), fastStart)
        fastStartTime.setOnClickListener(onFastStartClicked)

        fastEnd = System.currentTimeMillis()
        val fastEndTime = view.findViewById<Button>(R.id.fastEndTime)
        fastEndTime.text = getDateStringFromMillis(requireActivity(), fastEnd)
        fastEndTime.setOnClickListener(onFastEndClicked)

        updateFastDuration()
    }

    fun updateFastDuration() {
        val duration = fastEnd!! - fastStart!!
        val (hours, minutes, _) = millisToHMS(kotlin.math.abs(duration))

        val timeFmt = getString(R.string.fast_duration)
        val fastDuration = requireView().findViewById<TextView>(R.id.fastDuration)
        fastDuration.text = timeFmt.format(hours, minutes)
    }

    private val onFastStartClicked = View.OnClickListener { view ->
        val dateTimePicker = DateTimePickerDialog()
        val callback: (Long) -> Unit = { newStart: Long ->
            fastStart = newStart
            val fastStartTime = view.findViewById<Button>(R.id.fastStartTime)
            fastStartTime.text = getDateStringFromMillis(requireActivity(), fastStart)

            updateFastDuration()
        }

        dateTimePicker.showWithCallback(
            supportFragmentManager,
            dateTimePicker.tag,
            fastStart!!,
            callback
        )
    }

    private val onFastEndClicked = View.OnClickListener { view ->
        val dateTimePicker = DateTimePickerDialog()
        val callback: (Long) -> Unit = { newStart: Long ->
            fastEnd = newStart
            val fastEndTime = view.findViewById<Button>(R.id.fastEndTime)
            fastEndTime.text = getDateStringFromMillis(requireActivity(), fastEnd)

            updateFastDuration()
        }

        dateTimePicker.showWithCallback(
            supportFragmentManager,
            dateTimePicker.tag,
            fastEnd!!,
            callback
        )
    }

    private val onYesListener = View.OnClickListener { view ->
        model.saveFast(fastStart!!, fastEnd!!)
        dismiss()
    }

    private val onCancelListener = View.OnClickListener { view ->
        dismiss()
    }

}