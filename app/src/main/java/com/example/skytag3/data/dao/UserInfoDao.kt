package com.example.skytag3.data.dao

import androidx.room.*
import com.example.skytag3.data.entity.UserInfoEntity

@Dao
interface UserInfoDao {

    @Query("SELECT * FROM user_info_entity")
    fun getAllData() : UserInfoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserInfo(userInfoEntity: UserInfoEntity)

    @Update
    fun updateUserInfo( userInfoEntity: UserInfoEntity)

    @Query("DELETE FROM user_info_entity")
    fun deleteUserInfo()
}