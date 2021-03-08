package fi.nikulaj.paasto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val fastSet: Array<Fast>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dates: TextView = view.findViewById(R.id.dates)
        val fastDurTarget: TextView = view.findViewById(R.id.fastDurationAndTarget)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.log_row_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fast = fastSet[position]
        val target = when(fast.targetDuration)
        {
            null -> null
            else -> {
                val (hours, _, _) = millisToHMS(fast.targetDuration!!)
                hours
            }
        }
        val (hours, mins, _) = millisToHMS(fast.stopTime!! - fast.startTime)

        val durTargetFmt = holder.itemView.context.getString(R.string.log_fast_dur_target)

        holder.fastDurTarget.text = durTargetFmt.format(hours, mins, target)
    }

    override fun getItemCount() = fastSet.size
}