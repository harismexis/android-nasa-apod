package com.harismexis.apod.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "apod")
data class ApodEntity(
    @PrimaryKey
    val date: String,
    val title: String?,
    val explanation: String?,
    val mediaType: String?,
    val url: String?,
    val hdurl: String?,
)

@Database(
    entities = [ApodEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApodDatabase : RoomDatabase() {
    abstract fun getApodDao(): ApodDao
}

@Dao
interface ApodDao {

    @Query("SELECT * FROM apod WHERE date = :date")
    suspend fun getApod(date: String): ApodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apod: ApodEntity)

    @Query("DELETE FROM apod")
    suspend fun clear()

    @Query("SELECT * FROM apod ORDER BY date DESC")
    fun getAllApodsFlow(): Flow<List<ApodEntity>>
}

object DatabaseFactory {

    private var database: ApodDatabase? = null

    private fun getDatabase(context: Context): ApodDatabase {
        return database ?: Room.databaseBuilder(
            context.applicationContext,
            ApodDatabase::class.java,
            "apod.db"
        ).build().also {
            database = it
        }
    }

    fun getDao(context: Context) = getDatabase(context).getApodDao()
}