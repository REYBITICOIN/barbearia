package com.example.ui.screens

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.BarberLabViewModel
import com.example.ui.ChatMessage
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutonomousAgentScreen(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    var selectedAiSubTab by remember { mutableStateOf(0) }
    // 0: WhatsApp Bot, 1: Google Maps Grounding, 2: Veo Video Animation, 3: Imagen Image Studio, 4: Flash-Lite & Voice Live

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // AI Header Sub-Tabs Selector
        ScrollableTabRow(
            selectedTabIndex = selectedAiSubTab,
            edgePadding = 0.dp,
            containerColor = BarberDarkCard,
            contentColor = primaryColor,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedAiSubTab == 0,
                onClick = { selectedAiSubTab = 0 },
                text = { Text("WhatsApp Bot", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_whatsapp")
            )
            Tab(
                selected = selectedAiSubTab == 1,
                onClick = { selectedAiSubTab = 1 },
                text = { Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_maps")
            )
            Tab(
                selected = selectedAiSubTab == 2,
                onClick = { selectedAiSubTab = 2 },
                text = { Text("Animação Veo", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_veo")
            )
            Tab(
                selected = selectedAiSubTab == 3,
                onClick = { selectedAiSubTab = 3 },
                text = { Text("Estúdio Imagens", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_image")
            )
            Tab(
                selected = selectedAiSubTab == 4,
                onClick = { selectedAiSubTab = 4 },
                text = { Text("Flash-Lite & Voz", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_fast")
            )
        }

        when (selectedAiSubTab) {
            0 -> WhatsAppBotView(viewModel = viewModel, primaryColor = primaryColor)
            1 -> GoogleMapsGroundingView(viewModel = viewModel, primaryColor = primaryColor)
            2 -> VeoVideoAnimationView(viewModel = viewModel, primaryColor = primaryColor)
            3 -> ImagenStudioView(viewModel = viewModel, primaryColor = primaryColor)
            4 -> FlashLiteVoiceView(viewModel = viewModel, primaryColor = primaryColor)
        }
    }
}

@Composable
fun WhatsAppBotView(viewModel: BarberLabViewModel, primaryColor: Color) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isConnected by viewModel.isWhatsAppConnected.collectAsState()
    val connectedPhone by viewModel.whatsappPhone.collectAsState()
    val sessionId by viewModel.whatsappSessionId.collectAsState()
    val remindersSent by viewModel.whatsappRemindersSent.collectAsState()
    val appointments by viewModel.appointments.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showQrModal by remember { mutableStateOf(false) }
    var showRemindersModal by remember { mutableStateOf(false) }
    var selectedWhatsAppSubTab by remember { mutableStateOf(0) } // 0: Chat Simulator, 1: Reminders & Broadcasts
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- 1. WHATSAPP WEB CONNECTION STATUS HEADER BAR ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color(0xFF25D366).copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "WhatsApp Web QR Code",
                                tint = if (isConnected) Color(0xFF25D366) else Color.Red,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WHATSAPP WEB CONECTADO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (isConnected) Color(0xFF25D366) else Color.Red,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isConnected) "ONLINE ⚡" else "DESCONECTADO",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isConnected) "$connectedPhone • Sessão: $sessionId" else "Escaneie o QR Code para conectar a barbearia",
                                fontSize = 11.sp,
                                color = if (isConnected) Color(0xFF25D366) else Color.Gray
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showQrModal = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnected) Color(0xFF2E3842) else Color(0xFF25D366),
                                contentColor = if (isConnected) Color.White else Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_connect_qr_code")
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isConnected) "Reconectar QR" else "Escanear QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Divider(color = Color(0xFF2E3842))

                Spacer(modifier = Modifier.height(8.dp))

                // Action Bar for Quick Reminders and Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📲 $remindersSent lembretes disparados",
                            fontSize = 11.sp,
                            color = BarberAiCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "🤖 IA Atendente Ativa",
                            fontSize = 11.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { showRemindersModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_send_client_reminders")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enviar Lembretes (${appointments.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2. SUB-TABS SELECTOR: WEBVIEW vs. CHAT SIMULATOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedWhatsAppSubTab == 0,
                onClick = { selectedWhatsAppSubTab = 0 },
                label = { Text("🌐 WhatsApp Web Live (WebView)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF25D366),
                    selectedLabelColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_whatsapp_webview_live")
            )

            FilterChip(
                selected = selectedWhatsAppSubTab == 1,
                onClick = { selectedWhatsAppSubTab = 1 },
                label = { Text("🤖 Simulador Chat IA", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BarberAiCyan,
                    selectedLabelColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_whatsapp_chat_simulator")
            )
        }

        if (selectedWhatsAppSubTab == 0) {
            // --- EMBEDDED WHATSAPP WEB WEBVIEW ---
            WhatsAppWebViewCard(
                primaryColor = primaryColor,
                modifier = Modifier.weight(1f)
            )
        } else {
            // --- WHATSAPP CHAT INTERFACE SIMULATION ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1418)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat header
                    Surface(
                        color = Color(0xFF1F2C34),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = activeShop?.name ?: "BarberLab", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = if (isConnected) "WhatsApp Web Ativo • Atendente IA 24/7" else "Desconectado do WhatsApp",
                                    color = if (isConnected) Color(0xFF25D366) else Color.Red,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Chat Messages List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatMessages) { msg ->
                            ChatMessageBubble(msg = msg, primaryColor = primaryColor)
                        }

                        if (isThinking) {
                            item {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = BarberAiCyan,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Agente IA consultando horários...", color = BarberAiCyan, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Chat Input Bar
                    Surface(
                        color = Color(0xFF1F2C34),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Simular mensagem do cliente...", color = Color.Gray, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field")
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        val text = inputText
                                        inputText = ""
                                        viewModel.sendChatMessage(text)
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(primaryColor)
                                    .testTag("send_chat_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.Black)
                            }
                        }
                    }
                }
            }

            // Quick Suggestion Chips for Testing
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val suggestions = listOf(
                    "Quais os preços dos serviços?",
                    "Tem horário livre hoje às 16:00?",
                    "Quero agendar um Combo Cabelo + Barba"
                )
                items(suggestions) { sugg ->
                    SuggestionChip(
                        onClick = { inputText = sugg },
                        label = { Text(sugg, fontSize = 11.sp) },
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = primaryColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }

    // --- MODAL: CONECTAR WHATSAPP WEB VIA QR CODE ---
    if (showQrModal) {
        WhatsAppQrCodeModal(
            viewModel = viewModel,
            primaryColor = primaryColor,
            onDismiss = { showQrModal = false }
        )
    }

    // --- MODAL: ENVIAR LEMBRETES DE AGENDAMENTO ---
    if (showRemindersModal) {
        WhatsAppRemindersModal(
            viewModel = viewModel,
            appointments = appointments,
            primaryColor = primaryColor,
            onDismiss = { showRemindersModal = false }
        )
    }
}

@Composable
fun WhatsAppQrCodeModal(
    viewModel: BarberLabViewModel,
    primaryColor: Color,
    onDismiss: () -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current

    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanProgress = 0f
            while (scanProgress < 1f) {
                kotlinx.coroutines.delay(200)
                scanProgress += 0.2f
            }
            viewModel.connectWhatsAppWeb("+55 (11) 98765-4321")
            Toast.makeText(context, "WhatsApp Web conectado com sucesso!", Toast.LENGTH_SHORT).show()
            isScanning = false
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF25D366))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Conectar WhatsApp Web", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aponte a câmera do seu celular para esta tela para parear o WhatsApp da sua barbearia.",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                // Simulated Interactive QR Code Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=BARBERLAB-WHATSAPP-WEB-CONNECT-SESSION-8F92A",
                            contentDescription = "QR Code WhatsApp Web",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        if (isScanning) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color(0xFF25D366))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Validando chave de sessão...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Instructions list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E252B))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("1. Abra o WhatsApp no seu celular", fontSize = 11.sp, color = Color.White)
                    Text("2. Toque em Configurações > Aparelhos Conectados", fontSize = 11.sp, color = Color.White)
                    Text("3. Toque em 'Conectar um Aparelho'", fontSize = 11.sp, color = Color.White)
                    Text("4. Aponte para este QR Code para ativar o robô 24/7", fontSize = 11.sp, color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { isScanning = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.Black),
                enabled = !isScanning,
                modifier = Modifier.testTag("btn_simulated_qr_scan")
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Escanear & Parear Agora", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun WhatsAppRemindersModal(
    viewModel: BarberLabViewModel,
    appointments: List<com.example.data.local.AppointmentEntity>,
    primaryColor: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disparar Lembretes WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Selecione um cliente com agendamento para enviar uma mensagem de confirmação via WhatsApp Web:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                if (appointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E252B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum agendamento encontrado.", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 250.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(appointments) { apt ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E252B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = apt.clientName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text(text = "${apt.serviceName} • ${apt.dateTimeStr}", fontSize = 11.sp, color = Color.LightGray)
                                        Text(text = "Fone: ${apt.clientPhone}", fontSize = 10.sp, color = Color(0xFF25D366))
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.sendAppointmentReminder(apt)
                                            Toast.makeText(context, "Lembrete enviado para ${apt.clientName}!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.Black),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Enviar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black)
            ) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ChatMessageBubble(msg: ChatMessage, primaryColor: Color) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Color(0xFF005C4B) else Color(0xFF202C33),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Agente Atendente (Temp 0.2)", color = BarberAiCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (msg.isAppointmentConfirmed) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = BarberAiCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Agendado no Banco de Dados!", color = BarberAiCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapsGroundingView(viewModel: BarberLabViewModel, primaryColor: Color) {
    var queryText by remember { mutableStateOf("Fornecedores de cosméticos e lâminas para barbearia") }
    val mapsResult by viewModel.mapsGroundingResult.collectAsState()
    val isLoading by viewModel.isMapsLoading.collectAsState()

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = primaryColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Google Maps Grounding", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("Modelo: gemini-3.5-flash (com ferramenta googleMaps)", fontSize = 11.sp, color = BarberAiCyan)
                }
            }

            Text(
                "Pesquise fornecedores de cosméticos, distribuidores de equipamentos ou barbearias concorrentes com dados geográficos atualizados:",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                label = { Text("Busca Geográfica / Fornecedores") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_maps_query"),
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.queryMapsGrounding(queryText) },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = primaryColor)
                    }
                }
            )

            Button(
                onClick = { viewModel.queryMapsGrounding(queryText) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_execute_maps_grounding"),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultando Google Maps...")
                } else {
                    Icon(Icons.Default.PinDrop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar via Google Maps Grounding", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            mapsResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E252B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Resultado Google Maps:", fontWeight = FontWeight.Bold, color = BarberAiCyan, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(result, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun VeoVideoAnimationView(viewModel: BarberLabViewModel, primaryColor: Color) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("Showcase 360 do corte Mid Fade com risquinho e iluminação de estúdio") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }

    val videoUrl by viewModel.veoVideoResult.collectAsState()
    val isGenerating by viewModel.isGeneratingVeo.collectAsState()

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = primaryColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Animação de Vídeo Veo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("Modelo: veo-3.1-fast-generate-preview (16:9 ou 9:16)", fontSize = 11.sp, color = BarberAiCyan)
                }
            }

            Text(
                "Crie animações cinematográficas em vídeo dos cortes de cabelo para publicar em Reels, TikTok ou Stories:",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("Descreva a animação do vídeo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_veo_prompt")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Formato do Vídeo:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedAspectRatio == "16:9",
                        onClick = { selectedAspectRatio = "16:9" },
                        label = { Text("16:9 (Landscape)") }
                    )
                    FilterChip(
                        selected = selectedAspectRatio == "9:16",
                        onClick = { selectedAspectRatio = "9:16" },
                        label = { Text("9:16 (Reels/Stories)") }
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.generateVeoVideoAnimation(promptText, selectedAspectRatio)
                    Toast.makeText(context, "Gerando vídeo via Veo 3.1 Fast...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_veo_video"),
                enabled = !isGenerating
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processando Vídeo no Veo AI...")
                } else {
                    Icon(Icons.Default.Videocam, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gerar Vídeo de Animação Veo", fontWeight = FontWeight.Bold)
                }
            }

            videoUrl?.let { url ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Vídeo Gerado com Sucesso! (Adicionado à Galeria):", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = "Veo Video Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenStudioView(viewModel: BarberLabViewModel, primaryColor: Color) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("Retrato em alta definição de um corte Taper Fade com barba alinhada") }
    val imageUrl by viewModel.aiImageResult.collectAsState()
    val isGenerating by viewModel.isGeneratingAiImage.collectAsState()

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Image, contentDescription = null, tint = primaryColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Criador & Editor de Imagens IA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("Modelo: gemini-3.1-flash-image-preview", fontSize = 11.sp, color = BarberAiCyan)
                }
            }

            Text(
                "Crie conceitos de corte ou edite fotos existentes digitando instruções em texto para a inteligência artificial:",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("Prompt para Criar/Editar Foto de Corte") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_imagen_prompt")
            )

            Button(
                onClick = {
                    viewModel.generateAiBarberImage(promptText)
                    Toast.makeText(context, "Criando conceito visual de corte...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_imagen_photo"),
                enabled = !isGenerating
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Renderizando Foto com Gemini Flash...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gerar Foto de Corte com IA", fontWeight = FontWeight.Bold)
                }
            }

            imageUrl?.let { img ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Conceito Visual Gerado:", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = img,
                                contentDescription = "Conceito IA",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashLiteVoiceView(viewModel: BarberLabViewModel, primaryColor: Color) {
    var fastQuery by remember { mutableStateOf("Qual o tempo exato de descoloração para cabelos escuros?") }
    var voiceQuery by remember { mutableStateOf("Quais os próximos cortes agendados?") }

    val flashResponse by viewModel.flashLiteResponse.collectAsState()
    val isFlashLoading by viewModel.isFlashLiteLoading.collectAsState()

    val voiceResponse by viewModel.voiceLiveResponse.collectAsState()
    val isVoiceActive by viewModel.isVoiceLiveActive.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section 1: Flash Lite
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Atendimento Ultra-Rápido", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                    Text("Modelo: gemini-3.1-flash-lite-preview (Baixa latência <100ms)", fontSize = 11.sp, color = BarberAiCyan)

                    OutlinedTextField(
                        value = fastQuery,
                        onValueChange = { fastQuery = it },
                        label = { Text("Dúvida rápida de bancada") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_flash_lite_query")
                    )

                    Button(
                        onClick = { viewModel.queryFlashLite(fastQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = BarberAiCyan, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_query_flash_lite"),
                        enabled = !isFlashLoading
                    ) {
                        if (isFlashLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                        } else {
                            Icon(Icons.Default.FlashOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Perguntar Instantaneamente", fontWeight = FontWeight.Bold)
                        }
                    }

                    flashResponse?.let { resp ->
                        Surface(
                            color = Color(0xFF1E252B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(resp, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }

        // Section 2: Voice Live API
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Assistente por Voz (Live API)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                    Text("Modelo: gemini-3.1-flash-live-preview (Conversa em tempo real mãos livres)", fontSize = 11.sp, color = primaryColor)

                    OutlinedTextField(
                        value = voiceQuery,
                        onValueChange = { voiceQuery = it },
                        label = { Text("Comando de voz para o assistente") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_voice_live_query")
                    )

                    Button(
                        onClick = { viewModel.queryVoiceLive(voiceQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_query_voice_live"),
                        enabled = !isVoiceActive
                    ) {
                        if (isVoiceActive) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ouvindo e Processando Voz...")
                        } else {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Falar com Assistente de Voz", fontWeight = FontWeight.Bold)
                        }
                    }

                    voiceResponse?.let { resp ->
                        Surface(
                            color = Color(0xFF1E252B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(resp, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsAppWebViewCard(
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentUrl by remember { mutableStateOf("https://web.whatsapp.com") }
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WebView Top Browser Control Bar
            Surface(
                color = Color(0xFF111B21),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "WhatsApp Web (Modo Desktop)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentUrl,
                                fontSize = 10.sp,
                                color = Color(0xFF25D366),
                                maxLines = 1
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { webViewRef?.reload() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://web.whatsapp.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao abrir navegador", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Navegador Externo", tint = BarberAiCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF25D366),
                    trackColor = Color(0xFF1E252B),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                HorizontalDivider(color = Color(0xFF2E3842))
            }

            // Embedded Android WebView
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0B141A))
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                allowFileAccess = true
                                allowContentAccess = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false

                                // Desktop user agent is required so WhatsApp Web presents the QR code & chat interface
                                userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    url?.let { currentUrl = it }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    url?.let { currentUrl = it }
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    return false
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    progress = newProgress / 100f
                                    if (newProgress == 100) {
                                        isLoading = false
                                    }
                                }
                            }

                            loadUrl("https://web.whatsapp.com")
                            webViewRef = this
                        }
                    },
                    update = { view ->
                        webViewRef = view
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("whatsapp_web_webview")
                )
            }
        }
    }
}
