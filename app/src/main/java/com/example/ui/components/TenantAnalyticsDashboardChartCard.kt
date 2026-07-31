package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold
import com.example.util.TaxCalculator

data class MonthlyAnalyticsData(
    val monthName: String,
    val grossRevenue: Double,
    val appointmentCount: Int,
    val machineFeePercent: Double = 3.2,
    val taxPercent: Double = 6.0
) {
    val machineFeeAmount: Double get() = grossRevenue * (machineFeePercent / 100.0)
    val taxAmount: Double get() = grossRevenue * (taxPercent / 100.0)
    val netRevenue: Double get() = grossRevenue - machineFeeAmount - taxAmount
    val averageTicket: Double get() = if (appointmentCount > 0) grossRevenue / appointmentCount else 0.0
}

/**
 * Interactive Data Visualization Dashboard for Tenants.
 * Displays monthly revenue charts, appointment volume trends, and Brazilian tax breakdowns.
 */
@Composable
fun TenantAnalyticsDashboardChartCard(
    primaryColor: Color = BarberGold,
    modifier: Modifier = Modifier
) {
    var selectedPeriodTab by remember { mutableIntStateOf(0) } // 0: 6 Meses, 1: 3 Meses, 2: Este Mês
    var selectedChartMetric by remember { mutableIntStateOf(0) } // 0: Receita Mensal, 1: Volume de Agendamentos, 2: Distribuição de Impostos
    var selectedMonthIndex by remember { mutableIntStateOf(5) } // Default last month (Jul)

    val allMonthsData = remember {
        listOf(
            MonthlyAnalyticsData("Fev", 12400.0, 155, 3.1, 6.0),
            MonthlyAnalyticsData("Mar", 14800.0, 185, 3.2, 6.0),
            MonthlyAnalyticsData("Abr", 16200.0, 202, 3.0, 6.0),
            MonthlyAnalyticsData("Mai", 18500.0, 230, 3.3, 6.0),
            MonthlyAnalyticsData("Jun", 21000.0, 262, 3.2, 6.0),
            MonthlyAnalyticsData("Jul", 24850.0, 310, 3.19, 6.0)
        )
    }

    val displayData = when (selectedPeriodTab) {
        1 -> allMonthsData.takeLast(3)
        2 -> allMonthsData.takeLast(1)
        else -> allMonthsData
    }

    val totalGross = displayData.sumOf { it.grossRevenue }
    val totalAppointments = displayData.sumOf { it.appointmentCount }
    val totalMachineFees = displayData.sumOf { it.machineFeeAmount }
    val totalTaxes = displayData.sumOf { it.taxAmount }
    val totalNet = displayData.sumOf { it.netRevenue }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PAINEL DE ANÁLISE DE DADOS E IMPOSTOS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Métricas em Tempo Real • Barbearia & Agendamentos",
                            fontSize = 11.sp,
                            color = BarberAiCyan
                        )
                    }
                }

                // Time Period Selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E252B))
                        .padding(2.dp)
                ) {
                    val periods = listOf("6M", "3M", "1M")
                    periods.forEachIndexed { idx, label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedPeriodTab == idx) primaryColor else Color.Transparent)
                                .clickable { selectedPeriodTab = idx }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedPeriodTab == idx) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryBadge(
                    title = "Faturamento Bruto",
                    value = TaxCalculator.formatCurrencyBrl(totalGross),
                    subtitle = "${totalAppointments} Atendimentos",
                    accentColor = primaryColor,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryBadge(
                    title = "Lucro Líquido Real",
                    value = TaxCalculator.formatCurrencyBrl(totalNet),
                    subtitle = "Livre de Impostos",
                    accentColor = BarberAiCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart View Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedChartMetric == 0,
                    onClick = { selectedChartMetric = 0 },
                    label = { Text("📊 Receita Mensal", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("chart_tab_monthly_revenue")
                )

                FilterChip(
                    selected = selectedChartMetric == 1,
                    onClick = { selectedChartMetric = 1 },
                    label = { Text("📈 Volume de Clientes", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarberAiCyan,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("chart_tab_appointment_volume")
                )

                FilterChip(
                    selected = selectedChartMetric == 2,
                    onClick = { selectedChartMetric = 2 },
                    label = { Text("🍩 Divisão Impostos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF9F43),
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("chart_tab_tax_breakdown")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Canvas Section
            Surface(
                color = Color(0xFF131A20),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    when (selectedChartMetric) {
                        0 -> MonthlyRevenueBarChart(
                            dataList = displayData,
                            primaryColor = primaryColor,
                            selectedIndex = selectedMonthIndex.coerceAtMost(displayData.size - 1),
                            onSelectIndex = { selectedMonthIndex = it }
                        )
                        1 -> AppointmentVolumeLineChart(
                            dataList = displayData,
                            accentColor = BarberAiCyan,
                            selectedIndex = selectedMonthIndex.coerceAtMost(displayData.size - 1),
                            onSelectIndex = { selectedMonthIndex = it }
                        )
                        else -> TaxBreakdownDonutChart(
                            gross = totalGross,
                            net = totalNet,
                            machineFees = totalMachineFees,
                            taxes = totalTaxes,
                            primaryColor = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Month Detailed Breakdown Footer
            val safeMonthIdx = selectedMonthIndex.coerceIn(0, displayData.size - 1)
            val monthDetail = displayData.getOrNull(safeMonthIdx) ?: displayData.last()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E252B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mês Selecionado: ${monthDetail.monthName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${monthDetail.appointmentCount} Clientes • Ticket Médio: ${TaxCalculator.formatCurrencyBrl(monthDetail.averageTicket)}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Líquido: ${TaxCalculator.formatCurrencyBrl(monthDetail.netRevenue)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "Taxas + Impostos: ${TaxCalculator.formatCurrencyBrl(monthDetail.machineFeeAmount + monthDetail.taxAmount)}",
                        fontSize = 9.sp,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricSummaryBadge(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF192229),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MonthlyRevenueBarChart(
    dataList: List<MonthlyAnalyticsData>,
    primaryColor: Color,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    val maxRevenue = (dataList.maxOfOrNull { it.grossRevenue } ?: 1.0) * 1.15

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Evolução Mensal: Bruto (Escuro) vs. Líquido (Dourado)", fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barCount = dataList.size
                val barSpacing = canvasWidth / (barCount * 2f)
                val barWidth = (canvasWidth - (barSpacing * (barCount + 1))) / barCount

                dataList.forEachIndexed { index, data ->
                    val x = barSpacing + index * (barWidth + barSpacing)
                    val grossHeight = (data.grossRevenue / maxRevenue * canvasHeight).toFloat()
                    val netHeight = (data.netRevenue / maxRevenue * canvasHeight).toFloat()

                    val isSelected = index == selectedIndex

                    // Gross Bar Background
                    drawRoundRect(
                        color = Color(0xFF2B3843),
                        topLeft = Offset(x, canvasHeight - grossHeight),
                        size = Size(barWidth, grossHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )

                    // Net Bar Foreground
                    drawRoundRect(
                        color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.7f),
                        topLeft = Offset(x, canvasHeight - netHeight),
                        size = Size(barWidth, netHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Month Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dataList.forEachIndexed { index, data ->
                Text(
                    text = data.monthName,
                    fontSize = 10.sp,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == selectedIndex) primaryColor else Color.Gray,
                    modifier = Modifier.clickable { onSelectIndex(index) }
                )
            }
        }
    }
}

@Composable
private fun AppointmentVolumeLineChart(
    dataList: List<MonthlyAnalyticsData>,
    accentColor: Color,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    val maxVolume = (dataList.maxOfOrNull { it.appointmentCount } ?: 1) * 1.2

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Volume Mensal de Agendamentos Concluídos", fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val stepX = width / (dataList.size - 1).coerceAtLeast(1)

                val points = dataList.mapIndexed { idx, data ->
                    val x = idx * stepX
                    val y = height - ((data.appointmentCount / maxVolume) * height).toFloat()
                    Offset(x, y)
                }

                // Draw filled gradient area
                val path = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                }

                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )

                // Draw line connection
                val linePath = Path().apply {
                    points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                }

                drawPath(
                    path = linePath,
                    color = accentColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dots
                points.forEachIndexed { i, p ->
                    val isSel = i == selectedIndex
                    drawCircle(
                        color = if (isSel) Color.White else accentColor,
                        radius = if (isSel) 6.dp.toPx() else 4.dp.toPx(),
                        center = p
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataList.forEachIndexed { idx, data ->
                Text(
                    text = "${data.monthName}\n(${data.appointmentCount})",
                    fontSize = 9.sp,
                    fontWeight = if (idx == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (idx == selectedIndex) accentColor else Color.Gray,
                    modifier = Modifier.clickable { onSelectIndex(idx) }
                )
            }
        }
    }
}

@Composable
private fun TaxBreakdownDonutChart(
    gross: Double,
    net: Double,
    machineFees: Double,
    taxes: Double,
    primaryColor: Color
) {
    val netAngle = if (gross > 0) ((net / gross) * 360f).toFloat() else 240f
    val feeAngle = if (gross > 0) ((machineFees / gross) * 360f).toFloat() else 40f
    val taxAngle = if (gross > 0) ((taxes / gross) * 360f).toFloat() else 80f

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()

                // Net Profit Arc
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = netAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )

                // Machine Fees Arc
                drawArc(
                    color = Color(0xFFFF9F43),
                    startAngle = -90f + netAngle,
                    sweepAngle = feeAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )

                // Taxes Arc
                drawArc(
                    color = Color(0xFFFF6B6B),
                    startAngle = -90f + netAngle + feeAngle,
                    sweepAngle = taxAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "LÍQUIDO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(
                    text = "${"%.1f".format(if (gross > 0) (net / gross * 100) else 0.0)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryColor
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DonutLegendItem(color = primaryColor, label = "Lucro Líquido Real", value = TaxCalculator.formatCurrencyBrl(net))
            DonutLegendItem(color = Color(0xFFFF9F43), label = "Taxas da Maquininha", value = TaxCalculator.formatCurrencyBrl(machineFees))
            DonutLegendItem(color = Color(0xFFFF6B6B), label = "Imposto DAS / Simples", value = TaxCalculator.formatCurrencyBrl(taxes))
        }
    }
}

@Composable
private fun DonutLegendItem(color: Color, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 10.sp, color = Color.LightGray)
        }
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
