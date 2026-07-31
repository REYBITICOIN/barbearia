package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class BarberLabRepository(private val db: BarberLabDatabase) {

    val allBarbershops: Flow<List<BarbershopEntity>> = db.barbershopDao().getAllBarbershops()

    suspend fun getBarbershop(tenantId: String): BarbershopEntity? = withContext(Dispatchers.IO) {
        db.barbershopDao().getBarbershopById(tenantId)
    }

    suspend fun updateBarbershop(barbershop: BarbershopEntity) = withContext(Dispatchers.IO) {
        db.barbershopDao().updateBarbershop(barbershop)
    }

    suspend fun createBarbershop(barbershop: BarbershopEntity) = withContext(Dispatchers.IO) {
        db.barbershopDao().insertBarbershop(barbershop)
        
        // Seed default services for all 6 popular categories matching web model
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_1", barbershop.tenantId, "Corte Masculino Premium", 55.0, 35, "Cabelo", "Corte moderno com lavagem e alinhamento de fios."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_2", barbershop.tenantId, "Barba de Respeito com Toalha Quente", 45.0, 30, "Barba", "Barboterapia completa com óleos essenciais e toalha quente."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_3", barbershop.tenantId, "Pezinho & Acabamento Navalhado", 25.0, 15, "Acabamento", "Contorno perfeito de pezinho e barba alinhados na navalha."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_4", barbershop.tenantId, "Massagem Capilar & Facial", 40.0, 20, "Massagem", "Massagem relaxante couro cabeludo com óleos essenciais."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_5", barbershop.tenantId, "Design de Sobrancelha na Navalha", 20.0, 15, "Sobrancelha", "Alinhamento preciso de sobrancelhas masculinas."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_6", barbershop.tenantId, "Hidratação & Cauterização Capilar", 50.0, 25, "Hidratação", "Tratamento profundo para brilho e maciez dos fios."))
        db.serviceDao().insertService(ServiceEntity("srv_${barbershop.tenantId}_7", barbershop.tenantId, "Combo BarberLab (Cabelo + Barba)", 90.0, 60, "Combo", "O pacote VIP completo da barbearia com acabamento impecável."))

        // Seed default barber
        db.barberDao().insertBarber(BarberEntity("barb_${barbershop.tenantId}_1", barbershop.tenantId, barbershop.ownerName, "Master Barber", barbershop.ownerPhotoUrl, barbershop.phone, 5.0f, true))

        // Seed default AI config
        db.aiAgentConfigDao().insertOrUpdateConfig(
            AiAgentConfigEntity(
                tenantId = barbershop.tenantId,
                agentName = "Agente Atendente IA - ${barbershop.name}",
                temperature = 0.2f,
                whatsappConnected = true,
                autoSchedulingEnabled = true
            )
        )
    }

    fun getServices(tenantId: String): Flow<List<ServiceEntity>> = db.serviceDao().getServicesForTenant(tenantId)

    suspend fun addService(service: ServiceEntity) = withContext(Dispatchers.IO) {
        db.serviceDao().insertService(service)
    }

    suspend fun deleteService(serviceId: String) = withContext(Dispatchers.IO) {
        db.serviceDao().deleteService(serviceId)
    }

    fun getBarbers(tenantId: String): Flow<List<BarberEntity>> = db.barberDao().getBarbersForTenant(tenantId)

    suspend fun addBarber(barber: BarberEntity) = withContext(Dispatchers.IO) {
        db.barberDao().insertBarber(barber)
    }

    fun getAppointments(tenantId: String): Flow<List<AppointmentEntity>> = db.appointmentDao().getAppointmentsForTenant(tenantId)

    suspend fun addAppointment(appointment: AppointmentEntity) = withContext(Dispatchers.IO) {
        db.appointmentDao().insertAppointment(appointment)
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String) = withContext(Dispatchers.IO) {
        db.appointmentDao().updateStatus(appointmentId, status)
    }

    suspend fun deleteAppointment(appointmentId: String) = withContext(Dispatchers.IO) {
        db.appointmentDao().deleteAppointment(appointmentId)
    }

    fun getAiConfig(tenantId: String): Flow<AiAgentConfigEntity?> = db.aiAgentConfigDao().getConfigForTenant(tenantId)

    suspend fun updateAiConfig(config: AiAgentConfigEntity) = withContext(Dispatchers.IO) {
        db.aiAgentConfigDao().insertOrUpdateConfig(config)
    }

    fun getMarketingLogs(tenantId: String): Flow<List<MarketingLogEntity>> = db.marketingLogDao().getLogsForTenant(tenantId)

    suspend fun addMarketingLog(log: MarketingLogEntity) = withContext(Dispatchers.IO) {
        db.marketingLogDao().insertLog(log)
    }

    // User Authentication
    suspend fun getUserByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        db.userDao().getUserByUsername(username)
    }

    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        db.userDao().insertUser(user)
    }

    fun getUsersForTenant(tenantId: String): Flow<List<UserEntity>> = db.userDao().getUsersForTenant(tenantId)

    // Barber Media Portfolio (CameraX photos & videos)
    fun getMediaForTenant(tenantId: String): Flow<List<BarberMediaEntity>> = db.barberMediaDao().getMediaForTenant(tenantId)

    fun getMediaForBarber(tenantId: String, barberId: String): Flow<List<BarberMediaEntity>> = db.barberMediaDao().getMediaForBarber(tenantId, barberId)

    suspend fun addMedia(media: BarberMediaEntity) = withContext(Dispatchers.IO) {
        db.barberMediaDao().insertMedia(media)
    }

    suspend fun deleteMedia(mediaId: String) = withContext(Dispatchers.IO) {
        db.barberMediaDao().deleteMedia(mediaId)
    }

    suspend fun seedDefaultMultiTenantDataIfEmpty() = withContext(Dispatchers.IO) {
        val existing = db.barbershopDao().getAllBarbershops().firstOrNull()
        if (existing.isNullOrEmpty()) {
            // Tenant 1: Barbearia do João (Default Tenant)
            val t1 = BarbershopEntity(
                tenantId = "tenant_barberlab_master",
                name = "Barbearia do João",
                ownerName = "João Silva",
                phone = "(11) 98877-6655",
                address = "Av. Paulista, 1500 - São Paulo/SP",
                facadePhotoUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800",
                ownerPhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                primaryColorHex = "#D4AF37", // Gold
                secondaryColorHex = "#1E1F22",
                logoUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=200"
            )
            createBarbershop(t1)

            // Seed initial appointments for Tenant 1
            db.appointmentDao().insertAppointment(
                AppointmentEntity(
                    appointmentId = "apt_1",
                    tenantId = t1.tenantId,
                    clientName = "Carlos Eduardo",
                    clientPhone = "(11) 97711-2233",
                    serviceName = "Combo BarberLab (Cabelo + Barba)",
                    barberName = t1.ownerName,
                    dateTimeStr = "Hoje, 14:30",
                    price = 90.0,
                    status = "Agendado",
                    createdByAi = true,
                    notes = "Agendado via Agente Atendente WhatsApp IA"
                )
            )
            db.appointmentDao().insertAppointment(
                AppointmentEntity(
                    appointmentId = "apt_2",
                    tenantId = t1.tenantId,
                    clientName = "Lucas Mendes",
                    clientPhone = "(11) 96622-3344",
                    serviceName = "Corte Masculino Premium",
                    barberName = t1.ownerName,
                    dateTimeStr = "Hoje, 16:00",
                    price = 55.0,
                    status = "Agendado",
                    createdByAi = false,
                    notes = "Cliente VIP recorrente"
                )
            )

            // Seed initial marketing logs
            db.marketingLogDao().insertLog(
                MarketingLogEntity(
                    logId = "log_1",
                    tenantId = t1.tenantId,
                    channel = "Google Business API",
                    title = "Resposta automática a avaliação 5 estrelas",
                    content = "IA respondeu: 'Obrigado Carlos! É uma honra cuidar do seu estilo na BarberLab Master Studio. Até a próxima!'",
                    mediaUrl = "",
                    status = "Publicado"
                )
            )
            db.marketingLogDao().insertLog(
                MarketingLogEntity(
                    logId = "log_2",
                    tenantId = t1.tenantId,
                    channel = "Meta API (Instagram)",
                    title = "Post de Transformação Fade & Barba",
                    content = "🔥 Fade navalhado com barba alinhada! Agende seu horário com nosso Agente IA no WhatsApp! 💈 #barberlab #fade",
                    mediaUrl = "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?w=600",
                    status = "Publicado"
                )
            )
            db.marketingLogDao().insertLog(
                MarketingLogEntity(
                    logId = "log_3",
                    tenantId = t1.tenantId,
                    channel = "YouTube Shorts",
                    title = "Vídeo Short: Degradê Perfeito em 30s",
                    content = "Confira a transformação incrível do dia na BarberLab Studio! Assista o Short completo.",
                    mediaUrl = "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=600",
                    status = "Publicado"
                )
            )

            // Tenant 2: Vintage Barber Club
            val t2 = BarbershopEntity(
                tenantId = "tenant_vintage_club",
                name = "Vintage Cuts Barber Club",
                ownerName = "Barbeiro Pedro Ramos",
                phone = "(21) 99881-1122",
                address = "Rua Visconde de Pirajá, 300 - Ipanema, Rio de Janeiro/RJ",
                facadePhotoUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=800",
                ownerPhotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                primaryColorHex = "#2E7D32", // Emerald Green
                secondaryColorHex = "#121212"
            )
            createBarbershop(t2)

            // Seed default user accounts for login
            db.userDao().insertUser(
                UserEntity(
                    username = "admin",
                    passwordHash = "123456",
                    tenantId = t1.tenantId,
                    fullName = "Mestre Luan Silva",
                    role = "Proprietário",
                    avatarUrl = t1.ownerPhotoUrl
                )
            )
            db.userDao().insertUser(
                UserEntity(
                    username = "barbeiro1",
                    passwordHash = "123456",
                    tenantId = t1.tenantId,
                    fullName = "Barbeiro Lucas Fade",
                    role = "Barbeiro",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400"
                )
            )
            db.userDao().insertUser(
                UserEntity(
                    username = "vintage",
                    passwordHash = "123456",
                    tenantId = t2.tenantId,
                    fullName = "Barbeiro Pedro Ramos",
                    role = "Proprietário",
                    avatarUrl = t2.ownerPhotoUrl
                )
            )

            // Seed initial Barber Work Photos / Videos
            db.barberMediaDao().insertMedia(
                BarberMediaEntity(
                    mediaId = "med_1",
                    tenantId = t1.tenantId,
                    barberId = "barb_${t1.tenantId}_1",
                    barberName = "Mestre Luan Silva",
                    mediaType = "PHOTO",
                    fileUriOrUrl = "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?w=800",
                    title = "Mid Fade Navalhado com Pigmentação",
                    haircutStyle = "Mid Fade",
                    clientName = "Carlos Eduardo"
                )
            )
            db.barberMediaDao().insertMedia(
                BarberMediaEntity(
                    mediaId = "med_2",
                    tenantId = t1.tenantId,
                    barberId = "barb_${t1.tenantId}_1",
                    barberName = "Mestre Luan Silva",
                    mediaType = "PHOTO",
                    fileUriOrUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800",
                    title = "Barboterapia com Toalha Quente",
                    haircutStyle = "Barba Spar",
                    clientName = "Lucas Mendes"
                )
            )
            db.barberMediaDao().insertMedia(
                BarberMediaEntity(
                    mediaId = "med_3",
                    tenantId = t1.tenantId,
                    barberId = "barb_${t1.tenantId}_1",
                    barberName = "Mestre Luan Silva",
                    mediaType = "VIDEO",
                    fileUriOrUrl = "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=800",
                    title = "Vídeo 360: Taper Fade de Alta Precisão",
                    haircutStyle = "Taper Fade",
                    clientName = "Rafael Souza"
                )
            )
        }
    }
}
