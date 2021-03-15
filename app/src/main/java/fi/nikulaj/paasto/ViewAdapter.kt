import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import fi.nikulaj.paasto.LogFragment
import fi.nikulaj.paasto.ProgressFragment
import fi.nikulaj.paasto.TimerFragment

class ViewAdapter(fragAct: FragmentActivity) : FragmentStateAdapter(fragAct) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TimerFragment()
            1 -> LogFragment()
            2 -> ProgressFragment()
            else -> TODO("Not implemented")
        }
    }

}