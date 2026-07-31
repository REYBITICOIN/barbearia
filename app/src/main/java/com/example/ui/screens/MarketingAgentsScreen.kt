package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor

@Composable
fun MarketingAgentsScreen(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val marketingLogs by viewModel.marketingLogs.collectAsState()

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    var selectedChannelTab by remember { mutableStateOf(0) } // 0: Google, 1: Meta, 2: YouTube

    // Form inputs
    var reviewClientName by remember { mutableStateOf("Marcelo Oliveira") }
    var reviewText by remember { mutableStateOf("Atendimento sensacional! O corte e a barba ficaram perfeitos.") }
    var reviewRating by remember { mutableStateOf(5) }

    var haircutType by remember { mutableStateOf("Mid Fade Navalhado com Barba Alinhada") }
    var promoOffer by remember { mutableStateOf("10% OFF para o primeiro agendamento da semana!") }

    var videoTopic by remember { mutableStateOf("Degradê Perfeito e Acabamento com Navalha ⚡") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "AGENTES DE MARKETING AUTÔNOMOS (ETAPA 3)",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automação Multi-Canal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Integração para redes sociais: Google Meu Negócio, Meta (FB/IG) e YouTube Shorts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Channel Selector Tabs
        item {
            TabRow(
                selectedTabIndex = selectedChannelTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = primaryColor
            ) {
                Tab(
                    selected = selectedChannelTab == 0,
                    onClick = { selectedChannelTab = 0 },
                    text = { Text("Google Business", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedChannelTab == 1,
                    onClick = { selectedChannelTab = 1 },
                    text = { Text("Meta (FB/IG)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedChannelTab == 2,
                    onClick = { selectedChannelTab = 2 },
                    text = { Text("YouTube Shorts", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Action Form
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
                    when (selectedChannelTab) {
                        0 -> {
                            // Google Business API Form
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF4285F4))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Google Meu Negócio API", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Atualização automática da fachada e respostas com IA às avaliações dos clientes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = reviewClientName,
                                onValueChange = { reviewClientName = it },
                                label = { Text("Nome do Cliente da Avaliação") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = reviewText,
                                onValueChange = { reviewText = it },
                                label = { Text("Comentário do Cliente") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    viewModel.triggerGoogleBusinessReviewReply(reviewClientName, reviewText, reviewRating)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("publish_google_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gerar & Responder Avaliação com IA", color = Color.White)
                            }
                        }

                        1 -> {
                            // Meta API Form
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFFE1306C))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Meta API (Facebook & Instagram)", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Geração de posts e legendas atrativas com hashtags e chamadas de agendamento.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = haircutType,
                                onValueChange = { haircutType = it },
                                label = { Text("Estilo de Corte / Trabalho") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = promoOffer,
                                onValueChange = { promoOffer = it },
                                label = { Text("Oferta / Promoção (Opcional)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    viewModel.publishMetaPost(haircutType, promoOffer)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("publish_meta_button")
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gerar & Publicar Post com IA", color = Color.White)
                            }
                        }

                        2 -> {
                            // YouTube Shorts API Form
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFFF0000))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "YouTube API (Shorts)", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Upload e geração de títulos virais e hashtags para Shorts dos cortes do dia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = videoTopic,
                                onValueChange = { videoTopic = it },
                                label = { Text("Título / Tema do Vídeo Short") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    viewModel.publishYouTubeShort(videoTopic)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("publish_youtube_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gerar Detalhes & Simular Upload", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Marketing Campaign Logs History
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "HISTÓRICO DE CAMPANHAS PUBLICADAS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                if (marketingLogs.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nenhuma publicação no histórico ainda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    marketingLogs.forEach { log ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = when {
                                            log.channel.contains("Google") -> Color(0xFF4285F4)
                                            log.channel.contains("Meta") -> Color(0xFFE1306C)
                                            else -> Color(0xFFFF0000)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = log.channel,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Text(
                                        text = log.status,
                                        color = BarberAiCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = log.title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
