package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BarbershopDao {
    @Query("SELECT * FROM barbershops ORDER BY createdAt ASC")
    fun getAllBarbershops(): Flow<List<BarbershopEntity>>

    @Query("SELECT * FROM barbershops WHERE tenantId = :tenantId")
    suspend fun getBarbershopById(tenantId: String): BarbershopEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarbershop(barbershop: BarbershopEntity)

    @Update
    suspend fun updateBarbershop(barbershop: BarbershopEntity)

    @Query("DELETE FROM barbershops WHERE tenantId = :tenantId")
    suspend fun deleteBarbershop(tenantId: String)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE tenantId = :tenantId ORDER BY name ASC")
    fun getServicesForTenant(tenantId: String): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE serviceId = :serviceId")
    suspend fun deleteService(serviceId: String)
}

@Dao
interface BarberDao {
    @Query("SELECT * FROM barbers WHERE tenantId = :tenantId AND active = 1")
    fun getBarbersForTenant(tenantId: String): Flow<List<BarberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarber(barber: BarberEntity)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments WHERE tenantId = :tenantId ORDER BY timestamp DESC")
    fun getAppointmentsForTenant(tenantId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE appointmentId = :appointmentId")
    suspend fun getAppointmentById(appointmentId: String): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE tenantId = :tenantId AND clientName LIKE '%' || :clientName || '%' ORDER BY timestamp DESC")
    fun getAppointmentsByClient(tenantId: String, clientName: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE appointmentId = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM appointments WHERE appointmentId = :id")
    suspend fun deleteAppointment(id: String)
}

@Dao
interface AiAgentConfigDao {
    @Query("SELECT * FROM ai_agent_configs WHERE tenantId = :tenantId")
    fun getConfigForTenant(tenantId: String): Flow<AiAgentConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AiAgentConfigEntity)
}

@Dao
interface MarketingLogDao {
    @Query("SELECT * FROM marketing_logs WHERE tenantId = :tenantId ORDER BY timestamp DESC")
    fun getLogsForTenant(tenantId: String): Flow<List<MarketingLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MarketingLogEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE tenantId = :tenantId")
    fun getUsersForTenant(tenantId: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUser(username: String)
}

@Dao
interface BarberMediaDao {
    @Query("SELECT * FROM barber_media WHERE tenantId = :tenantId ORDER BY timestamp DESC")
    fun getMediaForTenant(tenantId: String): Flow<List<BarberMediaEntity>>

    @Query("SELECT * FROM barber_media WHERE tenantId = :tenantId AND barberId = :barberId ORDER BY timestamp DESC")
    fun getMediaForBarber(tenantId: String, barberId: String): Flow<List<BarberMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: BarberMediaEntity)

    @Query("DELETE FROM barber_media WHERE mediaId = :mediaId")
    suspend fun deleteMedia(mediaId: String)
}
