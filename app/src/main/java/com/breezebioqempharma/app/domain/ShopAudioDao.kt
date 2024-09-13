package com.breezebioqempharma.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.breezebioqempharma.app.AppConstant

@Dao
interface ShopAudioDao {
    @Insert
    fun insert(vararg obj: ShopAudioEntity)

    @Query("select * from shop_audio where isUploaded=:isUploaded")
    fun getUnsyncL(isUploaded: Boolean):List<ShopAudioEntity>

    @Query("update shop_audio  set isUploaded=:isUploaded where shop_id=:shop_id and datetime=:datetime")
    fun updateIsUploaded(isUploaded: Boolean,shop_id: String,datetime:String)
}