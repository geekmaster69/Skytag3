package com.example.skytag3.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skytag3.data.dao.UserInfoDao
import com.example.skytag3.data.entity.UserInfoEntity

@Database(entities = arrayOf(UserInfoEntity::class), version = 1)
abstract class UserInfoDatabase: RoomDatabase(){
    abstract fun userInfoDao(): UserInfoDao
}

