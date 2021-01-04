package com.example.lab2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sequence::class], version = 1, exportSchema = false)
abstract class SequenceDatabase : RoomDatabase() {

    abstract fun sequenceDao(): SequenceDao

    companion object {
        @Volatile
        private var INSTANCE: SequenceDatabase? = null

        fun getDatabase(context: Context): SequenceDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SequenceDatabase::class.java,
                    "sequence_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}