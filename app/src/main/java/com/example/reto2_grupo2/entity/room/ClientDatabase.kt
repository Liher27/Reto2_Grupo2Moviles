package com.example.reto2_grupo2.entity.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.reto2_grupo2.entity.Client

@Database(entities = [Client::class], version = 1)
abstract class ClientDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var instance: ClientDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }

        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            ClientDatabase::class.java,
            "my_database"
        ).build()

    }

    abstract fun clientDao(): ClientDao

}