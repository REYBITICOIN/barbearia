package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barbershops")
data class BarbershopEntity(
    @PrimaryKey val tenantId: String,
    val name: String,
    val ownerName: String,
    val phone: String,
    val address: String,
    val facadePhotoUrl: String,
    val ownerPhotoUrl: String,
    val primaryColorHex: String = "#D4AF37", // Gold default
    val secondaryColorHex: String = "#1A1C23", // Dark Charcoal
    val logoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "barbers")
data class BarberEntity(
    @PrimaryKey val barberId: String,
    val tenantId: String,
    val name: String,
    val specialty: String,
    val photoUrl: String,
    val phone: String,
    val rating: Float = 4.9f,
    val active: Boolean = true
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val serviceId: String,
    val tenantId: String,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val category: String, // e.g., Cabelo, Barba, Combo
    val description: String
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val appointmentId: String,
    val tenantId: String,
    val clientName: String,
    val clientPhone: String,
    val serviceName: String,
    val barberName: String,
    val dateTimeStr: String,
    val price: Double,
    val status: String = "Agendado", // Agendado, Concluído, Cancelado
    val createdByAi: Boolean = false,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_agent_configs")
data class AiAgentConfigEntity(
    @PrimaryKey val tenantId: String,
    val agentName: String = "Agente Atendente IA",
    val temperature: Float = 0.2f, // Exact 0.2 precision for scheduling as required
    val modelName: String = "NVIDIA NIM Llama 3.1 / Gemini",
    val whatsappConnected: Boolean = true,
    val autoSchedulingEnabled: Boolean = true,
    val systemPrompt: String = "Você é o Agente Atendente autônomo da barbearia. Responda de forma cortês, verifique disponibilidade, informe os preços dos serviços e confirme agendamentos.",
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketing_logs")
data class MarketingLogEntity(
    @PrimaryKey val logId: String,
    val tenantId: String,
    val channel: String, // Google Business, Meta (FB/IG), YouTube Shorts
    val title: String,
    val content: String,
    val mediaUrl: String,
    val status: String = "Publicado", // Publicado, Agendado, Falha
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val tenantId: String,
    val fullName: String,
    val role: String = "Barbeiro", // Barbeiro, Proprietário, Atendente
    val avatarUrl: String = ""
)

@Entity(tableName = "barber_media")
data class BarberMediaEntity(
    @PrimaryKey val mediaId: String,
    val tenantId: String,
    val barberId: String,
    val barberName: String,
    val mediaType: String, // PHOTO or VIDEO
    val fileUriOrUrl: String,
    val title: String,
    val haircutStyle: String,
    val clientName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
