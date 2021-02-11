package fi.nikulaj.paasto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

object EndFastDialog : DialogFragment() {

    private val model: MainViewModel by activityViewModels()

    private var fastStart: Long? = null
    private var fastEnd: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_end_fast_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        yesButton.setOnClickListener(onYesListener)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener(onCancelListener)

        fastStart = model.fastStart.value
        fastEnd = System.currentTimeMillis()
        val duration = fastEnd!! - fastStart!!

        val (hours, minutes, _) = millisToHMS(kotlin.math.abs(duration))

        val timeFmt = getString(R.string.fast_duration)
        val fastDuration = view.findViewById<TextView>(R.id.fastDuration)
        fastDuration.text = timeFmt.format(hours, minutes)
    }

    private val onYesListener = View.OnClickListener { view ->
        model.saveFast(fastStart!!, fastEnd!!)
        dismiss()
    }

    private val onCancelListener = View.OnClickListener { view ->
        dismiss()
    }

}