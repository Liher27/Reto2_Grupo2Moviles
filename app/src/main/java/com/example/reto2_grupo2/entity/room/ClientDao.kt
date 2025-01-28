package com.example.reto2_grupo2.entity.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_grupo2.entity.Client

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(client: Client)

    @Query("SELECT * FROM client")
    fun getAll(): List<Client>

    @Query("DELETE FROM client")
    fun deleteClients()

}