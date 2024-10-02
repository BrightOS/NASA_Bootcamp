package ru.berserkers.deepspace.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import ru.berserkers.deepspace.data.dto.spaceX.launches.Launch

/*
 * @author Danil Khairulin
 */
@Dao
interface LaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLaunches(launches: List<Launch>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM launches_table")
    suspend fun getAllLaunches(): List<Launch>
}
