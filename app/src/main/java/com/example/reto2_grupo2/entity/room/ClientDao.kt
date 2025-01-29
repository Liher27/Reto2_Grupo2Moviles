package com.example.reto2_grupo2.entity.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(client: LoginForROOM)

    @Query("SELECT * FROM loginForROOM")
    fun getAll(): List<LoginForROOM>

    @Query("SELECT * FROM loginForROOM LIMIT 1")
    fun getOne(): LoginForROOM

    @Query("DELETE FROM loginForROOM")
    fun deleteClients()

}