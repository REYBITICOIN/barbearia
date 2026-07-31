package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BarbershopEntity::class,
        BarberEntity::class,
        ServiceEntity::class,
        AppointmentEntity::class,
        AiAgentConfigEntity::class,
        MarketingLogEntity::class,
        UserEntity::class,
        BarberMediaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BarberLabDatabase : RoomDatabase() {
    abstract fun barbershopDao(): BarbershopDao
    abstract fun serviceDao(): ServiceDao
    abstract fun barberDao(): BarberDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun aiAgentConfigDao(): AiAgentConfigDao
    abstract fun marketingLogDao(): MarketingLogDao
    abstract fun userDao(): UserDao
    abstract fun barberMediaDao(): BarberMediaDao

    companion object {
        @Volatile
        private var INSTANCE: BarberLabDatabase? = null

        fun getDatabase(context: Context): BarberLabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarberLabDatabase::class.java,
                    "barberlab_saas_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
