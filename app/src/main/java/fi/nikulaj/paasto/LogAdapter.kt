package fi.nikulaj.paasto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

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
        setFastDurTarget(holder, fast)
        setDates(holder, fast)
    }

    override fun getItemCount() = fastSet.size

    private fun setFastDurTarget(holder: ViewHolder, fast: Fast) {
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

    private fun setDates(holder: LogAdapter.ViewHolder, fast: Fast) {
        val (startDay, startMonth) = getDayMonthFromMillis(fast.startTime)
        val (endDay, endMonth) = getDayMonthFromMillis(fast.stopTime!!)

        holder.dates.text = when (startMonth == endMonth) {
            true -> when(startDay == endDay) {
                true -> "%02d.%02d.".format(startDay, startMonth)
                false -> "%02d. - %02d.%02d.".format(startDay, endDay, endMonth)
            }
            false -> "%02d.%02d. - %02d.%02d.".format(startDay, startMonth, endDay, endMonth)
        }
    }
}