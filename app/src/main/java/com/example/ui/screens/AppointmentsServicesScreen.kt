package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppointmentEntity
import com.example.data.local.ServiceEntity
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor
import com.example.ui.components.PopularCutCategoriesSection

@Composable
fun AppointmentsServicesScreen(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val services by viewModel.services.collectAsState()
    val appointments by viewModel.appointments.collectAsState()

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    var selectedTab by remember { mutableStateOf(0) } // 0: Agendamentos, 1: Serviços
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddAptDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GESTÃO DO TENANT",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Agendamentos & Catálogo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    if (selectedTab == 0) showAddAptDialog = true else showAddServiceDialog = true
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(primaryColor)
                    .testTag("add_item_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black)
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = primaryColor
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Agendamentos (${appointments.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Serviços (${services.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Appointments List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (appointments.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum agendamento cadastrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(appointments) { apt ->
                        AppointmentItemCard(
                            apt = apt,
                            primaryColor = primaryColor,
                            onStatusChange = { newStatus ->
                                viewModel.updateAppointmentStatus(apt.appointmentId, newStatus)
                            }
                        )
                    }
                }
            }
        } else {
            // Services List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    PopularCutCategoriesSection(
                        primaryColor = primaryColor,
                        services = services
                    )
                }

                if (services.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum serviço no catálogo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(services) { srv ->
                        ServiceItemCard(
                            service = srv,
                            primaryColor = primaryColor,
                            onDelete = { viewModel.deleteService(srv.serviceId) }
                        )
                    }
                }
            }
        }
    }

    if (showAddServiceDialog) {
        AddServiceModal(
            onDismiss = { showAddServiceDialog = false },
            onAdd = { name, price, dur, cat, desc ->
                viewModel.addService(name, price, dur, cat, desc)
                showAddServiceDialog = false
            }
        )
    }

    if (showAddAptDialog) {
        AddAppointmentModal(
            services = services,
            ownerName = activeShop?.ownerName ?: "Mestre Barbeiro",
            onDismiss = { showAddAptDialog = false },
            onAdd = { name, phone, srvName, barbName, dateStr, price ->
                viewModel.addAppointment(name, phone, srvName, barbName, dateStr, price)
                showAddAptDialog = false
            }
        )
    }
}

@Composable
fun AppointmentItemCard(
    apt: AppointmentEntity,
    primaryColor: Color,
    onStatusChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (apt.createdByAi) Icons.Default.SmartToy else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (apt.createdByAi) BarberAiCyan else primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = apt.clientName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Surface(
                    color = when (apt.status) {
                        "Concluído" -> Color(0xFF2E7D32)
                        "Cancelado" -> Color(0xFFC62828)
                        else -> primaryColor.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = apt.status,
                        color = if (apt.status == "Agendado") primaryColor else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "✂️ ${apt.serviceName} • R$ ${"%.2f".format(apt.price)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "🕒 ${apt.dateTimeStr} • Barbeiro: ${apt.barberName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (apt.notes.isNotBlank()) {
                Text(text = "📝 ${apt.notes}", style = MaterialTheme.typography.bodySmall, color = BarberAiCyan)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (apt.status != "Concluído") {
                        OutlinedButton(
                            onClick = { onStatusChange("Concluído") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Marcar Concluído", fontSize = 10.sp)
                        }
                    }
                    if (apt.status != "Cancelado") {
                        OutlinedButton(
                            onClick = { onStatusChange("Cancelado") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Cancelar", fontSize = 10.sp)
                        }
                    }
                }

                val context = LocalContext.current
                IconButton(
                    onClick = {
                        val message = "Olá ${apt.clientName}! Seu agendamento na Barbearia do João foi confirmado para ${apt.dateTimeStr} com ${apt.barberName}."
                        com.example.data.remote.WhatsappCloudApiService.openWhatsAppDirectIntent(
                            context = context,
                            phone = apt.clientPhone,
                            message = message
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar WhatsApp",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceItemCard(
    service: ServiceEntity,
    primaryColor: Color,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = primaryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = service.category,
                            color = primaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${service.durationMinutes} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = service.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = service.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "R$ ${"%.2f".format(service.price)}",
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddServiceModal(
    onDismiss: () -> Unit,
    onAdd: (name: String, price: Double, dur: Int, cat: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("50.0") }
    var durationStr by remember { mutableStateOf("30") }
    var category by remember { mutableStateOf("Cabelo") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Novo Serviço") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Serviço") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Preço (R$)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = durationStr, onValueChange = { durationStr = it }, label = { Text("Duração (Minutos)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoria (Cabelo/Barba/Combo)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 50.0
                    val dur = durationStr.toIntOrNull() ?: 30
                    if (name.isNotBlank()) {
                        onAdd(name, price, dur, category, desc)
                    }
                }
            ) {
                Text("Salvar Serviço")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun AddAppointmentModal(
    services: List<ServiceEntity>,
    ownerName: String,
    onDismiss: () -> Unit,
    onAdd: (clientName: String, clientPhone: String, serviceName: String, barberName: String, dateTime: String, price: Double) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf(services.firstOrNull()?.name ?: "Corte Masculino Premium") }
    var dateTimeStr by remember { mutableStateOf("Hoje, 17:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Agendamento Manual") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nome do Cliente") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = clientPhone, onValueChange = { clientPhone = it }, label = { Text("Telefone WhatsApp") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = selectedService, onValueChange = { selectedService = it }, label = { Text("Serviço") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dateTimeStr, onValueChange = { dateTimeStr = it }, label = { Text("Data / Horário") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = services.find { it.name == selectedService }?.price ?: 55.0
                    if (clientName.isNotBlank()) {
                        onAdd(clientName, clientPhone, selectedService, ownerName, dateTimeStr, price)
                    }
                }
            ) {
                Text("Agendar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
