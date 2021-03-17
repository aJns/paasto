package fi.nikulaj.paasto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels

class FastDurationDialog : DialogFragment() {

    private val model: MainViewModel by activityViewModels()

    lateinit var supportFragmentManager: FragmentManager
    lateinit var durationEditText: EditText


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fast_duration_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supportFragmentManager = requireActivity().supportFragmentManager

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        yesButton.setOnClickListener(onYesListener)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener(onCancelListener)

        val (hours, _, _) = millisToHMS(model.targetDuration.value!!)
        durationEditText = view.findViewById<EditText>(R.id.hourEdit)
        durationEditText.setText(hours.toString())
    }

    private val onYesListener = View.OnClickListener { view ->
        val hours: Long = if (durationEditText.text != null) {
            durationEditText.text.toString().toLong()
        } else {
            0
        }

        val millis = hours * 60 * 60 * 1000
        model.setFastTarget(millis)
        dismiss()
    }

    private val onCancelListener = View.OnClickListener { view ->
        dismiss()
    }

}