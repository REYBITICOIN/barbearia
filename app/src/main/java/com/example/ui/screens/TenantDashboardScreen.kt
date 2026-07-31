package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.BarbershopEntity
import com.example.ui.BarberLabViewModel
import com.example.ui.BrazilianCardMachineRates
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor
import com.example.util.TaxCalculator
import com.example.ui.components.TaxCalculatorCard
import com.example.ui.components.TenantAnalyticsDashboardChartCard
import com.example.ui.components.BarberLogoVideoAvatar
import com.example.ui.components.BarberAppSplashScreen
import com.example.ui.components.PopularCutCategoriesSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDashboardScreen(
    viewModel: BarberLabViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val barbershops by viewModel.barbershops.collectAsState()
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val services by viewModel.services.collectAsState()
    val barberMedia by viewModel.barberMedia.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cardMachineRates by viewModel.cardMachineRates.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var showNewTenantModal by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showMachineRatesModal by remember { mutableStateOf(false) }
    var showVideoSplashFullscreen by remember { mutableStateOf(false) }
    var barberStatus by remember { mutableStateOf("Disponível na Bancada") }

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    if (showVideoSplashFullscreen) {
        BarberAppSplashScreen(
            shopName = activeShop?.name ?: "Barbearia do João - Corte Moderno e Clássico",
            onDismiss = { showVideoSplashFullscreen = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. PERFIL DO BARBEIRO AUTENTICADO ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Barber Video Logo Avatar (Substitui a foto estática do barbeiro)
                            BarberLogoVideoAvatar(
                                primaryColor = primaryColor,
                                shopName = activeShop?.name ?: "Barbearia do João - Corte Moderno e Clássico",
                                onClick = { showVideoSplashFullscreen = true }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.fullName ?: activeShop?.ownerName ?: "Barbeiro Master",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = primaryColor,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = currentUser?.role?.uppercase() ?: "PROPRIETÁRIO",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Especialidade: Mestre em Fade & Barba",
                                    fontSize = 12.sp,
                                    color = BarberAiCyan
                                )

                                Text(
                                    text = "Barbearia: ${activeShop?.name ?: "BarberLab Studio"}",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Theme Selector Button
                            IconButton(
                                onClick = { viewModel.toggleDarkMode() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("btn_dashboard_theme_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Modo Claro" else "Modo Escuro",
                                    tint = if (isDarkMode) BarberGold else BarberAiCyan
                                )
                            }

                            // Profile Button
                            IconButton(
                                onClick = { showProfileModal = true },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("btn_barber_profile")
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Perfil", tint = primaryColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = Color(0xFF2E3842))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Barbeiro Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (barberStatus.contains("Disponível")) Color(0xFF4CAF50) else Color(0xFFFF9800))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status: $barberStatus",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        TextButton(
                            onClick = {
                                barberStatus = if (barberStatus.contains("Disponível")) "Em Atendimento ✂️" else "Disponível na Bancada"
                            }
                        ) {
                            Text("Alternar Status", fontSize = 11.sp, color = primaryColor)
                        }
                    }
                }
            }
        }

        // --- SEÇÃO DE CATEGORIAS DE CORTE (MODELO WEB: SERVIÇOS POPULARES) ---
        item {
            PopularCutCategoriesSection(
                primaryColor = primaryColor,
                services = services
            )
        }

        // --- 2. NAVEGAÇÃO PRINCIPAL (HUB: PERFIL, GALERIA, CONFIGURAÇÕES) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "NAVEGAÇÃO RÁPIDA DO ESTÚDIO",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Shortcut 1: Perfil do Barbeiro
                    MainHubCard(
                        title = "Perfil Barbeiro",
                        subtitle = "Sua Conta & Status",
                        icon = Icons.Default.Badge,
                        accentColor = primaryColor,
                        onClick = { showProfileModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_card_profile")
                    )

                    // Shortcut 2: Galeria do Portfólio
                    MainHubCard(
                        title = "Galeria Fotos",
                        subtitle = "${barberMedia.size} Mídias Salvas",
                        icon = Icons.Default.PhotoLibrary,
                        accentColor = BarberAiCyan,
                        onClick = { onNavigateToTab(1) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_card_gallery")
                    )

                    // Shortcut 3: Configurações Barbearia
                    MainHubCard(
                        title = "Configurações",
                        subtitle = "Marca & Cores",
                        icon = Icons.Default.Settings,
                        accentColor = Color(0xFFFF9800),
                        onClick = { onNavigateToTab(2) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_card_settings")
                    )
                }
            }
        }

        // --- 3. GALERIA DE FOTOS (PREVIEW DO PORTFÓLIO COM COIL) ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Collections, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GALERIA DO PORTFÓLIO DE CORTES",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        TextButton(onClick = { onNavigateToTab(1) }) {
                            Text("Ver Galeria Completa (${barberMedia.size})", color = BarberAiCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (barberMedia.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E252B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nenhum corte fotografado ainda. Acesse a Câmera!", fontSize = 12.sp, color = Color.Gray)
                        }
                    } else {
                        // Horizontal Preview Row of Haircuts using Coil
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(barberMedia.take(6)) { media ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E252B)),
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { onNavigateToTab(1) }
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                        ) {
                                            AsyncImage(
                                                model = media.fileUriOrUrl,
                                                contentDescription = media.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = media.haircutStyle,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = media.barberName,
                                                fontSize = 9.sp,
                                                color = primaryColor,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. CONFIGURAÇÕES DA BARBEARIA & SELETOR DE TENANT ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Tenant",
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "CONFIGURAÇÕES DA BARBEARIA (TENANT)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Barbearia Ativa: ${activeShop?.name ?: "BarberLab"}",
                                    fontSize = 11.sp,
                                    color = primaryColor
                                )
                            }
                        }

                        Button(
                            onClick = { showNewTenantModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_tenant_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nova Unidade", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tenants horizontal chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(barbershops) { shop ->
                            val isSelected = shop.tenantId == activeShop?.tenantId
                            val chipPrimary = parseHexColor(shop.primaryColorHex, BarberGold)

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) chipPrimary.copy(alpha = 0.2f) else Color(0xFF1E252B),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, chipPrimary) else null,
                                modifier = Modifier
                                    .clickable { viewModel.selectTenant(shop.tenantId) }
                                    .testTag("tenant_chip_${shop.tenantId}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(chipPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = shop.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) chipPrimary else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barbershop Details Quick Action Bar
                    activeShop?.let { shop ->
                        Surface(
                            color = Color(0xFF1E252B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Endereço: ${shop.address}", fontSize = 11.sp, color = Color.White)
                                    Text("WhatsApp: ${shop.phone} • Cor: ${shop.primaryColorHex}", fontSize = 11.sp, color = Color.LightGray)
                                }

                                Button(
                                    onClick = { onNavigateToTab(2) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3842), contentColor = primaryColor),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Personalizar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. PAINEL DE ANÁLISE DE DADOS E IMPOSTOS (DASHBOARD VISUALIZATION) ---
        item {
            TenantAnalyticsDashboardChartCard(
                primaryColor = primaryColor
            )
        }

        // --- 6. CALCULADORA BRASILEIRA DE TAXAS DA MAQUININHA & IMPOSTOS ---
        item {
            BrazilianTaxCalculatorCard(
                rates = cardMachineRates,
                primaryColor = primaryColor,
                onEditRatesClick = { showMachineRatesModal = true }
            )
        }

        // --- 6. AGENDAMENTOS RECENTES ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRÓXIMOS CLIENTES AGENDADOS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        TextButton(onClick = { onNavigateToTab(5) }) {
                            Text("Ver Agenda", color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (appointments.isEmpty()) {
                        Text(
                            text = "Nenhum agendamento para esta unidade hoje.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        appointments.take(3).forEach { apt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (apt.createdByAi) BarberAiCyan.copy(alpha = 0.2f) else primaryColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (apt.createdByAi) Icons.Default.SmartToy else Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (apt.createdByAi) BarberAiCyan else primaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = apt.clientName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text(text = "${apt.serviceName} • ${apt.dateTimeStr}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                    }
                                }

                                Text(
                                    text = "R$ ${"%.2f".format(apt.price)}",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    fontSize = 13.sp
                                )
                            }
                            Divider(color = Color(0xFF2E3842))
                        }
                    }
                }
            }
        }
    }

    // Modal Barber Profile
    if (showProfileModal) {
        BarberProfileModal(
            currentUser = currentUser,
            activeShop = activeShop,
            primaryColor = primaryColor,
            onDismiss = { showProfileModal = false }
        )
    }

    // Modal New Tenant
    if (showNewTenantModal) {
        NewTenantModal(
            onDismiss = { showNewTenantModal = false },
            onCreate = { name, owner, phone, address, primaryHex ->
                viewModel.createNewBarbershop(
                    name = name,
                    ownerName = owner,
                    phone = phone,
                    address = address,
                    facadePhotoUrl = "",
                    ownerPhotoUrl = "",
                    primaryColorHex = primaryHex
                )
                showNewTenantModal = false
            }
        )
    }

    // Modal Tax & Machine Fee Config
    if (showMachineRatesModal) {
        BrazilianTaxMachineRatesModal(
            currentRates = cardMachineRates,
            primaryColor = primaryColor,
            onSave = { updatedRates ->
                viewModel.updateCardMachineRates(updatedRates)
                showMachineRatesModal = false
            },
            onDismiss = { showMachineRatesModal = false }
        )
    }
}

@Composable
fun MainHubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.LightGray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BarberProfileModal(
    currentUser: com.example.data.local.UserEntity?,
    activeShop: BarbershopEntity?,
    primaryColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Perfil do Barbeiro Autenticado", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, primaryColor, CircleShape)
                ) {
                    val avatar = currentUser?.avatarUrl?.ifBlank { null }
                        ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
                    AsyncImage(
                        model = avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = currentUser?.fullName ?: activeShop?.ownerName ?: "Barbeiro Master",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Surface(color = primaryColor, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "CARGO: ${currentUser?.role?.uppercase() ?: "PROPRIETÁRIO"}",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Divider(color = Color.DarkGray)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("• Usuário: @${currentUser?.username ?: "master"}", fontSize = 12.sp, color = Color.LightGray)
                    Text("• E-mail: ${currentUser?.username ?: "barbeiro"}@barberlab.com", fontSize = 12.sp, color = Color.LightGray)
                    Text("• Telefone: ${activeShop?.phone ?: "(11) 99999-9999"}", fontSize = 12.sp, color = Color.LightGray)
                    Text("• Especialidade: Mestre em Fade & Barba Imperial", fontSize = 12.sp, color = BarberAiCyan)
                    Text("• Unidade Ativa: ${activeShop?.name ?: "BarberLab Unit 1"}", fontSize = 12.sp, color = primaryColor)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black)
            ) {
                Text("Fechar Perfil", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun NewTenantModal(
    onDismiss: () -> Unit,
    onCreate: (name: String, owner: String, phone: String, address: String, primaryHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var primaryHex by remember { mutableStateOf("#D4AF37") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cadastrar Nova Barbearia (Tenant)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Barbearia") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tenant_name")
                )
                OutlinedTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = { Text("Nome do Proprietário / Mestre Barbeiro") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefone WhatsApp") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço Completo") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, owner, phone, address, primaryHex)
                    }
                },
                modifier = Modifier.testTag("submit_tenant_button")
            ) {
                Text("Criar Tenant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun BrazilianTaxCalculatorCard(
    rates: BrazilianCardMachineRates,
    primaryColor: Color,
    onEditRatesClick: () -> Unit
) {
    var inputValueStr by remember { mutableStateOf("80.00") }
    var selectedMethodIndex by remember { mutableIntStateOf(2) } // Default: Crédito 1x
    var passFeeToClient by remember { mutableStateOf(rates.passFeeToClientByDefault) }

    val baseValue = inputValueStr.replace(",", ".").toDoubleOrNull() ?: 0.0

    val (methodName, feePercent) = when (selectedMethodIndex) {
        0 -> "PIX" to rates.pixFeePercent
        1 -> "Cartão Débito" to rates.debitFeePercent
        2 -> "Crédito à Vista (1x)" to rates.credit1xFeePercent
        3 -> "Crédito 2x a 6x" to rates.credit2xFeePercent
        else -> "Crédito 7x a 12x" to rates.credit12xFeePercent
    }

    val taxPercent = rates.taxPercent

    // Tax calculation using TaxCalculator utility class
    val calcResult = TaxCalculator.calculateNetEarnings(
        servicePrice = baseValue,
        machineFeePercent = feePercent,
        taxPercent = taxPercent,
        passFeeToClient = passFeeToClient,
        paymentMethodName = methodName,
        taxRegimeName = rates.taxRegimeName
    )

    val chargedToClient = calcResult.chargedToClient
    val machineFeeReais = calcResult.machineFeeAmount
    val taxReais = calcResult.taxAmount
    val netReceived = calcResult.netEarnings

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CALCULADORA DE TAXAS DA MAQUININHA (BRASIL)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Regime: ${rates.taxRegimeName} (${rates.taxPercent}%)",
                            fontSize = 10.sp,
                            color = BarberAiCyan
                        )
                    }
                }

                Button(
                    onClick = onEditRatesClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3842), contentColor = primaryColor),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_configure_machine_rates")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Taxas", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Preset Price Chips
            Text("Presets Rápidos de Serviços:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val presets = listOf("35.00" to "Corte", "50.00" to "Barba", "80.00" to "Combo Master", "120.00" to "Noivo")
                items(presets) { (valStr, label) ->
                    SuggestionChip(
                        onClick = { inputValueStr = valStr },
                        label = { Text("R$ $valStr ($label)", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (inputValueStr == valStr) primaryColor.copy(alpha = 0.2f) else Color(0xFF1E252B),
                            labelColor = if (inputValueStr == valStr) primaryColor else Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Value Input Field
            OutlinedTextField(
                value = inputValueStr,
                onValueChange = { inputValueStr = it },
                label = { Text(if (passFeeToClient) "Valor Desejado Líquido no Caixa (R$)" else "Valor do Serviço / Passado na Máquina (R$)") },
                prefix = { Text("R$ ", color = primaryColor, fontWeight = FontWeight.Bold) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFF2E3842),
                    focusedLabelColor = primaryColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tax_calculator_value")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Methods Selector
            Text("Forma de Pagamento na Maquininha:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val methods = listOf(
                    "⚡ PIX (${rates.pixFeePercent}%)",
                    "💳 Débito (${rates.debitFeePercent}%)",
                    "💳 Crédito 1x (${rates.credit1xFeePercent}%)",
                    "💳 Parcelado 2-6x (${rates.credit2xFeePercent}%)",
                    "💳 Parcelado 7-12x (${rates.credit12xFeePercent}%)"
                )
                itemsIndexed(methods) { index, label ->
                    FilterChip(
                        selected = selectedMethodIndex == index,
                        onClick = { selectedMethodIndex = index },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF1E252B),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Pass Fee to Client
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E252B))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Repassar Taxas ao Cliente?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Calcula o valor exato a ser cobrado no cartão", fontSize = 10.sp, color = Color.Gray)
                }

                Switch(
                    checked = passFeeToClient,
                    onCheckedChange = { passFeeToClient = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = primaryColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calculation Results Box
            Surface(
                color = Color(0xFF131A20),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Valor Cobrado no Cartão:", fontSize = 12.sp, color = Color.LightGray)
                        Text("R$ ${"%.2f".format(chargedToClient)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("(-) Taxa Maquininha $methodName ($feePercent%):", fontSize = 11.sp, color = Color(0xFFFF6B6B))
                        Text("- R$ ${"%.2f".format(machineFeeReais)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B6B))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("(-) Imposto Previsto ($taxPercent%):", fontSize = 11.sp, color = Color(0xFFFF6B6B))
                        Text("- R$ ${"%.2f".format(taxReais)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B6B))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF2E3842))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VALOR LÍQUIDO NO CAIXA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = BarberAiCyan)
                            Text(if (passFeeToClient) "(Cliente paga as taxas)" else "(Barbearia absorve as taxas)", fontSize = 9.sp, color = Color.Gray)
                        }

                        Text(
                            text = "R$ ${"%.2f".format(netReceived)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrazilianTaxMachineRatesModal(
    currentRates: BrazilianCardMachineRates,
    primaryColor: Color,
    onSave: (BrazilianCardMachineRates) -> Unit,
    onDismiss: () -> Unit
) {
    var pixFee by remember { mutableStateOf(currentRates.pixFeePercent.toString()) }
    var debitFee by remember { mutableStateOf(currentRates.debitFeePercent.toString()) }
    var credit1xFee by remember { mutableStateOf(currentRates.credit1xFeePercent.toString()) }
    var credit2xFee by remember { mutableStateOf(currentRates.credit2xFeePercent.toString()) }
    var credit12xFee by remember { mutableStateOf(currentRates.credit12xFeePercent.toString()) }
    var taxRegime by remember { mutableStateOf(currentRates.taxRegimeName) }
    var taxPercent by remember { mutableStateOf(currentRates.taxPercent.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Taxas da Maquininha & Impostos", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Configure as taxas cobradas pela maquininha do seu estabelecimento:", fontSize = 11.sp, color = Color.LightGray)

                OutlinedTextField(
                    value = pixFee,
                    onValueChange = { pixFee = it },
                    label = { Text("Taxa PIX (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = debitFee,
                    onValueChange = { debitFee = it },
                    label = { Text("Taxa Débito (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = credit1xFee,
                    onValueChange = { credit1xFee = it },
                    label = { Text("Taxa Crédito 1x (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = credit2xFee,
                    onValueChange = { credit2xFee = it },
                    label = { Text("Taxa Crédito Parcelado 2x-6x (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = credit12xFee,
                    onValueChange = { credit12xFee = it },
                    label = { Text("Taxa Crédito Parcelado 7x-12x (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = Color.DarkGray)

                OutlinedTextField(
                    value = taxRegime,
                    onValueChange = { taxRegime = it },
                    label = { Text("Nome do Regime Tributário (ex: Simples / MEI)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = taxPercent,
                    onValueChange = { taxPercent = it },
                    label = { Text("Alíquota do Imposto (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = currentRates.copy(
                        pixFeePercent = pixFee.replace(",", ".").toDoubleOrNull() ?: currentRates.pixFeePercent,
                        debitFeePercent = debitFee.replace(",", ".").toDoubleOrNull() ?: currentRates.debitFeePercent,
                        credit1xFeePercent = credit1xFee.replace(",", ".").toDoubleOrNull() ?: currentRates.credit1xFeePercent,
                        credit2xFeePercent = credit2xFee.replace(",", ".").toDoubleOrNull() ?: currentRates.credit2xFeePercent,
                        credit12xFeePercent = credit12xFee.replace(",", ".").toDoubleOrNull() ?: currentRates.credit12xFeePercent,
                        taxRegimeName = taxRegime.ifBlank { currentRates.taxRegimeName },
                        taxPercent = taxPercent.replace(",", ".").toDoubleOrNull() ?: currentRates.taxPercent
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                modifier = Modifier.testTag("save_machine_rates_button")
            ) {
                Text("Salvar Taxas", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}


