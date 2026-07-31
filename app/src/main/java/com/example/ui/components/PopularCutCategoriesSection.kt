package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ServiceEntity
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold

data class ServiceCategoryItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val defaultPrice: String,
    val badge: String = "Popular"
)

val POPULAR_CUT_CATEGORIES = listOf(
    ServiceCategoryItem("cabelo", "Cabelo", "Corte masculino, Fade & Degradê", Icons.Outlined.ContentCut, "R$ 55,00"),
    ServiceCategoryItem("barba", "Barba", "Barboterapia com toalha quente", Icons.Outlined.Face, "R$ 45,00"),
    ServiceCategoryItem("acabamento", "Acabamento", "Pezinho & alinhamento na navalha", Icons.Outlined.Build, "R$ 25,00"),
    ServiceCategoryItem("massagem", "Massagem", "Massagem capilar & relaxamento", Icons.Outlined.SelfImprovement, "R$ 40,00"),
    ServiceCategoryItem("sobrancelha", "Sobrancelha", "Design & alinhamento navalhado", Icons.Outlined.Visibility, "R$ 20,00"),
    ServiceCategoryItem("hidratacao", "Hidratação", "Cauterização & tratamento capilar", Icons.Outlined.WaterDrop, "R$ 50,00")
)

@Composable
fun PopularCutCategoriesSection(
    primaryColor: Color = BarberGold,
    services: List<ServiceEntity> = emptyList(),
    onSelectCategory: (String) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SERVIÇOS POPULARES",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Categorias de Corte & Cuidados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = primaryColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "6 CATEGORIAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid 2x3 for the 6 popular categories matching the web model
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val rows = POPULAR_CUT_CATEGORIES.chunked(3)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { cat ->
                        val isSelected = selectedCategory == cat.id
                        val count = services.count { it.category.equals(cat.title, ignoreCase = true) }

                        Surface(
                            onClick = {
                                selectedCategory = if (isSelected) null else cat.id
                                onSelectCategory(cat.title)
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) primaryColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) primaryColor else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cat_card_${cat.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) primaryColor else primaryColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.title,
                                            tint = if (isSelected) Color.Black else primaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (count > 0) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BarberAiCyan.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "$count",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BarberAiCyan,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = cat.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = cat.defaultPrice,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expanded detail when category selected
        AnimatedVisibility(visible = selectedCategory != null) {
            val selectedItem = POPULAR_CUT_CATEGORIES.find { it.id == selectedCategory }
            selectedItem?.let { cat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(cat.icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Detalhes: ${cat.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                            IconButton(onClick = { selectedCategory = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(cat.description, fontSize = 12.sp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))

                        val catServices = services.filter { it.category.equals(cat.title, ignoreCase = true) }
                        if (catServices.isEmpty()) {
                            Text(
                                text = "Valor sugerido de catálogo: ${cat.defaultPrice}. Você pode adicionar novos serviços nesta categoria na aba Agendamentos & Catálogo.",
                                fontSize = 11.sp,
                                color = BarberAiCyan
                            )
                        } else {
                            catServices.forEach { srv ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${srv.name}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    Text("R$ ${"%.2f".format(srv.price)}", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
