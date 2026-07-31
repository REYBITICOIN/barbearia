package com.example.data.agent

import com.example.data.local.AppointmentEntity
import com.example.data.local.BarbershopEntity
import com.example.data.local.ServiceEntity
import com.example.data.remote.NvidiaNimService
import com.example.data.repository.BarberLabRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * SchedulingAgent: Autonomous AI Scheduling Agent that utilizes the NVIDIA NIM API
 * (Llama 3.1 / NeVA model with temperature 0.2) to parse natural language messages
 * from clients and automatically create appointments in the local Room database.
 */
class SchedulingAgent(
    private val repository: BarberLabRepository
) {
    /**
     * Parses an incoming natural language message from a client using NVIDIA NIM API
     * and automatically inserts the generated appointment into the Room database.
     */
    suspend fun parseAndSchedule(
        message: String,
        tenantId: String,
        clientName: String = "Cliente WhatsApp",
        clientPhone: String = "(11) 99887-7665"
    ): SchedulingResult = withContext(Dispatchers.IO) {
        val barbershop = repository.getBarbershop(tenantId)
            ?: BarbershopEntity(
                tenantId = tenantId,
                name = "BarberLab Studio",
                ownerName = "Mestre Barbeiro",
                phone = clientPhone,
                address = "Endereço Principal",
                facadePhotoUrl = "",
                ownerPhotoUrl = ""
            )

        val servicesList = repository.getServices(tenantId).firstOrNull() ?: emptyList()

        // Call NVIDIA NIM API with strict temperature 0.2 for accurate parsing
        val aiAgentResult = NvidiaNimService.generateAutonomousResponse(
            userMessage = message,
            barbershop = barbershop,
            services = servicesList
        )

        var createdAppointment: AppointmentEntity? = null

        if (aiAgentResult.isAppointmentCreated) {
            val appointment = AppointmentEntity(
                appointmentId = "apt_nim_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                tenantId = tenantId,
                clientName = clientName,
                clientPhone = clientPhone,
                serviceName = aiAgentResult.detectedServiceName,
                barberName = barbershop.ownerName,
                dateTimeStr = "Hoje, 16:30",
                price = aiAgentResult.detectedPrice,
                status = "Agendado",
                createdByAi = true,
                notes = "Agendado automaticamente pelo SchedulingAgent (NVIDIA NIM API Temp 0.2)"
            )

            // Automatically insert into local Room database
            repository.addAppointment(appointment)
            createdAppointment = appointment
        }

        SchedulingResult(
            replyText = aiAgentResult.reply,
            isAppointmentCreated = aiAgentResult.isAppointmentCreated,
            appointment = createdAppointment,
            tenantId = tenantId
        )
    }

    /**
     * Alias method for processing client natural language messages.
     */
    suspend fun processClientMessage(
        clientMessage: String,
        tenantId: String,
        clientName: String = "Cliente WhatsApp",
        clientPhone: String = "(11) 99887-7665"
    ): SchedulingResult = parseAndSchedule(clientMessage, tenantId, clientName, clientPhone)
}

/**
 * Result model returned by the SchedulingAgent after processing natural language messages.
 */
data class SchedulingResult(
    val replyText: String,
    val isAppointmentCreated: Boolean,
    val appointment: AppointmentEntity?,
    val tenantId: String
)
