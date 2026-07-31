package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.util.BrazilianTaxRegime
import com.example.util.PaymentMethodType
import com.example.util.TaxCalculator

/**
 * Reusable Composable Tax Calculator Component for Brazilian Barbershops.
 * Allows barbers to input service prices, machine rates, select tax regimes,
 * and view calculated net earnings according to Brazilian tax rules.
 */
@Composable
fun TaxCalculatorCard(
    primaryColor: Color,
    initialServicePrice: Double = 80.00,
    initialMachineFeePercent: Double = 3.19,
    initialTaxPercent: Double = 6.00,
    onConfigureRatesClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var inputValueStr by remember { mutableStateOf("%.2f".format(initialServicePrice)) }
    var selectedMethodIndex by remember { mutableIntStateOf(2) } // Default: Crédito 1x
    var selectedTaxRegimeIndex by remember { mutableIntStateOf(1) } // Default: Simples Nacional 6%
    var customMachineFeeStr by remember { mutableStateOf("%.2f".format(initialMachineFeePercent)) }
    var passFeeToClient by remember { mutableStateOf(false) }

    val baseValue = inputValueStr.replace(",", ".").toDoubleOrNull() ?: 0.0

    val currentPaymentMethod = PaymentMethodType.entries.getOrElse(selectedMethodIndex) { PaymentMethodType.CREDIT_1X }
    val currentTaxRegime = BrazilianTaxRegime.entries.getOrElse(selectedTaxRegimeIndex) { BrazilianTaxRegime.SIMPLES_NACIONAL_6 }

    val effectiveMachineFee = customMachineFeeStr.replace(",", ".").toDoubleOrNull() ?: currentPaymentMethod.defaultRatePercent
    val effectiveTaxPercent = if (currentTaxRegime == BrazilianTaxRegime.CPF_ISENTO || currentTaxRegime == BrazilianTaxRegime.MEI) 0.0 else currentTaxRegime.defaultTaxPercent

    val result = TaxCalculator.calculateNetEarnings(
        servicePrice = baseValue,
        machineFeePercent = effectiveMachineFee,
        taxPercent = effectiveTaxPercent,
        passFeeToClient = passFeeToClient,
        paymentMethodName = currentPaymentMethod.displayName,
        taxRegimeName = currentTaxRegime.displayName
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar
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
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CALCULADORA LÍQUIDA DE SERVIÇOS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${currentTaxRegime.displayName} (${result.taxPercent}%)",
                            fontSize = 11.sp,
                            color = BarberAiCyan
                        )
                    }
                }

                if (onConfigureRatesClick != null) {
                    Button(
                        onClick = onConfigureRatesClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3842), contentColor = primaryColor),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_configure_machine_rates")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Configurar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preset Service Quick Chips
            Text("Selecione um Serviço da Barbearia:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val servicePresets = listOf(
                    "35.00" to "Corte Cabelo",
                    "45.00" to "Barba Terapia",
                    "80.00" to "Combo Cabelo + Barba",
                    "120.00" to "Dia do Noivo",
                    "150.00" to "Selagem / Pigmentação"
                )
                items(servicePresets) { (valStr, label) ->
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

            // Service Price Input Field
            OutlinedTextField(
                value = inputValueStr,
                onValueChange = { inputValueStr = it },
                label = { Text(if (passFeeToClient) "Valor do Serviço Líquido no Caixa (R$)" else "Preço do Serviço na Tabela (R$)") },
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
                    .testTag("tax_calculator_service_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Method Selector Chips
            Text("Forma de Pagamento (Maquininha):", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(PaymentMethodType.entries) { index, method ->
                    FilterChip(
                        selected = selectedMethodIndex == index,
                        onClick = {
                            selectedMethodIndex = index
                            customMachineFeeStr = "%.2f".format(method.defaultRatePercent)
                        },
                        label = { Text("${method.displayName} (${method.defaultRatePercent}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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

            // Brazilian Tax Regime Selector Chips
            Text("Regime Tributário (Brasil):", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(BrazilianTaxRegime.entries) { index, regime ->
                    FilterChip(
                        selected = selectedTaxRegimeIndex == index,
                        onClick = { selectedTaxRegimeIndex = index },
                        label = { Text(regime.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BarberAiCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF1E252B),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Pass Fee to Client Option
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
                    Text("Repassar Taxas da Maquininha ao Cliente?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Calcula o acréscimo automático a cobrar no cartão", fontSize = 10.sp, color = Color.Gray)
                }

                Switch(
                    checked = passFeeToClient,
                    onCheckedChange = { passFeeToClient = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = primaryColor
                    ),
                    modifier = Modifier.testTag("switch_pass_fee_to_client")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detailed Output Results Box
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
                        Text("Valor Total no Cartão (Cobrado do Cliente):", fontSize = 11.sp, color = Color.LightGray)
                        Text(TaxCalculator.formatCurrencyBrl(result.chargedToClient), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("(-) Taxa Maquininha ${result.paymentMethodName} (${"%.2f".format(result.machineFeePercent)}%):", fontSize = 11.sp, color = Color(0xFFFF6B6B))
                        Text("- ${TaxCalculator.formatCurrencyBrl(result.machineFeeAmount)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B6B))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("(-) Imposto Previsto ${result.taxRegimeName} (${"%.2f".format(result.taxPercent)}%):", fontSize = 11.sp, color = Color(0xFFFF6B6B))
                        Text("- ${TaxCalculator.formatCurrencyBrl(result.taxAmount)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B6B))
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
                            Text("LÍQUIDO REAL NO CAIXA DA BARBEARIA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = BarberAiCyan)
                            Text(
                                if (passFeeToClient) "✓ Cliente pagou as taxas" else "⚠ Barbearia absorveu as taxas",
                                fontSize = 9.sp,
                                color = if (passFeeToClient) Color(0xFF25D366) else Color.Gray
                            )
                        }

                        Text(
                            text = TaxCalculator.formatCurrencyBrl(result.netEarnings),
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
