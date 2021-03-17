package fi.nikulaj.paasto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class WeekFragment : Fragment() {

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViews = arrayOf(
            view.findViewById<TextView>(R.id.weekDay1),
            view.findViewById(R.id.weekDay2),
            view.findViewById(R.id.weekDay3),
            view.findViewById(R.id.weekDay4),
            view.findViewById(R.id.weekDay5),
            view.findViewById(R.id.weekDay6),
            view.findViewById(R.id.weekDay7),
        )

        val logObserver = Observer<Array<Fast>> { newArr ->
            val revArr = newArr.reversedArray()

            for (i in 0..6) {
                if (i < revArr.size) {
                    val duration = revArr[i].stopTime!! - revArr[i].startTime
                    val (h, m, _) = millisToHMS(duration)
                    textViews[i].text = requireActivity().getString(R.string.hour_min_duration, h, m)

                    if (revArr[i].targetDuration != null) {
                        textViews[i].setTextColor(
                            if (duration >= revArr[i].targetDuration!!) {
                                ContextCompat.getColor(requireContext(), R.color.primaryDark)
                            } else {
                                ContextCompat.getColor(requireContext(), R.color.warning)
                            }
                        )
                    }
                }
            }
        }
        model.fastLog.observe(viewLifecycleOwner, logObserver)
    }
}