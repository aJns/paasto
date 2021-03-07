package fi.nikulaj.paasto

import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class FastState {
    FAST,
    EAT
}

@Entity
data class Fast(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "start_time") var startTime: Long,
    @ColumnInfo(name = "end_time") var stopTime: Long?,
    @ColumnInfo(name = "target_duration") var targetDuration: Long?
)

@Dao
interface FastDao {
    @Query("SELECT * FROM fast WHERE start_time IS NOT NULL AND end_time IS NULL")
    suspend fun getOngoing(): Fast?

    @Query("SELECT * FROM fast WHERE uid IS (SELECT MAX(uid) FROM fast)")
    suspend fun getLast(): Fast?

    @Query("SELECT * FROM fast WHERE start_time IS NULL AND end_time IS NULL AND target_duration IS NOT NULL")
    suspend fun getTargetOnly(): Fast?

    @Insert
    suspend fun insert(fast: Fast)

    @Update(entity = Fast::class)
    suspend fun update(fast: Fast)
}

@Database(entities = arrayOf(Fast::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fastDao(): FastDao
}

object MainModel {
    val db: AppDatabase by lazy {
        MainActivity.getDatabase()!!
    }

    val fastDao by lazy {
        db.fastDao()
    }

    var targetDuration: Long? = null
        get() {
            if (field == null) {
                GlobalScope.launch {
                    field = getTargetDurationFromDb()
                }
            }
            return field
        }
        set(value) {
            if (field != value) {
                field = value
                GlobalScope.launch {
                    if (fastDao.getOngoing() != null) {
                        val ongoing = fastDao.getOngoing()
                        ongoing!!.targetDuration = field
                        fastDao.update(ongoing)
                    } else if (fastDao.getTargetOnly() != null) {
                        val to = fastDao.getTargetOnly()
                        to!!.targetDuration = field
                        fastDao.update(to)
                    }
                }
            }
        }

    suspend fun getTargetDurationFromDb(): Long {
        val target = when {
            fastDao.getOngoing() != null -> {
                val ongoing = fastDao.getOngoing()
                ongoing!!.targetDuration
            }
            fastDao.getTargetOnly() != null -> {
                val to = fastDao.getTargetOnly()
                to!!.targetDuration
            }
            else -> {
                val last = fastDao.getLast()
                last?.targetDuration
            }
        }
        return when (target) {
            null -> 18 * 60 * 60 * 1000
            else -> target
        }
    }

    suspend fun hasOngoingFast(): Boolean {
        return fastDao.getOngoing() != null
    }

    suspend fun getOngoingFastStart(): Long {
        if (BuildConfig.DEBUG && !hasOngoingFast()) {
            error("Assertion failed")
        }

        return fastDao.getOngoing()!!.startTime
    }

    suspend fun setOngoingFastStart(newStart: Long) {
        if (BuildConfig.DEBUG && !hasOngoingFast()) {
            error("Assertion failed")
        }

        val ongoing = fastDao.getOngoing()!!
        ongoing.startTime = newStart
        fastDao.update(ongoing)
    }

    suspend fun getLastFastStop(): Long? {
        return fastDao.getLast()?.stopTime
    }

    suspend fun startFastAt(startTime: Long) {
        if (BuildConfig.DEBUG && hasOngoingFast()) {
            error("Assertion failed")
        }
        fastDao.insert(Fast(null, startTime, null, targetDuration))
    }

    suspend fun stopFastAt(stopTime: Long) {
        if (BuildConfig.DEBUG && !hasOngoingFast()) {
            error("Assertion failed")
        }
        val ongoing = fastDao.getOngoing()!!
        ongoing.stopTime = stopTime
        fastDao.update(ongoing)
    }
}