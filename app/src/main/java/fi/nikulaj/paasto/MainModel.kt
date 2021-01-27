package fi.nikulaj.paasto

import androidx.room.*

enum class FastState {
    FAST,
    EAT
}

@Entity
data class Fast(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "start_time") var startTime: Long,
    @ColumnInfo(name = "end_time") var stopTime: Long?
)

@Dao
interface FastDao {
    @Query("SELECT * FROM fast WHERE end_time IS NULL")
    suspend fun getOngoing(): Fast?

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

    fun getLastFastStop(): Long {
        TODO()
    }

    suspend fun startFastAt(startTime: Long) {
        if (BuildConfig.DEBUG && hasOngoingFast()) {
            error("Assertion failed")
        }
        fastDao.insert(Fast(null, startTime, null))
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