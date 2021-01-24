package fi.nikulaj.paasto

import android.content.Context
import androidx.room.*

enum class FastState {
    FAST,
    EAT
}

@Entity
data class Fast(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val stopTime: Long?
)

@Dao
interface FastDao {
    @Query("SELECT * FROM fast WHERE end_time IS NULL")
    fun getOngoing(): Fast

    @Insert
    fun insert(fast: Fast)
}

@Database(entities = arrayOf(Fast::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fastDao(): FastDao
}

class MainModel(appContext: Context) {
    var start: Long? = null
    var stop: Long? = null

    val db = Room.databaseBuilder(appContext, AppDatabase::class.java, "fast-db").build()

    fun hasOngoingFast(): Boolean {
        return (start != null && stop == null)
    }

    fun getOngoingFastStart(): Long {
        if (BuildConfig.DEBUG && !hasOngoingFast()) {
            error("Assertion failed")
        }

        return start!!
    }

    fun getLastFastStop(): Long {
        if (BuildConfig.DEBUG && hasOngoingFast()) {
            error("Assertion failed")
        }
        return stop!!
    }

    fun startFastAt(startTime: Long) {
        if (BuildConfig.DEBUG && hasOngoingFast()) {
            error("Assertion failed")
        }
        start = startTime
        stop = null
    }

    fun stopFastAt(stopTime: Long) {
        if (BuildConfig.DEBUG && !hasOngoingFast()) {
            error("Assertion failed")
        }
        stop = stopTime
    }
}