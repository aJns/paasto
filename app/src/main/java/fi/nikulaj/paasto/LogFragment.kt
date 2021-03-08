package fi.nikulaj.paasto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView

class LogFragment() : Fragment() {

    private val model: MainViewModel by activityViewModels()
    private val adapter: LogAdapter = LogAdapter(emptyArray())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logObserver = Observer<Array<Fast>> { newArr ->
            adapter.fastSet = newArr.reversedArray()
            adapter.notifyDataSetChanged()
        }
        model.fastLog.observe(viewLifecycleOwner, logObserver)

        val recView = view.findViewById<RecyclerView>(R.id.logEvents)
        recView.adapter = adapter
    }

}