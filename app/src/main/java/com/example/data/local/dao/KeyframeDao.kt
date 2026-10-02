package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.KeyframeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyframeDao {

    @Query("SELECT * FROM keyframes WHERE layerId = :layerId ORDER BY timeMs ASC")
    fun getKeyframesForLayer(layerId: String): Flow<List<KeyframeEntity>>

    @Query("SELECT * FROM keyframes WHERE layerId = :layerId AND property = :property ORDER BY timeMs ASC")
    fun getKeyframesForProperty(layerId: String, property: String): Flow<List<KeyframeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyframe(keyframe: KeyframeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyframes(keyframes: List<KeyframeEntity>)

    @Update
    suspend fun updateKeyframe(keyframe: KeyframeEntity)

    @Query("DELETE FROM keyframes WHERE id = :keyframeId")
    suspend fun deleteKeyframeById(keyframeId: String)

    @Query("DELETE FROM keyframes WHERE layerId = :layerId AND property = :property AND timeMs = :timeMs")
    suspend fun deleteKeyframeAtTime(layerId: String, property: String, timeMs: Long)

    @Query("DELETE FROM keyframes WHERE layerId = :layerId")
    suspend fun deleteKeyframesForLayer(layerId: String)
}
