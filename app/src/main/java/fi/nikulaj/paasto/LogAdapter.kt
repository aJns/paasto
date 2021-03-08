package fi.nikulaj.paasto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(var fastSet: Array<Fast>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
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
        val (target, metTarget) = when(fast.targetDuration)
        {
            null -> Pair(null, null)
            else -> {
                val (hours, _, _) = millisToHMS(fast.targetDuration!!)
                val met = (fast.stopTime!! - fast.startTime) >= fast.targetDuration!!
                Pair(hours, met)
            }
        }
        val (hours, mins, _) = millisToHMS(fast.stopTime!! - fast.startTime)

        val context = holder.itemView.context

        val durTargetFmt = context.getString(R.string.log_fast_dur_target)
        holder.fastDurTarget.text = durTargetFmt.format(hours, mins, target)

        when(metTarget) {
            null -> {}
            true -> {
                holder.fastDurTarget.setTextColor(ContextCompat.getColor(context, R.color.primary))
            }
            false -> {
                holder.fastDurTarget.setTextColor(ContextCompat.getColor(context, R.color.warning))
            }
        }
    }

    override fun getItemCount() = fastSet.size
}