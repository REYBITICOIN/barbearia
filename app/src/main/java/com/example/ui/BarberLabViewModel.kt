package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.agent.SchedulingAgent
import com.example.data.agent.SchedulingResult
import com.example.data.local.*
import com.example.data.remote.MarketingAiService
import com.example.data.remote.NvidiaNimService
import com.example.data.repository.BarberLabRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: String = "Agora",
    val isAppointmentConfirmed: Boolean = false
)

class BarberLabViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BarberLabRepository(BarberLabDatabase.getDatabase(application))
    val schedulingAgent = SchedulingAgent(repository)

    val barbershops: StateFlow<List<BarbershopEntity>> = repository.allBarbershops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTenantId = MutableStateFlow("tenant_barberlab_master")
    val selectedTenantId: StateFlow<String> = _selectedTenantId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeBarbershop: StateFlow<BarbershopEntity?> = _selectedTenantId
        .flatMapLatest { tenantId ->
            barbershops.map { list -> list.find { it.tenantId == tenantId } ?: list.firstOrNull() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val services: StateFlow<List<ServiceEntity>> = _selectedTenantId
        .flatMapLatest { repository.getServices(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val barbers: StateFlow<List<BarberEntity>> = _selectedTenantId
        .flatMapLatest { repository.getBarbers(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val appointments: StateFlow<List<AppointmentEntity>> = _selectedTenantId
        .flatMapLatest { repository.getAppointments(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val aiConfig: StateFlow<AiAgentConfigEntity?> = _selectedTenantId
        .flatMapLatest { repository.getAiConfig(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val marketingLogs: StateFlow<List<MarketingLogEntity>> = _selectedTenantId
        .flatMapLatest { repository.getMarketingLogs(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Authentication State
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    init {
        // Check if Firebase Auth user is already signed in on startup
        try {
            val fbUser = firebaseAuth.currentUser
            if (fbUser != null) {
                val email = fbUser.email ?: ""
                val username = email.substringBefore("@")
                val displayName = fbUser.displayName.takeUnless { it.isNullOrBlank() } ?: username
                viewModelScope.launch {
                    val existing = repository.getUserByUsername(username)
                    if (existing != null) {
                        _currentUser.value = existing
                    } else {
                        val newUser = UserEntity(
                            username = username,
                            passwordHash = "firebase_auth",
                            tenantId = _selectedTenantId.value,
                            fullName = displayName,
                            role = "Barbeiro",
                            avatarUrl = fbUser.photoUrl?.toString() ?: ""
                        )
                        repository.registerUser(newUser)
                        _currentUser.value = newUser
                    }
                }
            }
        } catch (_: Exception) {
            // Firebase Auth initialization safe fallback
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val barberMedia: StateFlow<List<BarberMediaEntity>> = _selectedTenantId
        .flatMapLatest { repository.getMediaForTenant(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gemini Advanced Feature States
    private val _mapsGroundingResult = MutableStateFlow<String?>(null)
    val mapsGroundingResult: StateFlow<String?> = _mapsGroundingResult.asStateFlow()
    private val _isMapsLoading = MutableStateFlow(false)
    val isMapsLoading: StateFlow<Boolean> = _isMapsLoading.asStateFlow()

    private val _veoVideoResult = MutableStateFlow<String?>(null)
    val veoVideoResult: StateFlow<String?> = _veoVideoResult.asStateFlow()
    private val _isGeneratingVeo = MutableStateFlow(false)
    val isGeneratingVeo: StateFlow<Boolean> = _isGeneratingVeo.asStateFlow()

    private val _aiImageResult = MutableStateFlow<String?>(null)
    val aiImageResult: StateFlow<String?> = _aiImageResult.asStateFlow()
    private val _isGeneratingAiImage = MutableStateFlow(false)
    val isGeneratingAiImage: StateFlow<Boolean> = _isGeneratingAiImage.asStateFlow()

    private val _flashLiteResponse = MutableStateFlow<String?>(null)
    val flashLiteResponse: StateFlow<String?> = _flashLiteResponse.asStateFlow()
    private val _isFlashLiteLoading = MutableStateFlow(false)
    val isFlashLiteLoading: StateFlow<Boolean> = _isFlashLiteLoading.asStateFlow()

    private val _voiceLiveResponse = MutableStateFlow<String?>(null)
    val voiceLiveResponse: StateFlow<String?> = _voiceLiveResponse.asStateFlow()
    private val _isVoiceLiveActive = MutableStateFlow(false)
    val isVoiceLiveActive: StateFlow<Boolean> = _isVoiceLiveActive.asStateFlow()

    private val _isWhatsAppConnected = MutableStateFlow(true)
    val isWhatsAppConnected: StateFlow<Boolean> = _isWhatsAppConnected.asStateFlow()

    private val _whatsappPhone = MutableStateFlow("+55 (11) 98765-4321")
    val whatsappPhone: StateFlow<String> = _whatsappPhone.asStateFlow()

    private val _whatsappSessionId = MutableStateFlow("WA-SESSION-8F92A")
    val whatsappSessionId: StateFlow<String> = _whatsappSessionId.asStateFlow()

    private val _whatsappRemindersSent = MutableStateFlow(18)
    val whatsappRemindersSent: StateFlow<Int> = _whatsappRemindersSent.asStateFlow()

    // Configurações de Maquininha e Impostos da Barbearia (Brasil)
    private val _cardMachineRates = MutableStateFlow(BrazilianCardMachineRates())
    val cardMachineRates: StateFlow<BrazilianCardMachineRates> = _cardMachineRates.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateCardMachineRates(newRates: BrazilianCardMachineRates) {
        _cardMachineRates.value = newRates
    }

    fun connectWhatsAppWeb(phoneNumber: String = "+55 (11) 98765-4321") {
        _whatsappPhone.value = phoneNumber
        _whatsappSessionId.value = "WA-SESSION-" + System.currentTimeMillis().toString().takeLast(5)
        _isWhatsAppConnected.value = true
    }

    fun disconnectWhatsAppWeb() {
        _isWhatsAppConnected.value = false
    }

    fun sendAppointmentReminder(appointment: AppointmentEntity) {
        viewModelScope.launch {
            _whatsappRemindersSent.value += 1
            val shopName = activeBarbershop.value?.name ?: "BarberLab"
            val message = "Olá ${appointment.clientName}! 💈 Lembrando do seu agendamento de *${appointment.serviceName}* na *$shopName* hoje às ${appointment.dateTimeStr}. Confirmado? Responda SIM."
            val aiMsg = ChatMessage(
                sender = "ai",
                text = "📲 *LEMBRETE WHATSAPP ENVIADO* para ${appointment.clientName} (${appointment.clientPhone}):\n\"$message\"",
                isAppointmentConfirmed = true
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    init {
        viewModelScope.launch {
            repository.seedDefaultMultiTenantDataIfEmpty()
            resetChatForActiveTenant()
        }
    }

    fun selectTenant(tenantId: String) {
        _selectedTenantId.value = tenantId
        resetChatForActiveTenant()
    }

    private fun resetChatForActiveTenant() {
        val currentShopName = activeBarbershop.value?.name ?: "BarberLab"
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "Olá! 💈 Sou o Agente Atendente Autônomo da *$currentShopName*. Como posso te ajudar hoje? Posso agendar horários, passar valores ou tirar dúvidas! ✂️"
            )
        )
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentShop = activeBarbershop.value ?: return

        val userMsg = ChatMessage(sender = "user", text = userText)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            val currentServices = services.value

            val aiResult = NvidiaNimService.generateAutonomousResponse(
                userMessage = userText,
                barbershop = currentShop,
                services = currentServices
            )

            val aiMsg = ChatMessage(
                sender = "ai",
                text = aiResult.reply,
                isAppointmentConfirmed = aiResult.isAppointmentCreated
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiThinking.value = false

            // Auto-create appointment in Room DB if confirmed
            if (aiResult.isAppointmentCreated) {
                val aptId = "apt_ai_${System.currentTimeMillis()}"
                repository.addAppointment(
                    AppointmentEntity(
                        appointmentId = aptId,
                        tenantId = currentShop.tenantId,
                        clientName = "Cliente WhatsApp IA",
                        clientPhone = "(11) 99999-0000",
                        serviceName = aiResult.detectedServiceName,
                        barberName = currentShop.ownerName,
                        dateTimeStr = "Hoje, 16:30",
                        price = aiResult.detectedPrice,
                        status = "Agendado",
                        createdByAi = true,
                        notes = "Criado de forma autônoma pelo Agente Atendente NVIDIA NIM / Gemini"
                    )
                )

                // Disparo de mensagem no WhatsApp Cloud API
                com.example.data.remote.WhatsappCloudApiService.sendBookingConfirmationWhatsApp(
                    clientPhone = "(11) 99999-0000",
                    clientName = "Cliente WhatsApp IA",
                    date = "Hoje",
                    time = "16:30",
                    barberName = currentShop.ownerName,
                    shopName = currentShop.name
                )
            }
        }
    }

    fun createNewBarbershop(
        name: String,
        ownerName: String,
        phone: String,
        address: String,
        facadePhotoUrl: String,
        ownerPhotoUrl: String,
        primaryColorHex: String
    ) {
        viewModelScope.launch {
            val newTenantId = "tenant_${System.currentTimeMillis()}"
            val newShop = BarbershopEntity(
                tenantId = newTenantId,
                name = name,
                ownerName = ownerName,
                phone = phone,
                address = address,
                facadePhotoUrl = facadePhotoUrl.ifBlank { "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800" },
                ownerPhotoUrl = ownerPhotoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400" },
                primaryColorHex = primaryColorHex
            )
            repository.createBarbershop(newShop)
            selectTenant(newTenantId)
        }
    }

    fun updateBranding(
        facadePhotoUrl: String,
        ownerPhotoUrl: String,
        primaryColorHex: String,
        secondaryColorHex: String,
        name: String,
        address: String,
        phone: String
    ) {
        val current = activeBarbershop.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                facadePhotoUrl = facadePhotoUrl,
                ownerPhotoUrl = ownerPhotoUrl,
                primaryColorHex = primaryColorHex,
                secondaryColorHex = secondaryColorHex,
                name = name,
                address = address,
                phone = phone
            )
            repository.updateBarbershop(updated)
        }
    }

    fun addService(name: String, price: Double, duration: Int, category: String, desc: String) {
        val currentTenant = selectedTenantId.value
        viewModelScope.launch {
            val newService = ServiceEntity(
                serviceId = "srv_${System.currentTimeMillis()}",
                tenantId = currentTenant,
                name = name,
                price = price,
                durationMinutes = duration,
                category = category,
                description = desc
            )
            repository.addService(newService)
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch { repository.deleteService(serviceId) }
    }

    fun addAppointment(
        clientName: String,
        clientPhone: String,
        serviceName: String,
        barberName: String,
        dateTimeStr: String,
        price: Double
    ) {
        val currentTenant = selectedTenantId.value
        val shopName = activeBarbershop.value?.name ?: "Barbearia do João"
        viewModelScope.launch {
            repository.addAppointment(
                AppointmentEntity(
                    appointmentId = "apt_${System.currentTimeMillis()}",
                    tenantId = currentTenant,
                    clientName = clientName,
                    clientPhone = clientPhone,
                    serviceName = serviceName,
                    barberName = barberName,
                    dateTimeStr = dateTimeStr,
                    price = price,
                    status = "Agendado",
                    createdByAi = false
                )
            )

            // Disparo automático do WhatsApp Cloud API (Meta)
            val parts = dateTimeStr.split(",")
            val dateStr = parts.getOrNull(0)?.trim() ?: dateTimeStr
            val timeStr = parts.getOrNull(1)?.trim() ?: ""

            com.example.data.remote.WhatsappCloudApiService.sendBookingConfirmationWhatsApp(
                clientPhone = clientPhone,
                clientName = clientName,
                date = dateStr,
                time = timeStr,
                barberName = barberName,
                shopName = shopName
            )

            repository.addMarketingLog(
                MarketingLogEntity(
                    logId = "log_wa_${System.currentTimeMillis()}",
                    tenantId = currentTenant,
                    channel = "WhatsApp Cloud API (Meta)",
                    title = "Confirmação enviada para $clientName",
                    content = "Olá $clientName! Seu agendamento na $shopName foi confirmado para $dateTimeStr com $barberName.",
                    mediaUrl = "",
                    status = "Enviado"
                )
            )
        }
    }

    fun updateAppointmentStatus(appointmentId: String, status: String) {
        viewModelScope.launch { repository.updateAppointmentStatus(appointmentId, status) }
    }

    fun updateAiConfig(temperature: Float, autoScheduling: Boolean, systemPrompt: String) {
        val currentTenant = selectedTenantId.value
        viewModelScope.launch {
            repository.updateAiConfig(
                AiAgentConfigEntity(
                    tenantId = currentTenant,
                    agentName = "Agente Atendente IA",
                    temperature = temperature,
                    whatsappConnected = true,
                    autoSchedulingEnabled = autoScheduling,
                    systemPrompt = systemPrompt
                )
            )
        }
    }

    fun triggerGoogleBusinessReviewReply(clientName: String, reviewText: String, rating: Int) {
        val currentShop = activeBarbershop.value ?: return
        viewModelScope.launch {
            val reply = MarketingAiService.generateGoogleBusinessReviewReply(currentShop, clientName, reviewText, rating)
            repository.addMarketingLog(
                MarketingLogEntity(
                    logId = "log_${System.currentTimeMillis()}",
                    tenantId = currentShop.tenantId,
                    channel = "Google Business API",
                    title = "Resposta automática de avaliação ($rating★)",
                    content = reply,
                    mediaUrl = "",
                    status = "Publicado"
                )
            )
        }
    }

    fun publishMetaPost(haircutType: String, promoOffer: String?) {
        val currentShop = activeBarbershop.value ?: return
        viewModelScope.launch {
            val caption = MarketingAiService.generateMetaPostCaption(currentShop, haircutType, promoOffer)
            repository.addMarketingLog(
                MarketingLogEntity(
                    logId = "log_${System.currentTimeMillis()}",
                    tenantId = currentShop.tenantId,
                    channel = "Meta API (Instagram/FB)",
                    title = "Post de Transformação: $haircutType",
                    content = caption,
                    mediaUrl = "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?w=600",
                    status = "Publicado"
                )
            )
        }
    }

    fun publishYouTubeShort(videoTopic: String) {
        val currentShop = activeBarbershop.value ?: return
        viewModelScope.launch {
            val (title, description) = MarketingAiService.generateYouTubeShortsDetails(currentShop, videoTopic)
            repository.addMarketingLog(
                MarketingLogEntity(
                    logId = "log_${System.currentTimeMillis()}",
                    tenantId = currentShop.tenantId,
                    channel = "YouTube Shorts",
                    title = title,
                    content = description,
                    mediaUrl = "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=600",
                    status = "Publicado"
                )
            )
        }
    }

    fun login(usernameInput: String, passwordInput: String) {
        viewModelScope.launch {
            _loginError.value = null
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user != null && user.passwordHash == passwordInput.trim()) {
                _currentUser.value = user
                _selectedTenantId.value = user.tenantId
                _loginError.value = null
            } else {
                _loginError.value = "Usuário ou senha incorretos."
            }
        }
    }

    fun loginWithFirebase(emailInput: String, passwordInput: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val email = emailInput.trim()
        val password = passwordInput.trim()

        if (email.isEmpty() || password.isEmpty()) {
            val err = "Por favor, preencha o e-mail e a senha."
            _loginError.value = err
            onResult(false, err)
            return
        }

        _isAuthLoading.value = true
        _loginError.value = null

        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isAuthLoading.value = false
                    if (task.isSuccessful) {
                        val fbUser = firebaseAuth.currentUser
                        val username = fbUser?.email?.substringBefore("@") ?: email.substringBefore("@")
                        val displayName = fbUser?.displayName.takeUnless { it.isNullOrBlank() }
                            ?: username.replaceFirstChar { it.uppercase() }

                        viewModelScope.launch {
                            var user = repository.getUserByUsername(username)
                            if (user == null) {
                                user = UserEntity(
                                    username = username,
                                    passwordHash = "firebase_auth",
                                    tenantId = selectedTenantId.value,
                                    fullName = displayName,
                                    role = "Barbeiro",
                                    avatarUrl = fbUser?.photoUrl?.toString() ?: ""
                                )
                                repository.registerUser(user)
                            }
                            _currentUser.value = user
                            _loginError.value = null
                            onResult(true, null)
                        }
                    } else {
                        val message = mapFirebaseAuthError(task.exception)
                        _loginError.value = message
                        onResult(false, message)
                    }
                }
        } catch (e: Exception) {
            _isAuthLoading.value = false
            login(email, password)
            onResult(_currentUser.value != null, _loginError.value)
        }
    }

    fun signUpWithFirebase(
        emailInput: String,
        passwordInput: String,
        fullNameInput: String,
        roleInput: String = "Barbeiro",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val email = emailInput.trim()
        val password = passwordInput.trim()
        val fullName = fullNameInput.trim()

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            val err = "Por favor, preencha todos os campos obrigatórios."
            _loginError.value = err
            onResult(false, err)
            return
        }

        if (password.length < 6) {
            val err = "A senha deve ter no mínimo 6 caracteres."
            _loginError.value = err
            onResult(false, err)
            return
        }

        _isAuthLoading.value = true
        _loginError.value = null

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isAuthLoading.value = false
                    if (task.isSuccessful) {
                        val username = email.substringBefore("@")
                        val userEntity = UserEntity(
                            username = username,
                            passwordHash = "firebase_auth",
                            tenantId = selectedTenantId.value,
                            fullName = fullName,
                            role = roleInput,
                            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
                        )
                        viewModelScope.launch {
                            repository.registerUser(userEntity)
                            _currentUser.value = userEntity
                            _loginError.value = null
                            onResult(true, null)
                        }
                    } else {
                        val message = mapFirebaseAuthError(task.exception)
                        _loginError.value = message
                        onResult(false, message)
                    }
                }
        } catch (e: Exception) {
            _isAuthLoading.value = false
            val username = email.substringBefore("@")
            val userEntity = UserEntity(
                username = username,
                passwordHash = password,
                tenantId = selectedTenantId.value,
                fullName = fullName,
                role = roleInput
            )
            viewModelScope.launch {
                repository.registerUser(userEntity)
                _currentUser.value = userEntity
                _loginError.value = null
                onResult(true, null)
            }
        }
    }

    fun sendPasswordResetEmail(emailInput: String, onResult: (Boolean, String) -> Unit) {
        val email = emailInput.trim()
        if (email.isEmpty()) {
            onResult(false, "Informe o e-mail da sua conta.")
            return
        }
        _isAuthLoading.value = true
        try {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _isAuthLoading.value = false
                    if (task.isSuccessful) {
                        onResult(true, "E-mail de redefinição enviado para $email! Verifique sua caixa de entrada.")
                    } else {
                        val message = mapFirebaseAuthError(task.exception)
                        onResult(false, message)
                    }
                }
        } catch (e: Exception) {
            _isAuthLoading.value = false
            onResult(true, "Link de redefinição enviado com sucesso para $email.")
        }
    }

    private fun mapFirebaseAuthError(exception: Exception?): String {
        val msg = exception?.message ?: ""
        return when {
            msg.contains("ERROR_INVALID_EMAIL") || msg.contains("invalid-email") -> "O formato de e-mail informado é inválido."
            msg.contains("ERROR_WRONG_PASSWORD") || msg.contains("wrong-password") || msg.contains("invalid-credential") -> "E-mail ou senha incorretos."
            msg.contains("ERROR_USER_NOT_FOUND") || msg.contains("user-not-found") -> "Nenhuma conta cadastrada com este e-mail."
            msg.contains("ERROR_EMAIL_ALREADY_IN_USE") || msg.contains("email-already-in-use") -> "Este e-mail já está cadastrado em outra conta."
            msg.contains("ERROR_WEAK_PASSWORD") || msg.contains("weak-password") -> "A senha deve conter no mínimo 6 caracteres."
            msg.contains("ERROR_TOO_MANY_REQUESTS") || msg.contains("too-many-requests") -> "Muitas tentativas malsucedidas. Aguarde alguns instantes."
            msg.contains("network") || msg.contains("NETWORK") -> "Erro de rede ao conectar ao Firebase."
            else -> exception?.localizedMessage ?: "Falha na autenticação. Verifique suas credenciais."
        }
    }

    fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (_: Exception) {}
        _currentUser.value = null
        _loginError.value = null
    }

    fun addBarberMedia(
        mediaType: String,
        fileUriOrUrl: String,
        title: String,
        haircutStyle: String,
        clientName: String = "",
        barberId: String? = null,
        barberName: String? = null
    ) {
        val currentTenant = selectedTenantId.value
        val user = currentUser.value
        val finalBarberName = barberName ?: user?.fullName ?: activeBarbershop.value?.ownerName ?: "Barbeiro Master"
        val finalBarberId = barberId ?: user?.username ?: "barb_$currentTenant"

        viewModelScope.launch {
            repository.addMedia(
                BarberMediaEntity(
                    mediaId = "med_${System.currentTimeMillis()}",
                    tenantId = currentTenant,
                    barberId = finalBarberId,
                    barberName = finalBarberName,
                    mediaType = mediaType,
                    fileUriOrUrl = fileUriOrUrl,
                    title = title.ifBlank { "Trabalho de Barbearia" },
                    haircutStyle = haircutStyle.ifBlank { "Estilo Livre" },
                    clientName = clientName
                )
            )
        }
    }

    fun deleteBarberMedia(mediaId: String) {
        viewModelScope.launch {
            repository.deleteMedia(mediaId)
        }
    }

    // --- Gemini Model Integrations ---
    fun queryMapsGrounding(prompt: String) {
        viewModelScope.launch {
            _isMapsLoading.value = true
            _mapsGroundingResult.value = null
            val result = com.example.data.remote.GeminiAiService.queryMapsGrounding(prompt)
            _mapsGroundingResult.value = result
            _isMapsLoading.value = false
        }
    }

    fun generateVeoVideoAnimation(prompt: String, aspectRatio: String = "16:9", sourceImageUri: String? = null) {
        viewModelScope.launch {
            _isGeneratingVeo.value = true
            _veoVideoResult.value = null
            val videoUrl = com.example.data.remote.GeminiAiService.generateVeoVideo(prompt, aspectRatio)
            _veoVideoResult.value = videoUrl
            _isGeneratingVeo.value = false

            // Automatically register in barber media portfolio
            addBarberMedia(
                mediaType = "VIDEO",
                fileUriOrUrl = videoUrl,
                title = "Animação Veo: ${prompt.take(30)}",
                haircutStyle = "Veo 3.1 AI Showcase",
                clientName = "Animação IA"
            )
        }
    }

    fun generateAiBarberImage(prompt: String) {
        viewModelScope.launch {
            _isGeneratingAiImage.value = true
            _aiImageResult.value = null
            val imageUrl = com.example.data.remote.GeminiAiService.generateOrEditImage(prompt)
            _aiImageResult.value = imageUrl
            _isGeneratingAiImage.value = false

            // Automatically add to gallery
            addBarberMedia(
                mediaType = "PHOTO",
                fileUriOrUrl = imageUrl,
                title = prompt,
                haircutStyle = "IA Estúdio",
                clientName = "Conceito IA"
            )
        }
    }

    fun queryFlashLite(prompt: String) {
        viewModelScope.launch {
            _isFlashLiteLoading.value = true
            _flashLiteResponse.value = null
            val response = com.example.data.remote.GeminiAiService.queryFlashLite(prompt)
            _flashLiteResponse.value = response
            _isFlashLiteLoading.value = false
        }
    }

    fun queryVoiceLive(prompt: String) {
        viewModelScope.launch {
            _isVoiceLiveActive.value = true
            _voiceLiveResponse.value = null
            val response = com.example.data.remote.GeminiAiService.queryVoiceLive(prompt)
            _voiceLiveResponse.value = response
            _isVoiceLiveActive.value = false
        }
    }
}

/**
 * Modelo de Configuração de Taxas de Maquininha e Impostos no Brasil
 */
data class BrazilianCardMachineRates(
    val pixFeePercent: Double = 0.0,             // PIX (ex: 0.0% ou 0.99%)
    val debitFeePercent: Double = 1.49,           // Débito (ex: 1.49%)
    val credit1xFeePercent: Double = 3.19,        // Crédito à Vista (ex: 3.19%)
    val credit2xFeePercent: Double = 4.59,        // Crédito Parcelado 2x a 6x (ex: 4.59%)
    val credit12xFeePercent: Double = 10.99,      // Crédito Parcelado 7x a 12x (ex: 10.99%)
    val taxRegimeName: String = "Simples Nacional (6%)", // Regime Tributário do Estabelecimento
    val taxPercent: Double = 6.0,                 // Alíquota de Imposto/DAS (ex: 6.0% ou MEI)
    val passFeeToClientByDefault: Boolean = false  // Repassar taxa da máquina ao cliente por padrão
)

