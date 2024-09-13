package com.breezebioqempharma.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
@Dao
interface LmsUserInfoDao {

    @Insert
    fun insertAll(vararg activity: LmsUserInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<LmsUserInfoEntity>)

}