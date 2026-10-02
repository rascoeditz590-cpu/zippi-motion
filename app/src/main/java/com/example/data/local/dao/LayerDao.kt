package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.relations.LayerWithKeyframes
import kotlinx.coroutines.flow.Flow

@Dao
interface LayerDao {

    @Query("SELECT * FROM layers WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getLayersForProject(projectId: String): Flow<List<LayerEntity>>

    @Transaction
    @Query("SELECT * FROM layers WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getLayersWithKeyframesForProject(projectId: String): Flow<List<LayerWithKeyframes>>

    @Transaction
    @Query("SELECT * FROM layers WHERE id = :layerId")
    fun getLayerWithKeyframes(layerId: String): Flow<LayerWithKeyframes?>

    @Query("SELECT * FROM layers WHERE id = :id")
    suspend fun getLayerById(id: String): LayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayer(layer: LayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayers(layers: List<LayerEntity>)

    @Update
    suspend fun updateLayer(layer: LayerEntity)

    @Delete
    suspend fun deleteLayer(layer: LayerEntity)

    @Query("DELETE FROM layers WHERE id = :layerId")
    suspend fun deleteLayerById(layerId: String)

    @Query("DELETE FROM layers WHERE projectId = :projectId")
    suspend fun deleteLayersForProject(projectId: String)

    @Query("UPDATE layers SET isVisible = :isVisible WHERE id = :layerId")
    suspend fun setLayerVisibility(layerId: String, isVisible: Boolean)

    @Query("UPDATE layers SET isLocked = :isLocked WHERE id = :layerId")
    suspend fun setLayerLocked(layerId: String, isLocked: Boolean)

    @Query("UPDATE layers SET isMuted = :isMuted WHERE id = :layerId")
    suspend fun setLayerMuted(layerId: String, isMuted: Boolean)

    @Query("UPDATE layers SET orderIndex = :newIndex WHERE id = :layerId")
    suspend fun updateLayerOrder(layerId: String, newIndex: Int)
}
