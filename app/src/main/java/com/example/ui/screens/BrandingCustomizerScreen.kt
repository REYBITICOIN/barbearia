package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor

@Composable
fun BrandingCustomizerScreen(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()

    var name by remember(activeShop) { mutableStateOf(activeShop?.name ?: "") }
    var address by remember(activeShop) { mutableStateOf(activeShop?.address ?: "") }
    var phone by remember(activeShop) { mutableStateOf(activeShop?.phone ?: "") }
    var facadeUrl by remember(activeShop) { mutableStateOf(activeShop?.facadePhotoUrl ?: "") }
    var ownerUrl by remember(activeShop) { mutableStateOf(activeShop?.ownerPhotoUrl ?: "") }
    var selectedColorHex by remember(activeShop) { mutableStateOf(activeShop?.primaryColorHex ?: "#D4AF37") }

    var isSavedAlert by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        "#D4AF37" to "Ouro Nobre (Gold)",
        "#2E7D32" to "Verde Esmeralda",
        "#C62828" to "Vermelho Barber",
        "#1565C0" to "Azul Safira",
        "#6A1B9A" to "Roxo Cyber",
        "#FF8F00" to "Âmbar Vintages"
    )

    val currentThemeColor = parseHexColor(selectedColorHex, BarberGold)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "PERSONALIZAÇÃO DE MARCA & ETAPA 1",
                    style = MaterialTheme.typography.labelSmall,
                    color = currentThemeColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Identidade Visual do Tenant",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Personalize a fachada, foto do barbeiro e cores dinâmicas da sua barbearia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Preview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, currentThemeColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRÉ-VISUALIZAÇÃO DO CLIENTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = currentThemeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(currentThemeColor.copy(alpha = 0.2f))
                                .border(2.dp, currentThemeColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = null,
                                tint = currentThemeColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column {
                            Text(text = name.ifBlank { "Sua Barbearia" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = address.ifBlank { "Endereço da Barbearia" }, style = MaterialTheme.typography.bodySmall)
                            Text(text = "WhatsApp: ${phone.ifBlank { "(00) 00000-0000" }}", style = MaterialTheme.typography.bodySmall, color = currentThemeColor)
                        }
                    }
                }
            }
        }

        // Color Palette Selector
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = currentThemeColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Cor Principal do Tema", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(colorOptions) { (hex, label) ->
                            val color = parseHexColor(hex, BarberGold)
                            val isSelected = selectedColorHex.equals(hex, ignoreCase = true)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedColorHex = hex }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = label.split(" ")[0], fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Form details
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Dados e Mídias do Estabelecimento", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome da Barbearia") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_branding_name")
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Endereço Físico / Localização") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone WhatsApp de Contato") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = facadeUrl,
                        onValueChange = { facadeUrl = it },
                        label = { Text("URL / Foto de Fachada do Estabelecimento") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ownerUrl,
                        onValueChange = { ownerUrl = it },
                        label = { Text("URL / Foto de Perfil do Barbeiro Proprietário") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.updateBranding(
                                facadePhotoUrl = facadeUrl,
                                ownerPhotoUrl = ownerUrl,
                                primaryColorHex = selectedColorHex,
                                secondaryColorHex = "#121316",
                                name = name,
                                address = address,
                                phone = phone
                            )
                            isSavedAlert = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = currentThemeColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_branding_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salvar Personalização de Marca", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (isSavedAlert) {
        AlertDialog(
            onDismissRequest = { isSavedAlert = false },
            title = { Text("Marca Atualizada com Sucesso!") },
            text = { Text("As configurações visuais, fotos e paleta de cores foram salvas no banco de dados isolado do tenant.") },
            confirmButton = {
                Button(onClick = { isSavedAlert = false }) { Text("Ok") }
            }
        )
    }
}
